// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;

/**
 * Base class for the various bending light modules.
 *
 * @author Sam Reid
 */
public class BendingLightModule<T extends BendingLightModel> extends Module {
    private final T model;
    protected final BooleanProperty moduleActive = new BooleanProperty( isActive() );//Keep track of whether the module is active for making sure only one clock is running at a time
    protected final Resettable resetAll = new Resettable() {
        public void reset() {
            resetAll();
        }
    };

    public BendingLightModule( String name, T model ) {
        super( name, model.getClock() );
        this.model = model;

        setClockControlPanel( null );//Clock control is floating in the play area, and only visible when in wave mode
        getModulePanel().setLogoPanel( null );

        //Keep a boolean flag for whether this module is active so subclasses can update when necessary (for performance reasons)
        addListener( new Listener() {
            public void activated() {
                moduleActive.setValue( true );
            }

            public void deactivated() {
                moduleActive.setValue( false );
            }
        } );
    }

    protected void resetAll() {
        model.resetAll();
    }

    public T getBendingLightModel() {
        return model;
    }
}
