package com.devarena.project.service;

import com.devarena.user.entity.User;
import com.devarena.user.enums.UserRole;
import com.devarena.user.repository.UserRepository;
import com.devarena.project.repository.ProjectRepository;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devarena.exception.ResourceNotFoundException;
import com.devarena.project.dto.request.ProjectRequestDto;
import com.devarena.project.dto.response.ProjectResponseDto;
import com.devarena.project.dto.response.ProjectSummaryResponseDto;
import com.devarena.project.entity.Project;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final UserRepository userRepo;
    private final ProjectRepository projectRepo;

    public ProjectServiceImpl(UserRepository userRepo, ProjectRepository projectRepo) {
        this.userRepo = userRepo;
        this.projectRepo = projectRepo;
    }

    @Override
    public ProjectResponseDto createProject(ProjectRequestDto request) {
        Long ownerId = request.getOwnerId();
        User owner = userRepo.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner with user id: " + ownerId + " not found"));

        if (owner.getRole() == UserRole.COMPANY) {
            throw new UnsupportedOperationException("A company can't create a project");
        }

        Project project = Project.builder()
                .owner(owner)
                .title(request.getTitle())
                .description(request.getDescription())
                .topic(request.getTopic())
                .build();

        Project savedProject = projectRepo.save(project);
        return mapToResponse(savedProject);
    }

    @Override
    public ProjectResponseDto getProjectById(Long projectId) {
        Project project = projectRepo.findProjectWithOwner(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("No project found with id " + projectId));
        return mapToResponse(project);
    }

    @Override
    public List<ProjectSummaryResponseDto> getProjectByOwner(Long ownerId) {
        List<ProjectSummaryResponseDto> projects = projectRepo.findByOwnerId(ownerId);
        return projects;
    }

    @Override
    public List<ProjectSummaryResponseDto> getProjectsByTopic(String topic, int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("createdAt").descending());
        List<ProjectSummaryResponseDto> projectPage = projectRepo.findByTopic(topic, pageable);
        return projectPage;
    }

    @Override
    public List<ProjectSummaryResponseDto> searchProjects(String keyword) {
        List<ProjectSummaryResponseDto> projects = projectRepo.searchByTitleContaining(keyword);
        return projects;
    }

    private ProjectResponseDto mapToResponse(Project project) {
        return ProjectResponseDto.builder()
                .projectId(project.getProjectId())
                .ownerUsername(project.getOwner().getUsername())
                .title(project.getTitle())
                .topic(project.getTopic())
                .description(project.getDescription())
                .repoUrl(project.getRepoUrl())
                .voteCount(project.getVoteCount())
                .createdAt(project.getCreatedAt())
                .build();
    }

    
}
