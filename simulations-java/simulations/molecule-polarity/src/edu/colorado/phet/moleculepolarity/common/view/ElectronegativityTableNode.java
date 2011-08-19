// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Table that shows electronegativity for a select of elements.
 * Cells in the table can be highlighted.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ElectronegativityTableNode extends PComposite {

    private static final Color BACKGROUND = Color.LIGHT_GRAY;

    public ElectronegativityTableNode( final Property<Molecule3D> currentMolecule, final JmolViewerNode viewerNode ) {
        //TODO
        addChild( new PText( "<Atom electronegativities displayed here>" ) {{
            setFont( new PhetFont( 20 ) );
        }} );
    }

    public void setHighlighted( int elementNumber, Color highlightColor ) {
        //TODO
    }
}
