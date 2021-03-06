/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ftpserver.command.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.apache.ftpserver.DataConnectionException;
import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.impl.ServerDataConnectionFactory;
import org.apache.ftpserver.util.SocketAddressEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * 
 * <code>PASV &lt;CRLF&gt;</code><br>
 * 
 * This command requests the server-DTP to "listen" on a data port (which is not
 * its default data port) and to wait for a connection rather than initiate one
 * upon receipt of a transfer command. The response to this command includes the
 * host and port address this server is listening on.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a> 
 */
public class PASV extends AbstractCommand {

    private final Logger LOG = LoggerFactory.getLogger(PASV.class);

    /**
     * Execute command
     */
    public void execute(final FtpIoSession session,
            final FtpServerContext context, final FtpRequest request)
            throws IOException, FtpException {

        // reset state variables
        session.resetState();

        // set data connection
         ServerDataConnectionFactory dataCon = session.getDataConnection();
         String externalPassiveAddress = session.getListener()
         .getDataConnectionConfiguration().getPassiveExernalAddress(); 
        
        try {
        	
        	InetSocketAddress dataConAddress = dataCon
                    .initPassiveDataConnection();

            // get connection info
            InetAddress servAddr;
            if (externalPassiveAddress != null) {
                servAddr = resolveAddress(externalPassiveAddress);
            } else {
                servAddr = dataConAddress.getAddress();
            }

            // send connection info to client
            InetSocketAddress externalDataConAddress = new InetSocketAddress(
                    servAddr, dataConAddress.getPort());

            String addrStr = SocketAddressEncoder
                    .encode(externalDataConAddress);
            session.write(LocalizedFtpReply.translate(session, request, context,
                    FtpReply.REPLY_227_ENTERING_PASSIVE_MODE, "PASV", addrStr));
        } catch (DataConnectionException e) {
            LOG.warn("Failed to open passive data connection", e);
            session
                    .write(LocalizedFtpReply.translate(session, request, context,
                            FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION,
                            "PASV", null));
            return;
        }

    }
    /*
     *  (non-Javadoc)
     *   Returns an InetAddress object from a hostname or IP address.
     */
    private InetAddress resolveAddress(String host) throws DataConnectionException{
    	try{
    		return InetAddress.getByName(host);
    	}catch(UnknownHostException ex){
    		throw new DataConnectionException(ex.getLocalizedMessage(),ex);
    	}
    }
}
