// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.geneexpressionbasics.common.model.PlacementHint;
import edu.umd.cs.piccolo.PNode;

/**
 * Class for displaying placement hints, which let the user know where various
 * things (e.g. biomolecules) can and should be placed.
 */
public class PlacementHintNode extends PNode {

    private static final Stroke HINT_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 5.0f, new float[] { 5f, 5f }, 0f );
    private static final Color HINT_STROKE_COLOR = new Color( 0, 0, 0, 100 ); // Somewhat transparent stroke.

    /**
     * Constructor.
     *
     * @param mvt
     * @param placementHint
     */
    public PlacementHintNode( final ModelViewTransform mvt, final PlacementHint placementHint ) {

        // Create a transparent color based on the base color of the molecule.
        Color transparentColor = new Color( placementHint.getBaseColor().getRed(), placementHint.getBaseColor().getGreen(),
                                            placementHint.getBaseColor().getBlue(), 150 );

        addChild( new PhetPPath( transparentColor, HINT_STROKE, HINT_STROKE_COLOR ) {{
            // Update the shape whenever it changes.
            placementHint.addShapeChangeObserver( new VoidFunction1<Shape>() {
                public void apply( Shape shape ) {
                    setPathTo( mvt.modelToView( shape ) );
                }
            } );
        }} );

        // Listen to the property that indicates whether the hint is active and
        // only be visible when it is.
        placementHint.active.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean hintActive ) {
                setVisible( hintActive );
            }
        } );
    }
}
