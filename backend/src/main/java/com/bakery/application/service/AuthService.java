package com.bakery.application.service;

import com.bakery.application.dto.AuthResponse;
import com.bakery.application.dto.LoginRequest;
import com.bakery.application.dto.RegisterRequest;
import com.bakery.application.exception.EmailAlreadyExistsException;
import com.bakery.application.exception.InvalidCredentialsException;
import com.bakery.application.mapper.UserMapper;
import com.bakery.application.port.ICostSettingsRepository;
import com.bakery.application.port.IUserRepository;
import com.bakery.application.port.TokenService;
import com.bakery.domain.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Casos de uso de autenticación: registro e inicio de sesión.
 * El logout es del lado del cliente (descartar el token JWT).
 */
@Service
public class AuthService {

    private final IUserRepository userRepository;
    private final ICostSettingsRepository costSettingsRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserMapper userMapper;

    public AuthService(IUserRepository userRepository,
                       ICostSettingsRepository costSettingsRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.costSettingsRepository = costSettingsRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.userMapper = userMapper;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
        User user = new User(email, passwordEncoder.encode(request.getPassword()), request.getDisplayName());
        User saved = userRepository.save(user);
        // Cada usuario arranca con su propia configuración de costeo por defecto.
        costSettingsRepository.createDefaultFor(saved.getId());
        return buildResponse(saved, false);
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return buildResponse(user, userRepository.hasPhoto(user.getId()));
    }

    private AuthResponse buildResponse(User user, boolean hasPhoto) {
        String token = tokenService.generateToken(user.getId());
        return new AuthResponse(token, userMapper.toDto(user, hasPhoto));
    }
}
