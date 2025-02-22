package com.courier.officeservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.courier.officeservice.manager.BranchManager;
import com.courier.officeservice.objects.criteria.BranchCriteria;
import com.courier.officeservice.objects.dto.BranchBaseDto;
import com.courier.officeservice.objects.dto.BranchDto;
import com.courier.officeservice.objects.entity.Branch;
import com.courier.officeservice.objects.mapper.BranchMapper;
import com.courier.officeservice.repository.BranchRepository;
import com.courier.officeservice.service.BranchService;

@Service
public class BranchServiceImpl implements BranchService {

  private final BranchManager branchManager;
  private final BranchMapper branchMapper;
  private final BranchRepository branchRepository;

  public BranchServiceImpl(
      BranchManager branchManager, BranchMapper branchMapper, BranchRepository branchRepository) {
    this.branchManager = branchManager;
    this.branchMapper = branchMapper;
    this.branchRepository = branchRepository;
  }

  @Override
  public Page<BranchDto> getBranches(Pageable pageable) {
    return branchRepository.findByEnabledTrue(pageable).map(branchMapper::toDto);
  }

  @Override
  public List<BranchBaseDto> getBranchesWithoutPagination() {
    return branchRepository.findByEnabledTrue().stream()
        .map(branchMapper::toBaseDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<BranchBaseDto> getBranchesByOfficeId(Long officeId) {
    return branchRepository.findByEnabledTrueAndOfficeId(officeId).stream()
        .map(branchMapper::toBaseDto)
        .collect(Collectors.toList());
  }

  @Override
  public BranchDto getBranchById(Long id) {
    return branchRepository
        .findById(id)
        .filter(Branch::isEnabled)
        .map(branchMapper::toDto)
        .orElseThrow(() -> new IllegalArgumentException("Branch not found"));
  }

  @Override
  public BranchDto createBranch(BranchDto branchDto) {
    return branchMapper.toDto(branchManager.createBranch(branchDto));
  }

  @Override
  public BranchDto updateBranch(Long id, BranchDto branchDto) {
    return branchMapper.toDto(branchManager.updateBranch(id, branchDto));
  }

  @Override
  public void disabledBranch(Long id) {
    branchManager.disableBranch(id);
  }

  @Override
  public Page<BranchDto> searchBranches(String searchQuery, Pageable pageable) {
    if (searchQuery == null || searchQuery.trim().isEmpty()) {
      return Page.empty();
    }

    Specification<Branch> spec = BranchCriteria.containsText(searchQuery);
    Page<Branch> branches = branchRepository.findAll(spec, pageable);

    return branches.map(branchMapper::toDto);
  }
}
