// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.SurfaceType;

/**
 * Control panel for selecting a surface.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SurfaceControlPanel extends MPVerticalPanel {

    public SurfaceControlPanel( final Property<SurfaceType> surfaceType ) {
        super( MPStrings.SURFACE );
        add( new PropertyRadioButton<SurfaceType>( MPStrings.NONE, surfaceType, SurfaceType.NONE ) );
        add( new PropertyRadioButton<SurfaceType>( MPStrings.ELECTROSTATIC_POTENTIAL, surfaceType, SurfaceType.ELECTROSTATIC_POTENTIAL ) );
        add( new PropertyRadioButton<SurfaceType>( MPStrings.ELECTRON_DENSITY, surfaceType, SurfaceType.ELECTRON_DENSITY ) );
    }
}
