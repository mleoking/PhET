/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.test;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.event.ConstrainedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Test program for node structure and event handling of magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestMagnifyingGlass extends JFrame {

    private static final Dimension FRAME_SIZE = new Dimension( 800, 600 );
    private static final double LENS_DIAMETER = 100; // pixels
    private static final double BEZEL_WIDTH = 12; // pixels
    private static final double HANDLE_LENGTH = 65; // pixels
    private static final double HANDLE_WIDTH = HANDLE_LENGTH/4; // pixels
    private static final double HANDLE_ARC_SIZE = 10; // pixels
    private static final double HANDLE_ROTATION = -20; // degrees;
    private static final Color BEZEL_COLOR = Color.GRAY;
    private static final Color HANDLE_COLOR = Color.ORANGE;
    
    public TestMagnifyingGlass() {
        super( "Test Magnifying Glass stuff" );
        
        PhetPCanvas playArea = createCanvas();
        
        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );
        panel.add( playArea, BorderLayout.CENTER );
        
        getContentPane().add( panel );
    }
    
    private PhetPCanvas createCanvas() {
        PhetPCanvas canvas = new PhetPCanvas( new Dimension( 500, 500 ) );
        
        // Drag Area
        PPath dragAreaNode = new PPath();
        dragAreaNode.setPaint( Color.BLACK );
        dragAreaNode.setPathTo( new Rectangle( 0, 0, 350, 300 ) );
        dragAreaNode.setPickable( false );
        canvas.addScreenChild( dragAreaNode );
        dragAreaNode.setOffset( 200, 150 );
        
        // Magnifying Glass
        MagnifyingGlass magnifyingGlass = new MagnifyingGlass();
        canvas.addScreenChild( magnifyingGlass );
        magnifyingGlass.setOffset( 350, 350 );
        Rectangle2D dragBounds = dragAreaNode.localToGlobal( dragAreaNode.getBounds() );
        magnifyingGlass.setDragBounds( dragBounds );
        
        return canvas;
    }
    
    private static class MagnifyingGlass extends PComposite {
        
        private PPath _lensPath;
        private PPath _bezelPath;
        private PPath _handlePath;
        private MagnifyingGlassDragHandler _dragHandler;
        
        public MagnifyingGlass() {
            initNodes();
            initEventHandling();
        }
        
        private void initNodes() {
            
            // Lens
            final double glassRadius = LENS_DIAMETER / 2;
            {
                Shape glassShape = new Ellipse2D.Double( -glassRadius, -glassRadius, LENS_DIAMETER, LENS_DIAMETER ); // x,y,w,h
                _lensPath = new PPath();
                _lensPath.setPathTo( glassShape );
                _lensPath.setPaint( Color.BLACK );
            }
            
            // Bezel 
            {
                final double bezelDiameter = ( LENS_DIAMETER + BEZEL_WIDTH );
                Shape glassShape = new Ellipse2D.Double( -bezelDiameter/2, -bezelDiameter/2, bezelDiameter, bezelDiameter ); // x,y,w,h
                _bezelPath = new PPath();
                _bezelPath.setPathTo( glassShape ); // same shape as glass, but we'll stroke it instead of filling it
                _bezelPath.setPaint( BEZEL_COLOR );
            }
            
            // Handle
            {
                Shape handleShape = new RoundRectangle2D.Double( -HANDLE_WIDTH / 2, glassRadius, HANDLE_WIDTH, HANDLE_LENGTH, HANDLE_ARC_SIZE, HANDLE_ARC_SIZE );
                _handlePath = new PPath();
                _handlePath.setPathTo( handleShape );
                _handlePath.setPaint( HANDLE_COLOR );
                _handlePath.rotate( Math.toRadians( HANDLE_ROTATION ) );
            }
            
            addChild( _handlePath );
            addChild( _bezelPath );
            addChild( _lensPath );
        }
        
        private void initEventHandling() {
            
            addInputEventListener( new CursorHandler() );
            
            _dragHandler = new MagnifyingGlassDragHandler();
            _dragHandler.setTreatAsPointEnabled( true );
            final double bezelRadius = ( LENS_DIAMETER + BEZEL_WIDTH ) / 2;
            _dragHandler.setNodeCenter( bezelRadius, bezelRadius );
            addInputEventListener( _dragHandler );
        }
        
        public void setDragBounds( Rectangle2D dragBounds ) {
            _dragHandler.setDragBounds( dragBounds );
        }
        
        private boolean isInLens( PInputEvent e ) {
            Rectangle2D lensBounds = localToGlobal( _lensPath.getFullBounds() );
            return lensBounds.contains( e.getPosition() );
        }
        
        private void updateDisplay( Point2D point ) {
            System.out.println( "updateDisplay" );
        }
        
        private void selectEigenstate( Point2D point ) {
            System.out.println( "selectEigenstate" );
        }
        
        private void hiliteEigenstate( Point2D point ) {
            System.out.println( "hiliteEigenstate" );
        }
        
        private class MagnifyingGlassDragHandler extends ConstrainedDragHandler {
            
            private boolean _dragging;
            
            public MagnifyingGlassDragHandler() {
                _dragging = false;
            }
            
            public void mouseMoved( PInputEvent e ) {
                if ( isInLens( e ) ) {
                    hiliteEigenstate( e.getPosition() );
                }
            }
            
            public void mouseReleased( PInputEvent e ) {
                super.mouseReleased( e );
                if ( !_dragging ) {
                    if ( isInLens( e ) ) {
                        selectEigenstate( e.getPosition() );
                    }
                }
                _dragging = false;
            }
            
            public void mouseDragged( PInputEvent e ) {
                _dragging = true;
                super.mouseDragged( e );
                updateDisplay( e.getPosition() );
            }
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new TestMagnifyingGlass();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setSize(  FRAME_SIZE );
        frame.setResizable( false );
        frame.show();
    }
}
    
