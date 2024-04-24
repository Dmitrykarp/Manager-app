package com.dmitrykarp.managerapp.security;

import com.dmitrykarp.managerapp.entity.Authority;
import com.dmitrykarp.managerapp.entity.ManagerUser;
import com.dmitrykarp.managerapp.repository.ManagerUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ManagerUserDetailService implements UserDetailsService {

    private final ManagerUserRepository managerUserRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.managerUserRepository.findByUsername(username)
                .map(managerUser -> User.builder()
                        .username(managerUser.getUsername())
                        .password(managerUser.getPassword())
                        .authorities(managerUser.getAuthorityList().stream()
                                .map(Authority::getAuthority)
                                .map(SimpleGrantedAuthority::new)
                                .toList())
                        .build())
                .orElseThrow(()-> new UsernameNotFoundException("User %s not found".formatted(username)));
    }
}
