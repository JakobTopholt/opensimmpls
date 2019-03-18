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

import java.util.TreeSet;
import com.manolodominguez.opensimmpls.protocols.TAbstractPDU;
import com.manolodominguez.opensimmpls.protocols.TMPLSPDU;
import com.manolodominguez.opensimmpls.commons.TRotaryIDGenerator;
import com.manolodominguez.opensimmpls.commons.TLock;
import com.manolodominguez.opensimmpls.commons.UnitsTranslations;

/**
 * This class implements a DMGP memory to save GoS-aware PDUs temporarily.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TDMGP {

    /**
     * This method is the class constructor. It creates a new instance of TDMGP
     * and initialize its attributes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TDMGP() {
        this.lock = new TLock();
        this.idGenerator = new TRotaryIDGenerator();
        this.flows = new TreeSet<>();
        this.totalAvailablePercentage = DEFAULT_TOTAL_AVAILABLE_PERCENTAGE;
        this.totalDMGPSizeInKB = DEFAULT_TOTAL_DMGP_SIZE_IN_KB;
        this.totalAssignedOctects = DEFAULT_TOTAL_ASSIGNED_OCTECTS;
    }

    /**
     * This method establish the global size of DMGP in kilobytes.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param totalDMGPSizeInKB Size in kilobytes.
     */
    public void setDMGPSizeInKB(int totalDMGPSizeInKB) {
        this.totalDMGPSizeInKB = totalDMGPSizeInKB;
        this.reset();
    }

    /**
     * This method obtains the globals soze of DMGP in kilobites.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Size in kilobytes.
     * @since 2.0
     */
    public int getDMGPSizeInKB() {
        return this.totalDMGPSizeInKB;
    }

    /**
     * This method look for a packet tagged as GoS within the DMGP memory.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param flowID Identifier of the flow the packet belongs to.
     * @param packetID Identifier of the packet.
     * @return The packet, if in the DMGP. NULL on the contrary.
     * @since 2.0
     */
    public TMPLSPDU getPacket(int flowID, int packetID) {
        TMPLSPDU wantedPacket = null;
        TDMGPFlowEntry requestedDMGPFlowEntry = this.getFlow(flowID);
        // If the requested flowID is already created...
        if (requestedDMGPFlowEntry != null) {
            this.lock.lock();
            for (TDMGPEntry dmgpEntry : requestedDMGPFlowEntry.getEntries()) {
                if (dmgpEntry.getPacketID() == packetID) {
                    wantedPacket = dmgpEntry.getPacket();
                    this.lock.unLock();
                    return wantedPacket;
                }
            }
            this.lock.unLock();
        }
        return null;
    }

    /**
     * This method insert a new GoS packet into the DMGP memory.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param packet The packet to be inserted into the DMGP memory.
     * @since 2.0
     */
    public void addPacket(TMPLSPDU packet) {
        TDMGPFlowEntry dmgpFlowEntry = this.getFlow(packet);
        if (dmgpFlowEntry == null) { // The correponding flow, dows not exists
            dmgpFlowEntry = this.createFlow(packet);
        }
        if (dmgpFlowEntry != null) { // The corresponding flow already exists
            dmgpFlowEntry.addPacket(packet);
        } else {
            packet = null;
        }
    }

    /**
     * This method restores the value of all attributes as when created by the
     * constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public void reset() {
        this.lock = null;
        this.idGenerator = null;
        this.flows = null;
        this.lock = new TLock();
        this.idGenerator = new TRotaryIDGenerator();
        this.flows = new TreeSet<>();
        this.totalAvailablePercentage = DEFAULT_TOTAL_AVAILABLE_PERCENTAGE;
        this.totalAssignedOctects = DEFAULT_TOTAL_ASSIGNED_OCTECTS;
    }

    private int getDMGPSizeInOctects() {
        return (this.totalDMGPSizeInKB * UnitsTranslations.OCTETS_PER_KILOBYTE.getUnits());
    }

    private TDMGPFlowEntry getFlow(TAbstractPDU packet) {
        TDMGPFlowEntry dmgpFlowEntry = null;
        int flowID = packet.getIPv4Header().getOriginIPv4Address().hashCode();
        dmgpFlowEntry = getFlow(flowID);
        return dmgpFlowEntry;
    }

    private TDMGPFlowEntry getFlow(int flowID) {
        this.lock.lock();
        for (TDMGPFlowEntry dmgpFlowEntry : this.flows) {
            if (dmgpFlowEntry.getFlowID() == flowID) {
                this.lock.unLock();
                return dmgpFlowEntry;
            }
        }
        this.lock.unLock();
        return null;
    }

    private TDMGPFlowEntry createFlow(TAbstractPDU packet) {
        this.lock.lock();
        TDMGPFlowEntry dmgpFlowEntry = null;
        int flowID = packet.getIPv4Header().getOriginIPv4Address().hashCode();
        int percentageToBeAssigned = ZERO;
        int octectsToBeAssigned = ZERO;
        if (this.totalAssignedOctects < this.getDMGPSizeInOctects()) {
            percentageToBeAssigned = this.getPercentageToBeAssigned(packet);
            octectsToBeAssigned = this.getOctectsToBeAssigned(packet);
            if (octectsToBeAssigned > ZERO) {
                this.totalAssignedOctects += octectsToBeAssigned;
                this.totalAvailablePercentage -= percentageToBeAssigned;
                dmgpFlowEntry = new TDMGPFlowEntry(this.idGenerator.getNextIdentifier());
                dmgpFlowEntry.setFlowID(flowID);
                dmgpFlowEntry.setAssignedPercentage(percentageToBeAssigned);
                dmgpFlowEntry.setAssignedOctects(octectsToBeAssigned);
                flows.add(dmgpFlowEntry);
                this.lock.unLock();
                return dmgpFlowEntry;
            }
        }
        this.lock.unLock();
        return null;
    }

    private int getOctectsToBeAssigned(TAbstractPDU packet) {
        int reservedPercentage = getRequestedPercentage(packet);
        int reservedOctects = ZERO;
        if (this.totalAvailablePercentage > ZERO) {
            if (this.totalAvailablePercentage > reservedPercentage) {
                reservedOctects = ((this.getDMGPSizeInOctects() * reservedPercentage) / ONE_HUNDRED);
                return reservedOctects;
            } else {
                reservedOctects = this.getDMGPSizeInOctects() - this.totalAssignedOctects;
                return reservedOctects;
            }
        }
        return ZERO;
    }

    private int getPercentageToBeAssigned(TAbstractPDU packet) {
        int reservedPercentage = getRequestedPercentage(packet);
        if (this.totalAvailablePercentage > ZERO) {
            if (this.totalAvailablePercentage > reservedPercentage) {
                return reservedPercentage;
            } else {
                return this.totalAvailablePercentage;
            }
        }
        return ZERO;
    }

    private int getRequestedPercentage(TAbstractPDU packet) {
        int packetGoSLevel = ZERO;
        if (packet.getIPv4Header().getOptionsField().isUsed()) {
            packetGoSLevel = packet.getIPv4Header().getOptionsField().getRequestedGoSLevel();
            // The following values have been defined by design. See "Guarantee
            // of Service (GoS) support over MPLS using Active Techniques"
            // proposal. They determines the number of GoS flows that can be 
            // using the DMGP in a given moment concurrently
            switch (packetGoSLevel) {
                case TAbstractPDU.EXP_LEVEL3_WITH_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS3;
                case TAbstractPDU.EXP_LEVEL3_WITHOUT_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS3;
                case TAbstractPDU.EXP_LEVEL2_WITH_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS2;
                case TAbstractPDU.EXP_LEVEL2_WITHOUT_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS2;
                case TAbstractPDU.EXP_LEVEL1_WITH_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS1;
                case TAbstractPDU.EXP_LEVEL1_WITHOUT_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS1;
                case TAbstractPDU.EXP_LEVEL0_WITH_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS0;
                case TAbstractPDU.EXP_LEVEL0_WITHOUT_BACKUP_LSP:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS0;
                default:
                    return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS0;
            }
        } else {
            return PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS0;
        }
    }

    private TLock lock;
    private TRotaryIDGenerator idGenerator;
    private TreeSet<TDMGPFlowEntry> flows;
    private int totalAvailablePercentage;
    private int totalDMGPSizeInKB;
    private int totalAssignedOctects;

    private static final int DEFAULT_TOTAL_AVAILABLE_PERCENTAGE = 100;
    private static final int DEFAULT_TOTAL_DMGP_SIZE_IN_KB = 1;
    private static final int DEFAULT_TOTAL_ASSIGNED_OCTECTS = 0;
    private static final int ZERO = 0;
    private static final int ONE_HUNDRED = 100;
    private static final int PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS0 = 0;
    private static final int PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS1 = 4;
    private static final int PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS2 = 8;
    private static final int PERCENTAGE_OF_DMGP_RESERVED_FOR_GOS3 = 12;
}
