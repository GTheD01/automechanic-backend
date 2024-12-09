package com.popeftimov.automechanic.user;

import com.popeftimov.automechanic.auth.passwordresettoken.PasswordResetToken;
import com.popeftimov.automechanic.auth.passwordresettoken.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
                .role(user.getRole())
                .build();
    }

    @Override
    public ResponseEntity<?> uploadAvatar(Long userId, MultipartFile avatarFile) throws IOException {
        System.out.println("INSIDE CONTROLLER UPLOAD");
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (avatarFile == null || avatarFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file selected");
        }

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(avatarFile.getOriginalFilename()));
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        if (!fileExtension.matches("jpg|jpeg|png|gif|bmp")) {
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
                .role(user.getRole())
                .avatar(avatarUrl)
                .build();

        return ResponseEntity.ok(userResponse);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
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
