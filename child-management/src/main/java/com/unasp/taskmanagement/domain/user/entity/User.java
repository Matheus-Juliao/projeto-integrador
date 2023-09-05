package com.unasp.taskmanagement.domain.user.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
@Table(name = "USERS")
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "external_id", length = 45, nullable = false, unique = true)
  private String externalId;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, length = 100, unique = true)
  private String email;

  @Column(nullable = false, length = 20)
  private String password;

  @Column(name = "token_email", length = 100)
  private String tokenEmail;

  private boolean active;

  @Column(name = "read_terms")
  private boolean readTerms;

  @Column(name = "created_date", nullable = false)
  private LocalDateTime createdDate;

  @Column(name = "updated_date")
  private LocalDateTime updatedDate;
}