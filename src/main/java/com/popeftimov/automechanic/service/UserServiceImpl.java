package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.UserFilter;
import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.dto.UserUpdateProfileResponse;
import com.popeftimov.automechanic.exception.user.UserExceptions;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.UserRepository;
import com.popeftimov.automechanic.specifications.UserSpecification;
import com.popeftimov.automechanic.validator.UserPhoneNumberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final static String USER_EMAIL_NOT_FOUND_MSG = "User with email %s not found";
    private final static String USER_ID_NOT_FOUND_MSG = "User with id %d not found";
    private final UserRepository userRepository;
    private final UserPhoneNumberValidator userPhoneNumberValidator;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_EMAIL_NOT_FOUND_MSG, email)));
    }

    public Optional<User> findOptionalUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User loadUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserExceptions.UserNotFoundException(String.format(USER_EMAIL_NOT_FOUND_MSG, email)));
    }

    public User loadUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserExceptions.UserNotFoundException(String.format(USER_ID_NOT_FOUND_MSG, id)));
    }

    @Override
    public UserResponse getUserResponseByUserId(Long userId) {
        User user = this.loadUser(userId);
        return this.convertToUserResponse(user);
    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void deleteUserById(Long userId) {
        User user = this.loadUser(userId);
        userRepository.delete(user);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
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
                user.getPhoneNumber(),
                user.getCarsCount(),
                user.getAppointmentsCount(),
                user.getEnabled()
        );
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

    private UserUpdateProfileResponse convertToUserUpdateProfileResponse(User user) {
        return new UserUpdateProfileResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }

    @Override
    public UserUpdateProfileResponse updateUserProfileById(Long userId, UserUpdateProfileResponse userData) {
        User fetchedUser = this.loadUser(userId);

        return this.updateUserProfile(fetchedUser, userData);
    }

    @Override
    public UserUpdateProfileResponse updateLoggedInUserProfile(User user, UserUpdateProfileResponse userData) {
        return this.updateUserProfile(user, userData);
    }

    private UserUpdateProfileResponse updateUserProfile(User user, UserUpdateProfileResponse userData) {
        if (isUserDataUnchanged(user, userData)) {
            // If no changes, return the existing profile response
            return this.convertToUserUpdateProfileResponse(user);
        }
        validatePhoneNumber(userData.getPhoneNumber());

        Optional.ofNullable(userData.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(userData.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(userData.getPhoneNumber()).ifPresent(user::setPhoneNumber);

        userRepository.save(user);

        return this.convertToUserUpdateProfileResponse(user);
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            boolean isValidPhone = userPhoneNumberValidator.test(phoneNumber);
            if (!isValidPhone) {
                throw new UserExceptions.InvalidPhoneNumber();
            }
        }
    }

//    Helper method to check if the provided newly provided userData is unchanged
    private boolean isUserDataUnchanged(User user, UserUpdateProfileResponse userData) {
        return Objects.equals(userData.getFirstName(), user.getFirstName()) &&
                Objects.equals(userData.getLastName(), user.getLastName()) &&
                Objects.equals(userData.getPhoneNumber(), user.getPhoneNumber());
    }

}
