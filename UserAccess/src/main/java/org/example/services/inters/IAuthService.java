package org.example.services.inters;




import org.example.entities.ERole;
import org.example.entities.Role;
import org.example.entities.User;

import java.util.Optional;

public interface IAuthService {
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<Role> findByName(ERole name);

    User saveUser(User user);

    String forgetPassword(String email);

    String resetPassword(String token, String password);
}
