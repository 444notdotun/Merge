package com.devarena.project.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponseDto {
    private Long projectId;
    private String ownerUsername;
    private String title;
    private String topic;
    private String description;
    private String repoUrl;
    private Integer voteCount;
    private LocalDateTime createdAt;
}
