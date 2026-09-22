package com.company.studentmanagement.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {

    public CustomUserDetails get() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails cud) {
            return cud;
        }
        throw new IllegalStateException("No authenticated user in context");
    }

    public Long userId() {
        return get().getId();
    }

    public String role() {
        return get().getUser().getRole().name();
    }
}
