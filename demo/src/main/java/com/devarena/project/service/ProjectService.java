package com.devarena.project.service;

import java.util.List;

import com.devarena.project.dto.request.ProjectRequestDto;
import com.devarena.project.dto.response.ProjectResponseDto;
import com.devarena.project.dto.response.ProjectSummaryResponseDto;

public interface ProjectService {
    public ProjectResponseDto createProject(ProjectRequestDto request);
    public ProjectResponseDto getProjectById(Long projectId);
    public List<ProjectSummaryResponseDto> getProjectByOwner(Long ownerId);
    public List<ProjectSummaryResponseDto> getProjectsByTopic(String topic, int page, int size);
    public List<ProjectSummaryResponseDto> searchProjects(String keyword);
}
