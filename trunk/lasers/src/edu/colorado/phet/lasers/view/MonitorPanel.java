/**
 * Class: MonitorPanel
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Oct 20, 2004
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.photon.Photon;

import javax.swing.*;

public abstract class MonitorPanel extends JPanel implements SimpleObserver {
    protected int numGroundLevel;
    protected int numMiddleLevel;
    protected int numHighLevel;

    public void photonEmitted( Atom atom, Photon photon ) {
    }

    public void leftSystem( Atom atom ) {
        //        AtomicState state = atom.getState();
        //        if( state instanceof GroundState ) {
        //            numGroundLevel--;
        //        }
        //        if( state instanceof MiddleEnergyState ) {
        //            numMiddleLevel--;
        //        }
    }

    public void stateChanged( Atom atom, AtomicState oldState, AtomicState newState ) {
        //        if( oldState instanceof GroundState ) {
        //            numGroundLevel--;
        //        }
        //        if( oldState instanceof MiddleEnergyState ) {
        //            numMiddleLevel--;
        //        }
        //        if( newState instanceof GroundState ) {
        //            numGroundLevel++;
        //        }
        //        if( newState instanceof MiddleEnergyState ) {
        //            numMiddleLevel++;
        //        }
    }
}
