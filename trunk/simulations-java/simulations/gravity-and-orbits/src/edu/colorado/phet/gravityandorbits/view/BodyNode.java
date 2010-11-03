package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
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
    private ModelViewTransform2D modelViewTransform2D;
    private Body body;
    private final Property<Boolean> toScaleProperty;

    public BodyNode( final Body body, final ModelViewTransform2D modelViewTransform2D, Property<Boolean> toScaleProperty ) {
        this.modelViewTransform2D = modelViewTransform2D;
        this.body = body;
        this.toScaleProperty = toScaleProperty;
        // Create and add the sphere node.
        final SphericalNode sphere = new SphericalNode( getViewDiameter(), createPaint( getViewDiameter() ), false );
        addChild( sphere );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                body.setUserControlled( true );
            }

            public void mouseDragged( PInputEvent event ) {
                body.translate( modelViewTransform2D.viewToModelDifferential( event.getDeltaRelativeTo( getParent() ) ) );
            }

            public void mouseReleased( PInputEvent event ) {
                body.setUserControlled( false );
            }
        } );
        body.getPositionProperty().addObserver( new SimpleObserver() {
            public void update() {
                setOffset( modelViewTransform2D.modelToView( body.getPosition() ) );
            }
        } );
        final SimpleObserver updateDiameter = new SimpleObserver() {
            public void update() {
                sphere.setDiameter( getViewDiameter() );
                sphere.setPaint( createPaint( getViewDiameter() ) );
            }
        };
        body.getDiameterProperty().addObserver( updateDiameter );
        toScaleProperty.addObserver( updateDiameter );
    }

    private double getViewDiameter() {
        if ( toScaleProperty.getValue() ) {
            return Math.max( modelViewTransform2D.modelToViewDifferentialXDouble( body.getDiameter() ), 2 );
        }
        else {
            return modelViewTransform2D.modelToViewDifferentialXDouble( body.getDiameter() ) + 20;
        }
    }

    private Paint createPaint( double diameter ) {// Create the gradient paint for the sphere in order to give it a 3D look.
        Paint spherePaint = new RoundGradientPaint( diameter / 8, -diameter / 8,
                                                    body.getHighlight(),
                                                    new Point2D.Double( diameter / 4, diameter / 4 ),
                                                    body.getColor() );
        return spherePaint;
    }
}
