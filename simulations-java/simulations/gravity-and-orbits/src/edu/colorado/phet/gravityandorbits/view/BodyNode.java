package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * @author Sam Reid
 */
public class BodyNode extends PNode {

    public BodyNode( final Body body, final ModelViewTransform2D modelViewTransform2D ) {
        double diameter = modelViewTransform2D.modelToViewDifferentialXDouble( body.getDiameter() );
        // Create the gradient paint for the sphere in order to give it a 3D look.
        Paint spherePaint = new RoundGradientPaint( diameter / 8, -diameter / 8,
                                                    Color.white,
                                                    new Point2D.Double( diameter / 4, diameter / 4 ),
                                                    Color.blue );

        // Create and add the sphere node.
        SphericalNode sphere = new SphericalNode( diameter, spherePaint, false );
        sphere.setOffset( diameter / 2, diameter / 2 );
        addChild( sphere );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseDragged( PInputEvent event ) {
                body.translate( modelViewTransform2D.viewToModelDifferential( event.getDeltaRelativeTo( getParent() ) ) );
            }
        } );
        body.getPositionProperty().addObserver( new SimpleObserver() {
            public void update() {
                setOffset( modelViewTransform2D.modelToView( body.getPosition() ) );
            }
        } );
    }
}
