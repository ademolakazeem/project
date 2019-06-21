package com.hubspot.project.service;

import com.hubspot.project.model.Country;
import com.hubspot.project.model.Partner;
import com.hubspot.project.model.SecCountry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class InvitationServiceImpl implements InvitationService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private PartnerService partnerService;
    @Value("${endpoint-url}")
    private String restUrl;

    @Override
    public SecCountry getPartnersInvitations() {
        List<Country> invites = new ArrayList<>();

        Map<String, Map<LocalDate, List<Partner>>> invitationMap = partnerService.getPartnersInvitations(partnerService.getPartners());

        for (Map.Entry<String, Map<LocalDate, List<Partner>>> invitationEntry : invitationMap.entrySet()) {

            Map<LocalDate, List<Partner>> dateMap = invitationEntry.getValue();

            int max = -1;
            LocalDate date = null;
            List<Partner> availablePartners = null;

            for (Map.Entry<LocalDate, List<Partner>> entry : dateMap.entrySet()) {

                List<Partner> partnerList = entry.getValue();

                if (partnerList.size() > max) {
                    max = partnerList.size();
                    date = entry.getKey();
                    availablePartners = partnerList;
                }
            }

            Country country = new Country();

            country.setAttendeeCount(availablePartners.size());

            List<String> emailList = new ArrayList<>();

            for (Partner partner : availablePartners)
                emailList.add(partner.getEmail());
            country.setAttendees(emailList);

            country.setName(invitationEntry.getKey());
            country.setStartDate(date);
            invites.add(country);


        }
        SecCountry secCountry = new SecCountry();
        secCountry.setCountries(invites);
        return secCountry;
    }

    @Override
    public ResponseEntity<SecCountry> sendInvitations() {

        ResponseEntity<SecCountry> invitationResponseEntity = null;
        final String resultUrl = restUrl + "/" + "result?userKey=1a0b57e0554e2bc71017b364b122";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("userKey", "1a0b57e0554e2bc71017b364b122");
        HttpEntity entity = new HttpEntity(getPartnersInvitations(), headers);
        try {

            LOGGER.info("Url:" + resultUrl);
            LOGGER.info("Header information:" + entity.getHeaders().toString());
            invitationResponseEntity = restTemplate.exchange(resultUrl, HttpMethod.POST, entity,
                    new ParameterizedTypeReference<SecCountry>() {
                    });
            return invitationResponseEntity;

        } catch (Exception ex) {
            LOGGER.info(String.valueOf(ex));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
