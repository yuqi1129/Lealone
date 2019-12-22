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
package org.lealone.server.protocol;

import java.util.HashMap;

public class PacketDecoders {

    private static HashMap<Integer, PacketDecoder<? extends Packet>> decoders = new HashMap<>();

    public static void register(PacketType type, PacketDecoder<? extends Packet> decoder) {
        decoders.put(type.value, decoder);
    }

    public static PacketDecoder<? extends Packet> getDecoder(PacketType type) {
        return decoders.get(type.value);
    }

    public static PacketDecoder<? extends Packet> getDecoder(int type) {
        return decoders.get(type);
    }

    static {
        register(PacketType.COMMAND_REPLICATION_COMMIT, ReplicationCommit.decoder);
        register(PacketType.COMMAND_REPLICATION_ROLLBACK, ReplicationRollback.decoder);
        register(PacketType.COMMAND_REPLICATION_CHECK_CONFLICT, ReplicationCheckConflict.decoder);
        register(PacketType.COMMAND_REPLICATION_CHECK_CONFLICT_ACK, ReplicationCheckConflictAck.decoder);
        register(PacketType.COMMAND_REPLICATION_HANDLE_CONFLICT, ReplicationHandleConflict.decoder);

        register(PacketType.COMMAND_DISTRIBUTED_TRANSACTION_COMMIT, DistributedTransactionCommit.decoder);
        register(PacketType.COMMAND_DISTRIBUTED_TRANSACTION_ROLLBACK, DistributedTransactionRollback.decoder);
        register(PacketType.COMMAND_DISTRIBUTED_TRANSACTION_ADD_SAVEPOINT, DistributedTransactionAddSavepoint.decoder);
        register(PacketType.COMMAND_DISTRIBUTED_TRANSACTION_ROLLBACK_SAVEPOINT,
                DistributedTransactionRollbackSavepoint.decoder);
        register(PacketType.COMMAND_DISTRIBUTED_TRANSACTION_VALIDATE, DistributedTransactionValidate.decoder);
        register(PacketType.COMMAND_DISTRIBUTED_TRANSACTION_VALIDATE_ACK, DistributedTransactionValidateAck.decoder);

        register(PacketType.COMMAND_STORAGE_PUT, StoragePut.decoder);
        register(PacketType.COMMAND_STORAGE_PUT_ACK, StoragePutAck.decoder);
        register(PacketType.COMMAND_STORAGE_APPEND, StorageAppend.decoder);
        register(PacketType.COMMAND_STORAGE_APPEND_ACK, StorageAppendAck.decoder);
        register(PacketType.COMMAND_STORAGE_GET, StorageGet.decoder);
        register(PacketType.COMMAND_STORAGE_GET_ACK, StorageGetAck.decoder);
        register(PacketType.COMMAND_STORAGE_PREPARE_MOVE_LEAF_PAGE, StoragePrepareMoveLeafPage.decoder);
        register(PacketType.COMMAND_STORAGE_PREPARE_MOVE_LEAF_PAGE_ACK, StoragePrepareMoveLeafPageAck.decoder);
        register(PacketType.COMMAND_STORAGE_MOVE_LEAF_PAGE, StorageMoveLeafPage.decoder);
        register(PacketType.COMMAND_STORAGE_REPLICATE_ROOT_PAGES, StorageReplicateRootPages.decoder);
        register(PacketType.COMMAND_STORAGE_READ_PAGE, StorageReadPage.decoder);
        register(PacketType.COMMAND_STORAGE_READ_PAGE_ACK, StorageReadPageAck.decoder);
        register(PacketType.COMMAND_STORAGE_REMOVE_LEAF_PAGE, StorageRemoveLeafPage.decoder);
    }
}
