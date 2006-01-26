/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.help;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 * HelpPane
 * 
 * PROBLEMS:
 * - glass pane doesn't draw until you cause a refresh
 * - glass pane doesn't refresh whenever what it's on top of changes
 * - two strange red circles in upper left of PCanvas
 * - piccolo-phet CursorHandler doesn't work
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HelpPane extends PCanvas {

    private JFrame parentFrame;
    private PCanvas playArea;
    private MouseTracker mouseTracker;

    public HelpPane( final JFrame parentFrame, PCanvas playArea ) {
        super();

        this.parentFrame = parentFrame;
        this.playArea = playArea;

        setEnabled( false );
        setFocusable( false );
        setBackground( new Color( 0, 0, 0, 0 ) ); // transparent
        setPanEventHandler( null );
        setZoomEventHandler( null );

        getLayer().setPickable( false );
        getLayer().setChildrenPickable( false );

        mouseTracker = new MouseTracker( parentFrame );
        super.addMouseListener( mouseTracker );
        super.addMouseMotionListener( mouseTracker );
        
        Timer timer = new Timer( 1000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getLayer().removeAllChildren();
                showAll( parentFrame.getContentPane() );
            }
        } );
        timer.start();
    }

    public synchronized void addFocusListener( FocusListener l ) {}

    public synchronized void addInputEventListener( PInputEventListener l ) {}

    public synchronized void addInputMethodListener( InputMethodListener l ) {}

    public synchronized void addKeyListener( KeyListener l ) {}

    public synchronized void addMouseListener( MouseListener l ) {}

    public synchronized void addMouseMotionListener( MouseMotionListener l ) {}

    public synchronized void addMouseWheelListener( MouseWheelListener l ) {}

//    public Cursor getCursor() {
//        return playArea.getCursor();
//    }

    public Cursor getCursor() {
        Cursor cursor = null;
        Component sibling = parentFrame.getLayeredPane();
        int x = mouseTracker.getMouseX();
        int y = mouseTracker.getMouseY();
        Component component = SwingUtilities.getDeepestComponentAt( sibling, x, y );
        if ( component != null ) {
            cursor = component.getCursor();
        }
        return cursor;
    }

    private void showAll( Container container ) {
        for ( int i = 0; i < container.getComponentCount(); i++ ) {
            Component c = container.getComponent( i );
            if ( c.isVisible() ) {
                if ( c instanceof JButton || c instanceof JRadioButton ) {
                    Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                    PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                    path.setPaint( Color.RED );
                    path.setOffset( loc );
                    getLayer().addChild( path );
                }
                else if ( c instanceof JCheckBox ) {
                    Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                    PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                    path.setPaint( Color.BLUE );
                    path.setOffset( loc );
                    getLayer().addChild( path );
                }
                else if ( c instanceof JSlider ) {
                    Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                    PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                    path.setPaint( Color.GREEN );
                    path.setOffset( loc );
                    getLayer().addChild( path );
                }
                else if ( c instanceof Container ) {
                    showAll( (Container) c );
                }
            }
        }
    }

    /////////////////////////////////////////////

    private class MouseTracker implements MouseInputListener {

        private JFrame _frame;
        private Point _mouseLocation;
        
        public MouseTracker( JFrame frame ) {
            this._frame = frame;
            _mouseLocation = new Point();
        }

        public Point getMouseLocation() {
            return _mouseLocation;
        }
        
        public int getMouseX() {
            return _mouseLocation.x;
        }
        
        public int getMouseY() {
            return _mouseLocation.y;
        }
        
        public void mouseClicked( MouseEvent event ) {
            redispatch( event );
        }
        
        public void mousePressed( MouseEvent event ) {
            redispatch( event );
        }
        
        public void mouseReleased( MouseEvent event ) {
            redispatch( event );
        }
        
        public void mouseEntered( MouseEvent event ) {
            redispatch( event );
        }
        
        public void mouseExited( MouseEvent event ) {
            redispatch( event );
        }
        
        public void mouseDragged( MouseEvent event ) {
            redispatch( event );
        }
        
        public void mouseMoved( MouseEvent event ) {
            _mouseLocation.setLocation( event.getPoint() );
            redispatch( event );
        }

        private void redispatch( MouseEvent event ) {
            
            Component glassPane = _frame.getGlassPane();
            Container contentPane = _frame.getContentPane();
            JMenuBar menuBar = _frame.getJMenuBar();
            
            Point glassPanePoint = event.getPoint();
            
            Container container = contentPane;
            Point containerPoint = SwingUtilities.convertPoint( glassPane, glassPanePoint, contentPane );
            if ( containerPoint.y < 0 ) {
                container = menuBar;
                containerPoint = SwingUtilities.convertPoint( glassPane, glassPanePoint, menuBar );
            }

            //XXX: If the event is from a component in a popped-up menu,
            //XXX: then the container should probably be the menu's
            //XXX: JPopupMenu, and containerPoint should be adjusted
            //XXX: accordingly.
            
            Component component = SwingUtilities.getDeepestComponentAt( container, containerPoint.x, containerPoint.y );
            if ( component != null ) {
                Point componentPoint = SwingUtilities.convertPoint( glassPane, glassPanePoint, component);
                component.dispatchEvent( remapMouseEvent( event, component, componentPoint ) );
            }
        }
        
        private MouseEvent remapMouseEvent( MouseEvent e, Component component, Point point ) {
            return new MouseEvent( component, e.getID(), e.getWhen(), e.getModifiers(), point.x, point.y, e.getClickCount(), e.isPopupTrigger() );
        }
    }
}