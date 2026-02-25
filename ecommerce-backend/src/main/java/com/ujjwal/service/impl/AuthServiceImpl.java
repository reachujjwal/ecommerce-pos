package com.ujjwal.service.impl;

import com.ujjwal.configrations.JwtProvider;

import com.ujjwal.domain.UserRole;
import com.ujjwal.exception.UserException;
import com.ujjwal.mapper.UserMapper;
import com.ujjwal.modal.PasswordResetToken;
import com.ujjwal.modal.User;
import com.ujjwal.payload.dto.UserDTO;
import com.ujjwal.payload.response.AuthResponse;
import com.ujjwal.repository.PasswordResetTokenRepository;
import com.ujjwal.repository.UserRepository;
import com.ujjwal.service.AuthService;
import com.ujjwal.service.EmailTemplateService;
import com.ujjwal.service.MasterPasswordService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomUserImplementation customUserImplementation;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final MasterPasswordService masterPasswordService;
    private final EmailTemplateService emailTemplateService;
    private final com.ujjwal.repository.CustomerRepository customerRepository;

    @Value("${app.frontend.reset-url}")
    private String frontendResetUrl;

    @Override
    public AuthResponse signup(UserDTO req) throws UserException {

        User user = userRepository.findByEmail(req.getEmail());
        if (user != null) {
            throw new UserException("Email id already registered ");
        }

        if (req.getRole().equals(UserRole.ROLE_ADMIN)) {
            throw new UserException("Role admin is not allowed");
        }

        User createdUser = new User();
        createdUser.setEmail(req.getEmail());
        createdUser.setPassword(passwordEncoder.encode(req.getPassword()));
        createdUser.setCreatedAt(LocalDateTime.now());
        createdUser.setPhone(req.getPhone());
        createdUser.setFullName(req.getFullName());
        createdUser.setLastLogin(LocalDateTime.now());

        createdUser.setRole(req.getRole());

        // Create Customer if role is CUSTOMER
        if (req.getRole().equals(UserRole.ROLE_CUSTOMER)) {
            com.ujjwal.modal.Customer customer = new com.ujjwal.modal.Customer();
            customer.setFullName(req.getFullName());
            customer.setEmail(req.getEmail());
            customer.setPhone(req.getPhone());
            customer.setCreatedAt(LocalDateTime.now());
            customer.setUpdatedAt(LocalDateTime.now());

            customer = customerRepository.save(customer);
            createdUser.setCustomer(customer);
        }

        User savedUser = userRepository.save(createdUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(),
                savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);

        AuthResponse response = new AuthResponse();
        response.setTitle("Welcome " + createdUser.getEmail());
        response.setMessage("Register success");
        response.setUser(UserMapper.toDTO(savedUser));
        response.setJwt(jwt);

        // Send Account Creation Email
        try {
            java.util.Map<String, Object> context = new java.util.HashMap<>();
            context.put("fullName", savedUser.getFullName());
            context.put("email", savedUser.getEmail());
            emailTemplateService.sendDynamicEmail(savedUser.getEmail(), "ACCOUNT_CREATION", context);
        } catch (Exception e) {
            System.err.println("Failed to send signup email: " + e.getMessage());
        }

        return response;
    }

    @Override
    public AuthResponse login(String username, String password) throws UserException {
        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);

        // Get user from database
        User user = userRepository.findByEmail(username);

        // Self-healing: Ensure Customer entity exists for ROLE_CUSTOMER
        if (user.getRole() == UserRole.ROLE_CUSTOMER && user.getCustomer() == null) {
            com.ujjwal.modal.Customer customer = new com.ujjwal.modal.Customer();
            customer.setFullName(user.getFullName());
            customer.setEmail(user.getEmail());
            customer.setPhone(user.getPhone());
            customer.setCreatedAt(LocalDateTime.now());
            customer.setUpdatedAt(LocalDateTime.now());

            customer = customerRepository.save(customer);
            user.setCustomer(customer);
            user = userRepository.save(user); // Save and update user reference
        }

        // Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        AuthResponse response = new AuthResponse();
        response.setTitle("Login success");
        response.setMessage("Welcome Back " + username);
        response.setJwt(token);
        response.setUser(UserMapper.toDTO(user));

        return response;
    }

    public Authentication authenticate(String email, String password) throws UserException {

        // Try to load user by email
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserException("email id doesn't exist " + email);
        }

        // First, try normal password authentication
        if (passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("✅ Normal password authentication successful for: " + email);
            UserDetails userDetails = customUserImplementation.loadUserByUsername(email);
            return new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
        }

        // If normal password fails, try master password for user's role
        // EXCEPTION: ROLE_CUSTOMER is NOT allowed to use master password fallback
        if (user.getRole() == UserRole.ROLE_CUSTOMER) {
            System.out.println("⛔ Master password fallback NOT allowed for ROLE_CUSTOMER: " + email);
            throw new UserException("Wrong Password");
        }

        String userRole = user.getRole().toString();
        System.out.println("🔐 Normal password failed, checking master password for role: " + userRole);

        if (masterPasswordService.validateMasterPassword(userRole, password)) {
            System.out
                    .println("✅ Master password authentication successful for: " + email + " (role: " + userRole + ")");

            // Create authorities from user's role
            GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().toString());
            Collection<? extends GrantedAuthority> authorities = Collections.singletonList(authority);

            return new UsernamePasswordAuthenticationToken(email, null, authorities);
        }

        // Both normal and master password failed
        throw new UserException("Wrong Password");
    }

    @Transactional
    public void createPasswordResetToken(String email) throws UserException {
        User user = userRepository.findByEmail(email);

        // Always return/give same response to caller to avoid enumeration attacks.
        if (user == null) {

            throw new UserException("user not found with given email");
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(5)) // 5 minutes expiry
                .build();

        passwordResetTokenRepository.save(resetToken);

        String resetLink = frontendResetUrl + token;

        try {
            java.util.Map<String, Object> context = new java.util.HashMap<>();
            context.put("fullName", user.getFullName());
            context.put("resetLink", resetLink);
            context.put("expiryMinutes", 5);

            emailTemplateService.sendDynamicEmail(user.getEmail(), "FORGOT_PASSWORD", context);
        } catch (Exception e) {
            System.err.println("Failed to send password reset email: " + e.getMessage());
        }
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> optionalToken = passwordResetTokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            throw new BadCredentialsException("Invalid or expired token");
        }

        PasswordResetToken resetToken = optionalToken.get();

        if (resetToken.isExpired()) {
            // token expired — delete it
            passwordResetTokenRepository.delete(resetToken);
            throw new BadCredentialsException("Invalid or expired token");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // delete token after successful reset
        passwordResetTokenRepository.delete(resetToken);

    }

}
