package com.chronos.observability.dto;

public enum SegmentType {
    PROCESSING,
    INTER_SERVICE,
    QUEUE_DELAY,
    FAULT_DELAY,
    UNKNOWN
}
