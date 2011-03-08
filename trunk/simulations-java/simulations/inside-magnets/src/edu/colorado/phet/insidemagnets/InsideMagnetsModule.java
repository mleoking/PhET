// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.insidemagnets;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * @author Sam Reid
 */
public class InsideMagnetsModule extends Module {
    private InsideMagnetsModel insideMagnetsModel;
    private Property<Boolean> showMagnetizationProperty = new Property<Boolean>( false );

    public InsideMagnetsModule() {
        this( new InsideMagnetsModel() );
    }

    public InsideMagnetsModule( InsideMagnetsModel model ) {
        super( "Inside Magnets", model.getClock() );
        this.insideMagnetsModel = model;
        setSimulationPanel( new InsideMagnetsCanvas( this ) );
        getModulePanel().setLogoPanel( null );
    }

    public InsideMagnetsModel getInsideMagnetsModel() {
        return insideMagnetsModel;
    }

    public void resetAll() {
    }

    public Property<Boolean> getShowMagnetizationProperty() {
        return showMagnetizationProperty;
    }
}
