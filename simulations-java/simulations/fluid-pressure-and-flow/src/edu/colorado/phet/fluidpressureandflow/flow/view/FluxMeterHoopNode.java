// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.dialogs.ColorChooserFactory;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.flow.model.FluxMeter;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * Interactive Piccolo graphic for the flux meter node, which attaches to the pipe and shows the flux as a function of position
 * The user can drag it back and forth, and enable/disable it from the control panel.
 *
 * @author Sam Reid
 */
public class FluxMeterHoopNode extends PNode {

    //Developer control for showing a color chooser
    private final boolean showColorChooser = false;

    public FluxMeterHoopNode( final ModelViewTransform transform, final FluxMeter fluxMeter, final boolean frontLayer ) {

        //Use a clip to split the hoop since the top layer should only show the left half of the hoop, so particles will go in front of it
        addChild( new PClip() {{
            final PClip clip = this;

            //Don't show the bounds of the clipping region
            setStrokePaint( null );

            //Show a watered-down color for the back layer so that it looks like it is behind water
            final Color color = frontLayer ? new Color( 27, 31, 208 ) : new Color( 84, 130, 193 );
            addChild( new PhetPPath( new BasicStroke( 8 ), color ) {{

                final SimpleObserver updateShape = new SimpleObserver() {
                    public void update() {
                        //Tuned by hand so it matches the perspective of the pipe graphics
                        double ellipseWidth = 0.45;

                        //Create a hoop to catch the water flux, have to flip the y-values going from model to view
                        final ImmutableVector2D top = fluxMeter.getTop();
                        final ImmutableVector2D bottom = fluxMeter.getBottom();
                        final Ellipse2D.Double modelShape = new Ellipse2D.Double( top.getX() - ellipseWidth / 2, bottom.getY(), ellipseWidth, -1 * ( bottom.getY() - top.getY() ) );
                        final Shape viewShape = transform.modelToView( modelShape );
                        setPathTo( viewShape );
                        int clipWidth = 1000;
                        clip.setPathTo( new Rectangle2D.Double( -clipWidth + viewShape.getBounds2D().getCenterX(), -1000, frontLayer ? clipWidth : clipWidth * 2, 2000 ) );
                    }
                };

                //Update the shape of the flux meter whenever the user drags it or when the pipe changes shape
                fluxMeter.x.addObserver( updateShape );
                fluxMeter.pipe.addShapeChangeListener( updateShape );

                //Make it so the user can drag the flux meter back and forth along the pipe
                addInputEventListener( new FluxMeterDragHandler( UserComponents.fluxMeterHoop, transform, fluxMeter, this ) );

                //Developer control for changing the color
                if ( showColorChooser ) {
                    ColorChooserFactory.showDialog( "Color", null, (Color) getStrokePaint(), new ColorChooserFactory.Listener() {
                        public void colorChanged( Color color ) {
                            setStrokePaint( color );
                        }

                        public void ok( Color color ) {
                            setStrokePaint( color );
                        }

                        public void cancelled( Color originalColor ) {
                            setStrokePaint( originalColor );
                        }
                    }, true );
                }
            }} );
        }} );

        //Only show the flux meter if the user has selected it in the control panel
        fluxMeter.visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );

                //Don't let invisible object intercept mouse events
                setPickable( visible );
                setChildrenPickable( visible );
            }
        } );
    }
}