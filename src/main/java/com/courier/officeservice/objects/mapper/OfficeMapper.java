package com.courier.officeservice.objects.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.courier.officeservice.objects.dto.OfficeBaseDto;
import com.courier.officeservice.objects.dto.OfficeDto;
import com.courier.officeservice.objects.entity.Office;

@Mapper(
    componentModel = "spring",
    uses = {BranchMapper.class})
public interface OfficeMapper {

  @Mapping(target = "branches", source = "branches", qualifiedByName = "toBaseDtoList")
  OfficeDto toDto(Office office);

  @Named("toBaseDto")
  OfficeBaseDto toBaseDto(Office office);

  List<OfficeDto> toDtoList(List<Office> offices);

  @Mapping(target = "branches", ignore = true)
  @Mapping(target = "contacts", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "enabled", ignore = true)
  @Mapping(target = "disabledAt", ignore = true)
  Office toEntity(OfficeBaseDto officeDto);

  @Mapping(target = "branches", ignore = true)
  @Mapping(target = "contacts", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "enabled", ignore = true)
  @Mapping(target = "disabledAt", ignore = true)
  Office toEntity(OfficeDto officeDto);
}
