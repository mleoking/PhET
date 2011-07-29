// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.LargeRibosomalSubunit;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRnaDestroyer;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Ribosome;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.RnaPolymerase;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.SmallRibosomalSubunit;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * This class defines the box on the user interface from which the user can
 * extract various biomolecules and put them into action within the cell.
 *
 * @author John Blanco
 */
public class BiomoleculeToolBoxNode extends PNode {
    private static final Font TITLE_FONT = new PhetFont( 20, true );
    protected final ManualGeneExpressionModel model;
    private final ManualGeneExpressionCanvas canvas;
    protected final ModelViewTransform mvt;

    public BiomoleculeToolBoxNode( ManualGeneExpressionModel model, ManualGeneExpressionCanvas canvas, ModelViewTransform mvt ) {
        this.model = model;
        this.canvas = canvas;
        this.mvt = mvt;
        // Create the body, i.e. the part below the title.
        PNode body = new SwingLayoutNode( new GridLayout( 5, 2, 20, 5 ) ) {{
            addChild( new RowLabel( "RNA Polymerase" ) );
            addChild( new HBox( new RnaPolymeraseCreatorNode( BiomoleculeToolBoxNode.this ),
                                new RnaPolymeraseCreatorNode( BiomoleculeToolBoxNode.this ),
                                new RnaPolymeraseCreatorNode( BiomoleculeToolBoxNode.this ) ) );
            addChild( new RowLabel( "Transcription Factor" ) );
            addChild( new HBox( new TranscriptionFactorCreatorNode( BiomoleculeToolBoxNode.this ) ) );
            addChild( new RowLabel( "Small Ribosomal Subunit" ) );
            addChild( new HBox( new SmallRibosomalSubunitCreatorNode( BiomoleculeToolBoxNode.this ),
                                new SmallRibosomalSubunitCreatorNode( BiomoleculeToolBoxNode.this ),
                                new SmallRibosomalSubunitCreatorNode( BiomoleculeToolBoxNode.this ) ) );
            addChild( new RowLabel( "Large Ribosomal Subunit" ) );
            addChild( new HBox( new LargeRibosomalSubunitCreatorNode( BiomoleculeToolBoxNode.this ),
                                new LargeRibosomalSubunitCreatorNode( BiomoleculeToolBoxNode.this ),
                                new LargeRibosomalSubunitCreatorNode( BiomoleculeToolBoxNode.this ) ) );
            addChild( new RowLabel( "mRNA Destroyer" ) );
            addChild( new HBox( new MessengerRnaDestroyerCreatorNode( BiomoleculeToolBoxNode.this ),
                                new MessengerRnaDestroyerCreatorNode( BiomoleculeToolBoxNode.this ),
                                new MessengerRnaDestroyerCreatorNode( BiomoleculeToolBoxNode.this ) ) );
        }};

        // Create the control panel node.
        PNode contentNode = new VBox(
                // TODO: i18n
                new PText( "Tool Box" ) {{ setFont( TITLE_FONT ); }},
                body
        );
        addChild( new ControlPanelNode( contentNode ) );
    }

    /**
     * Convenience class for creating row labels.
     */
    private static class RowLabel extends PText {
        private RowLabel( String text ) {
            super( text );
            setFont( new PhetFont( 14 ) );
        }
    }

