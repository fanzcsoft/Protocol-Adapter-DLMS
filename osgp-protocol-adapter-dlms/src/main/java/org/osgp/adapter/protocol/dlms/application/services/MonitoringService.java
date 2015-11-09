/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestData;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "dlmsDeviceMonitoringService")
public class MonitoringService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringService.class);

    private static final Random generator = new Random();

    /**
     * Constructor
     */
    public MonitoringService() {
        // Parameterless constructor required for transactions...
    }

    // === REQUEST PERIODIC METER DATA ===

    public void requestPeriodicMeterReads(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final PeriodicMeterReadsRequest periodicMeterReadsRequest,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType) {

        LOGGER.info("requestPeriodicMeterReads called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        try {
            // creating duMy periodicMeterReads

            final PeriodicMeterReadsContainer periodicMeterReadsContainer = new PeriodicMeterReadsContainer();
            periodicMeterReadsContainer.setDeviceIdentification(deviceIdentification);

            PeriodicMeterReads periodicMeterReads;
            for (final PeriodicMeterReadsRequestData p : periodicMeterReadsRequest.getPeriodicMeterReadsRequestData()) {
                // DuMy MeterReads with random values
                periodicMeterReads = new PeriodicMeterReads();
                periodicMeterReads.setLogTime(p.getDate());
                periodicMeterReads.setActiveEnergyImportTariffOne(Math.abs(generator.nextLong()));
                periodicMeterReads.setActiveEnergyImportTariffTwo(Math.abs(generator.nextLong()));
                periodicMeterReads.setActiveEnergyExportTariffOne(Math.abs(generator.nextLong()));
                periodicMeterReads.setActiveEnergyExportTariffTwo(Math.abs(generator.nextLong()));

                periodicMeterReads.setPeriodicMeterReads(periodicMeterReadsContainer);
                periodicMeterReadsContainer.addPeriodicMeterReads(periodicMeterReads);
            }

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender,
                    periodicMeterReadsContainer);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during requestPeriodicMeterReads", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender, null);
        }
    }

    private void sendResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender, final Serializable responseObject) {

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                correlationUid, organisationIdentification, deviceIdentification, result, osgpException, responseObject);

        responseMessageSender.send(responseMessage);
    }

    public void requestActualMeterReads(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final ActualMeterReadsRequest actualMeterReadsRequest,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType) {

        LOGGER.info("requestActualMeterReads called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        try {
            // Mock a return value for actual meter reads.
            final ActualMeterReads actualMeterReads = new ActualMeterReads();
            actualMeterReads.setLogTime(new Date());
            actualMeterReads.setActiveEnergyImportTariffOne(Math.abs(generator.nextLong()));
            actualMeterReads.setActiveEnergyImportTariffTwo(Math.abs(generator.nextLong()));
            actualMeterReads.setActiveEnergyExportTariffOne(Math.abs(generator.nextLong()));
            actualMeterReads.setActiveEnergyExportTariffTwo(Math.abs(generator.nextLong()));

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender, actualMeterReads);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during requestActualMeterReads", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender, null);
        }
    }
}
