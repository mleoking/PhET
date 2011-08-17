// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.SurfaceType;

/**
 * Control panel for selecting a surface.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SurfaceControlPanel extends MPControlPanel {

    public SurfaceControlPanel( final Property<SurfaceType> isosurfaceType ) {
        super( MPStrings.SURFACE );
        add( new GridPanel() {{
            setGridX( 0 ); // vertical
            setAnchor( Anchor.WEST ); // left justified
            add( new PropertyRadioButton<SurfaceType>( MPStrings.NONE, isosurfaceType, SurfaceType.NONE ) );
            add( new PropertyRadioButton<SurfaceType>( MPStrings.ELECTROSTATIC_POTENTIAL, isosurfaceType, SurfaceType.ELECTROSTATIC_POTENTIAL ) );
            add( new PropertyRadioButton<SurfaceType>( MPStrings.ELECTRON_DENSITY, isosurfaceType, SurfaceType.ELECTRON_DENSITY ) );
        }} );
    }
}
