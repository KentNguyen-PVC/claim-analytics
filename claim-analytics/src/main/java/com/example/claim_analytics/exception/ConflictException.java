package com.example.claim_analytics.exception;

public class ConflictException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5803235874285547749L;

	public ConflictException(String message) {
        super(message);
    }
}