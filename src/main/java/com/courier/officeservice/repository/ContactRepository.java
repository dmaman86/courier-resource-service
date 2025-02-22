package com.courier.officeservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.courier.officeservice.objects.entity.Contact;

public interface ContactRepository
    extends JpaRepository<Contact, Long>, JpaSpecificationExecutor<Contact> {

  Page<Contact> findByEnabledTrue(Pageable pageable);

  List<Contact> findByEnabledTrue();

  List<Contact> findByEnabledTrueAndOfficeId(Long officeId);

  List<Contact> findByEnabledTrueAndBranchesId(Long branchId);

  Optional<Contact> findByPhoneNumberAndEnabledTrue(String phoneNumber);

  long countByEnabledTrueAndOfficeId(Long officeId);

  boolean existsByPhoneNumberAndEnabledTrue(String phoneNumber);
}
