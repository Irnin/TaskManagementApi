package com.project.employee_records.controller;

import com.project.employee_records.model.ChangePasswordRequest;
import com.project.employee_records.model.Role;
import com.project.employee_records.model.User;
import com.project.employee_records.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/{idUser}")
    public ResponseEntity<User> getUser(@PathVariable Integer idUser){
        return ResponseEntity.of(userService.getUser(idUser));
    }

    @GetMapping("/")
    public Page<User> getUsers(Pageable pageable){
        return userService.getUsers(pageable);
    }

    @PostMapping("/")
    public ResponseEntity<Void> saveUser(@RequestBody User user){
        User createUser = userService.setUser(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{idUser}").buildAndExpand(createUser.getIdUser()).toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        headers.add("Access-Control-Expose-Headers", "*");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{idUser}")
    public ResponseEntity<Void> updateUser(@RequestBody User user, @PathVariable Integer idUser){
        return userService.getUser(idUser)
                .map(u -> {
                    userService.setUser(user);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{idUser}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer idUser){
        return userService.getUser(idUser)
                .map(u -> {
                    userService.deleteUser(idUser);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<User> getUser(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());

    }

    /**
     * Return information about user from session
     */
    @GetMapping("/myAccount")
    public ResponseEntity<User> getMyAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> user = userService.getUserByEmail(email);

        if(user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Change user's password
     */
    @PatchMapping("/update/password")
    public ResponseEntity<String> updatePassword(@RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(request.getCurrentPassword(),  user.getPassword())) {
            return ResponseEntity.badRequest().body("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.setUser(user);

        return ResponseEntity.ok("Password changed successfully");
    }

    /**
     * Add new user
     */
    @PutMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createUser(@RequestBody User newUser) {

        userService.setUser(newUser);

        return ResponseEntity.ok("User was created");
    }

    /**
     * Update role
     */
    @PatchMapping("/user/{idUser}/role/{idRole}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateRole(@PathVariable Integer idUser, @PathVariable Integer idRole) {

        Role role = Role.EMPLOYEE;

        switch(idRole) {
            case 0:
                role = Role.ADMIN;
            case 1:
                role = Role.EMPLOYEE;
        }

        User user = userService.getUser(idUser).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);

        userService.setUser(user);

        return ResponseEntity.ok("Updated role");
    }

}
