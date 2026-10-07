package com.dev.smartlabbackend.security;

import com.dev.smartlabbackend.entity.User;
import com.dev.smartlabbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String orgEmail) throws UsernameNotFoundException {
        User user = userRepository.findActiveByOrgEmail(orgEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with org_email: " + orgEmail));
        return CustomUserDetails.build(user);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return CustomUserDetails.build(user);
    }
}
