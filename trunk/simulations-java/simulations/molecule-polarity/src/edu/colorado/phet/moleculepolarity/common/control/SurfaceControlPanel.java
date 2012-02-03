// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.moleculepolarity.MPSimSharing.UserComponents;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPRadioButton;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPVerticalPanel;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.SurfaceType;

/**
 * Control panel for selecting a surface.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SurfaceControlPanel extends MPVerticalPanel {

    public SurfaceControlPanel( final Property<SurfaceType> surfaceType ) {
        super( MPStrings.SURFACE );
        add( new MPRadioButton<SurfaceType>( UserComponents.noneSurfaceRadioButton, MPStrings.NONE, surfaceType, SurfaceType.NONE ) );
        add( new MPRadioButton<SurfaceType>( UserComponents.electrostaticPotentialRadioButton, MPStrings.ELECTROSTATIC_POTENTIAL, surfaceType, SurfaceType.ELECTROSTATIC_POTENTIAL ) );
        add( new MPRadioButton<SurfaceType>( UserComponents.electronDensityRadioButton, MPStrings.ELECTRON_DENSITY, surfaceType, SurfaceType.ELECTRON_DENSITY ) );
    }
}
