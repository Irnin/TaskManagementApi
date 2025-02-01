package com.project.employee_records.auth;

import com.project.employee_records.model.User;
import com.project.employee_records.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findUserByEmail(username).orElseGet(() -> null);
        if(u != null)
            return new CustomUserDetails(username, u.getPassword(), u.getRole());

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
