package com.popeftimov.automechanic.service;

import com.popeftimov.automechanic.dto.UserResponse;
import com.popeftimov.automechanic.model.PasswordResetToken;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.repository.PasswordResetTokenRepository;
import com.popeftimov.automechanic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final static String USER_NOT_FOUND_MSG = "User with email %s not found";
    private final PasswordEncoder passwordEncoder;

    @Value("${upload.avatar-dir}")
    private String avatarDir;

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
                .build();
    }

    @Override
    public ResponseEntity<?> uploadAvatar(Long userId, MultipartFile avatarFile) throws IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (avatarFile == null || avatarFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file selected");
        }

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(avatarFile.getOriginalFilename()));
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        if (!fileExtension.matches("jpg|jpeg|png|gif|bmp|webp")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type. Only images are allowed");
        }
        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
        Path targetLocation = Paths.get(avatarDir + user.getEmail() + "/" + newFileName);

        File directory = new File(avatarDir + user.getEmail());
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File[] pictures = directory.listFiles();
        if (pictures != null) {
            for (File file : pictures) {
                file.delete();
            }
        }

        Files.copy(avatarFile.getInputStream(), targetLocation);

        String avatarUrl = "http://localhost:8080" + "/uploads/avatars/" + user.getEmail() + "/" + newFileName;
        user.setAvatar(avatarUrl);
        userRepository.save(user);

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .avatar(avatarUrl)
                .build();

        return ResponseEntity.ok(userResponse);
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
        fetchedUser.setFirstName(userData.getFirstName());
        fetchedUser.setLastName(userData.getLastName());

        userRepository.save(fetchedUser);
        UserResponse userResponse = new UserResponse(
                fetchedUser.getId(),
                userData.getFirstName(),
                userData.getLastName(),
                fetchedUser.getEmail(),
                fetchedUser.getUserRole(),
                fetchedUser.getAvatar()
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

}
