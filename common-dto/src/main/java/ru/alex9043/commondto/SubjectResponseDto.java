package ru.alex9043.commondto;

import java.util.List;

public class SubjectResponseDto {
    private String subject;
    private List<String> roles;

    public SubjectResponseDto() {
    }

    public SubjectResponseDto(String subject, List<String> roles) {
        this.subject = subject;
        this.roles = roles;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
