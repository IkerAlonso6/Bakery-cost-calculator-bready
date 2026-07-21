package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.FixedCostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FixedCostJpaRepository extends JpaRepository<FixedCostEntity, Integer> {

    Optional<FixedCostEntity> findByIdAndUserId(Integer id, Integer userId);

    List<FixedCostEntity> findByUserIdAndPeriod(Integer userId, LocalDate period);

    @Query("select max(f.period) from FixedCostEntity f where f.userId = :userId and f.period <= :period")
    Optional<LocalDate> findMostRecentPeriodWithDataUpTo(@Param("userId") Integer userId, @Param("period") LocalDate period);
}
