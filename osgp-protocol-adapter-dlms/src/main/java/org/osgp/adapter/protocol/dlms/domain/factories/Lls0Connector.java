/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.factories;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.openmuc.jdlms.settings.client.ReferencingMethod;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ConnectionException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;

import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

public class Lls0Connector extends DlmsConnector {

    protected final int responseTimeout;

    protected final int logicalDeviceAddress;

    protected final int clientAccessPoint;

    public Lls0Connector(final int responseTimeout, final int logicalDeviceAddress) {
        this(responseTimeout, logicalDeviceAddress, DlmsConnector.DLMS_PUBLIC_CLIENT_ID);
    }

    public Lls0Connector(final int responseTimeout, final int logicalDeviceAddress, final int clientAccessPoint) {
        this.responseTimeout = responseTimeout;
        this.logicalDeviceAddress = logicalDeviceAddress;
        this.clientAccessPoint = clientAccessPoint;
    }

    @Override
    public DlmsConnection connect(final DlmsDevice device, final DlmsMessageListener dlmsMessageListener)
            throws TechnicalException {

        // Make sure neither device or device.getIpAddress() is null.
        this.checkDevice(device);
        this.checkIpAddress(device);

        // Setup connection to device
        TcpConnectionBuilder tcpConnectionBuilder;
        try {

            tcpConnectionBuilder = new TcpConnectionBuilder(InetAddress.getByName(device.getIpAddress()))
                    .setResponseTimeout(this.responseTimeout).setLogicalDeviceId(this.logicalDeviceAddress)
                    .setClientId(this.clientAccessPoint)
                    .setReferencingMethod(device.isUseSn() ? ReferencingMethod.SHORT : ReferencingMethod.LOGICAL);

            if (device.isUseHdlc()) {
                tcpConnectionBuilder.useHdlc();
            }
        } catch (final UnknownHostException e) {
            throw new ConnectionException(e);
        }

        this.setOptionalValues(device, tcpConnectionBuilder);

        if (device.isInDebugMode()) {
            tcpConnectionBuilder.setRawMessageListener(dlmsMessageListener);
        }

        try {
            return tcpConnectionBuilder.build();
        } catch (final IOException e) {
            throw new ConnectionException(e);
        }
    }
}