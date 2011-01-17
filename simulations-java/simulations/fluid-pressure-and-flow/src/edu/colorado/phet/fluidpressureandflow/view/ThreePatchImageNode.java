// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.geom.AffineTransform;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowApplication;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * The ThreePatchImageNode combines 3 images with text to make a readout node.  The center image is stretched to
 * accommodate different lengths of string.
 *
 * @author Sam Reid
 */
public class ThreePatchImageNode extends PNode {
    public ThreePatchImageNode( final Property<String> text ) {
        final PImage leftPatch = new PImage( FluidPressureAndFlowApplication.RESOURCES.getImage( "pressure_meter_left.png" ) );
        addChild( leftPatch );
        final PImage centerPatch = new PImage( FluidPressureAndFlowApplication.RESOURCES.getImage( "pressure_meter_center.png" ) );
        addChild( centerPatch );
        final PImage rightPatch = new PImage( FluidPressureAndFlowApplication.RESOURCES.getImage( "pressure_meter_right.png" ) );
        addChild( rightPatch );
        final PText textNode = new PText( text.getValue() ) {{
            setFont( new PhetFont( 20, true ) );
        }};
        addChild( textNode );

        //Layout based on the size of the text string
        text.addObserver( new SimpleObserver() {
            public void update() {
                //Update the text itself.
                textNode.setText( text.getValue() );

                //Position the text node just to the right of the leftPatch and centered vertically.
                textNode.setOffset( leftPatch.getFullBounds().getMaxX(), centerPatch.getFullBounds().getHeight() / 2 - textNode.getFullBounds().getHeight() / 2 );

                //Stretch center piece to fit the text, it is always an exact fit and there is no minimum.
                centerPatch.setTransform( new AffineTransform() );//reset the centerPatch so that its bounds can be used to compute the right scale sx
                double sx = textNode.getFullBounds().getWidth() / centerPatch.getFullBounds().getWidth();//how much to scale the centerPatch
                centerPatch.setTransform( AffineTransform.getScaleInstance( sx, 1 ) );
                centerPatch.translate( leftPatch.getFullBounds().getMaxX() / sx, 0 );

                //Position the right patch to the side of the stretched center patch so they don't overlap
                rightPatch.setOffset( leftPatch.getFullBounds().getWidth() + textNode.getFullBounds().getWidth(), 0 );
            }
        } );
    }

    /**
     * Test the ThreePatchImageNode.  I'm not sure why the canvas runs away after pressing the spinner buttons.
     *
     * @param args
     */
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new JFrame() {{
                    setContentPane( new PSwingCanvas() {{
                        final Property<String> string = new Property<String>( "0" );
                        final ThreePatchImageNode child = new ThreePatchImageNode( string );
                        getLayer().addChild( child );
                        getLayer().addChild( new PSwing( new JSpinner( new SpinnerNumberModel( 1, 1, 10, 1 ) ) {{
                            addChangeListener( new ChangeListener() {
                                public void stateChanged( ChangeEvent e ) {
                                    String text = "";
                                    for ( int i = 0; i < (Integer) getValue(); i++ ) {
                                        text = text + "" + i;
                                    }
                                    string.setValue( text + " Pa" );
                                }
                            } );
                        }} ) {{
                            setOffset( 100, 100 );
                        }} );
                    }} );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                    setSize( 1024, 768 );
                }}.setVisible( true );
            }
        } );
    }
}
