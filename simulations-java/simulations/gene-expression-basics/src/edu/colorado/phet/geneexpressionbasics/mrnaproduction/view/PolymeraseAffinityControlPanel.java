// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.geneexpressionbasics.mrnaproduction.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.DnaMolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.RnaPolymerase;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor.TranscriptionFactorConfig;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.DnaMoleculeNode;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.MobileBiomoleculeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

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

    public PolymeraseAffinityControlPanel( TranscriptionFactorConfig tfConfig ) {
        PNode title = new PText( "RNA Polymerase" ) {{
            setFont( new PhetFont( 14 ) );
        }};

        PNode polymeraseNode = new MobileBiomoleculeNode( POLYMERASE_MVT, new RnaPolymerase() );
        PNode dnaFragmentNode = new DnaMoleculeNode( new DnaMolecule( DnaMolecule.BASE_PAIRS_PER_TWIST + 1, 0.0 ), DNA_AND_TF_MVT, 2, false );
        PNode transcriptionFactorNode = new MobileBiomoleculeNode( DNA_AND_TF_MVT, new TranscriptionFactor( tfConfig ) );
        transcriptionFactorNode.setOffset( 10, 0 ); // Position to be on top of the dna, values empirically determined.
        dnaFragmentNode.addChild( transcriptionFactorNode );
        PNode contents = new VBox(
                20,
                title,
                new AffinityController( polymeraseNode, dnaFragmentNode, new Property<Double>( 0.0 ) ) // TODO: Need to hook up to actual model.
        );

        addChild( new ControlPanelNode( contents ) );
    }
}
