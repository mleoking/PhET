// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;

/**
 * "View" control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends MPControlPanel {

    public ViewControlPanel( final ViewProperties properties ) {
        super( MPStrings.VIEW );
        add( new GridPanel() {{
            setGridX( 0 ); // vertical
            setAnchor( Anchor.WEST ); // left justified
            add( new PropertyCheckBox( MPStrings.BOND_DIPOLES, properties.bondDipolesVisible ) );
            add( new PropertyCheckBox( MPStrings.MOLECULAR_DIPOLE, properties.molecularDipoleVisible ) );
            add( new PropertyCheckBox( MPStrings.PARTIAL_CHARGES, properties.partialChargesVisible ) );
            add( new PropertyCheckBox( MPStrings.ATOM_LABELS, properties.atomLabelsVisible ) );
        }} );
    }
}
