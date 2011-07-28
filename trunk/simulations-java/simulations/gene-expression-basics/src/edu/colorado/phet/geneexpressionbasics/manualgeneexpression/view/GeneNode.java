// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for displaying and interacting with mobile biomolecules.  In
 * essence, this observes the shape of the biomolecule, which changes as it
 * moves.
 */
public class GeneNode extends PNode {
    private static final Font LABEL_FONT = new PhetFont( 11 );

    public GeneNode( final ModelViewTransform mvt, final Gene gene ) {
        addChild( new PhetPPath( mvt.modelToView( gene.getTranscribedRegionRect() ), gene.getColor() ) );
        addChild( new PhetPPath( mvt.modelToView( gene.getRegulatoryRegionRect() ), new Color( 30, 144, 255 ) ) );
        addChild( new HTMLNode( "<center>Regulatory<br>Region</center>" ) {{
            setFont( LABEL_FONT );
            setOffset( mvt.modelToViewX( gene.getRegulatoryRegionRect().getCenterX() ) - getFullBoundsReference().width / 2,
                       mvt.modelToViewY( gene.getRegulatoryRegionRect().getMinY() ) );
        }} );
        addChild( new HTMLNode( "<center>Transcribed<br>Region</center>" ) {{
            setFont( LABEL_FONT );
            setOffset( mvt.modelToViewX( gene.getTranscribedRegionRect().getCenterX() ) - getFullBoundsReference().width / 2,
                       mvt.modelToViewY( gene.getTranscribedRegionRect().getMinY() ) );
        }} );
    }
}
