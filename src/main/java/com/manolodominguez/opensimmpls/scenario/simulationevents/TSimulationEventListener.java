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
package com.manolodominguez.opensimmpls.scenario.simulationevents;

import java.util.Iterator;
import java.util.TreeSet;
import com.manolodominguez.opensimmpls.gui.simulator.JSimulationPanel;
import com.manolodominguez.opensimmpls.commons.TLock;

/**
 * This class implements a simulation event listener that will receive
 * simulation events.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TSimulationEventListener implements ISimulationEventListener {

    /**
     * This method is the constructor of the class. It creates a new instance of
     * TSimulationEventListener.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     */
    public TSimulationEventListener() {
        this.simulationEventsBuffer = new TreeSet();
        this.eventsLock = new TLock();
        this.simulationPanel = null;
    }

    /**
     * This method establishes the simulation panel where collected simulation
     * events will be displayed. It connects the generated events to the GUI.
     *
     * @since 2.0
     * @param simulationPanel The simulation panel where collected simulation
     * events will be displayed.
     */
    public synchronized void setSimulationPanel(JSimulationPanel simulationPanel) {
        this.simulationPanel = simulationPanel;
    }

    /**
     * This method, when implemented, will capture simulation events. It also
     * delivers them to the simulation panel to be displayed.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     * @param simulationEvent The simulation event received.
     */
    @Override
    public synchronized void captureSimulationEvents(TSimulationEvent simulationEvent) {
        // FIX: If nothing different is done... is it neccesary a switch?
        switch (simulationEvent.getSubtype()) {
            case TSimulationEvent.PACKET_GENERATED: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.PACKET_SENT: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.PACKET_RECEIVED: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.PACKET_SWITCHED: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.PACKET_DISCARDED: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.PACKET_ON_FLY: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.PACKET_ROUTED: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.LSP_ESTABLISHED: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.LSP_REMOVED: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.LABEL_ASSIGNED: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.LABEL_DENIED: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.LABEL_REMOVED: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.LABEL_RECEIVED: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.LABEL_REQUESTED: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.NODE_CONGESTED: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.LINK_BROKEN: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
            case TSimulationEvent.LINK_RECOVERED: {
                this.simulationPanel.addEvent(simulationEvent);
                break;
            }
        }
    }

    /**
     * This method reset to value of the class attributes to their original
     * values, as when created by the constructor.
     *
     * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
     * @since 2.0
     *
     */
    public void reset() {
        this.eventsLock.lock();
        Iterator iterator = this.simulationEventsBuffer.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        this.eventsLock.unLock();
    }

    private TLock eventsLock;
    private TreeSet simulationEventsBuffer;
    private JSimulationPanel simulationPanel;
}
