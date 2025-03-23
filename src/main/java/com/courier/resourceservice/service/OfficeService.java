package com.courier.resourceservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.courier.resourceservice.objects.dto.OfficeBaseDto;
import com.courier.resourceservice.objects.dto.OfficeDto;
import com.courier.resourceservice.objects.response.OfficeResponse;

public interface OfficeService {

  Page<OfficeResponse> getOffices(Pageable pageable);

  List<OfficeBaseDto> getOfficesWithoutPagination();

  OfficeBaseDto getOfficeById(Long id);

  OfficeResponse createOffice(OfficeBaseDto officeBaseDto);

  OfficeResponse createOfficeWithBranches(OfficeDto officeDto);

  OfficeResponse updateOffice(Long id, OfficeDto officeDto);

  void disabledOffice(Long id);

  Page<OfficeResponse> searchOffices(String searchQuery, Pageable pageable);
}
