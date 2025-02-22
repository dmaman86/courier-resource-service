package com.courier.officeservice.manager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.courier.officeservice.objects.dto.BranchBaseDto;
import com.courier.officeservice.objects.dto.BranchDto;
import com.courier.officeservice.objects.dto.OfficeDto;
import com.courier.officeservice.objects.entity.Branch;
import com.courier.officeservice.objects.entity.Office;
import com.courier.officeservice.objects.mapper.OfficeMapper;
import com.courier.officeservice.repository.BranchRepository;
import com.courier.officeservice.repository.ContactRepository;
import com.courier.officeservice.repository.OfficeRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@Component
public class OfficeManager {

  private final OfficeRepository officeRepository;
  private final ContactRepository contactRepository;
  private final BranchRepository branchRepository;
  private final OfficeMapper officeMapper;
  private final BranchManager branchManager;

  public OfficeManager(
      OfficeRepository officeRepository,
      ContactRepository contactRepository,
      BranchRepository branchRepository,
      OfficeMapper officeMapper,
      BranchManager branchManager) {
    this.officeRepository = officeRepository;
    this.contactRepository = contactRepository;
    this.branchRepository = branchRepository;
    this.officeMapper = officeMapper;
    this.branchManager = branchManager;
  }

  @Transactional
  public Office createOffice(OfficeDto officeDto) {
    Office office = officeMapper.toEntity(officeDto);
    office.setEnabled(true);

    if (officeDto.getBranches() != null && !officeDto.getBranches().isEmpty()) {
      officeDto.getBranches().stream()
          .map(
              branchBaseDto -> {
                return BranchDto.builder()
                    .id(branchBaseDto.getId())
                    .city(branchBaseDto.getCity())
                    .address(branchBaseDto.getAddress())
                    .office(officeMapper.toBaseDto(office))
                    .build();
              })
          .forEach(branchDto -> branchManager.createBranch(branchDto));
    }
    return officeRepository.save(office);
  }

  @Transactional
  public Office updateOffice(Long id, OfficeDto officeDto) {
    Office office =
        officeRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Office not found"));

    if (!office.getName().equals(officeDto.getName())) {
      office.setName(officeDto.getName());
    }

    List<Branch> existingBranches = office.getBranches();
    List<Long> branchIds =
        officeDto.getBranches().stream().map(BranchBaseDto::getId).collect(Collectors.toList());

    removeBranches(existingBranches, branchIds, office);
    addBranches(office, officeDto.getBranches());
    updateExistingBranches(existingBranches, officeDto.getBranches(), office);

    office.setBranches(existingBranches);

    return officeRepository.save(office);
  }

  @Transactional
  public void disabledOffice(Long id) {
    Office office =
        officeRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Office not found"));

    long activeContacts = contactRepository.countByEnabledTrueAndOfficeId(office.getId());
    if (activeContacts > 0) throw new EntityExistsException("Office has active contacts");

    office.getBranches().stream()
        .filter(Branch::isEnabled)
        .forEach(branch -> branchManager.disableBranch(branch.getId()));

    office.setEnabled(false);
    office.setDisabledAt(LocalDateTime.now());
    officeRepository.save(office);
  }

  private void removeBranches(List<Branch> existingBranches, List<Long> branchIds, Office office) {
    List<Branch> branchesToRemove =
        existingBranches.stream()
            .filter(branch -> !branchIds.contains(branch.getId()))
            .collect(Collectors.toList());

    if (existingBranches.size() - branchesToRemove.size() < 1) {
      throw new IllegalStateException("Office must have at least one active branch");
    }

    branchesToRemove.forEach(
        branch -> {
          branch.setEnabled(false);
          branch.setDisabledAt(LocalDateTime.now());
        });
  }

  private void addBranches(Office office, List<BranchBaseDto> branchDtos) {
    branchDtos.stream()
        .filter(branchDto -> branchDto.getId() == null)
        .forEach(
            branchDto -> {
              Branch newBranch =
                  Branch.builder()
                      .city(branchDto.getCity())
                      .address(branchDto.getAddress())
                      .office(office)
                      .enabled(true)
                      .build();

              office.getBranches().add(newBranch);
            });
  }

  private void updateExistingBranches(
      List<Branch> existingBranches, List<BranchBaseDto> branchDtos, Office office) {
    branchDtos.stream()
        .filter(branchDto -> branchDto.getId() != null)
        .forEach(
            branchDto -> {
              Branch branch =
                  existingBranches.stream()
                      .filter(b -> b.getId().equals(branchDto.getId()))
                      .findFirst()
                      .orElseThrow(() -> new EntityNotFoundException("Branch not found"));

              if (!branch.getCity().equals(branchDto.getCity())
                  || !branch.getAddress().equals(branchDto.getAddress())) {
                boolean exists =
                    branchRepository.existsByCityAndAddressAndOfficeIdAndEnabledTrue(
                        branchDto.getCity(), branchDto.getAddress(), office.getId());
                if (exists) {
                  throw new EntityExistsException(
                      "Another branch with the same city and address already exists for this"
                          + " office");
                }
                branch.setCity(branchDto.getCity());
                branch.setAddress(branchDto.getAddress());
              }
            });
  }
}
