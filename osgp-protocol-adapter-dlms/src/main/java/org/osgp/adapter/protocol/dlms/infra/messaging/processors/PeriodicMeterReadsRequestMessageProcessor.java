/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.osgp.adapter.protocol.dlms.application.services.MonitoringService;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest;
import com.alliander.osgp.shared.infra.jms.Constants;

/**
 * Class for processing Periodic Meter Request messages
 */
@Component("dlmsPeriodicMeterRequestMessageProcessor")
public class PeriodicMeterReadsRequestMessageProcessor extends DeviceRequestMessageProcessor {
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicMeterReadsRequestMessageProcessor.class);

    @Autowired
    private MonitoringService monitoringService;

    public PeriodicMeterReadsRequestMessageProcessor() {
        super(DeviceRequestMessageType.REQUEST_PERIODIC_METER_DATA);
    }

    @Override
    public void processMessage(final ObjectMessage message) {
        LOGGER.debug("Processing periodic meter reads request message");

        String correlationUid = null;
        String domain = null;
        String domainVersion = null;
        String messageType = null;
        String organisationIdentification = null;
        String deviceIdentification = null;

        try {
            correlationUid = message.getJMSCorrelationID();
            domain = message.getStringProperty(Constants.DOMAIN);
            domainVersion = message.getStringProperty(Constants.DOMAIN_VERSION);
            messageType = message.getJMSType();
            organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
            deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

            final PeriodicMeterReadsRequest periodicMeterReadsRequest = (PeriodicMeterReadsRequest) message.getObject();

            this.monitoringService.requestPeriodicMeterReads(organisationIdentification, deviceIdentification,
                    correlationUid, periodicMeterReadsRequest, this.responseMessageSender, domain, domainVersion,
                    messageType);

        } catch (final JMSException e) {
            LOGGER.error("UNRECOVERABLE ERROR, unable to read ObjectMessage instance, giving up.", e);
            LOGGER.debug("correlationUid: {}", correlationUid);
            LOGGER.debug("domain: {}", domain);
            LOGGER.debug("domainVersion: {}", domainVersion);
            LOGGER.debug("messageType: {}", messageType);
            LOGGER.debug("organisationIdentification: {}", organisationIdentification);
            LOGGER.debug("deviceIdentification: {}", deviceIdentification);
            return;
        }
    }
}