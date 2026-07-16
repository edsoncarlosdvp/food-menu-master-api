package br.com.inovationtech.FoodMenuMasterApi.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {

  private final String adminUsername;
  private final String adminPasswordHash;

  public AdminUserDetailsService(
          @Value("${app.security.admin.username}") String adminUsername,
          @Value("${app.security.admin.password-hash}") String adminPasswordHash) {
    this.adminUsername = adminUsername;
    this.adminPasswordHash = adminPasswordHash;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    if (!adminUsername.equals(username)) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }

        return User.builder()
                .username(adminUsername)
                .password(adminPasswordHash)
                .roles("ADMIN")
                .build();
  }
}
