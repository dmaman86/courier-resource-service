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

import com.courier.resource_service.objects.dto.ContactBaseDto;
import com.courier.resource_service.objects.dto.ContactDto;
import com.courier.resource_service.service.ContactService;

@RestController
@RequestMapping("/api/resource/contact")
public class ContactController {

  @Autowired private ContactService contactService;

  @GetMapping
  public ResponseEntity<Page<ContactBaseDto>> getAllContacts(Pageable pageable) {
    return ResponseEntity.ok(contactService.getContacts(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ContactDto> getContactById(@PathVariable Long id) {
    return ResponseEntity.ok(contactService.getContactById(id));
  }

  @GetMapping("/phone/{phoneNumber}")
  public ResponseEntity<ContactDto> getContactByPhone(@PathVariable String phoneNumber) {
    return ResponseEntity.ok(contactService.getContactByPhone(phoneNumber));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PostMapping
  public ResponseEntity<ContactDto> createContact(@RequestBody ContactDto contactDto) {
    return ResponseEntity.ok(contactService.createContact(contactDto));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<ContactDto> updateContact(
      @PathVariable Long id, @RequestBody ContactDto contactDto) {
    return ResponseEntity.ok(contactService.updateContact(id, contactDto));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> disableContact(@PathVariable Long id) {
    contactService.disabledContact(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  public ResponseEntity<Page<ContactBaseDto>> searchContacts(
      @RequestParam String query, Pageable pageable) {
    return ResponseEntity.ok(contactService.searchContacts(query, pageable));
  }

  @GetMapping("/office/{officeId}")
  public ResponseEntity<List<ContactBaseDto>> getContactsByOfficeId(@PathVariable Long officeId) {
    return ResponseEntity.ok(contactService.getContactsByOffice(officeId));
  }

  @GetMapping("/branch/{branchId}")
  public ResponseEntity<List<ContactBaseDto>> getContactsByBranchId(@PathVariable Long branchId) {
    return ResponseEntity.ok(contactService.getContactsByBranch(branchId));
  }
}
