/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lealone.p2p.locator;

import java.net.UnknownHostException;

import org.lealone.common.logging.Logger;
import org.lealone.common.logging.LoggerFactory;
import org.lealone.net.NetNode;
import org.lealone.p2p.gossip.ApplicationState;
import org.lealone.p2p.gossip.INodeStateChangeSubscriber;
import org.lealone.p2p.gossip.NodeState;
import org.lealone.p2p.gossip.VersionedValue;
import org.lealone.p2p.server.MessagingService;

/**
 * Sidekick helper for snitches that want to reconnect from one IP addr for a node to another.
 * Typically, this is for situations like EC2 where a node will have a public address and a private address,
 * where we connect on the public, discover the private, and reconnect on the private.
 */
public class ReconnectableSnitchHelper implements INodeStateChangeSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(ReconnectableSnitchHelper.class);
    private final INodeSnitch snitch;
    private final String localDc;
    private final boolean preferLocal;

    public ReconnectableSnitchHelper(INodeSnitch snitch, String localDc, boolean preferLocal) {
        this.snitch = snitch;
        this.localDc = localDc;
        this.preferLocal = preferLocal;
    }

    private void reconnect(NetNode publicAddress, VersionedValue localAddressValue) {
        try {
            NetNode localAddress = NetNode.getByName(localAddressValue.value);

            if (snitch.getDatacenter(publicAddress).equals(localDc)
                    && MessagingService.instance().getVersion(publicAddress) == MessagingService.CURRENT_VERSION
                    && !MessagingService.instance().getConnectionNode(publicAddress).equals(localAddress)) {

                MessagingService.instance().reconnect(publicAddress, localAddress);

                if (logger.isDebugEnabled())
                    logger.debug(String.format("Intiated reconnect to an Internal IP %s for the %s", localAddress,
                            publicAddress));
            }
        } catch (UnknownHostException e) {
            logger.error("Error in getting the IP address resolved: ", e);
        }
    }

    @Override
    public void onChange(NetNode node, ApplicationState state, VersionedValue value) {
        if (preferLocal && state == ApplicationState.INTERNAL_IP)
            reconnect(node, value);
    }

    @Override
    public void onJoin(NetNode node, NodeState epState) {
        if (preferLocal && epState.getApplicationState(ApplicationState.INTERNAL_IP) != null)
            reconnect(node, epState.getApplicationState(ApplicationState.INTERNAL_IP));
    }

    @Override
    public void onAlive(NetNode node, NodeState state) {
        onJoin(node, state);
    }
}
