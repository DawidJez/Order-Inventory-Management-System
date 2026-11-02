package pl.dawjez.orderinventory.common;

/**
 * Thrown when the order request is well-formed but makes no business sense
 */
public class InvalidOrderRequestException extends RuntimeException {

    public InvalidOrderRequestException(String message) {
        super(message);
    }
}
