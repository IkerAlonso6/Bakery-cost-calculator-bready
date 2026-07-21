package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, Integer> {

    Optional<EmployeeEntity> findByIdAndUserId(Integer id, Integer userId);

    List<EmployeeEntity> findByUserIdAndPeriod(Integer userId, LocalDate period);

    @Query("select max(e.period) from EmployeeEntity e where e.userId = :userId and e.period <= :period")
    Optional<LocalDate> findMostRecentPeriodWithDataUpTo(@Param("userId") Integer userId, @Param("period") LocalDate period);
}
