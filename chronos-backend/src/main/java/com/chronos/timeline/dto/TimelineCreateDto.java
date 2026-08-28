package com.chronos.timeline.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TimelineCreateDto {
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
}
