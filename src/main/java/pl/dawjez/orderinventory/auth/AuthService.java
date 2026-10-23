package pl.dawjez.orderinventory.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.dawjez.orderinventory.common.DuplicateEmailException;
import pl.dawjez.orderinventory.security.JwtProperties;
import pl.dawjez.orderinventory.security.JwtService;
import pl.dawjez.orderinventory.user.Role;
import pl.dawjez.orderinventory.user.User;
import pl.dawjez.orderinventory.user.UserRepository;
import pl.dawjez.orderinventory.user.UserResponse;

import java.util.Locale;

/**
 * Registration and login.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    /**
     * Creates a new account with the USER role.
     *
     * The password is hashed with BCrypt before it is stored, so the plain text never reaches the
     * database. The email is trimmed and lowercased: the UNIQUE constraint is case-sensitive, so
     * without this "A@x.com" and "a@x.com" would become two accounts. The existsByEmail check gives
     * a clean 409 in the normal case; the constraint is still the real guarantee for two parallel
     * requests.
     */
    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = normalize(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }
        User user = userRepository.save(new User(
                email,
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                Role.USER));
        return UserResponse.from(user);
    }

    /**
     * Checks the credentials and issues a JWT.
     *
     * AuthenticationManager does the real work: it loads the user through UserDetailsService and
     * compares the password with PasswordEncoder.matches. On failure it throws BadCredentialsException,
     * the same one for an unknown email and for a wrong password, so the API does not reveal which
     * emails have accounts. GlobalExceptionHandler turns it into 401.
     */
    public AuthResponse login(LoginRequest request) {
        String email = normalize(request.email());
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(email, request.password()));
        User user = userRepository.findByEmail(email).orElseThrow();
        return new AuthResponse(jwtService.issueToken(user), "Bearer", jwtProperties.expirationMs() / 1000);
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
