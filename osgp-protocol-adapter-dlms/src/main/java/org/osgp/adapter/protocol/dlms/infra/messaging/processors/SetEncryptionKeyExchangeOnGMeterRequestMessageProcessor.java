/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;

import org.osgp.adapter.protocol.dlms.application.services.ConfigurationService;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.GMeterInfo;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

/**
 * Class for processing set Activity Calendar request messages
 */
@Component("dlmsSetEncryptionKeyExchangeOnGMeterRequestMessageProcessor")
public class SetEncryptionKeyExchangeOnGMeterRequestMessageProcessor extends DeviceRequestMessageProcessor {

    @Autowired
    private ConfigurationService configurationService;

    public SetEncryptionKeyExchangeOnGMeterRequestMessageProcessor() {
        super(DeviceRequestMessageType.SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER);
    }

    @Override
    protected Serializable handleMessage(final DlmsDeviceMessageMetadata messageMetadata,
            final Serializable requestObject) throws OsgpException, ProtocolAdapterException {
        final GMeterInfo gMeterInfo = (GMeterInfo) requestObject;
        return this.configurationService.setEncryptionKeyExchangeOnGMeter(messageMetadata, gMeterInfo);
    }
}