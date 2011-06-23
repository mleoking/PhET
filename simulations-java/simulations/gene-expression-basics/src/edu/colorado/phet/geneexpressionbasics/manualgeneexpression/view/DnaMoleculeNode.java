// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule.DnaStrandSegment;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that represents the DNA molecule in the view.
 *
 * @author John Blanco
 */
public class DnaMoleculeNode extends PNode {

    public DnaMoleculeNode( DnaMolecule dnaMolecule, ModelViewTransform mvt ) {
        // Set up the layers that will allow the DNA molecule to look somewhat
        // 3D, since it will portray the 'twist'.
        PNode backLayer = new PNode();
        addChild( backLayer );
        PNode middleLayer = new PNode();
        addChild( middleLayer );
        PNode frontLayer = new PNode();
        addChild( frontLayer );

        // Add a strand.
        for ( DnaStrandSegment dnaStrandSegment : dnaMolecule.getStrand1() ) {
            PNode segmentNode = new PhetPPath( mvt.modelToView( dnaStrandSegment.getShape() ), new BasicStroke( 1 ), Color.BLUE );
            if ( dnaStrandSegment.inFront ) {
                frontLayer.addChild( segmentNode );
            }
            else {
                backLayer.addChild( segmentNode );
            }
        }
    }
}
