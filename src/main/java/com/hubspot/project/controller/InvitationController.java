package com.hubspot.project.controller;


import com.hubspot.project.model.SecCountry;
import com.hubspot.project.service.InvitationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvitationController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvitationService invitationService;

    @PostMapping("/invitations")
    public ResponseEntity<SecCountry> sendInvitations() {

        ResponseEntity<SecCountry> responseEntity = invitationService.sendInvitations();

        if (responseEntity == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } else if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            LOGGER.info("response: " + responseEntity.getBody());
            return ResponseEntity.ok().body(responseEntity.getBody());
        } else {
            return ResponseEntity.status(responseEntity.getStatusCode()).build();
        }
    }

}

