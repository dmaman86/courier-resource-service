package com.courier.resource_service.objects.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.courier.resource_service.objects.dto.ContactBaseDto;
import com.courier.resource_service.objects.dto.ContactDto;
import com.courier.resource_service.objects.entity.Contact;

@Mapper(
    componentModel = "spring",
    uses = {OfficeMapper.class, BranchMapper.class})
public interface ContactMapper {

  @Mapping(target = "office", qualifiedByName = "toBaseDto")
  @Mapping(target = "branches", qualifiedByName = "toBaseDtoList")
  ContactDto toDto(Contact contact);

  @Named("toBaseDto")
  ContactBaseDto toBaseDto(Contact contact);

  List<ContactDto> toDtoList(List<Contact> contacts);

  @Mapping(target = "office", ignore = true)
  @Mapping(target = "branches", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "enabled", ignore = true)
  @Mapping(target = "disabledAt", ignore = true)
  Contact toEntity(ContactDto contactDto);
}
