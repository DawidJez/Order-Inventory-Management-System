package pl.dawjez.orderinventory.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for registering a new account.
 *
 * The role is not part of it: a client must never be able to choose its own role,
 * so every registered user becomes USER (see AuthService).
 */
public record RegisterRequest(

        @NotBlank @Email @Size(max = 255)
        String email,

        // BCrypt only uses the first 72 bytes of the input, so a longer password would give a false
        // sense of security. The lower bound is a basic strength requirement.
        @NotBlank @Size(min = 8, max = 72)
        String password,

        @NotBlank @Size(max = 255)
        String firstName,

        @NotBlank @Size(max = 255)
        String lastName
) {
}
