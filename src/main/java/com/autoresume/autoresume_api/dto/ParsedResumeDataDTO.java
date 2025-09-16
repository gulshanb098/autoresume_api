package com.autoresume.autoresume_api.dto;

import lombok.Data;

@Data
public class ParsedResumeDataDTO {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String education;
    private String experience;
    private String skills;
    private String summary;
}
