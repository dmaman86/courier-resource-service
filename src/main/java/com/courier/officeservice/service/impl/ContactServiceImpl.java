package com.courier.officeservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.courier.officeservice.manager.ContactManager;
import com.courier.officeservice.objects.criteria.ContactCriteria;
import com.courier.officeservice.objects.dto.ContactBaseDto;
import com.courier.officeservice.objects.dto.ContactDto;
import com.courier.officeservice.objects.entity.Contact;
import com.courier.officeservice.objects.mapper.ContactMapper;
import com.courier.officeservice.repository.ContactRepository;
import com.courier.officeservice.service.ContactService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ContactServiceImpl implements ContactService {

  private final ContactRepository contactRepository;
  private final ContactMapper contactMapper;
  private final ContactManager contactManager;

  public ContactServiceImpl(
      ContactRepository contactRepository,
      ContactMapper contactMapper,
      ContactManager contactManager) {
    this.contactRepository = contactRepository;
    this.contactMapper = contactMapper;
    this.contactManager = contactManager;
  }

  @Override
  public Page<ContactBaseDto> getContacts(Pageable pageable) {
    return contactRepository.findByEnabledTrue(pageable).map(contactMapper::toBaseDto);
  }

  @Override
  public List<ContactBaseDto> getContactsByOffice(Long officeId) {
    return contactRepository.findByEnabledTrueAndOfficeId(officeId).stream()
        .map(contactMapper::toBaseDto)
        .collect(Collectors.toList());
  }

  @Override
  public List<ContactBaseDto> getContactsByBranch(Long branchId) {
    return contactRepository.findByEnabledTrueAndBranchesId(branchId).stream()
        .map(contactMapper::toBaseDto)
        .collect(Collectors.toList());
  }

  @Override
  public ContactDto getContactByPhone(String phoneNumber) {
    return contactRepository
        .findByPhoneNumberAndEnabledTrue(phoneNumber)
        .map(contactMapper::toDto)
        .orElseThrow(() -> new EntityNotFoundException("Contact not found"));
  }

  @Override
  public ContactDto getContactById(Long id) {
    return contactRepository
        .findById(id)
        .filter(Contact::isEnabled)
        .map(contactMapper::toDto)
        .orElseThrow(() -> new EntityNotFoundException("Contact not found"));
  }

  @Override
  public ContactDto createContact(ContactDto contactDto) {
    return contactMapper.toDto(contactManager.createContact(contactDto));
  }

  @Override
  public ContactDto updateContact(Long id, ContactDto contactDto) {
    return contactMapper.toDto(contactManager.updateContact(id, contactDto));
  }

  @Override
  public void disabledContact(Long id) {
    contactManager.disableContact(id);
  }

  @Override
  public Page<ContactBaseDto> searchContacts(String searchQuery, Pageable pageable) {
    if (searchQuery == null || searchQuery.trim().isEmpty()) {
      return Page.empty();
    }

    Specification<Contact> spec = ContactCriteria.containsText(searchQuery);
    Page<Contact> contacts = contactRepository.findAll(spec, pageable);
    return contacts.map(contactMapper::toBaseDto);
  }
}
