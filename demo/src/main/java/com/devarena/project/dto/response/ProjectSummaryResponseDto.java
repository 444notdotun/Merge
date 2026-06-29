package com.devarena.project.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder


public class ProjectSummaryResponseDto {
    private Long id;
    private String title;
    private String topic;
    private String ownerUsername;
}
