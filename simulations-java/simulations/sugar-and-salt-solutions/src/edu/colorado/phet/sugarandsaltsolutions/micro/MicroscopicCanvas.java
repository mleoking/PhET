// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.jbox2d.common.Vec2;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.micro.MicroscopicModel.CircleBody;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class MicroscopicCanvas extends PhetPCanvas {

    public static final Dimension canvasSize = new Dimension( 1008, 676 );
    private PNode rootNode;

    public MicroscopicCanvas( final MicroscopicModel model ) {
        //Set the stage size according to the same aspect ratio as used in the model

        //Gets the ModelViewTransform used to go between model coordinates (SI) and stage coordinates (roughly pixels)
        //Create the transform from model (SI) to view (stage) coordinates
        final ModelViewTransform transform = ModelViewTransform.createRectangleMapping( new Rectangle( -100, -100, 200, 200 ), new Rectangle2D.Double( 0, 0, canvasSize.getHeight(), canvasSize.getHeight() ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        //Show the circles in the model
        for ( final CircleBody circleBody : model.getBodyList() ) {
            double diameter = transform.modelToViewDeltaX( circleBody.circleDef.radius * 2 );
            final PhetPPath bodyPath = new PhetPPath( new Ellipse2D.Double( -diameter / 2, -diameter / 2, diameter, diameter ), Color.blue );

            //Update the graphics for the updated model objects
            VoidFunction0 update = new VoidFunction0() {
                public void apply() {
                    Vec2 position = circleBody.body.getPosition();
                    ImmutableVector2D view = transform.modelToView( new ImmutableVector2D( position.x, position.y ) );
                    bodyPath.setOffset( view.getX(), view.getY() );
                }
            };
            model.addFrameListener( update );

            //Also update initially
            update.apply();
            rootNode.addChild( bodyPath );
        }
    }
}
