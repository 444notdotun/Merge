package com.devarena.project.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProjectRequestDto {

    @NotBlank
    private Long ownerId;
    @NotBlank
    private String title;

    @NotBlank
    private String topic;

    @NotBlank
    private String description;
}
