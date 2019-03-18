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
package com.manolodominguez.opensimmpls.protocols;

/**
 * This class implements a IPv4 packet.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TIPv4PDU extends TAbstractPDU {

    /**
     * This method is the constructor of the class. It is create a new instance
     * of TIPv4PDU.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param id Packet identifier.
     * @param originIP IP addres of this packet's sender.
     * @param targetIP IP addres of this packet's receiver.
     * @param payloadSize The desired size for the payload of this IPv4 packet,
     * in bytes (octects).
     * @since 2.0
     */
    public TIPv4PDU(long id, String originIP, String targetIP, int payloadSize) {
        super(id, originIP, targetIP);
        this.TCPPayload = new TTCPPayload(payloadSize);
        this.subType = TAbstractPDU.IPV4;
    }

    /**
     * This method returns the size of the packet in bytes (octects).
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return Size of this packet in bytes (octects).
     * @since 2.0
     */
    @Override
    public int getSize() {
        return (super.getIPv4Header().getSize() + this.TCPPayload.getSize());
    }

    /**
     * This method returns the type of the packet, as defined by constants in
     * TAbstractPDU class.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The type of this packet.
     * @since 2.0
     */
    @Override
    public int getType() {
        return TAbstractPDU.IPV4;
    }

    /**
     * This method return the TCP payload of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return TCP payload of this packet.
     * @since 2.0
     */
    public TTCPPayload getTCPPayload() {
        return this.TCPPayload;
    }

    /**
     * This method set the TCP payload of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param TCPPayload The TCP payload for this packet.
     * @since 2.0
     */
    public void setTCPPayload(TTCPPayload TCPPayload) {
        this.TCPPayload = TCPPayload;
    }

    /**
     * This method gets the IPv4 header of this packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The IPv4 header of this packet.
     * @since 2.0
     */
    @Override
    public TIPv4Header getIPv4Header() {
        return super.getIPv4Header();
    }

    /**
     * This method returns the subtype of the packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @return The subtype of this packet. For instances of this class, it
     * returns IPV4, as defined in TAbstractPDU.
     * @since 2.0
     */
    @Override
    public int getSubtype() {
        return this.subType;
    }

    /**
     * This method has to be implemented by any subclasses. It has to allow
     * setting the subtype of the packet.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @param subtype The subtype of the packet.
     * @since 2.0
     */
    @Override
    public void setSubtype(int subtype) {
        this.subType = subtype;
    }

    private int subType;
    private TTCPPayload TCPPayload;
}
