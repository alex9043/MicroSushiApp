package ru.alex9043.commondto;

import java.util.List;
import java.util.UUID;

public class SubjectResponseDto {
    private UUID id;
    private String subject;
    private List<String> roles;

    public SubjectResponseDto() {
    }

    public SubjectResponseDto(UUID id, String subject, List<String> roles) {
        this.id = id;
        this.subject = subject;
        this.roles = roles;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
