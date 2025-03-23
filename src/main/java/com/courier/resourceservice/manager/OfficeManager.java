package com.courier.resourceservice.manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.courier.resourceservice.exception.BussinessRuleException;
import com.courier.resourceservice.exception.EntityNotFoundException;
import com.courier.resourceservice.objects.dto.BranchBaseDto;
import com.courier.resourceservice.objects.dto.BranchDto;
import com.courier.resourceservice.objects.dto.OfficeBaseDto;
import com.courier.resourceservice.objects.dto.OfficeDetailsDto;
import com.courier.resourceservice.objects.dto.OfficeDto;
import com.courier.resourceservice.objects.entity.Branch;
import com.courier.resourceservice.objects.entity.Contact;
import com.courier.resourceservice.objects.entity.Office;
import com.courier.resourceservice.objects.mapper.BranchMapper;
import com.courier.resourceservice.objects.mapper.ContactMapper;
import com.courier.resourceservice.objects.mapper.OfficeMapper;
import com.courier.resourceservice.repository.ContactRepository;
import com.courier.resourceservice.repository.OfficeRepository;

@Component
public class OfficeManager {

  @Autowired private OfficeRepository officeRepository;
  @Autowired private ContactRepository contactRepository;
  @Autowired private OfficeMapper officeMapper;
  @Autowired private BranchManager branchManager;
  @Autowired private BranchMapper branchMapper;
  @Autowired private ContactMapper contactMapper;

  @Transactional(readOnly = true)
  public OfficeDetailsDto getOfficeDetails(Long id) {
    Office office =
        officeRepository
            .findByIdAndEnabledTrue(id)
            .orElseThrow(() -> new EntityNotFoundException("Office not found"));

    return OfficeDetailsDto.builder()
        .id(office.getId())
        .name(office.getName())
        .branches(branchMapper.toBaseDtoList(office.getBranches()))
        .contacts(
            contactMapper.toBaseDtoList(contactRepository.findAllByOfficeAndEnabledTrue(office)))
        .build();
  }

  @Transactional
  public Office createOffice(OfficeBaseDto officeBaseDto) {
    Office office = Office.builder().name(officeBaseDto.getName()).build();
    office.setEnabled(true);

    return officeRepository.save(office);
  }

  @Transactional
  public Office createOffice(OfficeDto officeDto) {
    // Office office = officeMapper.toEntity(officeDto);
    Office office = Office.builder().name(officeDto.getName()).build();
    office.setEnabled(true);

    if (officeDto.getBranches() != null && !officeDto.getBranches().isEmpty()) {
      List<Branch> branches =
          officeDto.getBranches().stream()
              .map(
                  branchDto ->
                      Branch.builder()
                          .city(branchDto.getCity())
                          .address(branchDto.getAddress())
                          .office(office)
                          .build())
              .collect(Collectors.toList());
      office.setBranches(branches);
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

    if (officeDto.getBranches() == null || officeDto.getBranches().isEmpty()) {
      for (Branch branch : new ArrayList<>(existingBranches)) {
        if (branch.isEnabled()) {
          branchManager.disableBranch(branch.getId());
          existingBranches.remove(branch);
        }
      }
    } else {
      List<Long> updatedBranchIds =
          officeDto.getBranches().stream()
              .map(BranchBaseDto::getId)
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

      removeBranches(existingBranches, updatedBranchIds);
      addBranches(office, officeDto.getBranches());
      updateExistingBranches(officeDto.getBranches(), office);
    }

    office.setBranches(existingBranches);

    return officeRepository.save(office);
  }

  @Transactional
  public void disabledOffice(Long id) {
    Office office =
        officeRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Office not found"));

    List<Contact> contacts = contactRepository.findAllByOfficeAndEnabledTrue(office);

    if (!contacts.isEmpty()) {
      throw new BussinessRuleException(
          "Cannot disable office " + office.getId() + " because it has active contacts assigned");
    }

    office.getBranches().stream()
        .filter(Branch::isEnabled)
        .forEach(branch -> branchManager.disableBranch(branch.getId()));

    office.setEnabled(false);
    office.setDisabledAt(LocalDateTime.now());
    officeRepository.save(office);
  }

  private void removeBranches(List<Branch> existingBranches, List<Long> branchIds) {
    Iterator<Branch> iterator = existingBranches.iterator();
    while (iterator.hasNext()) {
      Branch branch = iterator.next();
      if (!branchIds.contains(branch.getId())) {
        branchManager.disableBranch(branch.getId());
        iterator.remove();
      }
    }
  }

  private void addBranches(Office office, List<BranchBaseDto> branchDtos) {
    for (BranchBaseDto dto : branchDtos) {
      if (dto.getId() == null) {
        Branch newBranch =
            Branch.builder()
                .city(dto.getCity())
                .address(dto.getAddress())
                .office(office)
                .enabled(true)
                .build();
        office.getBranches().add(newBranch);
      }
    }
  }

  private void updateExistingBranches(List<BranchBaseDto> branchDtos, Office office) {

    branchDtos.stream()
        .filter(branchDto -> branchDto.getId() != null)
        .forEach(
            branchBaseDto -> {
              BranchDto branchDto =
                  BranchDto.builder()
                      .id(branchBaseDto.getId())
                      .city(branchBaseDto.getCity())
                      .address(branchBaseDto.getAddress())
                      .office(officeMapper.toBaseDto(office))
                      .build();

              branchManager.updateBranch(branchDto.getId(), branchDto);
            });
  }
}
