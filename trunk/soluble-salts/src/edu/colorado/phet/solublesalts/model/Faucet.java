/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.util.EventChannel;

import java.util.EventObject;
import java.util.EventListener;

/**
 * Faucet
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Faucet extends Particle {
    SolubleSaltsModel model;
    private double flow;
    private double maxFlow = 500;

    public Faucet( SolubleSaltsModel model ) {
        this.model = model;
    }

    public double getFlow() {
        return flow;
    }

    public void setFlow( double flow ) {
        this.flow = flow;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public double getMaxFlow() {
        return maxFlow;
    }

    public void setMaxFlow( double maxFlow ) {
        this.maxFlow = maxFlow;
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public void stepInTime( double dt ) {
        Vessel vessel = model.getVessel();
        double area = vessel.getWidth();
        double volume = vessel.getWaterLevel() + flow / area;
        vessel.setWaterLevel( volume );
    }

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener);
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener);
    }

    public class ChangeEvent extends EventObject {

        public ChangeEvent( Object source ) {
            super( source );
        }

        public Faucet getFaucet() {
            return (Faucet)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }

}
