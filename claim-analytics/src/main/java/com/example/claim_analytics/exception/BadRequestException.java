package com.example.claim_analytics.exception;

public class BadRequestException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5803235874285547749L;

	public BadRequestException(String message) {
        super(message);
    }
}