// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule.DnaStrand;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule.DnaStrandSegment;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that represents the DNA molecule in the view.
 *
 * @author John Blanco
 */
public class DnaMoleculeNode extends PNode {

    public static Color STRAND_1_COLOR = new Color( 31, 163, 223 );
    public static Color STRAND_2_COLOR = new Color( 214, 87, 107 );
    public static Stroke STRAND_STROKE = new BasicStroke( 3 );

    // Layers for supporting the 3D look by allowing the "twist" to be depicted.
    private PNode backLayer = new PNode();
    private PNode middleLayer = new PNode();
    private PNode frontLayer = new PNode();

    public DnaMoleculeNode( DnaMolecule dnaMolecule, ModelViewTransform mvt ) {
        addChild( backLayer );
        addChild( middleLayer );
        addChild( frontLayer );

        // Add the first strand.
        for ( DnaStrandSegment dnaStrandSegment : dnaMolecule.getStrand1() ) {
            PNode segmentNode = new PhetPPath( mvt.modelToView( dnaStrandSegment.getShape() ), STRAND_STROKE, STRAND_1_COLOR );
            if ( dnaStrandSegment.inFront ) {
                frontLayer.addChild( segmentNode );
            }
            else {
                backLayer.addChild( segmentNode );
            }
        }
        // Add the other strand.
        for ( DnaStrandSegment dnaStrandSegment : dnaMolecule.getStrand2() ) {
            PNode segmentNode = new PhetPPath( mvt.modelToView( dnaStrandSegment.getShape() ), STRAND_STROKE, STRAND_2_COLOR );
            if ( dnaStrandSegment.inFront ) {
                frontLayer.addChild( segmentNode );
            }
            else {
                backLayer.addChild( segmentNode );
            }
        }
    }

    private void addStrand( DnaStrand dnaStrand, Color color ) {

    }
}
