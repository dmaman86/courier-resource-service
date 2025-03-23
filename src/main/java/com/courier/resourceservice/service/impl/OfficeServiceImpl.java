package com.courier.resourceservice.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.courier.resourceservice.exception.EntityNotFoundException;
import com.courier.resourceservice.manager.OfficeManager;
import com.courier.resourceservice.objects.dto.OfficeBaseDto;
import com.courier.resourceservice.objects.dto.OfficeDto;
import com.courier.resourceservice.objects.entity.Branch;
import com.courier.resourceservice.objects.entity.Contact;
import com.courier.resourceservice.objects.entity.Office;
import com.courier.resourceservice.objects.mapper.OfficeMapper;
import com.courier.resourceservice.objects.response.OfficeResponse;
import com.courier.resourceservice.repository.OfficeRepository;
import com.courier.resourceservice.service.OfficeService;

@Service
public class OfficeServiceImpl implements OfficeService {

  private final OfficeRepository officeRepository;
  private final OfficeMapper officeMapper;
  private final OfficeManager officeManager;

  public OfficeServiceImpl(
      OfficeRepository officeRepository, OfficeMapper officeMapper, OfficeManager officeManager) {
    this.officeRepository = officeRepository;
    this.officeMapper = officeMapper;
    this.officeManager = officeManager;
  }

  @Override
  public Page<OfficeResponse> getOffices(Pageable pageable) {
    Page<Office> offices = officeRepository.findByEnabledTrue(pageable);
    return offices.map(this::mapToOfficeResponse);
  }

  @Override
  public List<OfficeBaseDto> getOfficesWithoutPagination() {
    var offices = officeRepository.findByEnabledTrue();
    return officeMapper.toBaseDtoList(offices);
  }

  @Override
  public OfficeBaseDto getOfficeById(Long id) {
    Office office =
        officeRepository
            .findById(id)
            .filter(Office::isEnabled)
            .orElseThrow(() -> new EntityNotFoundException("Office not found"));

    if (office.getBranches() != null && !office.getBranches().isEmpty()) {
      return officeMapper.toDto(office);
    }
    return officeMapper.toBaseDto(office);
  }

  @Override
  public OfficeResponse createOffice(OfficeBaseDto officeBaseDto) {
    Office office = officeManager.createOffice(officeBaseDto);
    return mapToOfficeResponse(office);
    // return officeMapper.toBaseDto(officeRepository.save(office));
  }

  @Override
  public OfficeResponse createOfficeWithBranches(OfficeDto officeDto) {
    Office savedOffice = officeManager.createOffice(officeDto);
    return mapToOfficeResponse(savedOffice);
  }

  @Override
  public OfficeResponse updateOffice(Long id, OfficeDto officeDto) {
    Office savedOffice = officeManager.updateOffice(id, officeDto);
    return mapToOfficeResponse(savedOffice);
    // return officeMapper.toDto(officeManager.updateOffice(id, officeDto));
  }

  @Override
  public void disabledOffice(Long id) {
    officeManager.disabledOffice(id);
  }

  @Override
  public Page<OfficeResponse> searchOffices(String searchQuery, Pageable pageable) {
    if (searchQuery == null || searchQuery.isEmpty()) {
      return Page.empty(pageable);
    }

    Page<Office> offices =
        officeRepository.findByEnabledTrueAndNameContainingIgnoreCase(searchQuery, pageable);
    return offices.map(this::mapToOfficeResponse);
  }

  private OfficeResponse mapToOfficeResponse(Office office) {
    Long countBranches =
        office.getBranches() != null
            ? office.getBranches().stream().filter(Branch::isEnabled).count()
            : 0;

    Long countContacts =
        office.getContacts() != null
            ? office.getContacts().stream().filter(Contact::isEnabled).count()
            : 0;

    return OfficeResponse.builder()
        .id(office.getId())
        .name(office.getName())
        .countBranches(countBranches)
        .countContacts(countContacts)
        .build();
  }
}
