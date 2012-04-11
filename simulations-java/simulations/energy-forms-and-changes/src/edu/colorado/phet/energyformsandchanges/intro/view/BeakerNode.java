// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.intro.model.Beaker;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Piccolo node that represents a beaker in the view.
 *
 * @author John Blanco
 */
public class BeakerNode extends PNode {

    private static final Stroke OUTLINE_STROKE = new BasicStroke( 3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Color OUTLINE_COLOR = Color.LIGHT_GRAY;
    private static final Color FLUID_COLOR = new Color( 220, 180, 180 );
    private static final double PERSPECTIVE_PROPORTION = 0.2;
    private static final Font LABEL_FONT = new PhetFont( 32, false );
    private static final boolean SHOW_MODEL_RECT = true;

    public BeakerNode( final Beaker beaker, final ModelViewTransform mvt ) {

        // Get a version of the rectangle that defines the beaker size and
        // location in the view.
        final Rectangle2D beakerViewRect = mvt.modelToView( beaker.getOutlineRect() ).getBounds2D();

        // Add the left and right sides of the beaker.
        addChild( new PhetPPath( new Line2D.Double( beakerViewRect.getMinX(), beakerViewRect.getMinY(), beakerViewRect.getMinX(), beakerViewRect.getMaxY() ),
                                 OUTLINE_STROKE,
                                 OUTLINE_COLOR ) );
        addChild( new PhetPPath( new Line2D.Double( beakerViewRect.getMaxX(), beakerViewRect.getMinY(), beakerViewRect.getMaxX(), beakerViewRect.getMaxY() ),
                                 OUTLINE_STROKE,
                                 OUTLINE_COLOR ) );

        // Add the top and bottom of the beaker.
        double ellipseHeight = beakerViewRect.getWidth() * PERSPECTIVE_PROPORTION;
        addChild( new PhetPPath( new Ellipse2D.Double( beakerViewRect.getMinX(), beakerViewRect.getMaxY() - ellipseHeight / 2, beakerViewRect.getWidth(), ellipseHeight ),
                                 OUTLINE_STROKE,
                                 OUTLINE_COLOR ) );
        addChild( new PhetPPath( new Ellipse2D.Double( beakerViewRect.getMinX(), beakerViewRect.getMinY() - ellipseHeight / 2, beakerViewRect.getWidth(), ellipseHeight ),
                                 OUTLINE_STROKE,
                                 OUTLINE_COLOR ) );

        // Add the label.
        PText label = new PText( "Water" );
        label.setFont( LABEL_FONT );
        label.centerFullBoundsOnPoint( beakerViewRect.getCenterX(), beakerViewRect.getMinY() + label.getFullBoundsReference().height * 2 );
        addChild( label );

        // If enabled, show the outline of the rectangle that represents the
        // beaker's position in the model.
        if ( SHOW_MODEL_RECT ) {
            addChild( new PhetPPath( mvt.modelToViewRectangle( beaker.getOutlineRect() ), new BasicStroke( 1 ), Color.RED ) );
        }
    }
}
