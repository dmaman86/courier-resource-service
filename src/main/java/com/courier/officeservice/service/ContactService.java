package com.courier.officeservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.courier.officeservice.objects.dto.ContactBaseDto;
import com.courier.officeservice.objects.dto.ContactDto;

public interface ContactService {

  Page<ContactBaseDto> getContacts(Pageable pageable);

  List<ContactBaseDto> getContactsByOffice(Long officeId);

  List<ContactBaseDto> getContactsByBranch(Long branchId);

  ContactDto getContactByPhone(String phoneNumber);

  ContactDto getContactById(Long id);

  ContactDto createContact(ContactDto contactDto);

  ContactDto updateContact(Long id, ContactDto contactDto);

  void disabledContact(Long id);

  Page<ContactBaseDto> searchContacts(String searchQuery, Pageable pageable);
}
