CREATE OR REPLACE PACKAGE CLAIM_SLA_PKG AS
    
    FUNCTION calculate_working_minutes_8_17(
        p_start        IN TIMESTAMP WITH TIME ZONE,
        p_end          IN TIMESTAMP WITH TIME ZONE,
        p_country_code IN VARCHAR2
    ) RETURN NUMBER;

    PROCEDURE finalize_claim(
	    p_claim_id     IN NUMBER,
	    p_from_status  IN VARCHAR2,
	    p_status       IN VARCHAR2
    );

    PROCEDURE extend_calendar(
        p_country_code IN VARCHAR2,
        p_until_date   IN DATE
    );

END CLAIM_SLA_PKG;
/