// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Class that represents a gene in the view.  Since a gene is basically a
 * sequential collection of base pairs, this node is basically something that
 * highlights and labels the appropriate areas on the DNA strand.
 */
public class GeneNode extends PNode {

    private static final Font REGION_LABEL_FONT = new PhetFont( 11 );
    private static final Font GENE_LABEL_FONT = new PhetFont( 16 );
    private static final double BRACKET_DEPTH = 20;
    private static final double RECT_ROUNDING = 15;

    public GeneNode( final ModelViewTransform mvt, final Gene gene, DnaMolecule dnaMolecule, String label, boolean showBracketLabel ) {
        double highlightHeight = -mvt.modelToViewDeltaY( dnaMolecule.getDiameter() * 1.5 );
        double highlightStartY = mvt.modelToViewY( dnaMolecule.getLeftEdgePos().getY() ) - highlightHeight / 2;

        // Add the highlight for the regulatory region.
        double regRegionHighlightStartX = mvt.modelToViewX( dnaMolecule.getBasePairXOffsetByIndex( gene.getRegulatoryRegion().getMin() ) );
        double regRegionWidth = mvt.modelToViewX( dnaMolecule.getBasePairXOffsetByIndex( gene.getRegulatoryRegion().getMax() ) ) - regRegionHighlightStartX;
        Shape regRegionShape = new RoundRectangle2D.Double( regRegionHighlightStartX, highlightStartY, regRegionWidth, highlightHeight, RECT_ROUNDING, RECT_ROUNDING );
        final PhetPPath regulatoryRegionNode = new PhetPPath( regRegionShape, gene.getRegulatoryRegionColor() );
        addChild( regulatoryRegionNode );
        // TODO: i18n
        PNode regulatoryRegionCaption = new HTMLNode( GeneExpressionBasicsResources.Strings.REGULATORY_REGION ) {{
            setFont( REGION_LABEL_FONT );
            setOffset( regulatoryRegionNode.getFullBoundsReference().getCenterX() - getFullBoundsReference().width / 2,
                       regulatoryRegionNode.getFullBoundsReference().getMaxY() );
        }};
        addChild( regulatoryRegionCaption );

        // Add the highlight for the transcribed region.
        double transcribedRegionHighlightStartX = mvt.modelToViewX( dnaMolecule.getBasePairXOffsetByIndex( gene.getTranscribedRegion().getMin() ) );
        double transcribedRegionWidth = mvt.modelToViewX( dnaMolecule.getBasePairXOffsetByIndex( gene.getTranscribedRegion().getMax() ) ) - transcribedRegionHighlightStartX;
        Shape transcribedRegionShape = new RoundRectangle2D.Double( transcribedRegionHighlightStartX, highlightStartY, transcribedRegionWidth, highlightHeight, RECT_ROUNDING, RECT_ROUNDING );
        final PhetPPath transcribedRegionNode = new PhetPPath( transcribedRegionShape, gene.getTranscribedRegionColor() );
        addChild( transcribedRegionNode );
        // TODO: i18n
        PNode transcribedRegionCaption = new HTMLNode( GeneExpressionBasicsResources.Strings.TRANSCRIBED_REGION ) {{
            setFont( REGION_LABEL_FONT );
            setOffset( transcribedRegionNode.getFullBoundsReference().getCenterX() - getFullBoundsReference().width / 2,
                       transcribedRegionNode.getFullBoundsReference().getMaxY() );
        }};
        addChild( transcribedRegionCaption );

        // Add the bracket.  This is a portion (the non-textual part) of the
        // label for the gene.
        if ( showBracketLabel ) {
            final DoubleGeneralPath bracketPath = new DoubleGeneralPath();
            bracketPath.moveTo( regulatoryRegionNode.getFullBoundsReference().getMinX(),
                                regulatoryRegionCaption.getFullBoundsReference().getMaxY() );
            bracketPath.lineToRelative( BRACKET_DEPTH, BRACKET_DEPTH );
            bracketPath.lineTo( transcribedRegionNode.getFullBoundsReference().getMaxX() - BRACKET_DEPTH,
                                transcribedRegionCaption.getFullBoundsReference().getMaxY() + BRACKET_DEPTH );
            bracketPath.lineToRelative( BRACKET_DEPTH, -BRACKET_DEPTH );
            addChild( new PhetPPath( bracketPath.getGeneralPath(), new BasicStroke( 2 ), Color.BLACK ) );

            // And the textual label for the gene.
            addChild( new PText( label ) {{
                setFont( GENE_LABEL_FONT );
                setOffset( bracketPath.getGeneralPath().getBounds2D().getCenterX() - getFullBoundsReference().width / 2,
                           bracketPath.getGeneralPath().getBounds2D().getMaxY() + 5 );
            }} );
        }
    }
}
