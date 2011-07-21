// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.Font;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.RnaPolymerase;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.SmallRibosomalSubunit;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class defines the box on the user interface from which the user can
 * extract various biomolecules and put them into action within the cell.
 *
 * @author John Blanco
 */
public class BiomoleculeBoxNode extends PNode {
    private static final Font LABEL_FONT = new PhetFont( 20 );
    private final ManualGeneExpressionModel model;
    private final ManualGeneExpressionCanvas canvas;
    private final ModelViewTransform mvt;

    public BiomoleculeBoxNode( ManualGeneExpressionModel model, ManualGeneExpressionCanvas canvas, ModelViewTransform mvt ) {
        this.model = model;
        this.canvas = canvas;
        this.mvt = mvt;
        PNode contentNode = new VBox(
                // TODO: i18n
                new PText( "Tool Box" ) {{ setFont( LABEL_FONT ); }},
                // TODO: i18n
                new HBox( new PText( "RNA Polymerase" ), new RnaPolymeraseCreatorNode( BiomoleculeBoxNode.this ), new RnaPolymeraseCreatorNode( BiomoleculeBoxNode.this ), new RnaPolymeraseCreatorNode( BiomoleculeBoxNode.this ) ),
                // TODO: i18n
                new HBox( new PText( "Small Ribosomal Subunit" ), new SmallRibosomalSubunitCreatorNode( BiomoleculeBoxNode.this ), new SmallRibosomalSubunitCreatorNode( BiomoleculeBoxNode.this ), new SmallRibosomalSubunitCreatorNode( BiomoleculeBoxNode.this ) )
        );
        addChild( new ControlPanelNode( contentNode ) );
    }

    // PNode that, when clicked on, will add an RNA polymerase to the model.
    private static class RnaPolymeraseCreatorNode extends BiomoleculeCreatorNode {
        // Scaling factor for this node when used as a creator node.  May be
        // significantly different from the size of the corresponding element
        // in the model.
        private static final double SCALING_FACTOR = 0.1;
        private static final ModelViewTransform SCALING_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, 0 ), SCALING_FACTOR );

        private RnaPolymeraseCreatorNode( final BiomoleculeBoxNode biomoleculeBoxNode ) {
            super( new MobileBiomoleculeNode( SCALING_MVT, new RnaPolymerase() ),
                   biomoleculeBoxNode.canvas,
                   biomoleculeBoxNode.mvt,
                   new Function1<Point2D, MobileBiomolecule>() {
                       public MobileBiomolecule apply( Point2D pos ) {
                           RnaPolymerase rnaPolymerase = new RnaPolymerase( pos );
                           biomoleculeBoxNode.model.addMobileBiomolecule( rnaPolymerase );
                           return rnaPolymerase;
                       }
                   },
                   true );
        }
    }

    // PNode that, when clicked on, will add a small ribosomal subunit to the active area.
    private static class SmallRibosomalSubunitCreatorNode extends BiomoleculeCreatorNode {
        // Scaling factor for this node when used as a creator node.  May be
        // significantly different from the size of the corresponding element
        // in the model.
        private static final double SCALING_FACTOR = 0.1;
        private static final ModelViewTransform SCALING_MVT = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, 0 ), SCALING_FACTOR );

        private SmallRibosomalSubunitCreatorNode( final BiomoleculeBoxNode biomoleculeBoxNode ) {
            super( new MobileBiomoleculeNode( SCALING_MVT, new SmallRibosomalSubunit() ),
                   biomoleculeBoxNode.canvas,
                   biomoleculeBoxNode.mvt,
                   new Function1<Point2D, MobileBiomolecule>() {
                       public MobileBiomolecule apply( Point2D pos ) {
                           SmallRibosomalSubunit srs = new SmallRibosomalSubunit( pos );
                           biomoleculeBoxNode.model.addMobileBiomolecule( srs );
                           return srs;
                       }
                   },
                   true );
        }
    }
}
