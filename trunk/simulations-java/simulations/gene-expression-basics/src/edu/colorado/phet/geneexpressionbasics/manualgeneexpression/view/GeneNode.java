// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Base class for displaying and interacting with mobile biomolecules.  In
 * essence, this observes the shape of the biomolecule, which changes as it
 * moves.
 */
public class GeneNode extends PNode {

    private static final Font REGION_LABEL_FONT = new PhetFont( 11 );
    private static final Font GENE_LABEL_FONT = new PhetFont( 16 );
    private static final double BRACKET_DEPTH = 20;

    public GeneNode( final ModelViewTransform mvt, final Gene gene, int geneNumber ) {
        // Add the areas that essentially highlight the gene on the DNA
        // strand.  Each of these is labeled.
        final PhetPPath transcribedRegionNode = new PhetPPath( mvt.modelToView( gene.getTranscribedRegionShape() ), gene.getTranscribedRegionColor() );
        addChild( transcribedRegionNode );
        // TODO: i18n
        PNode regulatoryRegionCaption = new HTMLNode( "<center>Transcribed<br>Region</center>" ) {{
            setFont( REGION_LABEL_FONT );
            setOffset( transcribedRegionNode.getFullBoundsReference().getCenterX() - getFullBoundsReference().width / 2,
                       transcribedRegionNode.getFullBoundsReference().getMaxY() );
        }};
        addChild( regulatoryRegionCaption );
        final PhetPPath regulatoryRegionNode = new PhetPPath( mvt.modelToView( gene.getRegulatoryRegionShape() ), gene.getRegulatoryRegionColor() );
        addChild( regulatoryRegionNode );
        // TODO: i18n
        PNode transcribedRegionCaption = new HTMLNode( "<center>Regulatory<br>Region</center>" ) {{
            setFont( REGION_LABEL_FONT );
            setOffset( regulatoryRegionNode.getFullBoundsReference().getCenterX() - getFullBoundsReference().width / 2,
                       regulatoryRegionNode.getFullBoundsReference().getMaxY() );
        }};
        addChild( transcribedRegionCaption );

        // Add the bracket.  This is a portion (the non-textual part) of the
        // overall label for the gene.
        final DoubleGeneralPath bracketPath = new DoubleGeneralPath();
        bracketPath.moveTo( regulatoryRegionNode.getFullBoundsReference().getMinX(),
                            regulatoryRegionCaption.getFullBoundsReference().getMaxY() );
        bracketPath.lineToRelative( BRACKET_DEPTH, BRACKET_DEPTH );
        bracketPath.lineTo( transcribedRegionNode.getFullBoundsReference().getMaxX() - BRACKET_DEPTH,
                            transcribedRegionCaption.getFullBoundsReference().getMaxY() + BRACKET_DEPTH );
        bracketPath.lineToRelative( BRACKET_DEPTH, -BRACKET_DEPTH );
        addChild( new PhetPPath( bracketPath.getGeneralPath(), new BasicStroke( 2 ), Color.BLACK ) );

        // And the textual label for the gene.
        addChild( new PText( "Gene " + geneNumber ) {{
            setFont( GENE_LABEL_FONT );
            setOffset( bracketPath.getGeneralPath().getBounds2D().getCenterX() - getFullBoundsReference().width / 2,
                       bracketPath.getGeneralPath().getBounds2D().getMaxY() + 5 );
        }} );

    }
}
