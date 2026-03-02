INSERT /*+ APPEND PARALLEL(8) */ INTO CLAIM (
    POLICY_ID,
    CLAIM_NUMBER,
    CLAIM_DATE,
    CLAIM_AMOUNT,
    APPROVED_AMOUNT,
    CLAIM_STATUS,
    CLAIM_TYPE,
    DESCRIPTION,
    CREATED_AT,
    UPDATED_AT,
    FINAL_DECISION_AT,
    TAT_WORKING_MINUTES
)
SELECT
    1,
    'CLM-2026' || LEVEL,
    TIMESTAMP '2024-01-01 08:00:00'
      + NUMTODSINTERVAL(TRUNC(DBMS_RANDOM.VALUE(0,365)),'DAY'),
    5000000,
    NULL,
    'SUBMITTED',
    CASE MOD(LEVEL,3)
        WHEN 0 THEN 'HOSPITALIZATION'
        WHEN 1 THEN 'OUTPATIENT'
        ELSE 'DENTAL'
    END,
    'Emergency surgery',
    SYSTIMESTAMP,
    SYSTIMESTAMP,
    TIMESTAMP '2024-01-01 08:00:00',
    NULL

FROM dual
CONNECT BY LEVEL <= 1000000;




INSERT INTO CALENDAR_DIM (
    COUNTRY_CODE,
    CAL_DATE,
    IS_WORKING_DAY,
    WORKING_DAY_SEQ,
    WORKING_MIN_SEQ,
    HOLIDAY_NAME,
    VERSION_NO
)
SELECT
    'VN' AS COUNTRY_CODE,
    d.cal_date,

    /* Is working day */
    CASE
        WHEN TO_CHAR(d.cal_date,'DY','NLS_DATE_LANGUAGE=ENGLISH') IN ('SAT','SUN')
             OR h.NAME IS NOT NULL
        THEN 0
        ELSE 1
    END AS IS_WORKING_DAY,

    /* Working day sequence */
    SUM(
        CASE
            WHEN TO_CHAR(d.cal_date,'DY','NLS_DATE_LANGUAGE=ENGLISH') IN ('SAT','SUN')
                 OR h.NAME IS NOT NULL
            THEN 0
            ELSE 1
        END
    ) OVER (ORDER BY d.cal_date) AS WORKING_DAY_SEQ,

    /* Working minute sequence (8AM–5PM = 540 phút) */
    SUM(
        CASE
            WHEN TO_CHAR(d.cal_date,'DY','NLS_DATE_LANGUAGE=ENGLISH') IN ('SAT','SUN')
                 OR h.NAME IS NOT NULL
            THEN 0
            ELSE 540
        END
    ) OVER (ORDER BY d.cal_date) AS WORKING_MIN_SEQ,

    h.NAME AS HOLIDAY_NAME,

    1 AS VERSION_NO

FROM (
    SELECT DATE '2024-01-01' + LEVEL - 1 AS cal_date
    FROM dual
    CONNECT BY LEVEL <= DATE '2050-12-31' - DATE '2024-01-01' + 1
) d
LEFT JOIN HOLIDAY h
    ON h.COUNTRY_CODE = 'VN'
   AND h.HOLIDAY_DATE = d.cal_date
ORDER BY d.cal_date;


DECLARE
    v_count NUMBER := 0;
    v_rand  NUMBER;
    v_new_status VARCHAR2(20);
BEGIN
    FOR r IN (
        SELECT CLAIM_ID
        FROM CLAIM
        WHERE CLAIM_STATUS = 'SUBMITTED'
    )
    LOOP
        v_rand := DBMS_RANDOM.VALUE(0,100);

        IF v_rand < 70 THEN
            v_new_status := 'APPROVED';
        ELSE
            v_new_status := 'REJECTED';
        END IF;

        CLAIM_SLA_PKG.finalize_claim(
            r.CLAIM_ID,
            'SUBMITTED',
            v_new_status
        );

        v_count := v_count + 1;

        IF MOD(v_count, 500) = 0 THEN
            COMMIT;
        END IF;
    END LOOP;

    COMMIT;
END;