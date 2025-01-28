package com.popeftimov.automechanic.utils;

import com.popeftimov.automechanic.model.Ownable;
import com.popeftimov.automechanic.model.User;
import com.popeftimov.automechanic.model.UserRole;

public class PermissionUtils {

    public static <T extends Ownable> boolean notOwnerOrAdmin(User user, T object) {
        if (user.getUserRole() != UserRole.ADMIN) {
            return true;
        }

        return !object.getUser().equals(user);
    }
}
