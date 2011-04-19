// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.bendinglight.model.Medium;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import static java.awt.Color.darkGray;

/**
 * Graphically depicts a draggable prism.
 *
 * @author Sam Reid
 */
public class PrismNode extends PNode {
    public final Prism prism;

    public PrismNode( final ModelViewTransform transform, final Prism prism, final Property<Medium> prismMedium ) {
        this.prism = prism;
        addChild( new PhetPPath( new BasicStroke(), darkGray ) {{
            prism.shape.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( prism.shape.getValue().toShape() ) );
                }
            } );
            prismMedium.addObserver( new SimpleObserver() {
                public void update() {
                    final Color color = prismMedium.getValue().color;
                    Function1<Integer, Integer> darker = new Function1<Integer, Integer>() {
                        public Integer apply( Integer value ) {
                            return Math.min( value - 28, 255 );
                        }
                    };
                    setPaint( color );
                    setStrokePaint( new Color( darker.apply( color.getRed() ), darker.apply( color.getGreen() ), darker.apply( color.getBlue() ) ) );
                }
            } );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    prism.translate( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
                }
            } );
        }} );

        class DragHandle extends PNode {
            double width = 10;

            DragHandle() {
                addChild( new PhetPPath( new Rectangle2D.Double( -width / 2, -width / 2, width, width ), Color.white, new BasicStroke( 1 ), Color.gray ) {{}} );
                prism.shape.addObserver( new SimpleObserver() {
                    public void update() {
                        setOffset( transform.modelToView( prism.shape.getValue().getPoint( 0 ) ).toPoint2D() );
                    }
                } );
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new PBasicInputEventHandler() {
                    double previousAngle;

                    public void mousePressed( PInputEvent event ) {
                        previousAngle = getAngle( event );
                    }

                    private double getAngle( PInputEvent event ) {
                        return new ImmutableVector2D( prism.shape.getValue().getCentroid().toPoint2D(),
                                                      transform.viewToModel( event.getPositionRelativeTo( getParent() ) ) ).getAngle();
                    }

                    public void mouseDragged( PInputEvent event ) {
                        double angle = getAngle( event );
                        prism.rotate( angle - previousAngle );
                        previousAngle = angle;
                    }
                } );
            }
        }
        addChild( new DragHandle() );
    }
}
