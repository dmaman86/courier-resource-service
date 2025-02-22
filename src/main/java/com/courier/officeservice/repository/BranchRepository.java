package com.courier.officeservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.courier.officeservice.objects.entity.Branch;

public interface BranchRepository
    extends JpaRepository<Branch, Long>, JpaSpecificationExecutor<Branch> {

  Page<Branch> findByEnabledTrue(Pageable pageable);

  List<Branch> findByEnabledTrue();

  List<Branch> findByEnabledTrueAndOfficeId(Long officeId);

  List<Branch> findByIdInAndOfficeIdAndEnabledTrue(List<Long> ids, Long officeId);

  boolean existsByCityAndAddressAndOfficeIdAndEnabledTrue(
      String city, String address, Long officeId);

  long countByEnabledTrueAndOfficeId(Long officeId);
}
