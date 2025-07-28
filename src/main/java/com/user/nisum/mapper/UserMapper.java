package com.user.nisum.mapper;

import com.user.nisum.dtos.PhoneDTODTO;
import com.user.nisum.dtos.UserRegistrationRequestDTO;
import com.user.nisum.dtos.UserRegistrationResponseDTO;
import com.user.nisum.entity.Phone;
import com.user.nisum.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "phones", source = "phones")
    User toEntity(UserRegistrationRequestDTO request);
    
    @Mapping(target = "phones", source = "phones")
    UserRegistrationResponseDTO toResponse(User user);
    
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    Phone toPhoneEntity(PhoneDTODTO phoneDTO);
    
    PhoneDTODTO toPhoneDTO(Phone phone);
    
    List<Phone> toPhoneEntityList(List<PhoneDTODTO> phoneDTOs);
    
    List<PhoneDTODTO> toPhoneDTOList(List<Phone> phones);
    
    // Métodos de conversión de fechas
    default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atOffset(ZoneOffset.UTC);
    }
    
    default LocalDateTime offsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.toLocalDateTime();
    }
} 