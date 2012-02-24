// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.RnaPolymerase;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor.TranscriptionFactorConfig;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.DnaMoleculeNode;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.MobileBiomoleculeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import com.sun.deploy.panel.ControlPanel;

/**
 * Control panel that present a user interface for controlling the affinity
 * of RNA polymerase to DNA plus a transcription factor.
 * 
 * @author John Blanco
 */
public class PolymeraseAffinityControlPanel extends PNode {

    public static final double POLYMERASE_SCALE = 0.08;
    private static final ModelViewTransform POLYMERASE_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                                                                  new Point2D.Double( 0, 0 ),
                                                                                                                                  POLYMERASE_SCALE );
    public static final double DNA_AND_TF_SCALE = 0.08;
    private static final ModelViewTransform DNA_AND_TF_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                                                                  new Point2D.Double( 0, 0 ),
                                                                                                                                  DNA_AND_TF_SCALE );

    public PolymeraseAffinityControlPanel( TranscriptionFactorConfig tfConfig, double minHeight ) {
        PNode title = new PText( "RNA Polymerase" ) {{
            setFont( new PhetFont( 16, true ) );
        }};

        PNode polymeraseNode = new MobileBiomoleculeNode( POLYMERASE_MVT, new RnaPolymerase() );
        PNode dnaFragmentNode = new DnaMoleculeNode( new DnaMolecule( DnaMolecule.BASE_PAIRS_PER_TWIST * 2 + 1, 0.0 ), DNA_AND_TF_MVT, 2, false );
        PNode transcriptionFactorNode = new MobileBiomoleculeNode( DNA_AND_TF_MVT, new TranscriptionFactor( tfConfig ) );
        transcriptionFactorNode.setOffset( 25, 0 ); // Set position to be on top of the dna, values empirically determined.
        dnaFragmentNode.addChild( transcriptionFactorNode );
        SpacerNode topSpacer = new SpacerNode();
        SpacerNode bottomSpacer = new SpacerNode();
        PNode contents = new VBox(
                20,
                title,
                topSpacer,
                new AffinityController( polymeraseNode, dnaFragmentNode, new Property<Double>( 0.0 ) ), // TODO: Need to hook up to actual model.
                bottomSpacer
        );

        ControlPanelNode controlPanel = new ControlPanelNode( contents );

        double growAmount = minHeight - controlPanel.getFullBoundsReference().height;

        // Make the panel meet the minimum height.
        if ( growAmount > 0 ){
            topSpacer.setSize( SpacerNode.MIN_DIMENSION, growAmount / 2 );
            bottomSpacer.setSize( SpacerNode.MIN_DIMENSION, growAmount / 2 );
        }

        addChild( controlPanel );
    }

    private static class SpacerNode extends PNode {

        private static final double MIN_DIMENSION = 1E-7;

        private final PPath spacer;

        public SpacerNode( ) {
            this( MIN_DIMENSION, MIN_DIMENSION );
        }

        public SpacerNode(double width, double height ) {
            spacer = new PhetPPath( new Rectangle2D.Double(0, 0, height, width ), new Color( 0, 0, 0, 0 ) );
        }

        public void setSize( double width, double height ){
            spacer.setPathTo( new Rectangle2D.Double(0, 0, height, width ) );
        }
    }
}
