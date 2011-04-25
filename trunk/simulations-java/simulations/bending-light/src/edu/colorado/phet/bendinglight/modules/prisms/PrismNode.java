// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.bendinglight.model.Medium;
import edu.colorado.phet.bendinglight.view.CanvasBoundedDragHandler;
import edu.colorado.phet.bendinglight.view.DragEvent;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
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

    public PrismNode( final ModelViewTransform transform,
                      final Prism prism,
                      final Property<Medium> prismMedium//The medium associated with the prism
    ) {
        this.prism = prism;

        //Show the draggable prism shape
        addChild( new PhetPPath( new BasicStroke(), darkGray ) {{
            prism.shape.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( prism.shape.getValue().toShape() ) );
                }
            } );
            prismMedium.addObserver( new SimpleObserver() {
                public void update() {
                    //Set the fill color
                    final Color color = prismMedium.getValue().color;
                    setPaint( color );

                    //Make the border color darker than the fill color
                    Function1<Integer, Integer> darker = new Function1<Integer, Integer>() {
                        public Integer apply( Integer value ) {
                            return (int) MathUtil.clamp( 0, value - 28, 255 );
                        }
                    };
                    setStrokePaint( new Color( darker.apply( color.getRed() ), darker.apply( color.getGreen() ), darker.apply( color.getBlue() ) ) );
                }
            } );

            //Make it draggable, but constrain it within the play area
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new CanvasBoundedDragHandler( this ) {
                @Override protected void dragNode( DragEvent event ) {
                    prism.translate( transform.viewToModelDelta( event.delta ) );
                }
            } );
        }} );

        //Depict drag handles on the PrismNode that allow it to be rotated
        class RotationDragHandle extends PNode {
            double width = 10;

            RotationDragHandle() {
                //It looks like a box on the side of the prism
                addChild( new PhetPPath( new Rectangle2D.Double( -width / 2, -width / 2, width, width ), Color.white, new BasicStroke( 1 ), Color.gray ) {{}} );
                prism.shape.addObserver( new SimpleObserver() {
                    public void update() {
                        setOffset( transform.modelToView( prism.shape.getValue().getPoint( 0 ) ).toPoint2D() );
                    }
                } );

                //Add interaction
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new PBasicInputEventHandler() {
                    double previousAngle;

                    //Store the original angle since rotations are computed as deltas between each event
                    public void mousePressed( PInputEvent event ) {
                        previousAngle = getAngle( event );
                    }

                    //Find the angle about the center of the prism
                    private double getAngle( PInputEvent event ) {
                        return new ImmutableVector2D( prism.shape.getValue().getCentroid().toPoint2D(),
                                                      transform.viewToModel( event.getPositionRelativeTo( getParent() ) ) ).getAngle();
                    }

                    //Drag the prism to rotate it
                    public void mouseDragged( PInputEvent event ) {
                        double angle = getAngle( event );
                        prism.rotate( angle - previousAngle );
                        previousAngle = angle;
                    }
                } );
            }
        }
        addChild( new RotationDragHandle() );
    }
}
