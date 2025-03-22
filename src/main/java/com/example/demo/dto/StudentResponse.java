package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
    private Long id;
    private String name;
    private String middleName;
    private String surname;
    private String studentClass;
    private String division;
    private String email;
    private TeacherDTO teacher;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeacherDTO {
        private Long id;
        private String email;
        private String role;
    }
}