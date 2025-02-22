package com.courier.officeservice.objects.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.courier.officeservice.objects.dto.BranchBaseDto;
import com.courier.officeservice.objects.dto.BranchDto;
import com.courier.officeservice.objects.entity.Branch;

@Mapper(
    componentModel = "spring",
    uses = {OfficeMapper.class})
public interface BranchMapper {

  @Mapping(target = "office", qualifiedByName = "toBaseDto")
  BranchDto toDto(Branch branch);

  @Named("toBaseDto")
  BranchBaseDto toBaseDto(Branch branch);

  List<BranchDto> toDtoList(List<Branch> branches);

  @Named("toBaseDtoList")
  List<BranchBaseDto> toBaseDtoList(List<Branch> branches);

  @Mapping(target = "contacts", ignore = true)
  @Mapping(target = "office", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "enabled", ignore = true)
  @Mapping(target = "disabledAt", ignore = true)
  Branch toEntity(BranchBaseDto branchDto);

  @Mapping(target = "contacts", ignore = true)
  @Mapping(target = "office", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "enabled", ignore = true)
  @Mapping(target = "disabledAt", ignore = true)
  Branch toEntity(BranchDto branchDto);
}
