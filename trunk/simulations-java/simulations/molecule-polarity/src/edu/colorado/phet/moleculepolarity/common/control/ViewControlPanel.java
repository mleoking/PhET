// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPCheckBox;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPVerticalPanel;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties;

/**
 * Control panel for various view parameters.
 * Constructor is parametrized with boolean flags so that this panel can be shared between modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends MPVerticalPanel {

    public ViewControlPanel( final ViewProperties properties, final boolean hasMolecularDipole, final boolean hasBondCharacter,
                             final boolean hasElectronegativityTable, final boolean hasAtomLabels, final String bondDipolesLabel ) {
        super( MPStrings.VIEW );
        add( new MPCheckBox( bondDipolesLabel, properties.bondDipolesVisible ) );
        if ( hasMolecularDipole ) {
            add( new MPCheckBox( MPStrings.MOLECULAR_DIPOLE, properties.molecularDipoleVisible ) );
        }
        add( new MPCheckBox( MPStrings.PARTIAL_CHARGES, properties.partialChargesVisible ) );
        if ( hasBondCharacter ) {
            add( new MPCheckBox( MPStrings.BOND_CHARACTER, properties.bondCharacterVisible ) );
        }
        if ( hasElectronegativityTable ) {
            add( new MPCheckBox( MPStrings.ATOM_ELECTRONEGATIVITIES, properties.electronegativityTableVisible ) );
        }
        if ( hasAtomLabels ) {
            add( new MPCheckBox( MPStrings.ATOM_LABELS, properties.atomLabelsVisible ) );
        }
    }
}
