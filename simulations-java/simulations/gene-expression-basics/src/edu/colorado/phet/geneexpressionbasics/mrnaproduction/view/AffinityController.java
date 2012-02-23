// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsSimSharing.UserComponents;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.StubGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor.TranscriptionFactorConfig;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.DnaMoleculeNode;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.MobileBiomoleculeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * User interface control that can be used to control the affinity between a
 * transcription factor and the DNA.  Presents a node with the transcription
 * factor, an arrow, and a fragment of DNA in order to create the idea that
 *
 * @author John Blanco
 */
class AffinityController extends PNode {

    private static final double ARROW_LENGTH = 30;
    private static final double ARROW_HEAD_HEIGHT = 10;

    AffinityController( PNode leftNode, PNode rightNode, Property<Double> affinityProperty ) {
        // TODO: i18n
        PText caption = new PText( "Affinity" ) {{
            setFont( new PhetFont( 14, false ) );
        }};
        PNode arrowNode = new DoubleArrowNode( new Point2D.Double( 0, 0 ), new Point2D.Double( ARROW_LENGTH, 0 ), ARROW_HEAD_HEIGHT / 2, ARROW_HEAD_HEIGHT, ARROW_HEAD_HEIGHT / 3 );
        arrowNode.setPaint( Color.BLACK );
        PNode affinityKey = new HBox( leftNode, arrowNode, rightNode );
        affinityKey.setPickable( false );
        affinityKey.setChildrenPickable( false );
        addChild( new VBox( 5,
                            caption,
                            affinityKey,
                            // TODO: i18n
                            new HorizontalSliderWithLabelsAtEnds( new UserComponent( UserComponents.transcriptionFactorLevelSlider ), "Low", "High " ) ) );
    }
}
