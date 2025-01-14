package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.UserFilter;
import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.dto.UserUpdateProfileResponse;
import com.popeftimov.automechanic.model.PasswordResetToken;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.PasswordResetTokenRepository;
import com.popeftimov.automechanic.repository.UserRepository;
import com.popeftimov.automechanic.specifications.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final static String USER_NOT_FOUND_MSG = "User with email %s not found";
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    @Override
    public int enableUser(String email) {
        return userRepository.enableUser(email);
    }

    @Override
    public UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .appointmentCount(user.getAppointmentsCount())
                .carsCount(user.getCarsCount())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    @Override
    public ResponseEntity<?> updateUserProfile(Long userId, UserResponse userData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User fetchedUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!fetchedUser.getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("You are not authorized to update this profile.");
        }
        fetchedUser.setFirstName(userData.getFirstName() != null && !userData.getFirstName().isEmpty() ? userData.getFirstName() : fetchedUser.getFirstName());
        fetchedUser.setLastName(userData.getLastName() != null && !userData.getLastName().isEmpty() ? userData.getLastName() : fetchedUser.getLastName());
        fetchedUser.setPhoneNumber(userData.getPhoneNumber() != null && !userData.getPhoneNumber().isEmpty() ? userData.getPhoneNumber() : fetchedUser.getPhoneNumber());

        userRepository.save(fetchedUser);

        UserUpdateProfileResponse userResponse = new UserUpdateProfileResponse(
                fetchedUser.getFirstName(),
                fetchedUser.getLastName(),
                fetchedUser.getEmail(),
                fetchedUser.getAvatar(),
                fetchedUser.getPhoneNumber()
        );
        return ResponseEntity.ok(userResponse);
    }

    @Override
    public void resetPassword(String email, String token, String newPassword, String repeatNewPassword) {
        Optional<PasswordResetToken> passwordResetTokenOptional = passwordResetTokenRepository.findByToken(token);
        if (passwordResetTokenOptional.isEmpty()) {
            return;
        }
        User user = passwordResetTokenOptional.get().getUser();

        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);

        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetTokenOptional.get());
    }

    @Override
    public ResponseEntity<Page<UserResponse>> getAllUsers(String name,
                                                          Boolean hasCars,
                                                          Boolean hasAppointments,
                                                          Pageable pageable) {
        UserFilter filter = new UserFilter(name, hasCars, hasAppointments);
        Specification<User> spec = UserSpecification.applyFilters(filter);

        Page<User> users = userRepository.findAll(spec, (org.springframework.data.domain.Pageable) pageable);

        Page<UserResponse> userResponses = users
                .map(this::convertToUserResponse);
        return ResponseEntity.ok().body(userResponses);
    }

}
