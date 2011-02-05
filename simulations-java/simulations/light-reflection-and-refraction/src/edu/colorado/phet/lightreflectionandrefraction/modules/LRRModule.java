// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;

/**
 * @author Sam Reid
 */
public class LRRModule<T extends LRRModel> extends Module {
    private final T model;

    public LRRModule( String name, T model ) {
        super( name, model.getClock() );
        this.model = model;
        setClockControlPanel( null );
        getModulePanel().setLogoPanel( null );
    }

    public T getLRRModel() {
        return model;
    }
}
