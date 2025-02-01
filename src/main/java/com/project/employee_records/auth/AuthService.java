package com.project.employee_records.auth;

import com.project.employee_records.config.JwtService;
import com.project.employee_records.model.Role;
import com.project.employee_records.model.User;
import com.project.employee_records.repository.UserRepository;
import com.project.employee_records.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final HttpServletRequest httpServletRequest;
    private final UserRepository userRepository;

    public AuthResponse register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.EMPLOYEE);
        User createdClient = userService.setUser(user);
        CustomUserDetails userDetails = new CustomUserDetails(createdClient.getEmail(), createdClient.getPassword(), Role.EMPLOYEE);
        String token = jwtService.generateToken(userDetails, user.getRole().toString(), user.getIdUser());
        return AuthResponse.builder().token(token).build();
    }

    public AuthResponse authenticate(@NotNull String email, @NotNull String password) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email, password));

        User u = userRepository.findUserByEmail(email).orElseGet(() -> null);
        CustomUserDetails user = null;
        if(u != null)
            user = new CustomUserDetails(u.getEmail(), u.getPassword(), u.getRole());
        else
            throw new UsernameNotFoundException("User not found with username: " + email);

        var token = jwtService.generateToken(user, user.getRole(), u.getIdUser());
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        return authenticate(authRequest.getEmail(), authRequest.getPassword());
    }

    public User getSessionUser() {
        String token = getSessionToken();
        String prefix = "Bearer";
        token = token.substring(prefix.length());
        String email = jwtService.extractUserName(token);
        return userRepository.findUserByEmail(email).get();
    }

    private String getSessionToken() {
        return httpServletRequest.getHeader("Authorization");
    }

    public void changePassword(String password) {
        User user = (User)getSessionUser();
        user.setPassword(passwordEncoder.encode(password));
        userService.setUser(user);
    }
}
