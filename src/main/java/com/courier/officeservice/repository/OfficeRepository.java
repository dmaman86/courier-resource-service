package com.courier.officeservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.courier.officeservice.objects.entity.Office;

public interface OfficeRepository
    extends JpaRepository<Office, Long>, JpaSpecificationExecutor<Office> {

  Page<Office> findByEnabledTrue(Pageable pageable);

  Page<Office> findByEnabledTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

  List<Office> findByEnabledTrue();
}
