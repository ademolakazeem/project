package com.hubspot.project.service;


import com.hubspot.project.model.Partner;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface PartnerService {
    List<Partner> getPartners();

    List<LocalDate> getPartnerAvailabilityDates(Partner partner);

    Map<String, Map<LocalDate, List<Partner>>> getPartnersInvitations(List<Partner> partners);

    Map<String, List<Partner>> determinePartnerCountry(List<Partner> partners);
}
