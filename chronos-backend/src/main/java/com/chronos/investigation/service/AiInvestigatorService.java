package com.chronos.investigation.service;

import com.chronos.incident.dto.IncidentReport;
import com.chronos.investigation.dto.InvestigationReport;

public interface AiInvestigatorService {
    InvestigationReport investigate(IncidentReport incidentReport);
}
