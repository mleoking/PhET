// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowApplication.RESOURCES;

/**
 * @author Sam Reid
 */
public class ThreePatchImagePressureNode extends PNode {
    private ThreePatchImageNode imageNode;

    public ThreePatchImagePressureNode( final Property<String> text ) {
        imageNode = new ThreePatchImageNode( RESOURCES.getImage( "pressure_meter_left.png" ), RESOURCES.getImage( "pressure_meter_center.png" ), RESOURCES.getImage( "pressure_meter_right.png" ) );
        addChild( imageNode );

        final PText textNode = new PText( text.getValue() ) {{
            setFont( new PhetFont( 20, true ) );
        }};
        addChild( textNode );

        //Layout based on the size of the text string
        text.addObserver( new SimpleObserver() {
            public void update() {
                //Update the text itself.
                textNode.setText( text.getValue() );

                imageNode.setCenterComponentWidth( textNode.getFullBounds().getWidth() );

                //Position the text node just to the right of the leftPatch and centered vertically.
                textNode.setOffset( imageNode.leftPatch.getFullBounds().getMaxX(), imageNode.centerPatch.getFullBounds().getHeight() / 2 - textNode.getFullBounds().getHeight() / 2 );
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
                        final ThreePatchImagePressureNode child = new ThreePatchImagePressureNode( string );
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
