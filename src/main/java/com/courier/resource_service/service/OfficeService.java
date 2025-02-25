package com.courier.resource_service.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.courier.resource_service.objects.dto.OfficeBaseDto;
import com.courier.resource_service.objects.dto.OfficeDto;

public interface OfficeService {

  Page<OfficeBaseDto> getOffices(Pageable pageable);

  List<OfficeBaseDto> getOfficesWithoutPagination();

  OfficeBaseDto getOfficeById(Long id);

  OfficeBaseDto createOffice(OfficeBaseDto officeBaseDto);

  OfficeDto createOfficeWithBranches(OfficeDto officeDto);

  OfficeDto updateOffice(Long id, OfficeDto officeDto);

  void disabledOffice(Long id);

  Page<OfficeBaseDto> searchOffices(String searchQuery, Pageable pageable);
}
