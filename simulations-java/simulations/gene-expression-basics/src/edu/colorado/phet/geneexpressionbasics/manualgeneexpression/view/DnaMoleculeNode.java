// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.BasePair;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule.DnaStrandSegment;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import static edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources.Strings.GENE;

/**
 * Class that represents the DNA molecule in the view.
 *
 * @author John Blanco
 */
public class DnaMoleculeNode extends PNode {

    public static Color STRAND_1_COLOR = new Color( 31, 163, 223 );
    public static Color STRAND_2_COLOR = new Color( 214, 87, 107 );

    // Layers for supporting the 3D look by allowing the "twist" to be depicted.
    private PNode dnaBackboneBackLayer = new PNode();
    private PNode dnaBackboneFrontLayer = new PNode();

    /**
     * Constructor.
     *
     * @param dnaMolecule
     * @param mvt
     * @param showGeneBracketLabels
     */
    public DnaMoleculeNode( DnaMolecule dnaMolecule, ModelViewTransform mvt, float backboneStrokeWidth, boolean showGeneBracketLabels ) {

        // Add the layers onto which the various nodes that represent parts of
        // the dna, the hints, etc. are placed.
        PNode geneBackgroundLayer = new PNode();
        addChild( geneBackgroundLayer );
        addChild( dnaBackboneBackLayer );
        PNode basePairLayer = new PNode();
        addChild( basePairLayer );
        addChild( dnaBackboneFrontLayer );

        // Put the gene backgrounds and labels behind everything.
        for ( int i = 0; i < dnaMolecule.getGenes().size(); i++ ) {
            geneBackgroundLayer.addChild( new GeneNode( mvt, dnaMolecule.getGenes().get( i ), dnaMolecule, GENE + ( i + 1 ), showGeneBracketLabels ) );
        }

        // Add the first backbone strand.
        for ( DnaStrandSegment dnaStrandSegment : dnaMolecule.getStrand1Segments() ) {
            addStrand( mvt, dnaStrandSegment, new BasicStroke( backboneStrokeWidth ), STRAND_1_COLOR );
        }

        // Add the other backbone strand.
        for ( DnaStrandSegment dnaStrandSegment : dnaMolecule.getStrand2Segments() ) {
            addStrand( mvt, dnaStrandSegment, new BasicStroke( backboneStrokeWidth ), STRAND_2_COLOR );
        }

        // Add the base pairs.
        for ( BasePair basePair : dnaMolecule.getBasePairs() ) {
            basePairLayer.addChild( new PhetPPath( mvt.modelToView( basePair.getShape() ), Color.DARK_GRAY ) );
        }
    }

    private void addStrand( ModelViewTransform mvt, DnaStrandSegment dnaStrandSegment, Stroke strandSegmentStroke, Color color ) {
        PNode segmentNode = new DnaStrandSegmentNode( dnaStrandSegment, mvt, strandSegmentStroke, color );
        if ( dnaStrandSegment.inFront ) {
            dnaBackboneFrontLayer.addChild( segmentNode );
        }
        else {
            dnaBackboneBackLayer.addChild( segmentNode );
        }
    }

    private class DnaStrandSegmentNode extends PNode {
        private DnaStrandSegmentNode( final DnaStrandSegment dnaStrandSegment, final ModelViewTransform mvt, Stroke strandSegmentStroke, Color color ) {
            final PPath pathNode = new PhetPPath( strandSegmentStroke, color );
            addChild( pathNode );
            dnaStrandSegment.addShapeChangeObserver( new VoidFunction1<Shape>() {
                public void apply( Shape shape ) {
                    pathNode.setPathTo( mvt.modelToView( shape ) );
                }
            } );
        }
    }
}
