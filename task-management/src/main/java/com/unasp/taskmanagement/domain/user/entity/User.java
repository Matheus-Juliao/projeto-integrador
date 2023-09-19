package com.unasp.taskmanagement.domain.user.entity;


import com.unasp.taskmanagement.domain.user.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@Table(name = "USERS")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable, UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;
  @Column(name = "external_id_user")
  private String externalId;
  @Column(name = "user_name")
  private String name;
  private String login;
  private String password;
  private int age;
  @Enumerated(EnumType.STRING)
  private UserRole role;
  private boolean active;
  @Column(name = "read_terms")
  private boolean readTerms;
  @Column(name = "user_creator")
  private String userCreator;
  @Column(name = "created_date")
  private LocalDateTime createdDate;
  @Column(name = "updated_date")
  private LocalDateTime updatedDate;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if(this.role == UserRole.SPONSOR) {
      return List.of(
          new SimpleGrantedAuthority("ROLE_SPONSOR"),
          new SimpleGrantedAuthority("ROLE_CHILD"));
    }
    else {
      return List.of(new SimpleGrantedAuthority("ROLE_CHILD"));
    }
  }

  @Override
  public String getUsername() {
    return login;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public String getExternalId() { return externalId; }
}