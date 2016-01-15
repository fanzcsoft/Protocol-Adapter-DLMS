/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.joda.time.DateTime;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.LnClientConnection;
import org.openmuc.jdlms.ObisCode;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsQuery;

@Component()
public class GetActualMeterReadsCommandExecutor implements CommandExecutor<ActualMeterReadsQuery, ActualMeterReads> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetActualMeterReadsCommandExecutor.class);

    private static final int CLASS_ID_REGISTER = 3;
    private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_IMPORT = new ObisCode("1.0.1.8.0.255");
    private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_EXPORT = new ObisCode("1.0.2.8.0.255");
    private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_1 = new ObisCode("1.0.1.8.1.255");
    private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_2 = new ObisCode("1.0.1.8.2.255");
    private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_1 = new ObisCode("1.0.2.8.1.255");
    private static final ObisCode OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_2 = new ObisCode("1.0.2.8.2.255");
    private static final byte ATTRIBUTE_ID_VALUE = 2;

    private static final int CLASS_ID_CLOCK = 8;
    private static final ObisCode OBIS_CODE_CLOCK = new ObisCode("0.0.1.0.0.255");
    private static final byte ATTRIBUTE_ID_TIME = 2;

    private static final AttributeAddress[] ATTRIBUTE_ADDRESSES = {
        new AttributeAddress(CLASS_ID_CLOCK, OBIS_CODE_CLOCK, ATTRIBUTE_ID_TIME),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_1, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_IMPORT_RATE_2, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_1, ATTRIBUTE_ID_VALUE),
        new AttributeAddress(CLASS_ID_REGISTER, OBIS_CODE_ACTIVE_ENERGY_EXPORT_RATE_2, ATTRIBUTE_ID_VALUE) };

    private static final int INDEX_TIME = 0;
    private static final int INDEX_ACTIVE_ENERGY_IMPORT = 1;
    private static final int INDEX_ACTIVE_ENERGY_IMPORT_RATE_1 = 2;
    private static final int INDEX_ACTIVE_ENERGY_IMPORT_RATE_2 = 3;
    private static final int INDEX_ACTIVE_ENERGY_EXPORT = 4;
    private static final int INDEX_ACTIVE_ENERGY_EXPORT_RATE_1 = 5;
    private static final int INDEX_ACTIVE_ENERGY_EXPORT_RATE_2 = 6;

    @Autowired
    private DlmsHelperService dlmsHelperService;

    @Override
    public ActualMeterReads execute(final LnClientConnection conn, final ActualMeterReadsQuery actualMeterReadsQuery)
            throws IOException, TimeoutException, ProtocolAdapterException {

        if (actualMeterReadsQuery != null && actualMeterReadsQuery.isGas()) {
            throw new IllegalArgumentException("ActualMeterReadsQuery object for energy reads should not be about gas.");
        }

        LOGGER.info("Retrieving actual energy reads");

        final List<GetResult> getResultList = conn.get(ATTRIBUTE_ADDRESSES);

        checkResultList(getResultList);

        final DateTime time = this.dlmsHelperService.readDateTime(getResultList.get(INDEX_TIME),
                "Actual Energy Reads Time");
        if (time == null) {
            throw new ProtocolAdapterException("Unexpected null value for Actual Energy Reads Time");
        }
        final Long activeEnergyImport = this.dlmsHelperService.readLongNotNull(
                getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT), "Actual Energy Reads +A");
        final Long activeEnergyExport = this.dlmsHelperService.readLongNotNull(
                getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT), "Actual Energy Reads -A");
        final Long activeEnergyImportRate1 = this.dlmsHelperService.readLongNotNull(
                getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT_RATE_1), "Actual Energy Reads +A rate 1");
        final Long activeEnergyImportRate2 = this.dlmsHelperService.readLongNotNull(
                getResultList.get(INDEX_ACTIVE_ENERGY_IMPORT_RATE_2), "Actual Energy Reads +A rate 2");
        final Long activeEnergyExportRate1 = this.dlmsHelperService.readLongNotNull(
                getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT_RATE_1), "Actual Energy Reads -A rate 1");
        final Long activeEnergyExportRate2 = this.dlmsHelperService.readLongNotNull(
                getResultList.get(INDEX_ACTIVE_ENERGY_EXPORT_RATE_2), "Actual Energy Reads -A rate 2");

        return new ActualMeterReads(time.toDate(), activeEnergyImport, activeEnergyExport, activeEnergyImportRate1,
                activeEnergyImportRate2, activeEnergyExportRate1, activeEnergyExportRate2);
    }

    private static void checkResultList(final List<GetResult> getResultList) throws ProtocolAdapterException {
        if (getResultList.isEmpty()) {
            throw new ProtocolAdapterException("No GetResult received while retrieving actual energy meter reads.");
        }

        if (getResultList.size() != ATTRIBUTE_ADDRESSES.length) {
            LOGGER.info("Expected " + ATTRIBUTE_ADDRESSES.length
                    + " GetResults while retrieving actual energy meter reads, got " + getResultList.size());
        }
    }
}