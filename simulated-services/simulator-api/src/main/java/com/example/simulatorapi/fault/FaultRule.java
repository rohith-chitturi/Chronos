package com.example.simulatorapi.fault;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaultRule {
    private String ruleId;
    private String experimentId;
    private String targetService;
    private FaultType faultType;
    private String targetEventType;
    private long durationMs;
    private double probability;
    private long seed;
    private ExecutionMode executionMode;
    private FaultStatus status;
}
