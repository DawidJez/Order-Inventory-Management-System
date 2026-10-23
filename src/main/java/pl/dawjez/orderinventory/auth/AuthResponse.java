package pl.dawjez.orderinventory.auth;

/**
 * Result of a successful login. The client sends accessToken in the header
 * "Authorization: Bearer <accessToken>" with every later request. expiresIn is in seconds.
 */
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
