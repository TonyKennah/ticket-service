package uk.org.kennah.exceptions;

public class InvalidPurchaseException extends RuntimeException {

    // Standard constructor that takes a custom error message
    public InvalidPurchaseException(String message) {
        super(message);
    }

    // Optional: Constructor that takes a message and a cause (another exception)
    public InvalidPurchaseException(String message, Throwable cause) {
        super(message, cause);
    }
} 