// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;

/**
 * @author Sam Reid
 */
public class LRRModule<T extends LRRModel> extends Module {
    private final T model;
    protected final BooleanProperty moduleActive = new BooleanProperty( isActive() );//Keep track of whether the module is active for making sure only one clock is running at a time
    protected final Resettable resetAll = new Resettable() {
        public void reset() {
            resetAll();
        }
    };

    protected void resetAll() {
        model.resetAll();
    }

    public LRRModule( String name, T model ) {
        super( name, model.getClock() );
        this.model = model;
        setClockControlPanel( null );
        getModulePanel().setLogoPanel( null );
        addListener( new Listener() {
            public void activated() {
                moduleActive.setValue( true );
            }

            public void deactivated() {
                moduleActive.setValue( false );
            }
        } );
    }


    public T getLRRModel() {
        return model;
    }
}
