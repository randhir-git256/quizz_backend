package com.example.demo.controller;

import com.example.demo.dto.AuthenticationRequest;
import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.AuthenticationService;
import com.example.demo.dto.AddStudentRequest;
import com.example.demo.dto.StudentResponse;
import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authService;
    private final JwtUtil jwtUtil;
    private final StudentRepository studentRepository;

    @PostMapping("/register-teacher")
    public ResponseEntity<AuthenticationResponse> registerTeacher(@RequestBody RegisterRequest request) {
        request.setRole(Role.TEACHER);
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/teacher/add-student")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<StudentResponse> addStudent(@RequestBody AddStudentRequest request) {
        // Get current teacher
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String teacherEmail = authentication.getName();
        User teacher = authService.getCurrentUser(teacherEmail);
        
        // Create user account for student
        RegisterRequest userRequest = RegisterRequest.builder()
                .firstName(request.getName())
                .lastName(request.getSurname())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(Role.STUDENT)
                .build();
        
        AuthenticationResponse authResponse = authService.register(userRequest);
        User studentUser = authService.getCurrentUser(request.getEmail());
        
        // Create student record with all details
        Student student = Student.builder()
                .name(request.getName())
                .middleName(request.getMiddleName())
                .surname(request.getSurname())
                .studentClass(request.getStudentClass())
                .division(request.getDivision())
                .email(request.getEmail())
                .password(request.getPassword()) // Note: Consider if you want to store this
                .user(studentUser)
                .teacher(teacher)
                .build();
        
        student = studentRepository.save(student);
        
        // Create response
        StudentResponse.TeacherDTO teacherDTO = StudentResponse.TeacherDTO.builder()
                .id(teacher.getId())
                .email(teacher.getEmail())
                .role(teacher.getRole().name())
                .build();
                
        StudentResponse response = StudentResponse.builder()
                .id(student.getId())
                .name(student.getName())
                .middleName(student.getMiddleName())
                .surname(student.getSurname())
                .studentClass(student.getStudentClass())
                .division(student.getDivision())
                .email(studentUser.getEmail())
                .teacher(teacherDTO)
                .build();
                
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = authService.getCurrentUser(email);
        
        return ResponseEntity.ok(UserDTO.fromUser(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = authService.getCurrentUser(email);
        String role = user.getRole().name();
        
        // Clear the security context
        SecurityContextHolder.clearContext();
        
        return ResponseEntity.ok(Map.of(
            "message", "Successfully logged out",
            "userEmail", email,
            "role", role
        ));
    }
}