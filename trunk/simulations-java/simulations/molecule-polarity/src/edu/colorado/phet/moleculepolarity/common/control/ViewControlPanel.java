// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;

/**
 * Control panel for various view parameters.
 * Constructor is parametrized with boolean flags so that this panel can be shared between modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends MPVerticalPanel {

    public ViewControlPanel( final ViewProperties properties, final boolean hasMolecularDipole, final boolean hasBondType,
                             final boolean hasElectronegativityTable, final boolean hasAtomLabels, final String bondDipolesLabel ) {
        super( MPStrings.VIEW );
        add( new PropertyCheckBox( bondDipolesLabel, properties.bondDipolesVisible ) );
        if ( hasMolecularDipole ) {
            add( new PropertyCheckBox( MPStrings.MOLECULAR_DIPOLE, properties.molecularDipoleVisible ) );
        }
        add( new PropertyCheckBox( MPStrings.PARTIAL_CHARGES, properties.partialChargesVisible ) );
        if ( hasBondType ) {
            add( new PropertyCheckBox( MPStrings.BOND_TYPE, properties.bondTypeVisible ) );
        }
        if ( hasElectronegativityTable ) {
            add( new PropertyCheckBox( MPStrings.ATOM_ELECTRONEGATIVITIES, properties.electronegativityTableVisible ) );
        }
        if ( hasAtomLabels ) {
            add( new PropertyCheckBox( MPStrings.ATOM_LABELS, properties.atomLabelsVisible ) );
        }
    }
}
