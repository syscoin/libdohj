/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.libdohj.params;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;

import java.math.BigInteger;

import static com.google.common.base.Preconditions.checkState;

/**
 * Network parameters for the regression test mode of bitcoind in which all blocks are trivially solvable.
 */
public class SyscoinRegTestParams extends SyscoinTestNet3Params {
    private static final BigInteger MAX_TARGET = new BigInteger("7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);

    public SyscoinRegTestParams() {
        super();
        // Difficulty adjustments are disabled for regtest.
        // By setting the block interval for difficulty adjustments to Integer.MAX_VALUE we make sure difficulty never changes.
        interval = Integer.MAX_VALUE;
        maxTarget = MAX_TARGET;
        subsidyDecreaseBlockCount = 150;
        port = 18444;
        id = ID_SYSCOIN_REGTEST;
        packetMagic = 0xfabfb5da;
        nBridgeStartBlock = 100;
    }

    @Override
    public boolean allowEmptyPeerChain() {
        return true;
    }

    private static Block genesis;

    @Override
    public Block getGenesisBlock() {
        synchronized (SyscoinRegTestParams.class) {
            if (genesis == null) {
                genesis = super.getGenesisBlock();
                genesis.setNonce(3);
                genesis.setDifficultyTarget(0x207fffffL);
                genesis.setTime(1553040331L);
                segwitAddressHrp = "sysrt";
                checkState(genesis.getVersion() == 1);
                checkState(genesis.getHashAsString().toLowerCase().equals("28a2c2d251f46fac05ade79085cbcb2ae4ec67ea24f1f1c7b40a348c00521194"));
                genesis.verifyHeader();
            }
            return genesis;
        }
    }

    private static SyscoinRegTestParams instance;

    public static synchronized SyscoinRegTestParams get() {
        if (instance == null) {
            instance = new SyscoinRegTestParams();
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return ID_SYSCOIN_REGTEST;
    }

    @Override
    /** the testnet rules don't work for regtest, where difficulty stays the same */
    public void checkDifficultyTransitions(StoredBlock storedPrev, Block nextBlock, BlockStore blockStore)
            throws VerificationException {
        final Block prev = storedPrev.getHeader();
        if (nextBlock.getDifficultyTarget() != prev.getDifficultyTarget())
            throw new VerificationException("Network provided difficulty bits do not match what was calculated: " +
                    Long.toHexString(nextBlock.getDifficultyTarget()) + " vs " + Long.toHexString(prev.getDifficultyTarget()));
    }


    @Override
    public boolean isTestNet() {
        return false;
    }
}