package com.autoresume.autoresume_api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParsedResumeData {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String email;
    private String phone;
    private String address;
    private String education;
    private String experience;
    private String skills;
    private String summary;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;
}