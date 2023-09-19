package com.unasp.taskmanagement.domain.task.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@Table(name = "TASK")
@NoArgsConstructor
@AllArgsConstructor
public class Task implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "task_id")
  private Long id;
  @Column(name = "external_id_task")
  private String externalId;
  @Column(name = "task_name")
  private String name;
  private String description;
  private double reward;
  private boolean performed;
  @Column(name = "external_id_user_sponsor")
  private String externalIdUserSponsor;
  @Column(name = "external_id_user_child")
  private String externalIdUserChild;
  @Column(name = "created_date")
  private LocalDateTime createdDate;
  @Column(name = "updated_date")
  private LocalDateTime updatedDate;
}