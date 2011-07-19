// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.util.ArrayList;

import edu.colorado.phet.common.piccolophet.nodes.ComboBoxNode;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D;

/**
 * Combo box for selecting a real molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Molecule3DComboBoxNode extends ComboBoxNode<Molecule3D> {

    public Molecule3DComboBoxNode( ArrayList<Molecule3D> molecules ) {
        super( molecules );
    }
}
