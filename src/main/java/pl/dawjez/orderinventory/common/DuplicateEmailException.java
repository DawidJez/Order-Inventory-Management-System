package pl.dawjez.orderinventory.common;


public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("User with email '%s' already exists".formatted(email));
    }
}
