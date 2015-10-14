/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.Configurations;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequestData;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDay;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestData;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "dlmsConfigurationService")
public class ConfigurationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);

    /**
     * Constructor
     */
    public ConfigurationService() {
        // Parameterless constructor required for transactions...
    }

    // === REQUEST Special Days DATA ===

    public void requestSpecialDays(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final SpecialDaysRequest specialDaysRequest,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType) {

        LOGGER.info("requestSpecialDays called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        try {
            // The Special days towards the Smart Meter
            SpecialDaysRequestData specialDaysRequestData = specialDaysRequest.getSpecialDaysRequestData();

            LOGGER.info("SpecialDaysRequest : {}", specialDaysRequest.getSpecialDaysRequestData());
            for (final SpecialDay specialDay : specialDaysRequestData.getSpecialDays()) {
                LOGGER.info("******************************************************");
                LOGGER.info("Special Day date :{} ", specialDay.getSpecialDayDate());
                LOGGER.info("Special Day dayId :{} ", specialDay.getDayId());
                LOGGER.info("******************************************************");
            }

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during set special days", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception during set special days", e);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

    // === REQUEST Configuration Object DATA ===

    public void requestSetConfiguration(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final SetConfigurationObjectRequest setConfigurationObjectRequest,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType) {

        LOGGER.info("requestSetConfiguration called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        try {
            // Configuration Object towards the Smart Meter
            SetConfigurationObjectRequestData setConfigurationObjectRequestData = setConfigurationObjectRequest
                    .getSetConfigurationObjectRequestData();

            LOGGER.info("Set Configuration Object : {}",
                    setConfigurationObjectRequest.getSetConfigurationObjectRequestData());

            Configurations configurations = setConfigurationObjectRequestData.getConfiguration();

            LOGGER.info("******************************************************");
            LOGGER.info("Configuration Object :{} ", configurations.getConfiguration());
            LOGGER.info("******************************************************");

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during set Configuration Object", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception during set Configuration Object", e);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

    private void sendResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender) {

        // Creating a ProtocolResponseMessage without a Serializable object
        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                correlationUid, organisationIdentification, deviceIdentification, result, osgpException, null);

        responseMessageSender.send(responseMessage);
    }

}