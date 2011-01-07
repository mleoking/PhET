// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import java.util.EventListener;
import java.util.EventObject;

import edu.colorado.phet.common.phetcommon.model.Particle;
import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;

/**
 * Spigot
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Spigot extends Particle {

    private SolubleSaltsModel model;
    private double flow;
    private double maxFlow = SolubleSaltsConfig.MAX_SPIGOT_FLOW;
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener) changeEventChannel.getListenerProxy();

    public Spigot( SolubleSaltsModel model ) {
        this.model = model;
    }

    protected SolubleSaltsModel getModel() {
        return model;
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

    //----------------------------------------------------------------
    // Events and listeners
    //----------------------------------------------------------------

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {

        public ChangeEvent( Spigot source ) {
            super( source );
        }

        public Spigot getSpigot() {
            return (Spigot) getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }
}
