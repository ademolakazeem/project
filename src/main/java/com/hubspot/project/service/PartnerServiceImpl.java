package com.hubspot.project.service;


import com.hubspot.project.model.Partner;
import com.hubspot.project.model.SecPartner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class PartnerServiceImpl implements PartnerService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    RestTemplate restTemplate = new RestTemplate();

    @Value("${endpoint-url}")
    private String restUrl;

    @Override
    public List<Partner> getPartners() {
        ResponseEntity<SecPartner> partnerResponseEntity = null;
        List<Partner> partners = null;
        ///dataset?userKey=1a0b57e0554e2bc71017b364b122
        final String partnerUrl = restUrl + "/" + "dataset?userKey=1a0b57e0554e2bc71017b364b122";
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("userKey", "1a0b57e0554e2bc71017b364b122");
        HttpEntity entity = new HttpEntity(headers);
        try {

            LOGGER.info("Url:" + partnerUrl);
            LOGGER.info("Header information:" + entity.getHeaders().toString());
            partnerResponseEntity = restTemplate.exchange(partnerUrl, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<SecPartner>() {
                    });
            partners = partnerResponseEntity.getBody().getPertners();
            LOGGER.info("Get partners:" + partners);

            for (Partner secPartner : partnerResponseEntity.getBody().getPertners()) {

                LOGGER.info("country=:" + secPartner.getCountry() + " name=:" + secPartner.getFirstName() + " "
                        + secPartner.getAvailableDates() + " " + secPartner.getEmail() + " " + secPartner.getLastName());

            }


        } catch (Exception ex) {
            LOGGER.info(String.valueOf(ex));
        }


        return partners;
    }

    @Override
    public List<LocalDate> getPartnerAvailabilityDates(Partner partner) {

        Collections.sort(partner.getAvailableDates());
        Map<LocalDate, Integer> startDates = new TreeMap<>();
        List<LocalDate> sortedDates = partner.getAvailableDates();

        startDates.put(sortedDates.get(0), 0);

        int index = 1;
        while (index < sortedDates.size()) {
            LocalDate previousDate = sortedDates.get(index - 1);
            LocalDate currentDate = sortedDates.get(index);

            long dateDifference = DAYS.between(previousDate, currentDate);
            LOGGER.info("Date diff: " + dateDifference);

            if (Math.abs(dateDifference) == 1) {
                LOGGER.info(" date: " + partner.getLastName() + " " + previousDate);
                int count = startDates.get(previousDate);
                startDates.put(previousDate, count + 1);
                startDates.put(currentDate, 1);
            }
            startDates.put(currentDate, 0);

            index++;
        }

        List<LocalDate> possibleDates = new ArrayList<>();

        for (Map.Entry<LocalDate, Integer> dateEntry : startDates.entrySet()) {
            if (dateEntry.getValue() > 0) {
                possibleDates.add(dateEntry.getKey());
            }
        }

        return possibleDates;
    }


    @Override
    public Map<String, Map<LocalDate, List<Partner>>> getPartnersInvitations(List<Partner> allPartners) {
        Map<String, Map<LocalDate, List<Partner>>> invitations = new HashMap<>();

        Map<String, List<Partner>> countryMap = determinePartnerCountry(allPartners);


        for (Map.Entry<String, List<Partner>> countryEntry : countryMap.entrySet()) {

            List<Partner> partners = countryEntry.getValue();

            Set<LocalDate> setDate = new TreeSet<>();

            for (Partner partner : partners) {
                setDate.addAll(getPartnerAvailabilityDates(partner));
            }

            Map<LocalDate, List<Partner>> partnerMap = new TreeMap<>();

            for (Partner partner : partners) {
                List<LocalDate> partnerDate = getPartnerAvailabilityDates(partner);

                for (LocalDate localDate : partnerDate) {
                    if (setDate.contains(localDate)) {
                        List<Partner> finalPartners = new ArrayList<>();
                        if (partnerMap.containsKey(localDate)) {
                            finalPartners = partnerMap.get(localDate);
                        }
                        finalPartners.add(partner);
                        partnerMap.put(localDate, finalPartners);
                    }
                }
            }

            invitations.put(countryEntry.getKey(), partnerMap);

        }


        return invitations;
    }

    @Override
    public Map<String, List<Partner>> determinePartnerCountry(List<Partner> partners) {
        Map<String, List<Partner>> partnersCountry = new HashMap<>();

        for (Partner partner : partners) {

            List<Partner> newPartners = new ArrayList<>();

            if (partnersCountry.containsKey(partner.getCountry())) {
                newPartners = partnersCountry.get(partner.getCountry());
                newPartners.add(partner);
            } else {
                newPartners.add(partner);
                partnersCountry.put(partner.getCountry(), newPartners);
            }
        }

        return partnersCountry;
    }
}
