INSERT /*+ APPEND PARALLEL(8) */ INTO CLAIM (
    CLAIM_NO,
    CLAIM_TYPE,
    COUNTRY_CODE,
    POLICY_NO,
    SUBMITTED_AT,
    FINAL_STATUS,
    FINAL_DECISION_AT,
    TAT_WORKING_MINUTES,
    TAT_CALENDAR_MINUTES,
    TAT_VERSION,
    CREATED_AT
)
SELECT
    'CLM-' || LEVEL,

    CASE MOD(LEVEL,3)
        WHEN 0 THEN 'HEALTH'
        WHEN 1 THEN 'MOTOR'
        ELSE 'TRAVEL'
    END,

    CASE MOD(LEVEL,3)
        WHEN 0 THEN 'VN'
        WHEN 1 THEN 'SG'
        ELSE 'MY'
    END,

    'POL-' || LEVEL,

    TIMESTAMP '2024-01-01 08:00:00'
      + NUMTODSINTERVAL(TRUNC(DBMS_RANDOM.VALUE(0,365)),'DAY'),

    'SUBMITTED'
    SYSTIMESTAMP,
    

    NULL,
    NULL,
    1,
    SYSTIMESTAMP

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
    'SG' AS COUNTRY_CODE,
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
    ON h.COUNTRY_CODE = 'SG'
   AND h.HOLIDAY_DATE = d.cal_date
ORDER BY d.cal_date;




DECLARE
    v_count NUMBER := 0;
BEGIN
    FOR r IN (
        SELECT ID
        FROM CLAIM
        WHERE FINAL_STATUS = 'SUBMITTED'
    )
    LOOP
        CLAIM_SLA_PKG.finalize_claim(r.ID, 'APPROVED');
        v_count := v_count + 1;

        IF v_count MOD 100 = 0 THEN
            COMMIT;
        END IF;
    END LOOP;

    COMMIT;
END;