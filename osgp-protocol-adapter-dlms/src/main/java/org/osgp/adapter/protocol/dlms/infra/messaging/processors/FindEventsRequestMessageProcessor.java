/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders.exceptions.SessionProviderException;
import org.osgp.adapter.protocol.dlms.application.services.ManagementService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsRequestList;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

/**
 * Class for processing find events request messages
 */
@Component
public class FindEventsRequestMessageProcessor extends DeviceRequestMessageProcessor {

    @Autowired
    private ManagementService managementService;

    public FindEventsRequestMessageProcessor() {
        super(DeviceRequestMessageType.FIND_EVENTS);
    }

    @Override
    protected Serializable handleMessage(final ClientConnection conn, final DlmsDevice device,
            final Serializable requestObject) throws OsgpException, ProtocolAdapterException, SessionProviderException {

        return this.managementService.findEvents(conn, device, (FindEventsRequestList) requestObject);
    }
}
