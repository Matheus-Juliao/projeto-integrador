package com.unasp.taskmanagement.domain.task.repository;

import com.unasp.taskmanagement.domain.task.entity.Task;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {
  Optional<Task> findByExternalId(String externalId);
  List<Task> findByExternalIdUserChild(String externalId);
  @Query("SELECT SUM(t.reward) FROM Task t WHERE t.performed = true")
  Double totalValueTasksPerformed();
  @Modifying
  @Query("DELETE FROM Task t WHERE t.externalIdUserChild = ?1")
  void deleteExternalIdUserChild(String externalId);
}