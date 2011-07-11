// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.control;

import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;

/**
 * "View" control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewPanel extends GridPanel {

    public ViewPanel( ViewProperties properties ) {
        setBorder( new TitledBorder( MPStrings.VIEW ) );
        setGridX( 0 ); // vertical
        setAnchor( Anchor.WEST ); // left justified
        add( new PropertyCheckBox( MPStrings.BOND_DIPOLES, properties.bondDipolesVisible ) );
        add( new PropertyCheckBox( MPStrings.MOLECULE_DIPOLES, properties.moleculeDipoleVisible ) );
        add( new PropertyCheckBox( MPStrings.PARTIAL_CHARGES, properties.partialChargesVisible ) );
    }
}
