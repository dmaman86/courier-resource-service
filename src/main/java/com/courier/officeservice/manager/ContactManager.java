package com.courier.officeservice.manager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.courier.officeservice.objects.dto.BranchBaseDto;
import com.courier.officeservice.objects.dto.ContactDto;
import com.courier.officeservice.objects.entity.Branch;
import com.courier.officeservice.objects.entity.Contact;
import com.courier.officeservice.objects.entity.Office;
import com.courier.officeservice.repository.BranchRepository;
import com.courier.officeservice.repository.ContactRepository;
import com.courier.officeservice.repository.OfficeRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@Component
public class ContactManager {

  private final ContactRepository contactRepository;
  private final OfficeRepository officeRepository;
  private final BranchRepository branchRepository;

  public ContactManager(
      ContactRepository contactRepository,
      OfficeRepository officeRepository,
      BranchRepository branchRepository) {
    this.contactRepository = contactRepository;
    this.officeRepository = officeRepository;
    this.branchRepository = branchRepository;
  }

  @Transactional
  public Contact createContact(ContactDto contactDto) {
    Office office =
        officeRepository
            .findById(contactDto.getOffice().getId())
            .orElseThrow(() -> new EntityNotFoundException("Office not found"));

    boolean existsContact =
        contactRepository.existsByPhoneNumberAndEnabledTrue(contactDto.getPhoneNumber());

    if (existsContact)
      throw new EntityExistsException("Contact with this phone number already exists");

    List<Branch> branches = validateBranches(contactDto.getBranches(), office);
    if (branches.isEmpty()) {
      throw new RuntimeException("Contact must be assigned to at least one branch of the office");
    }

    Contact contact =
        Contact.builder()
            .fullName(contactDto.getFullName())
            .phoneNumber(contactDto.getPhoneNumber())
            .office(office)
            .branches(branches)
            .enabled(true)
            .build();

    return contactRepository.save(contact);
  }

  @Transactional
  public Contact updateContact(Long id, ContactDto contactDto) {
    Contact contact =
        contactRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Contact not found"));

    Office office =
        officeRepository
            .findById(contactDto.getOffice().getId())
            .orElseThrow(() -> new EntityNotFoundException("Office not found"));

    List<Branch> branches = validateBranches(contactDto.getBranches(), office);
    if (branches.isEmpty()) {
      throw new RuntimeException("Contact must be assigned to at least one branch of the office");
    }

    contact.setFullName(contactDto.getFullName());
    contact.setPhoneNumber(contactDto.getPhoneNumber());
    contact.setOffice(office);

    contact.getBranches().clear();
    contact.getBranches().addAll(branches);

    return contactRepository.save(contact);
  }

  @Transactional
  public void disableContact(Long id) {
    Contact contact =
        contactRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Contact not found"));

    contact.getBranches().clear();
    contact.setEnabled(false);
    contact.setDisabledAt(LocalDateTime.now());
    contactRepository.save(contact);
  }

  private List<Branch> validateBranches(List<BranchBaseDto> branchDtos, Office office) {
    if (branchDtos == null || branchDtos.isEmpty()) {
      throw new EntityNotFoundException("At least one branch must be assigned to the contact");
    }

    List<Long> branchIds =
        branchDtos.stream().map(BranchBaseDto::getId).collect(Collectors.toList());

    List<Branch> branches =
        branchRepository.findByIdInAndOfficeIdAndEnabledTrue(branchIds, office.getId());

    if (branches.size() != branchIds.size()) {
      throw new EntityNotFoundException(
          "Some branches are invalid or do not belong to the specified office");
    }

    return branches;
  }
}
