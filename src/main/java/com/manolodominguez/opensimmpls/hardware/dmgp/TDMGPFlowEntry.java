/* 
 * Copyright (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manolodominguez.opensimmpls.hardware.dmgp;

import java.util.Iterator;
import java.util.TreeSet;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.commons.TRotaryIDGenerator;
import com.manolodominguez.opensimmpls.commons.TLock;

/**
 * This class implements a flow entry for the DMGP memory.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TDMGPFlowEntry implements Comparable<TDMGPFlowEntry> {

    /**
     * This method is the constructor. It creates a new TDMGPFlowEntry instance.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param arrivalOrder Incoming arrivalOrder of the flow to the DMGP memory.
     * @since 2.0
     */
    public TDMGPFlowEntry(int arrivalOrder) {
        this.arrivalOrder = arrivalOrder;
        this.flowID = DEFAULT_FLOWID;
        this.assignedPercentage = DEFAULT_ASSIGNED_PERCENTAGE;
        this.assignedOctects = DEFAULT_ASSIGNED_OCTECTS;
        this.usedOctects = DEFAULT_USED_OCTECTS;
        this.entries = new TreeSet<>();
        this.lock = new TLock();
        this.idGenerator = new TRotaryIDGenerator();
    }

    /**
     * This method establishes the flow identifier associated to this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param flowID The flow identifier.
     * @since 2.0
     */
    public void setFlowID(int flowID) {
        this.flowID = flowID;
    }

    /**
     * This method returns the identifier of the flow assigned to this entry.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The flow identifier.
     * @since 2.0
     */
    public int getFlowID() {
        return this.flowID;
    }

    /**
     * This method establishes the percentage of DMGP assigned to this flow.
     *
     * @param assignedPercentage Percentage of DMGP assigned to this flow.
     * @since 2.0
     */
    public void setAssignedPercentage(int assignedPercentage) {
        this.assignedPercentage = assignedPercentage;
    }

    /**
     * This method obtains the percentage of DMGP assigned to this flow..
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Percentage of DMGP assigned to this flow.
     * @since 2.0
     */
    public int getAssignedPercentage() {
        return this.assignedPercentage;
    }

    /**
     * This method establishes the number of DMGP octects assigned to this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param assignedOctects Number of DMGP octects assigned to this flow.
     * @since 2.0
     */
    public void setAssignedOctects(int assignedOctects) {
        this.assignedOctects = assignedOctects;
    }

    /**
     * This method obtains the number of DMGP octects assigned to this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The number of DMGP octects assigned to this flow.
     * @since 2.0
     */
    public int getAssignedOctects() {
        return this.assignedOctects;
    }

    /**
     * This method establishes the number of DMGP octects currently used by the
     * flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param usedOctects Number of DMGP octects currently used by the flow.
     * @since 2.0
     */
    public void setUsedOctects(int usedOctects) {
        this.usedOctects = usedOctects;
    }

    /**
     * This method obtains the number of DMGP octects currently used by the
     * flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Number of DMGP octects currently used by the flow.
     * @since 2.0
     */
    public int getUsedOctects() {
        return this.usedOctects;
    }

    /**
     * This method obtains the tree that contains all the packets of this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The tree containing all the packets of this flow.
     * @since 2.0
     */
    public TreeSet<TDMGPEntry> getEntries() {
        return this.entries;
    }

    /**
     * This method contains the arrivalOrder of incoming to the DMGP.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The arrival order of this flow in relation to others in the
     * global DMGP.
     * @since 2.0
     */
    public int getArrivalOrder() {
        return this.arrivalOrder;
    }

    /**
     * This method returns the monitor of this flow.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @return The monitor of this flow.
     */
    public TLock getMonitor() {
        return this.lock;
    }

    private void releaseMemory(int octectsToBeReleased) {
        int releasedOctects = ZERO;
        Iterator<TDMGPEntry> entriesIterator = this.entries.iterator();
        TDMGPEntry dmgpEntry = null;
        while ((entriesIterator.hasNext()) && (releasedOctects < octectsToBeReleased)) {
            dmgpEntry = entriesIterator.next();
            releasedOctects += dmgpEntry.getPacket().getSize();
            entriesIterator.remove();
        }
        this.usedOctects -= releasedOctects;
    }

    /**
     * This method inserts a packet that belongs to this flow, in the tree of
     * packets. If there is available space, the packet is inserted. Otherwise
     * packets are reselased untill there are space. If it is not possible even
     * releasing packets, the packet is not inserted (and the DMGP for this
     * packets remains intact).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param mplsPacket Packet belonging to this flow to be inserted in the
     * DMGP.
     * @since 2.0
     */
    public void addPacket(TMPLSPDU mplsPacket) {
        this.lock.lock();
        int availableOctects = this.assignedOctects - this.usedOctects;
        if (this.assignedOctects >= mplsPacket.getSize()) {
            if (availableOctects >= mplsPacket.getSize()) {
                TDMGPEntry dmgpEntry = new TDMGPEntry(idGenerator.getNextIdentifier());
                dmgpEntry.setPacket(mplsPacket);
                this.usedOctects += mplsPacket.getSize();
                this.entries.add(dmgpEntry);
            } else {
                releaseMemory(mplsPacket.getSize() - availableOctects);
                TDMGPEntry dmgpEntry = new TDMGPEntry(idGenerator.getNextIdentifier());
                dmgpEntry.setPacket(mplsPacket);
                this.usedOctects += mplsPacket.getSize();
                this.entries.add(dmgpEntry);
            }
        } else {
            mplsPacket = null;
        }
        this.lock.unLock();
    }

    /**
     * This method compares this flow entry with another of the same type.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param anotherDMGPFlowEntry The entry to be compared with.
     * @return -1, 0, 1, depending on whether the current instance is lesser,
     * equal or greater than the instance passed as an argument. In terms of
     * shorting.
     * @since 2.0
     */
    @Override
    public int compareTo(TDMGPFlowEntry anotherDMGPFlowEntry) {
        if (this.arrivalOrder < anotherDMGPFlowEntry.getArrivalOrder()) {
            return TDMGPFlowEntry.THIS_LOWER;
        }
        if (this.arrivalOrder > anotherDMGPFlowEntry.getArrivalOrder()) {
            return TDMGPFlowEntry.THIS_GREATER;
        }
        return TDMGPFlowEntry.THIS_EQUAL;
    }

    private static final int THIS_LOWER = -1;
    private static final int THIS_EQUAL = 0;
    private static final int THIS_GREATER = 1;

    private static final int ZERO = 0;
    private static final int DEFAULT_FLOWID = -1;
    private static final int DEFAULT_ASSIGNED_PERCENTAGE = 0;
    private static final int DEFAULT_ASSIGNED_OCTECTS = 0;
    private static final int DEFAULT_USED_OCTECTS = 0;

    private final int arrivalOrder;
    private int flowID;
    private int assignedPercentage;
    private int assignedOctects;
    private int usedOctects;
    private final TreeSet<TDMGPEntry> entries;
    private final TLock lock;
    private final TRotaryIDGenerator idGenerator;
}
