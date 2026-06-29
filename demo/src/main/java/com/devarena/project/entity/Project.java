package com.devarena.project.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.devarena.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;




@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder


@Entity
@Table(name = "projects", indexes = {
    @Index(name = "idx_proejct_owner_id", columnList = "owner_id"),
    @Index(name = "idx_project_topic", columnList = "topic")
})
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;
    

    @Column(length = 256, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column
    private String repoUrl;

    @Column
    private String topic;

    @Column
    private int voteCount;

    @ManyToOne
    @Column(name = "owner_id")
    private User owner;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;






}
