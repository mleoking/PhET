// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Gene;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.MessengerRnaDestroyer;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.Ribosome;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.RnaPolymerase;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.StubGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.TranscriptionFactor.TranscriptionFactorConfig;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

import static edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources.Strings.*;

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
    private final List<BiomoleculeCreatorNode> biomoleculeCreatorNodeList = new ArrayList<BiomoleculeCreatorNode>();

    public BiomoleculeToolBoxNode( ManualGeneExpressionModel model, ManualGeneExpressionCanvas canvas, ModelViewTransform mvt, final Gene gene ) {
        this.model = model;
        this.canvas = canvas;
        this.mvt = mvt;
        // Create the content of this control panel.
        PNode contentNode = new SwingLayoutNode( new GridBagLayout() ) {{
            GridBagConstraints constraints = new GridBagConstraints();
            // Add the title.
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.gridwidth = 2;
            addChild( new PText( BIOMOLECULE_TOOLBOX ) {{
                setFont( TITLE_FONT );
            }}, constraints );

            // Add the biomolecule rows, each of which has a title and a set of
            // biomolecules that can be added to the active area.
            constraints.gridy++;
            constraints.gridwidth = 1;
            constraints.anchor = GridBagConstraints.LINE_START;
            constraints.insets.top = 10;
            constraints.insets.left = 0;

            // Positive transcription factor(s).
            for ( TranscriptionFactorConfig tfConfig : gene.getTranscriptionFactorConfigs() ) {
                if ( tfConfig.isPositive ) {
                    addChild( new RowLabel( POSITIVE_TRANSCRIPTION_FACTOR_HTML ), constraints );
                    constraints.gridx++;
                    constraints.insets.left = 20;
                    addChild( new HBox( addCreatorNode( new TranscriptionFactorCreatorNode( BiomoleculeToolBoxNode.this, tfConfig, true ) ) ), constraints );
                    constraints.gridx = 0;
                    constraints.gridy++;
                    constraints.insets.left = 0;
                }
            }

            // Polymerase.
            addChild( new RowLabel( RNA_POLYMERASE ), constraints );
            constraints.gridx++;
            constraints.insets.left = 20;
            addChild( new HBox( addCreatorNode( new RnaPolymeraseCreatorNode( BiomoleculeToolBoxNode.this ) ),
                                addCreatorNode( new RnaPolymeraseCreatorNode( BiomoleculeToolBoxNode.this ) ) ),
                      constraints );

            // Ribosomes.
            constraints.gridx = 0;
            constraints.gridy++;
            constraints.insets.left = 0;
            addChild( new RowLabel( GeneExpressionBasicsResources.Strings.RIBOSOME ), constraints );

            constraints.gridx++;
            constraints.insets.left = 20;
            addChild( new HBox( addCreatorNode( new RibosomeCreatorNode( BiomoleculeToolBoxNode.this ) ),
                                addCreatorNode( new RibosomeCreatorNode( BiomoleculeToolBoxNode.this ) ) ),
                      constraints );

            // mRNA destroyer.
            constraints.gridx = 0;
            constraints.gridy++;
            constraints.insets.left = 0;
            addChild( new RowLabel( MRNA_DESTROYER ), constraints );

            constraints.gridx++;
            constraints.insets.left = 20;
            addChild( new HBox( addCreatorNode( new MessengerRnaDestroyerCreatorNode( BiomoleculeToolBoxNode.this ) ),
                                addCreatorNode( new MessengerRnaDestroyerCreatorNode( BiomoleculeToolBoxNode.this ) ) ),
                      constraints );

            // Negative transcription factor(s).
            for ( TranscriptionFactorConfig tfConfig : gene.getTranscriptionFactorConfigs() ) {
                if ( !tfConfig.isPositive ) {
                    constraints.gridx = 0;
                    constraints.gridy++;
                    constraints.insets.left = 0;
                    addChild( new RowLabel( GeneExpressionBasicsResources.Strings.NEGATIVE_TRANSCRIPTION_FACTOR_HTML ), constraints );
                    constraints.gridx++;
                    constraints.insets.left = 20;
                    addChild( new HBox( addCreatorNode( new TranscriptionFactorCreatorNode( BiomoleculeToolBoxNode.this, tfConfig, true ) ) ), constraints );
                }
            }
        }};

        // Place the content into a control panel node.
        addChild( new ControlPanelNode( contentNode, new Color( 250, 250, 250 ) ) );
    }

    public void reset() {
        for ( BiomoleculeCreatorNode biomoleculeCreatorNode : biomoleculeCreatorNodeList ) {
            biomoleculeCreatorNode.reset();
        }
    }

    // Convenience function for making it easy to create a biomolecule creator
    // node and add it to the content panel at the same time.
    private BiomoleculeCreatorNode addCreatorNode( BiomoleculeCreatorNode biomoleculeCreatorNode ) {
        biomoleculeCreatorNodeList.add( biomoleculeCreatorNode );
        return biomoleculeCreatorNode;
    }

    /**
     * Convenience class for creating row labels.
     */
    private static class RowLabel extends HTMLNode {
        private RowLabel( String text ) {
            super( text );
            setFont( new PhetFont( 16 ) );
        }
    }

    // PNode that, when clicked on, will add an RNA polymerase to the model.
    private static class RnaPolymeraseCreatorNode extends BiomoleculeCreatorNode {
        // Scaling factor for this node when used as a creator node.  May be
        // significantly different from the size of the corresponding element
        // in the model.
        private static final double SCALING_FACTOR = 0.07;
        private static final ModelViewTransform SCALING_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                                                         new Point2D.Double( 0, 0 ),
                                                                                                                         SCALING_FACTOR );

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
                           RnaPolymerase rnaPolymerase = new RnaPolymerase( biomoleculeBoxNode.model, pos );
                           biomoleculeBoxNode.model.addMobileBiomolecule( rnaPolymerase );
                           return rnaPolymerase;
                       }
                   },
                   new VoidFunction1<MobileBiomolecule>() {
                       public void apply( MobileBiomolecule mobileBiomolecule ) {
                           biomoleculeBoxNode.model.removeMobileBiomolecule( mobileBiomolecule );
                       }
                   },
                   biomoleculeBoxNode
            );
        }
    }

    // PNode that, when clicked on, will add a ribosome to the active area.
    private static class RibosomeCreatorNode extends BiomoleculeCreatorNode {
        // Scaling factor for this node when used as a creator node.  May be
        // significantly different from the size of the corresponding element
        // in the model.
        private static final double SCALING_FACTOR = 0.07;
        private static final ModelViewTransform SCALING_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                                                         new Point2D.Double( 0, 0 ),
                                                                                                                         SCALING_FACTOR );

        private RibosomeCreatorNode( final BiomoleculeToolBoxNode biomoleculeBoxNode ) {
            super( new MobileBiomoleculeNode( SCALING_MVT, new Ribosome( new StubGeneExpressionModel() ) ),
                   biomoleculeBoxNode.canvas,
                   biomoleculeBoxNode.mvt,
                   new Function1<Point2D, MobileBiomolecule>() {   // Molecule creator function.
                       public MobileBiomolecule apply( Point2D pos ) {
                           Ribosome srs = new Ribosome( biomoleculeBoxNode.model, pos );
                           biomoleculeBoxNode.model.addMobileBiomolecule( srs );
                           return srs;
                       }
                   },
                   new VoidFunction1<MobileBiomolecule>() {
                       public void apply( MobileBiomolecule mobileBiomolecule ) {
                           biomoleculeBoxNode.model.removeMobileBiomolecule( mobileBiomolecule );
                       }
                   },
                   biomoleculeBoxNode
            );
        }
    }

    // PNode that, when clicked on, will add a transcription factor to the active area.
    private static class TranscriptionFactorCreatorNode extends BiomoleculeCreatorNode {
        // Scaling factor for this node when used as a creator node.  May be
        // significantly different from the size of the corresponding element
        // in the model.
        private static final double SCALING_FACTOR = 0.07;
        private static final ModelViewTransform SCALING_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, 0 ), SCALING_FACTOR );

        private TranscriptionFactorCreatorNode( final BiomoleculeToolBoxNode biomoleculeBoxNode, final TranscriptionFactorConfig tfConfig, final boolean positive ) {
            super( new MobileBiomoleculeNode( SCALING_MVT, new TranscriptionFactor( new StubGeneExpressionModel(), tfConfig, new Point2D.Double( 0, 0 ) ) ),
                   biomoleculeBoxNode.canvas,
                   biomoleculeBoxNode.mvt,
                   new Function1<Point2D, MobileBiomolecule>() {
                       public MobileBiomolecule apply( Point2D pos ) {
                           TranscriptionFactor transcriptionFactor = new TranscriptionFactor( biomoleculeBoxNode.model, tfConfig, pos );
                           biomoleculeBoxNode.model.addMobileBiomolecule( transcriptionFactor );
                           return transcriptionFactor;
                       }
                   },
                   new VoidFunction1<MobileBiomolecule>() {
                       public void apply( MobileBiomolecule mobileBiomolecule ) {
                           biomoleculeBoxNode.model.removeMobileBiomolecule( mobileBiomolecule );
                       }
                   },
                   biomoleculeBoxNode
            );
        }
    }

    // PNode that, when clicked on, will add an mRNA destroyer to the active area.
    private static class MessengerRnaDestroyerCreatorNode extends BiomoleculeCreatorNode {
        // Scaling factor for this node when used as a creator node.  May be
        // significantly different from the size of the corresponding element
        // in the model.
        private static final double SCALING_FACTOR = 0.07;
        private static final ModelViewTransform SCALING_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, 0 ), SCALING_FACTOR );

        private MessengerRnaDestroyerCreatorNode( final BiomoleculeToolBoxNode biomoleculeBoxNode ) {
            super( new MobileBiomoleculeNode( SCALING_MVT, new MessengerRnaDestroyer( new StubGeneExpressionModel() ) ),
                   biomoleculeBoxNode.canvas,
                   biomoleculeBoxNode.mvt,
                   new Function1<Point2D, MobileBiomolecule>() {
                       public MobileBiomolecule apply( Point2D pos ) {
                           MessengerRnaDestroyer mRnaDestroyer = new MessengerRnaDestroyer( biomoleculeBoxNode.model, pos );
                           biomoleculeBoxNode.model.addMobileBiomolecule( mRnaDestroyer );
                           return mRnaDestroyer;
                       }
                   },
                   new VoidFunction1<MobileBiomolecule>() {
                       public void apply( MobileBiomolecule mobileBiomolecule ) {
                           biomoleculeBoxNode.model.removeMobileBiomolecule( mobileBiomolecule );
                       }
                   },
                   biomoleculeBoxNode
            );
        }
    }
}
