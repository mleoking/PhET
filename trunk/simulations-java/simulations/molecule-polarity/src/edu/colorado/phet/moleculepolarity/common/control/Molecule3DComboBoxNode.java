// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.text.MessageFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ComboBoxNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D;
import edu.umd.cs.piccolo.PNode;

/**
 * Combo box for selecting a real molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Molecule3DComboBoxNode extends ComboBoxNode<Molecule3D> {

    public Molecule3DComboBoxNode( ArrayList<Molecule3D> molecules ) {
        super( molecules, molecules.get( 0 ), new Function1<Molecule3D, PNode>() {
            public PNode apply( final Molecule3D molecule ) {
                return new HTMLNode() {{
                    setHTML( MessageFormat.format( "{0} ({1})", molecule.getSymbol(), molecule.getName() ) );
                    setFont( new PhetFont( 18 ) );
                }};
            }
        } );
    }
}