    // PNode that, when clicked on, will add an RNA polymerase to the model.
    private static class RnaPolymeraseCreatorNode extends BiomoleculeCreatorNode {
        // Scaling factor for this node when used as a creator node.  May be
        // significantly different from the size of the corresponding element
        // in the model.
        private static final double SCALING_FACTOR = 0.1;
        private static final ModelViewTransform SCALING_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, 0 ), SCALING_FACTOR );

        /**
         * Constructor.
         *
         * @param biomoleculeBoxNode - Biomolecule box, which is a sort of tool
         *                           box, in which this creator node exists.
         */
        private RnaPolymeraseCreatorNode( final BiomoleculeToolBoxNode biomoleculeBoxNode ) {
            super( new MobileBiomoleculeNode( SCALING_MVT, new RnaPolymerase() ),
                   biomoleculeBoxNode.canvas,
                   biomoleculeBoxNode.mvt,
                   new Function1<Point2D, MobileBiomolecule>() {   // Molecule creator function.
                       public MobileBiomolecule apply( Point2D pos ) {
                           final RnaPolymerase rnaPolymerase = new RnaPolymerase( pos );
                           System.out.println( "Created biomolecule: " + rnaPolymerase );
                           biomoleculeBoxNode.model.addMobileBiomolecule( rnaPolymerase );
                           return rnaPolymerase;
                       }
                   },
                   new VoidFunction1<MobileBiomolecule>() {
                       public void apply( MobileBiomolecule mobileBiomolecule ) {
                           biomoleculeBoxNode.model.removeMobileBiomolecule( mobileBiomolecule );
                       }
                   },
                   biomoleculeBoxNode,
                   true
            );
        }
    }

    // PNode that, when clicked on, will add a small ribosomal subunit to the active area.
    private static class SmallRibosomalSubunitCreatorNode extends BiomoleculeCreatorNode {
        // Scaling factor for this node when used as a creator node.  May be
        // significantly different from the size of the corresponding element
        // in the model.
        private static final double SCALING_FACTOR = 0.1;
        private static final ModelViewTransform SCALING_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, 0 ), SCALING_FACTOR );

        private SmallRibosomalSubunitCreatorNode( final BiomoleculeToolBoxNode biomoleculeBoxNode ) {
            super( new MobileBiomoleculeNode( SCALING_MVT, new SmallRibosomalSubunit() ),
                   biomoleculeBoxNode.canvas,
                   biomoleculeBoxNode.mvt,
                   new Function1<Point2D, MobileBiomolecule>() {
                       public MobileBiomolecule apply( Point2D pos ) {
                           Ribosome srs = new Ribosome( pos );
                           biomoleculeBoxNode.model.addMobileBiomolecule( srs );
                           return srs;
                       }
                   },
                   new VoidFunction1<MobileBiomolecule>() {
                       public void apply( MobileBiomolecule mobileBiomolecule ) {
                           biomoleculeBoxNode.model.removeMobileBiomolecule( mobileBiomolecule );
                       }
                   },
                   biomoleculeBoxNode,
                   true
            );
        }
    }

    // PNode that, when clicked on, will add a large ribosomal subunit to the active area.
    private static class LargeRibosomalSubunitCreatorNode extends BiomoleculeCreatorNode {
        // Scaling factor for this node when used as a creator node.  May be
        // significantly different from the size of the corresponding element
        // in the model.
        private static final double SCALING_FACTOR = 0.1;
        private static final ModelViewTransform SCALING_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, 0 ), SCALING_FACTOR );

        private LargeRibosomalSubunitCreatorNode( final BiomoleculeToolBoxNode biomoleculeBoxNode ) {
            super( new MobileBiomoleculeNode( SCALING_MVT, new LargeRibosomalSubunit() ),
                   biomoleculeBoxNode.canvas,
                   biomoleculeBoxNode.mvt,
                   new Function1<Point2D, MobileBiomolecule>() {
                       public MobileBiomolecule apply( Point2D pos ) {
                           LargeRibosomalSubunit lrs = new LargeRibosomalSubunit( pos );
                           biomoleculeBoxNode.model.addMobileBiomolecule( lrs );
                           return lrs;
                       }
                   },
                   new VoidFunction1<MobileBiomolecule>() {
                       public void apply( MobileBiomolecule mobileBiomolecule ) {
                           biomoleculeBoxNode.model.removeMobileBiomolecule( mobileBiomolecule );
                       }
                   },
                   biomoleculeBoxNode,
                   true
            );
        }
    }

    // PNode that, when clicked on, will add a transcription factor to the active area.
    private static class TranscriptionFactorCreatorNode extends BiomoleculeCreatorNode {
        // Scaling factor for this node when used as a creator node.  May be
        // significantly different from the size of the corresponding element
        // in the model.
        private static final double SCALING_FACTOR = 0.1;
        private static final ModelViewTransform SCALING_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, 0 ), SCALING_FACTOR );

        private TranscriptionFactorCreatorNode( final BiomoleculeToolBoxNode biomoleculeBoxNode ) {
            super( new MobileBiomoleculeNode( SCALING_MVT, new TranscriptionFactor() ),
                   biomoleculeBoxNode.canvas,
                   biomoleculeBoxNode.mvt,
                   new Function1<Point2D, MobileBiomolecule>() {
                       public MobileBiomolecule apply( Point2D pos ) {
                           TranscriptionFactor transcriptionFactor = new TranscriptionFactor( pos );
                           biomoleculeBoxNode.model.addMobileBiomolecule( transcriptionFactor );
                           return transcriptionFactor;
                       }
                   },
                   new VoidFunction1<MobileBiomolecule>() {
                       public void apply( MobileBiomolecule mobileBiomolecule ) {
                           biomoleculeBoxNode.model.removeMobileBiomolecule( mobileBiomolecule );
                       }
                   },
                   biomoleculeBoxNode,
                   true
            );
        }
    }

    // PNode that, when clicked on, will add an mRNA destroyer to the active area.
    private static class MessengerRnaDestroyerCreatorNode extends BiomoleculeCreatorNode {
        // Scaling factor for this node when used as a creator node.  May be
        // significantly different from the size of the corresponding element
        // in the model.
        private static final double SCALING_FACTOR = 0.1;
        private static final ModelViewTransform SCALING_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, 0 ), SCALING_FACTOR );

        private MessengerRnaDestroyerCreatorNode( final BiomoleculeToolBoxNode biomoleculeBoxNode ) {
            super( new MobileBiomoleculeNode( SCALING_MVT, new MessengerRnaDestroyer() ),
                   biomoleculeBoxNode.canvas,
                   biomoleculeBoxNode.mvt,
                   new Function1<Point2D, MobileBiomolecule>() {
                       public MobileBiomolecule apply( Point2D pos ) {
                           MessengerRnaDestroyer mRnaDestroyer = new MessengerRnaDestroyer( pos );
                           biomoleculeBoxNode.model.addMobileBiomolecule( mRnaDestroyer );
                           return mRnaDestroyer;
                       }
                   },
                   new VoidFunction1<MobileBiomolecule>() {
                       public void apply( MobileBiomolecule mobileBiomolecule ) {
                           biomoleculeBoxNode.model.removeMobileBiomolecule( mobileBiomolecule );
                       }
                   },
                   biomoleculeBoxNode,
                   true
            );
        }
    }

    /*
    private static class GenericCreatorNode<T extends MobileBiomolecule> extends BiomoleculeCreatorNode {
        private GenericCreatorNode( final BiomoleculeBoxNode biomoleculeBoxNode, double scalingFactor ) {
            super( new MobileBiomoleculeNode( ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, 0 ), scalingFactor ), new SmallRibosomalSubunit() ),
                   biomoleculeBoxNode.canvas,
                   biomoleculeBoxNode.mvt,
                   new Function1<Point2D, MobileBiomolecule>() {
                       public MobileBiomolecule apply( Point2D pos ) {
                           T srs = new T( pos );
                           biomoleculeBoxNode.model.addMobileBiomolecule( srs );
                           return srs;
                       }
                   },
                   new VoidFunction1<MobileBiomolecule>() {
                       public void apply( MobileBiomolecule mobileBiomolecule ) {
                           biomoleculeBoxNode.model.removeMobileBiomolecule( mobileBiomolecule );
                       }
                   },
                   biomoleculeBoxNode,
                   true
            );
        }
    }
    */
}
