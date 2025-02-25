package com.courier.resource_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.courier.resource_service.objects.dto.BranchBaseDto;
import com.courier.resource_service.objects.dto.BranchDto;
import com.courier.resource_service.service.BranchService;

@RestController
@RequestMapping("/api/courier/resource/branch")
public class BranchController {

  @Autowired private BranchService branchService;

  @GetMapping
  public ResponseEntity<Page<BranchDto>> getAllBranches(Pageable pageable) {
    return ResponseEntity.ok(branchService.getBranches(pageable));
  }

  @GetMapping("/all")
  public ResponseEntity<List<BranchBaseDto>> getAllBranchesWithoutPagination() {
    return ResponseEntity.ok(branchService.getBranchesWithoutPagination());
  }

  @GetMapping("/office/{officeId}")
  public ResponseEntity<List<BranchBaseDto>> getBranchesByOfficeId(@PathVariable Long officeId) {
    return ResponseEntity.ok(branchService.getBranchesByOfficeId(officeId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BranchDto> getBranchById(@PathVariable Long id) {
    return ResponseEntity.ok(branchService.getBranchById(id));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PostMapping
  public ResponseEntity<BranchDto> createBranch(@RequestBody BranchDto branchDto) {
    return ResponseEntity.ok(branchService.createBranch(branchDto));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<BranchDto> updateBranch(
      @PathVariable Long id, @RequestBody BranchDto branchDto) {
    return ResponseEntity.ok(branchService.updateBranch(id, branchDto));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> disableBranch(@PathVariable Long id) {
    branchService.disabledBranch(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  public ResponseEntity<Page<BranchDto>> searchBranches(
      @RequestParam String query, Pageable pageable) {
    return ResponseEntity.ok(branchService.searchBranches(query, pageable));
  }
}
