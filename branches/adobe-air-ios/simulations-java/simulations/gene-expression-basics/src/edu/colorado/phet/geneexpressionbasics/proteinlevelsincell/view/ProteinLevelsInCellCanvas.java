// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.proteinlevelsincell.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.geneexpressionbasics.GeneExpressionBasicsResources;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model.ManualGeneExpressionModel;
import edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view.BiomoleculeToolBoxNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author John Blanco
 */
public class ProteinLevelsInCellCanvas extends PhetPCanvas implements Resettable {

    // Stage size, based on default screen size.
    private static Dimension2D STAGE_SIZE = new PDimension( 1008, 679 );

    private final ModelViewTransform mvt;
    private PTransformActivity activity;
    private final Vector2D viewportOffset = new Vector2D( 0, 0 );
    private final List<BiomoleculeToolBoxNode> biomoleculeToolBoxNodeList = new ArrayList<BiomoleculeToolBoxNode>();

    public ProteinLevelsInCellCanvas( final ManualGeneExpressionModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this, STAGE_SIZE ) );

        // Set up the model-canvas transform.
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.70 ) ),
                0.1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        // Set the background color.
        setBackground( new Color( 190, 231, 251 ) );

        // TODO: Temp - put up a drawing of the way this tab will ultimately look.
        PNode screenImage = new PImage( GeneExpressionBasicsResources.Images.SECOND_TAB_STATIC_PICTURE_VERSION_2 );
        screenImage.scale( STAGE_SIZE.getWidth() / screenImage.getFullBoundsReference().getWidth() );
        addWorldChild( screenImage );

    }

    public void reset() {
    }
}
