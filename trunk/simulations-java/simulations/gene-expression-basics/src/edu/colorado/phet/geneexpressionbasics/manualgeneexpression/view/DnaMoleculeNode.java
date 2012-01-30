// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.common.model.PlacementHint;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.BasePair;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule.DnaStrandSegment;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;
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

    // Layer where the gene nodes are placed.
    private PNode geneBackgroundLayer = new PNode();

    // Layers for supporting the 3D look by allowing the "twist" to be depicted.
    private PNode dnaStrandBackLayer = new PNode();
    private PNode basePairLayer = new PNode();
    private PNode dnaStrandFrontLayer = new PNode();

    // Add the layer where placement hits are put.
    private PNode placementHintLayer = new PNode();

    /**
     * Constructor.
     *
     * @param dnaMolecule
     * @param mvt
     */
    public DnaMoleculeNode( DnaMolecule dnaMolecule, ModelViewTransform mvt ) {

        // Add the layers onto which the various nodes that represent parts of
        // the dna, the hints, etc. are placed.
        addChild( geneBackgroundLayer );
        addChild( dnaStrandBackLayer );
        addChild( basePairLayer );
        addChild( dnaStrandFrontLayer );
        addChild( placementHintLayer );

        // Put the genes backgrounds and labels behind everything.
        for ( Gene gene : dnaMolecule.getGenes() ) {
            geneBackgroundLayer.addChild( new GeneNode( mvt, gene, dnaMolecule ) );
        }
        // Add the first strand.
        for ( DnaStrandSegment dnaStrandSegment : dnaMolecule.getStrand1Segments() ) {
            addStrand( mvt, dnaStrandSegment, STRAND_1_COLOR );
        }
        // Add the other strand.
        for ( DnaStrandSegment dnaStrandSegment : dnaMolecule.getStrand2Segments() ) {
            addStrand( mvt, dnaStrandSegment, STRAND_2_COLOR );
        }
        // Add the base pairs.
        for ( BasePair basePair : dnaMolecule.getBasePairs() ) {
            basePairLayer.addChild( new PhetPPath( mvt.modelToView( basePair.getShape() ), Color.DARK_GRAY ) );
        }
        // Add the placement hints.
        for ( Gene gene : dnaMolecule.getGenes() ) {
            for ( PlacementHint placementHint : gene.getPlacementHints() ) {
                placementHintLayer.addChild( new PlacementHintNode( mvt, placementHint ) );
            }
        }
    }

    private void addStrand( ModelViewTransform mvt, DnaStrandSegment dnaStrandSegment, Color color ) {
        PNode segmentNode = new PhetPPath( mvt.modelToView( dnaStrandSegment.getShape() ), STRAND_STROKE, color );
        if ( dnaStrandSegment.inFront ) {
            dnaStrandFrontLayer.addChild( segmentNode );
        }
        else {
            dnaStrandBackLayer.addChild( segmentNode );
        }
    }
}
