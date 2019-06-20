package com.hubspot.project.service;

import com.hubspot.project.model.SecCountry;
import org.springframework.http.ResponseEntity;

public interface InvitationService {
    SecCountry getPartnersInvitations();

    ResponseEntity<SecCountry> sendInvitations();
}
