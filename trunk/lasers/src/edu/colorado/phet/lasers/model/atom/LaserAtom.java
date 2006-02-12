package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.quantum.model.*;
import edu.colorado.phet.lasers.model.LaserModel;

/**
 * Created by IntelliJ IDEA.
 * User: Ron LeMaster
 * Date: Feb 12, 2006
 * Time: 2:27:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class LaserAtom extends PropertiesBasedAtom {

    public static long stimulationPreventionTime = 00;

    public static void setStimulationPreventionTime( long time ) {
        stimulationPreventionTime = time;
    }

    private Boolean canBeStimulated = Boolean.TRUE;

    public LaserAtom(LaserModel model, ElementProperties elementProperties) {
        super(model, elementProperties);
    }

    public LaserAtom(LaserModel model, AtomicState[] states) {
        super(model, states);
    }

    public synchronized void setCurrState(final AtomicState newState) {
        super.setCurrState(newState);
        if (getCurrState() == getGroundState()) {
            new StimulationPreventionAgent().start();
        } else {
            canBeStimulated = Boolean.TRUE;
        }
    }

    public synchronized void collideWithPhoton(Photon photon) {
        if (canBeStimulated.booleanValue()) {
            super.collideWithPhoton(photon);
        }
    }

    private class StimulationPreventionAgent extends Thread {

        public void run() {
            super.run();
            synchronized( canBeStimulated ) {
                canBeStimulated = Boolean.FALSE;
            }
            try {
                Thread.sleep( stimulationPreventionTime );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized( canBeStimulated ) {
                canBeStimulated = Boolean.TRUE;
            }
        }
    }
}
