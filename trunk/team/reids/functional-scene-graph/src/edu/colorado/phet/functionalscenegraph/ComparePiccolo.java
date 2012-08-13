package edu.colorado.phet.functionalscenegraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * @author Sam Reid
 */
public class ComparePiccolo {
    public static void main( String[] args ) {
        new JFrame( "Test" ) {{
            setContentPane( new PCanvas() {{
                setPreferredSize( new Dimension( 800, 600 ) );
                setPanEventHandler( null );
                for ( int i = 0; i < 1000; i++ ) {
                    final int finalI = i;
                    getLayer().addChild( new PPath( new Ellipse2D.Double( 0, 0, 10, 10 ), null ) {{
                        setPaint( new Color( finalI % 255, 0, 0 ) );
                        translate( finalI / 10, finalI / 10 );
                    }} );
                }
                getLayer().addChild( new PPath( new Ellipse2D.Double( 0, 0, 200, 200 ), null ) {{
                    setPaint( Color.blue );
                    addInputEventListener( new PBasicInputEventHandler() {
                        @Override public void mouseDragged( final PInputEvent event ) {
                            translate( event.getCanvasDelta().width, event.getCanvasDelta().height );
                        }
                    } );
                }} );
            }} );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }}.setVisible( true );
    }
}