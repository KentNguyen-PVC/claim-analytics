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