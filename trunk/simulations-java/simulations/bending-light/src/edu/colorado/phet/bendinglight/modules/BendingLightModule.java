// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules;

import fj.F;
import fj.F3;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.umd.cs.piccolo.PCanvas;

/**
 * Base class for the various bending light modules.
 *
 * @author Sam Reid
 */
public class BendingLightModule<T extends BendingLightModel> extends Module {
    private T model;
    protected final BooleanProperty moduleActive = new BooleanProperty( isActive() );//Keep track of whether the module is active for making sure only one clock is running at a time
    protected final Resettable resetAll = new Resettable() {
        public void reset() {
            resetAll();
        }
    };
    private final F<ConstantDtClock, T> createModel;
    private final F3<T, BooleanProperty, Resettable, PCanvas> createSimulationPanel;

    public BendingLightModule( String name, ConstantDtClock clock, F<ConstantDtClock, T> createModel, F3<T, BooleanProperty, Resettable, PCanvas> createSimulationPanel ) {
        super( name, clock );
        this.createModel = createModel;
        this.createSimulationPanel = createSimulationPanel;
        this.model = createModel.f( clock );

        setClockControlPanel( null );//Clock control is floating in the play area, and only visible when in wave mode
        getModulePanel().setLogoPanel( null );

        //Keep a boolean flag for whether this module is active so subclasses can update when necessary (for performance reasons)
        addListener( new Listener() {
            public void activated() {
                moduleActive.set( true );
            }

            public void deactivated() {
                moduleActive.set( false );
            }
        } );
        setSimulationPanel( createSimulationPanel.f( model, moduleActive, resetAll ) );
    }

    protected final void resetAll() {
        getClock().removeAllClockListeners();
        this.model = createModel.f( (ConstantDtClock) getClock() );
        setSimulationPanel( createSimulationPanel.f( model, moduleActive, resetAll ) );
    }

    public T getBendingLightModel() { return model; }
}
