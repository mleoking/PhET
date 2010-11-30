package edu.colorado.phet.gravityandorbits.view;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class GrabbableVectorNode extends VectorNode {
    private PhetPPath grabArea;

    public GrabbableVectorNode( final Body body, final Property<ModelViewTransform> modelViewTransform, final Property<Boolean> visible, final Property<ImmutableVector2D> property,
                                final double scale, Color fill, Color outline ) {
        super( body, modelViewTransform, visible, property, scale, fill, outline );
        final Point2D tip = getTip();
        grabArea = new PhetPPath( new Ellipse2D.Double( 0, 0, 40, 40 ), new Color( 0, 0, 0, 0 ), new BasicStroke( 3 ), Color.lightGray ) {{
            final PNode parent = this;
            addChild( new PText( "V" ) {{
                PText v = this;
                setFont( new PhetFont( 28, true ) );
                setTextPaint( Color.gray );
                setOffset( parent.getFullBounds().getWidth() / 2 - v.getFullBounds().getWidth() / 2, parent.getFullBounds().getHeight() / 2 - v.getFullBounds().getHeight() / 2 );
            }} );
            setOffset( tip.getX() - getFullBounds().getWidth() / 2, tip.getY() - getFullBounds().getHeight() / 2 );
        }};
        final SimpleObserver updateGrabArea = new SimpleObserver() {
            public void update() {
                final Point2D tip = getTip();
                grabArea.setOffset( tip.getX() - grabArea.getFullBounds().getWidth() / 2, tip.getY() - grabArea.getFullBounds().getHeight() / 2 );
            }
        };
        property.addObserver( updateGrabArea );
        body.getPositionProperty().addObserver( updateGrabArea );
        addChild( grabArea );
        if ( body.isModifyable() ) {
            grabArea.addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    PDimension delta = event.getDeltaRelativeTo( getParent() );
                    final Dimension2D d = modelViewTransform.getValue().viewToModelDelta( delta );
                    Point2D modelDelta = new Point2D.Double( d.getWidth(), d.getHeight() );
                    body.setVelocity( body.getVelocity().getAddedInstance( modelDelta.getX() / scale, modelDelta.getY() / scale ) );
                }
            } );
            grabArea.addInputEventListener( new CursorHandler() );//todo: use same pattern as in body node so that mouse turns into cursor when arrow moves under stationary mouse?
        }
        else {
            grabArea.setVisible( false );
        }
        grabArea.moveToBack();
    }
}
