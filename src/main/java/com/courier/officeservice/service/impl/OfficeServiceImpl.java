package com.courier.officeservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.courier.officeservice.manager.OfficeManager;
import com.courier.officeservice.objects.dto.OfficeBaseDto;
import com.courier.officeservice.objects.dto.OfficeDto;
import com.courier.officeservice.objects.entity.Office;
import com.courier.officeservice.objects.mapper.OfficeMapper;
import com.courier.officeservice.repository.OfficeRepository;
import com.courier.officeservice.service.OfficeService;

import jakarta.persistence.EntityNotFoundException;

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
  public Page<OfficeBaseDto> getOffices(Pageable pageable) {
    return officeRepository.findByEnabledTrue(pageable).map(officeMapper::toBaseDto);
  }

  @Override
  public List<OfficeBaseDto> getOfficesWithoutPagination() {
    return officeRepository.findByEnabledTrue().stream()
        .map(officeMapper::toBaseDto)
        .collect(Collectors.toList());
  }

  @Override
  public OfficeBaseDto getOfficeById(Long id) {
    return officeRepository
        .findById(id)
        .filter(Office::isEnabled)
        .map(officeMapper::toBaseDto)
        .orElseThrow(() -> new EntityNotFoundException("Office not found"));
  }

  @Override
  public OfficeBaseDto createOffice(OfficeBaseDto officeBaseDto) {
    Office office = officeMapper.toEntity(officeBaseDto);
    office.setEnabled(true);
    return officeMapper.toBaseDto(officeRepository.save(office));
  }

  @Override
  public OfficeDto createOfficeWithBranches(OfficeDto officeDto) {
    return officeMapper.toDto(officeManager.createOffice(officeDto));
  }

  @Override
  public OfficeDto updateOffice(Long id, OfficeDto officeDto) {
    return officeMapper.toDto(officeManager.updateOffice(id, officeDto));
  }

  @Override
  public void disabledOffice(Long id) {
    officeManager.disabledOffice(id);
  }

  @Override
  public Page<OfficeBaseDto> searchOffices(String searchQuery, Pageable pageable) {
    if (searchQuery == null || searchQuery.isEmpty()) {
      return Page.empty(pageable);
    }

    return officeRepository
        .findByEnabledTrueAndNameContainingIgnoreCase(searchQuery, pageable)
        .map(officeMapper::toBaseDto);
  }
}
