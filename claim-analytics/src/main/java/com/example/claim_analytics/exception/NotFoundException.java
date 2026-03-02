package com.example.claim_analytics.exception;

public class NotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5803235874285547749L;

	public NotFoundException(String message) {
        super(message);
    }
}