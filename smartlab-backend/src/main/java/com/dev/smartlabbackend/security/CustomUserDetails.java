package com.dev.smartlabbackend.security;

import com.dev.smartlabbackend.entity.Role;
import com.dev.smartlabbackend.entity.User;
import com.dev.smartlabbackend.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@Builder
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String orgEmail;
    private final String password;
    private final String fullName;
    private final Role role;
    private final UserStatus status;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;
    private final boolean accountNonLocked;

    public static CustomUserDetails build(User user) {
        String roleName = user.getRole() != null ? user.getRole().getValue().toUpperCase() : "GUEST";
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleName);

        boolean isDeleted = user.getDeletedAt() != null;
        boolean isActive = UserStatus.ACTIVE.equals(user.getStatus()) && !isDeleted;
        boolean isNotLocked = !UserStatus.LOCKED.equals(user.getStatus()) && !isDeleted;

        return CustomUserDetails.builder()
                .id(user.getId())
                .orgEmail(user.getOrgEmail())
                .password(user.getPasswordHash())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus())
                .authorities(Collections.singletonList(authority))
                .enabled(isActive)
                .accountNonLocked(isNotLocked)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return orgEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
