package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.UserFilter;
import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.dto.UserUpdateProfileResponse;
import com.popeftimov.automechanic.exception.AuthenticationExceptions;
import com.popeftimov.automechanic.exception.UserExceptions;
import com.popeftimov.automechanic.model.PasswordResetToken;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.model.UserRole;
import com.popeftimov.automechanic.repository.PasswordResetTokenRepository;
import com.popeftimov.automechanic.repository.UserRepository;
import com.popeftimov.automechanic.specifications.UserSpecification;
import com.popeftimov.automechanic.validator.UserPhoneNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
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

    private final static String USER_EMAIL_NOT_FOUND_MSG = "User with email %s not found";
    private final static String USER_ID_NOT_FOUND_MSG = "User with id %d not found";
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPhoneNumberValidator userPhoneNumberValidator;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_EMAIL_NOT_FOUND_MSG, email)));
    }

    public User loadUser(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_EMAIL_NOT_FOUND_MSG, email)));
    }

    public User loadUser(Long id) throws UsernameNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_ID_NOT_FOUND_MSG, id)));
    }

    @Override
    public void enableUser(String email) {
        userRepository.enableUser(email);
    }

    @Override
    public UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUserRole(),
                user.getAvatar(),
                user.getPhoneNumber(),
                user.getCarsCount(),
                user.getAppointmentsCount(),
                user.getEnabled()
        );
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
    public Page<UserResponse> getAllUsers(String name,
                                          Boolean hasCars,
                                          Boolean hasAppointments,
                                          Pageable pageable) {
        String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        UserFilter filter = new UserFilter(name, hasCars, hasAppointments);
        Specification<User> spec = UserSpecification.applyFilters(filter, loggedInUserEmail);

        Page<User> users = userRepository.findAll(spec, pageable);

        return users.map(this::convertToUserResponse);
    }

    @Override
    public UserResponse getUser(Long userId) {
        User user = this.loadUser(userId);
        return this.convertToUserResponse(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public UserUpdateProfileResponse updateUserProfile(Long userId, UserUpdateProfileResponse userData) {
        if (userData.getPhoneNumber() != null) {
            boolean isValidPhone = userPhoneNumberValidator.test(
                    userData.getPhoneNumber()
            );

            if (!isValidPhone) {
                throw new UserExceptions.InvalidPhoneNumber();
            }
        }

        User fetchedUser = this.loadUser(userId);

        fetchedUser.setFirstName(userData.getFirstName() != null && !userData.getFirstName().isEmpty() ?
                userData.getFirstName() : fetchedUser.getFirstName());
        fetchedUser.setLastName(userData.getLastName() != null && !userData.getLastName().isEmpty() ?
                userData.getLastName() : fetchedUser.getLastName());
        fetchedUser.setPhoneNumber(userData.getPhoneNumber() != null ? userData.getPhoneNumber() : fetchedUser.getPhoneNumber());

        userRepository.save(fetchedUser);

        UserUpdateProfileResponse userResponse = new UserUpdateProfileResponse(
                fetchedUser.getFirstName(),
                fetchedUser.getLastName(),
                fetchedUser.getEmail(),
                fetchedUser.getAvatar(),
                fetchedUser.getPhoneNumber()
        );

        return userResponse;
    }

    @Override
    public UserUpdateProfileResponse updateLoggedInUserProfile(User user, UserUpdateProfileResponse userData) {
        if (userData.getPhoneNumber() != null) {
            boolean isValidPhone = userPhoneNumberValidator.test(
                    userData.getPhoneNumber()
            );

            if (!isValidPhone) {
                throw new UserExceptions.InvalidPhoneNumber();
            }
        }

        user.setFirstName(userData.getFirstName() != null && !userData.getFirstName().isEmpty() ?
                userData.getFirstName() : user.getFirstName());
        user.setLastName(userData.getLastName() != null && !userData.getLastName().isEmpty() ?
                userData.getLastName() : user.getLastName());
        user.setPhoneNumber(userData.getPhoneNumber() != null ? userData.getPhoneNumber() : user.getPhoneNumber());

        userRepository.save(user);

        UserUpdateProfileResponse userResponse = new UserUpdateProfileResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAvatar(),
                user.getPhoneNumber()
        );
        return userResponse;
    }

}
