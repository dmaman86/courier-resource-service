package com.courier.resource_service.manager;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.courier.resource_service.objects.dto.BranchDto;
import com.courier.resource_service.objects.entity.Branch;
import com.courier.resource_service.objects.entity.Office;
import com.courier.resource_service.objects.mapper.BranchMapper;
import com.courier.resource_service.repository.BranchRepository;
import com.courier.resource_service.repository.OfficeRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@Component
public class BranchManager {

  private final BranchRepository branchRepository;
  private final OfficeRepository officeRepository;
  private final BranchMapper branchMapper;

  public BranchManager(
      BranchRepository branchRepository,
      OfficeRepository officeRepository,
      BranchMapper branchMapper) {
    this.branchRepository = branchRepository;
    this.officeRepository = officeRepository;
    this.branchMapper = branchMapper;
  }

  @Transactional
  public Branch createBranch(BranchDto branchDto) {
    Office office =
        officeRepository
            .findById(branchDto.getOffice().getId())
            .orElseThrow(() -> new EntityNotFoundException("Office not found"));

    boolean existsBranch =
        branchRepository.existsByCityAndAddressAndOfficeIdAndEnabledTrue(
            branchDto.getCity(), branchDto.getAddress(), branchDto.getOffice().getId());

    if (existsBranch) throw new EntityExistsException("Branch already exists");

    Branch branch = branchMapper.toEntity(branchDto);
    branch.setOffice(office);
    branch.setEnabled(true);

    return branchRepository.save(branch);
  }

  @Transactional
  public Branch updateBranch(Long id, BranchDto branchDto) {
    Branch branch =
        branchRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Branch not found"));

    Office newOffice =
        officeRepository
            .findById(branchDto.getOffice().getId())
            .orElseThrow(() -> new EntityNotFoundException("Office not found"));

    boolean isCityChanged = !branch.getCity().equals(branchDto.getCity());
    boolean isAddressChanged = !branch.getAddress().equals(branchDto.getAddress());
    boolean isOfficeChanged = !branch.getOffice().getId().equals(newOffice.getId());

    if (isCityChanged || isAddressChanged || isOfficeChanged) {
      boolean exists =
          branchRepository.existsByCityAndAddressAndOfficeIdAndEnabledTrue(
              branchDto.getCity(), branchDto.getAddress(), newOffice.getId());

      if (exists)
        throw new EntityExistsException(
            "Another branch with the same city and address already exists for this office");
    }

    branch.setCity(branchDto.getCity());
    branch.setAddress(branchDto.getAddress());
    branch.setOffice(newOffice);

    return branchRepository.save(branch);
  }

  @Transactional
  public void disableBranch(Long id) {
    Branch branch =
        branchRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Branch not found"));

    long activeBranchesCount =
        branchRepository.countByEnabledTrueAndOfficeId(branch.getOffice().getId());

    if (activeBranchesCount <= 1)
      throw new IllegalStateException("Office must have at least one active branch");

    branch
        .getContacts()
        .forEach(
            contact -> {
              contact.getBranches().remove(branch);
              if (contact.getBranches().isEmpty()) {
                contact.setEnabled(false);
                contact.setDisabledAt(LocalDateTime.now());
              }
            });

    branch.setEnabled(false);
    branch.setDisabledAt(LocalDateTime.now());
    branchRepository.save(branch);
  }
}
