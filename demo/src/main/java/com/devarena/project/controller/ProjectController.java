package com.devarena.project.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devarena.project.dto.request.ProjectRequestDto;
import com.devarena.project.dto.response.ProjectResponseDto;
import com.devarena.project.dto.response.ProjectSummaryResponseDto;
import com.devarena.project.service.ProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/project")
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(@RequestBody @Valid ProjectRequestDto request) {
        ProjectResponseDto response = projectService.createProject(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable Long id) {
        ProjectResponseDto response = projectService.getProjectById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{ownerId}")
    public ResponseEntity<List<ProjectSummaryResponseDto>> getAllProjectsFromAUser(@PathVariable Long ownerId){
        List<ProjectSummaryResponseDto>response = projectService.getProjectByOwner(ownerId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/topic/{topic}")
    public ResponseEntity<List<ProjectSummaryResponseDto>> getProjectsByTopic(@PathVariable String topic,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<ProjectSummaryResponseDto> response = projectService.getProjectsByTopic(topic, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProjectSummaryResponseDto>> searchProjects(@RequestParam String keyword) {
        List<ProjectSummaryResponseDto> response = projectService.searchProjects(keyword);
        return ResponseEntity.ok(response);
    }
    
}
