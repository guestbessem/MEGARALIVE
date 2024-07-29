package org.example.controllers;



import jakarta.validation.Valid;
import org.example.entities.ERole;
import org.example.entities.RefreshToken;
import org.example.entities.Role;
import org.example.entities.User;
import org.example.exception.TokenRefreshException;
import org.example.payload.requests.*;
import org.example.payload.responses.JwtResponse;
import org.example.payload.responses.MessageResponse;
import org.example.payload.responses.TokenRefreshResponse;
import org.example.repositories.UserRepository;
import org.example.security.jwt.JwtUtils;
import org.example.security.services.RefreshTokenService;
import org.example.security.services.UserDetailsImpl;
import org.example.services.inters.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  IAuthService authService;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  RefreshTokenService refreshTokenService;
  @Autowired
  UserRepository userRepo;


  @PostMapping("/signin")

  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    String jwt = jwtUtils.generateJwtToken(userDetails);
    User user = userRepo.findById(userDetails.getId()).get();
    user.setToken(jwt);
    user.setTokenCreationDate(LocalDateTime.now());
    userRepo.save(user);
    List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

    return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
            userDetails.getUsername(), userDetails.getEmail(), roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (authService.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (authService.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
        signUpRequest.getPassword());

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = authService.findByName(ERole.Developpeur)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "AdminITVermeg" -> {
            Role adminRole = authService.findByName(ERole.AdminITVermeg)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
          }
          case "Developpeur" -> {
            Role modRole = authService.findByName(ERole.Developpeur )
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(modRole);
          }
          case "ChefDeProjet" -> { // Add this case
            Role chefRole = authService.findByName(ERole.ChefDeProjet)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(chefRole);
          }
        }
      });
    }

    user.setRoles(roles);
    authService.saveUser(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    return refreshTokenService.findByToken(requestRefreshToken)
        .map(refreshTokenService::verifyExpiration)
        .map(RefreshToken::getUser)
        .map(user -> {
          String token = jwtUtils.generateTokenFromUsername(user.getUsername());
          return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
        })
        .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
            "Refresh token is not in database!"));
  }

  @PostMapping("/signout/{username}")
  public ResponseEntity<?> logoutUser(@PathVariable String username) {
    User user = authService.findByUsername(username).get();
    refreshTokenService.deleteByUserId(user.getId());
    return ResponseEntity.ok(new MessageResponse("Log out successful!"));
  }

  @PostMapping("/forgetpassword")
  public ResponseEntity<?> forgetPassword(@RequestBody ForgetPassword fp) {
    return ResponseEntity.ok(new MessageResponse(authService.forgetPassword(fp.getEmail())));
  }

  @PutMapping("/resetpassword")
  public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest rpr) {
    return ResponseEntity.ok(new MessageResponse(authService.resetPassword(rpr.getToken(), rpr.getPassword())));
  }
  @PreAuthorize("hasRole('CLIENT')")
  @GetMapping("/date")
  public String getCurrentDateTime() {
    LocalDate currentDate = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);
    String CurrentDateTime = currentDate.format(formatter);
    return CurrentDateTime;
  }
}
