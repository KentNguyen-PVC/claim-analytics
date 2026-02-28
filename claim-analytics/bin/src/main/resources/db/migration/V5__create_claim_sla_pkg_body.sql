CREATE OR REPLACE PACKAGE BODY CLAIM_SLA_PKG AS

------------------------------------------------------------------
-- FUNCTION: calculate_working_minutes_8_17 (FIXED VERSION)
------------------------------------------------------------------
FUNCTION calculate_working_minutes_8_17(
    p_start        IN TIMESTAMP,
    p_end          IN TIMESTAMP,
    p_country_code IN VARCHAR2
) RETURN NUMBER
IS
    v_minutes NUMBER;
BEGIN
    IF p_start IS NULL OR p_end IS NULL THEN
        RAISE_APPLICATION_ERROR(-20001, 'Start or End time cannot be NULL');
    END IF;

    IF p_start >= p_end THEN
        RETURN 0;
    END IF;

    SELECT NVL(SUM(
        CASE
            WHEN c.IS_WORKING_DAY = 1 THEN
                GREATEST(
                    (
                        CAST(
                            LEAST(
                                p_end,
                                CAST(c.CAL_DATE AS TIMESTAMP) + INTERVAL '17' HOUR
                            ) AS DATE
                        )
                        -
                        CAST(
                            GREATEST(
                                p_start,
                                CAST(c.CAL_DATE AS TIMESTAMP) + INTERVAL '8' HOUR
                            ) AS DATE
                        )
                    ) * 1440,
                    0
                )
            ELSE 0
        END
    ),0)
    INTO v_minutes
    FROM CALENDAR_DIM c
    WHERE c.COUNTRY_CODE = p_country_code
      AND c.CAL_DATE BETWEEN TRUNC(p_start) AND TRUNC(p_end)
      AND c.IS_WORKING_DAY = 1;

    RETURN v_minutes;

END calculate_working_minutes_8_17;


------------------------------------------------------------------
-- PROCEDURE: finalize_claim
------------------------------------------------------------------
PROCEDURE finalize_claim(
    p_claim_id     IN NUMBER,
    p_status       IN VARCHAR2
)
IS
    v_submitted_at TIMESTAMP;
    v_country      VARCHAR2(10);
    v_sla_minutes  NUMBER;
BEGIN

    SELECT SUBMITTED_AT, COUNTRY_CODE
    INTO v_submitted_at, v_country
    FROM CLAIM
    WHERE ID = p_claim_id
    FOR UPDATE;

    IF v_submitted_at IS NULL THEN
        RAISE_APPLICATION_ERROR(-20002, 'Claim not submitted');
    END IF;

    IF p_status NOT IN ('APPROVED','REJECTED') THEN
        RAISE_APPLICATION_ERROR(-20003, 'Invalid final status');
    END IF;

    v_sla_minutes :=
        calculate_working_minutes_8_17(
            v_submitted_at,
            SYSTIMESTAMP,
            v_country
        );

    UPDATE CLAIM
    SET FINAL_DECISION_AT    = SYSTIMESTAMP,
        TAT_WORKING_MINUTES  = v_sla_minutes,
    	FINAL_STATUS         = p_status
    WHERE ID = p_claim_id;

    INSERT INTO CLAIM_STATUS_HISTORY(
        CLAIM_ID,
        STATUS,
        STATUS_AT,
        IS_FINAL
    )
    VALUES(
        p_claim_id,
        p_status,
        SYSTIMESTAMP,
        1
    );

    COMMIT;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20004, 'Claim not found');

END finalize_claim;


------------------------------------------------------------------
-- PROCEDURE: extend_calendar
------------------------------------------------------------------
PROCEDURE extend_calendar(
    p_country_code IN VARCHAR2,
    p_until_date   IN DATE
)
IS
    v_last_date     DATE;
    v_work_day_seq  NUMBER;
    v_work_min_seq  NUMBER;
    v_holiday_name  VARCHAR2(255);
BEGIN

    SELECT MAX(CAL_DATE)
    INTO v_last_date
    FROM CALENDAR_DIM
    WHERE COUNTRY_CODE = p_country_code;

    IF v_last_date IS NULL THEN
        v_last_date := TRUNC(SYSDATE) - 1;
        v_work_day_seq := 0;
        v_work_min_seq := 0;
    ELSE
        SELECT MAX(WORKING_DAY_SEQ),
               MAX(WORKING_MIN_SEQ)
        INTO v_work_day_seq, v_work_min_seq
        FROM CALENDAR_DIM
        WHERE COUNTRY_CODE = p_country_code;
    END IF;

    IF p_until_date <= v_last_date THEN
        RETURN;
    END IF;

    FOR i IN 1 .. (p_until_date - v_last_date) LOOP

        BEGIN
            SELECT NAME
            INTO v_holiday_name
            FROM HOLIDAY
            WHERE COUNTRY_CODE = p_country_code
              AND HOLIDAY_DATE = v_last_date + i;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                v_holiday_name := NULL;
        END;

        IF TO_CHAR(v_last_date + i,
                   'DY',
                   'NLS_DATE_LANGUAGE=ENGLISH')
           IN ('SAT','SUN')
           OR v_holiday_name IS NOT NULL THEN

            INSERT INTO CALENDAR_DIM
            VALUES (
                p_country_code,
                v_last_date + i,
                0,
                v_work_day_seq,
                v_work_min_seq,
                v_holiday_name,
                1
            );

        ELSE
            v_work_day_seq := v_work_day_seq + 1;
            v_work_min_seq := v_work_min_seq + 540;

            INSERT INTO CALENDAR_DIM
            VALUES (
                p_country_code,
                v_last_date + i,
                1,
                v_work_day_seq,
                v_work_min_seq,
                NULL,
                1
            );
        END IF;

    END LOOP;

    COMMIT;

END extend_calendar;

END CLAIM_SLA_PKG;
/