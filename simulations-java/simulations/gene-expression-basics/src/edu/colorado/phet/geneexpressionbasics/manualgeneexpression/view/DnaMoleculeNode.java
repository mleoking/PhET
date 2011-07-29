// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.BasePair;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
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

    // The middle layer can be used to show the base pairs, since they go between the strands
    private PNode middleLayer = new PNode();

    private PNode frontLayer = new PNode();

    public DnaMoleculeNode( DnaMolecule dnaMolecule, ModelViewTransform mvt ) {
        // Put the genes behind everything.
        for ( int i = 0; i < dnaMolecule.getGenes().size(); i++ ) {
            addChild( new GeneNode( mvt, dnaMolecule.getGenes().get( i ), i + 1 ) );
        }

        // Add the layers onto which the DNA backbone and base pairs will be placed.
        addChild( backLayer );
        addChild( middleLayer );
        addChild( frontLayer );

        // Add the first strand.
        for ( DnaStrandSegment dnaStrandSegment : dnaMolecule.getStrand1() ) {
            addStrand( mvt, dnaStrandSegment, STRAND_1_COLOR );
        }
        // Add the other strand.
        for ( DnaStrandSegment dnaStrandSegment : dnaMolecule.getStrand2() ) {
            addStrand( mvt, dnaStrandSegment, STRAND_2_COLOR );
        }
        // Add the base pairs.
        for ( BasePair basePair : dnaMolecule.getBasePairs() ) {
            middleLayer.addChild( new PhetPPath( mvt.modelToView( basePair.getShape() ), Color.DARK_GRAY ) );
        }
    }

    private void addStrand( ModelViewTransform mvt, DnaStrandSegment dnaStrandSegment, Color color ) {
        PNode segmentNode = new PhetPPath( mvt.modelToView( dnaStrandSegment.getShape() ), STRAND_STROKE, color );
        if ( dnaStrandSegment.inFront ) {
            frontLayer.addChild( segmentNode );
        }
        else {
            backLayer.addChild( segmentNode );
        }
    }
}
