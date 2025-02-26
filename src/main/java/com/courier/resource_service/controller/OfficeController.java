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

import com.courier.resource_service.objects.dto.OfficeBaseDto;
import com.courier.resource_service.objects.dto.OfficeDto;
import com.courier.resource_service.service.OfficeService;

@RestController
@RequestMapping("/api/resource/office")
public class OfficeController {

  @Autowired private OfficeService officeService;

  @GetMapping
  public ResponseEntity<Page<OfficeBaseDto>> getAllOffices(Pageable pageable) {
    return ResponseEntity.ok(officeService.getOffices(pageable));
  }

  @GetMapping("/all")
  public ResponseEntity<List<OfficeBaseDto>> getAllOfficesWithoutPagination() {
    return ResponseEntity.ok(officeService.getOfficesWithoutPagination());
  }

  @GetMapping("/{id}")
  public ResponseEntity<OfficeBaseDto> getOfficeById(@PathVariable Long id) {
    return ResponseEntity.ok(officeService.getOfficeById(id));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PostMapping("/base")
  public ResponseEntity<OfficeBaseDto> createOffice(@RequestBody OfficeBaseDto officeBaseDto) {
    return ResponseEntity.ok(officeService.createOffice(officeBaseDto));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PostMapping
  public ResponseEntity<OfficeDto> createOfficeWithBranches(@RequestBody OfficeDto officeDto) {
    return ResponseEntity.ok(officeService.createOfficeWithBranches(officeDto));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<OfficeDto> updateOffice(
      @PathVariable Long id, @RequestBody OfficeDto officeDto) {
    return ResponseEntity.ok(officeService.updateOffice(id, officeDto));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> disableOffice(@PathVariable Long id) {
    officeService.disabledOffice(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  public ResponseEntity<Page<OfficeBaseDto>> searchOffices(
      @RequestParam String query, Pageable pageable) {
    return ResponseEntity.ok(officeService.searchOffices(query, pageable));
  }
}
