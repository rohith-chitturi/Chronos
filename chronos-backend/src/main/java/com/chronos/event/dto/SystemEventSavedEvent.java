package com.chronos.event.dto;

import com.chronos.event.entity.SystemEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SystemEventSavedEvent {
    private final SystemEvent systemEvent;
}
