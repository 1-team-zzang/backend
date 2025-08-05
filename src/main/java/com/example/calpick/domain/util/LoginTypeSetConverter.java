package com.example.calpick.domain.util;

import com.example.calpick.domain.entity.enums.LoginType;
import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class LoginTypeSetConverter implements AttributeConverter<Set<LoginType>, String> {

    // Set<LoginType>를 DB에 저장할 String으로 변환
    @Override
    public String convertToDatabaseColumn(Set<LoginType> loginTypes) {
        if (loginTypes == null || loginTypes.isEmpty()) return "";
        return loginTypes.stream()
                .map(Enum::name)
                .sorted()
                .collect(Collectors.joining(","));
    }

    //DB의 Sting을 Set<LoginType>으로 변환
    @Override
    public Set<LoginType> convertToEntityAttribute(String data) {
        if (data ==null || data.trim().isEmpty()) return Set.of();
        return Arrays.stream(data.split(","))
                .map(LoginType::valueOf)
                .collect(Collectors.toSet());
    }
}
