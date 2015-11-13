package org.osgp.adapter.protocol.dlms.domain.commands;

import java.io.IOException;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

/**
 * Interface for executing a command on a smart meter over a client connection,
 * taking input of type <T>.
 *
 * @param <T>
 *            the type of object used as input for executing a command.
 * @param <R>
 *            the type of object returned as a result from executing a command.
 */
public interface CommandExecutor<T, R> {

    R execute(ClientConnection conn, T object) throws IOException, ProtocolAdapterException;

}