package com.schoolsystem.sms.config;

import com.schoolsystem.sms.model.ClassLevel;
import com.schoolsystem.sms.repository.ClassLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Allows Spring MVC to convert a form-submitted Long (class level ID)
 * into a ClassLevel entity for binding on the Student form.
 */
@Component
@RequiredArgsConstructor
public class ClassLevelConverter implements Converter<String, ClassLevel> {

    private final ClassLevelRepository classLevelRepository;

    @Override
    public ClassLevel convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        try {
            Long id = Long.parseLong(source);
            return classLevelRepository.findById(id).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
