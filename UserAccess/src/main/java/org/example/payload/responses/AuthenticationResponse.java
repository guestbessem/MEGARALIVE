package org.example.payload.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthenticationResponse {
    private String username;
    private int userId;
    private String base64EncodedAuthenticationKey;
    private boolean authenticated;
    private int officeId;
    private String officeName;
    private List<Role> roles;
    private List<String> permissions;
    private boolean shouldRenewPassword;
    private boolean isTwoFactorAuthenticationRequired;
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Role {
        private int id;
        private String name;
        private String description;
        private boolean disabled;
    }
}
