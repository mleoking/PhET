// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.view;

import java.awt.Color;
import java.awt.geom.Point2D;

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
 * @author John Blanco
 */
class AffinityController extends PNode {

    private static final double ARROW_LENGTH = 30;
    private static final double ARROW_HEAD_HEIGHT = 10;

    AffinityController( TranscriptionFactorConfig transcriptionFactorConfig, double transcriptionFactorScale, double dnaScale ) {
        ModelViewTransform transcriptionFactorTransform = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                                                     new Point2D.Double( 0, 0 ),
                                                                                                                     transcriptionFactorScale );
        ModelViewTransform dnaTransform = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                                     new Point2D.Double( 0, 0 ),
                                                                                                     dnaScale );
        // TODO: i18n
        PText caption = new PText( "Affinity" ) {{
            setFont( new PhetFont( 14, false ) );
        }};
        PNode arrowNode = new DoubleArrowNode( new Point2D.Double( 0, 0 ), new Point2D.Double( ARROW_LENGTH, 0 ), ARROW_HEAD_HEIGHT / 2, ARROW_HEAD_HEIGHT, ARROW_HEAD_HEIGHT / 3 );
        arrowNode.setPaint( Color.BLACK );
        PNode affinityKey = new HBox(
                new MobileBiomoleculeNode( transcriptionFactorTransform, new TranscriptionFactor( new StubGeneExpressionModel(), transcriptionFactorConfig ) ),
                arrowNode,
                new DnaMoleculeNode( new DnaMolecule( new StubGeneExpressionModel(), DnaMolecule.BASE_PAIRS_PER_TWIST + 1, 0 ), dnaTransform, 2, false )
        );
        affinityKey.setPickable( false );
        affinityKey.setChildrenPickable( false );
        addChild( new VBox( 5,
                            caption,
                            affinityKey,
                            new HorizontalSliderWithLabelsAtEnds( new UserComponent( UserComponents.transcriptionFactorLevelSlider ),
                                                                  // TODO: i18n
                                                                  "Low",
                                                                  "High " ) ) );
    }
}
