// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.CellFactory.HighlightElements;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A mutable periodic table node.
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MPPeriodicTableNode extends PComposite {

    private static final Color BACKGROUND = new Color( 248, 255, 222 ); // light yellow

    private PNode periodicTableNode; // use composition since PeriodicTableNode's selection is immutable

    public MPPeriodicTableNode( final Property<Molecule3D> currentMolecule, final JmolViewerNode viewerNode ) {
        currentMolecule.addObserver( new VoidFunction1<Molecule3D>() {
            public void apply( Molecule3D molecule3D ) {
                Integer[] elementNumbers = viewerNode.getElementNumbers();
                removeChild( periodicTableNode );
                periodicTableNode = new PeriodicTableNode( BACKGROUND, new HighlightElements( elementNumbers ) );
                addChild( periodicTableNode );
            }
        } );
    }
}
