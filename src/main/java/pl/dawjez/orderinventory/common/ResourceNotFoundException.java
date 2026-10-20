package pl.dawjez.orderinventory.common;

// Thrown when a requested entity does not exist.
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
