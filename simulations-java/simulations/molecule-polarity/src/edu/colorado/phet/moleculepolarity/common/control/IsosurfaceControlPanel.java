// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.IsosurfaceType;

/**
 * "Model" control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IsosurfaceControlPanel extends MPControlPanel {

    public IsosurfaceControlPanel( final Property<IsosurfaceType> isosurfaceType ) {
        super( MPStrings.ISOSURFACE );
        add( new GridPanel() {{
            setGridX( 0 ); // vertical
            setAnchor( Anchor.WEST ); // left justified
            add( new PropertyRadioButton<IsosurfaceType>( MPStrings.NONE, isosurfaceType, IsosurfaceType.NONE ) );
            add( new PropertyRadioButton<IsosurfaceType>( MPStrings.ELECTROSTATIC_POTENTIAL, isosurfaceType, IsosurfaceType.ELECTROSTATIC_POTENTIAL ) );
            add( new PropertyRadioButton<IsosurfaceType>( MPStrings.ELECTRON_DENSITY, isosurfaceType, IsosurfaceType.ELECTRON_DENSITY ) );
        }} );
    }
}
