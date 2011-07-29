// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.text.MessageFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ComboBoxNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Control for selecting a real molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MoleculeControlNode extends PhetPNode {

    private final MoleculeComboBoxNode comboBoxNode; // keep a reference so we can add observers to ComboBoxNode.selectedItem

    public MoleculeControlNode( ArrayList<Molecule3D> molecules ) {

        PText labelNode = new PText( MessageFormat.format( MPStrings.PATTERN_0LABEL, MPStrings.MOLECULE ) ) {{
            setFont( new PhetFont( 18 ) );
        }};
        addChild( labelNode );

        comboBoxNode = new MoleculeComboBoxNode( molecules );
        addChild( comboBoxNode );

        // layout
        final double heightDiff = comboBoxNode.getFullBoundsReference().getHeight() - labelNode.getFullBoundsReference().getHeight();
        labelNode.setOffset( 0, Math.max( 0, heightDiff / 2 ) );
        comboBoxNode.setOffset( labelNode.getFullBoundsReference().getMaxX() + 5, Math.min( 0, heightDiff / 2 ) );
    }

    public void addSelectedItemObserver( VoidFunction1<Molecule3D> observer ) {
        comboBoxNode.selectedItem.addObserver( observer );
    }

    private static class MoleculeComboBoxNode extends ComboBoxNode<Molecule3D> {
        public MoleculeComboBoxNode( ArrayList<Molecule3D> molecules ) {
            super( molecules, molecules.get( 0 ), new Function1<Molecule3D, PNode>() {
                public PNode apply( final Molecule3D molecule ) {
                    return new HTMLNode() {{
                        setHTML( MessageFormat.format( "{0} ({1})", molecule.getSymbol(), molecule.getName() ) );
                        setFont( new PhetFont( 14 ) );
                    }};
                }
            } );
        }
    }
}
