/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging;

import org.apache.commons.lang3.StringUtils;

import com.alliander.osgp.dlms.DlmsPushNotificationAlarm;

public class DlmsLogItemRequestMessage {

    private static final int MAX_MESSAGE_LENGTH = 8000;

    private boolean incoming;

    private String encodedMessage;

    private String decodedMessage;

    private String deviceIdentification;

    private boolean valid;

    private int payloadMessageSerializedSize;

    public DlmsLogItemRequestMessage(final String deviceIdentification, final boolean incoming, final boolean valid,
            final DlmsPushNotificationAlarm message, final int payloadMessageSerializedSize) {
        this.deviceIdentification = deviceIdentification;
        this.incoming = incoming;
        this.valid = valid;
        this.payloadMessageSerializedSize = payloadMessageSerializedSize;

        // Truncate the log-items to max length.
        this.encodedMessage = StringUtils.substring(bytesToCArray(message.toByteArray()), 0, MAX_MESSAGE_LENGTH);
        this.decodedMessage = StringUtils.substring(message.toString(), 0, MAX_MESSAGE_LENGTH);
    }

    public Boolean isIncoming() {
        return this.incoming;
    }

    public String getEncodedMessage() {
        return this.encodedMessage;
    }

    public String getDecodedMessage() {
        return this.decodedMessage;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    private static String bytesToCArray(final byte[] bytes) {
        String s = "";
        if (bytes.length > 0) {
            s = javax.xml.bind.DatatypeConverter.printHexBinary(bytes);
            // Split every two chars with
            // ', ' to create a C array.
            s = s.replaceAll("(.{2})", ", 0x$1");
            // Remove the leading comma.
            s = s.substring(2);
        }
        return s;
    }

    public Boolean isValid() {
        return this.valid;
    }

    public int getPayloadMessageSerializedSize() {
        return this.payloadMessageSerializedSize;
    }
}
