package com.example.qafordevs.dto;

import com.example.qafordevs.entity.DeveloperEntity;
import com.example.qafordevs.entity.Status;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperDto {

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String specialty;
    private Status status;

    public DeveloperEntity toEntity() {
        return DeveloperEntity.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .specialty(specialty)
                .status(status)
                .build();
    }

    public static DeveloperDto fromEntity(DeveloperEntity developer) {
        return DeveloperDto.builder()
                .id(developer.getId())
                .firstName(developer.getFirstName())
                .lastName(developer.getLastName())
                .specialty(developer.getSpecialty())
                .status(developer.getStatus())
                .build();
    }
}