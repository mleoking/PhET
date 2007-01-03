/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;


/**
 * ZoomControl is a control for horizontal or vertical zooming.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 *          <p/>
 *          <p/>
 *          Copied from Fourier simulation on 9-3-2006.
 *          <p/>
 *          Ported from ZoomControl in Fourier/Moving Man.
 */
public class ZoomControlNode extends PNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static final Point IN_LOCATION = new Point( 31, 13 );
    private static final Point OUT_LOCATION = new Point( 3, 13 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private int _orientation;
    private PImage _inButton, _inButtonPressed;
    private PImage _outButton, _outButtonPressed;
    private EventListenerList _listenerList;
    private boolean _inPressed, _outPressed;

    private static final String IMAGES_DIRECTORY = "images/zoom/";
    public static final String ZOOM_BACKGROUND_HORIZONTAL_IMAGE = IMAGES_DIRECTORY + "zoomBackgroundHorizontal.png";
    public static final String ZOOM_BACKGROUND_VERTICAL_IMAGE = IMAGES_DIRECTORY + "zoomBackgroundVertical.png";
    public static final String ZOOM_IN_BUTTON_IMAGE = IMAGES_DIRECTORY + "zoomInButton.png";
    public static final String ZOOM_IN_BUTTON_PRESSED_IMAGE = IMAGES_DIRECTORY + "zoomInButtonPressed.png";
    public static final String ZOOM_OUT_BUTTON_IMAGE = IMAGES_DIRECTORY + "zoomOutButton.png";
    public static final String ZOOM_OUT_BUTTON_PRESSED_IMAGE = IMAGES_DIRECTORY + "zoomOutButtonPressed.png";

    public static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ZoomControlNode( int orientation ) {
        _orientation = orientation;

        PImage background;
        if( orientation == HORIZONTAL ) {
            background = new PImage( loadBufferedImage( ZOOM_BACKGROUND_HORIZONTAL_IMAGE ) );
        }
        else {
            background = new PImage( loadBufferedImage( ZOOM_BACKGROUND_VERTICAL_IMAGE ) );
        }
        addChild( background );

        _inButton = new PImage( loadBufferedImage( ZOOM_IN_BUTTON_IMAGE ) );
        _inButtonPressed = new PImage( loadBufferedImage( ZOOM_IN_BUTTON_PRESSED_IMAGE ) );
        _inButton.setOffset( IN_LOCATION );
        _inButtonPressed.setOffset( IN_LOCATION );
        addChild( _inButton );
        addChild( _inButtonPressed );

        _outButton = new PImage( loadBufferedImage( ZOOM_OUT_BUTTON_IMAGE ) );
        _outButtonPressed = new PImage( loadBufferedImage( ZOOM_OUT_BUTTON_PRESSED_IMAGE ) );
        _outButton.setOffset( OUT_LOCATION );
        _outButtonPressed.setOffset( OUT_LOCATION );
        addChild( _outButton );
        addChild( _outButtonPressed );

        // Interactivity

        background.setPickable( false );
        background.setChildrenPickable( false );

        _inButton.addInputEventListener( new CursorHandler() );
        _inButtonPressed.addInputEventListener( new CursorHandler() );
        _outButton.addInputEventListener( new CursorHandler() );
        _outButtonPressed.addInputEventListener( new CursorHandler() );

        PBasicInputEventHandler inListener = new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                if( !_inButtonPressed.getVisible() ) {
                    _inButtonPressed.setVisible( true );
                    _inButtonPressed.setPickable( true );
                    _inButtonPressed.setChildrenPickable( true );
                    _inPressed = true;
                }
            }

            public void mouseReleased( PInputEvent event ) {
                if( _inPressed ) {
                    _inButtonPressed.setVisible( false );
                    _inButtonPressed.setPickable( false );
                    _inButtonPressed.setChildrenPickable( false );
                    _inPressed = false;
                    // Handle the event
                    fireZoomIn();
                }
            }
        };
        _inButton.addInputEventListener( inListener );
        _inButtonPressed.addInputEventListener( inListener );

        PBasicInputEventHandler outListener = new PBasicInputEventHandler() {

            public void mousePressed( PInputEvent event ) {
                if( !_outButtonPressed.getVisible() ) {
                    _outButtonPressed.setVisible( true );
                    _outButtonPressed.setPickable( true );
                    _outButtonPressed.setChildrenPickable( true );
                    _outPressed = true;
                }
            }

            public void mouseReleased( PInputEvent event ) {
                if( _outPressed ) {
                    _outButtonPressed.setVisible( false );
                    _outButtonPressed.setPickable( false );
                    _outButtonPressed.setChildrenPickable( false );
                    _outPressed = false;
                    // Handle the event
                    fireZoomOut();
                }
            }
        };
        _outButton.addInputEventListener( outListener );
        _outButtonPressed.addInputEventListener( outListener );

        // Initial visibility
        _inButtonPressed.setVisible( false );
        _inButtonPressed.setPickable( false );
        _inButtonPressed.setChildrenPickable( false );

        _outButtonPressed.setVisible( false );
        _outButtonPressed.setPickable( false );
        _outButtonPressed.setChildrenPickable( false );

        _listenerList = new EventListenerList();
    }


    public int getOrientation() {
        return _orientation;
    }

    private BufferedImage loadBufferedImage( String image ) {
        try {
            return ImageLoader.loadBufferedImage( image );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public void setZoomInEnabled( boolean enabled ) {
        _inButton.setVisible( enabled );
        _inButton.setPickable( enabled );
        _inButton.setChildrenPickable( enabled );
    }

    public void setZoomOutEnabled( boolean enabled ) {
        _outButton.setVisible( enabled );
        _outButton.setPickable( enabled );
        _outButton.setChildrenPickable( enabled );
    }

    public void addZoomListener( ZoomListener listener ) {
        _listenerList.add( ZoomListener.class, listener );
    }

    public void removeZoomListener( ZoomListener listener ) {
        _listenerList.remove( ZoomListener.class, listener );
    }

    public void removeAllZoomListeners() {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ZoomListener.class ) {
                _listenerList.remove( ZoomListener.class, (ZoomListener)listeners[i + 1] );
            }
        }
    }

    private void fireZoomIn() {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ZoomListener.class ) {
                ( (ZoomListener)listeners[i + 1] ).zoomedIn();
            }
        }
    }

    private void fireZoomOut() {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ZoomListener.class ) {
                ( (ZoomListener)listeners[i + 1] ).zoomedOut();
            }
        }
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /**
     * ZoomListener is the listener interface for zoom events.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    public interface ZoomListener extends java.util.EventListener {

        /**
         * Invoked when a zoom out occurs.
         */
        void zoomedOut();

        /**
         * Invoked when a zoom in occurs.
         */
        void zoomedIn();
    }


    public static void main( String[] args ) {
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        ZoomControlNode zc = new ZoomControlNode( HORIZONTAL );
        ZoomControlNode zc2 = new ZoomControlNode( VERTICAL );
        zc2.setOffset( 300, 300 );
        phetPCanvas.addScreenChild( zc );
        phetPCanvas.addScreenChild( zc2 );
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( phetPCanvas );
        frame.setSize( 800, 600 );
        frame.setVisible( true );
        zc.setZoomInEnabled( false );
        zc.addZoomListener( new ZoomListener() {
            public void zoomedOut() {
                System.out.println( "horizontal zoom out" );
            }

            public void zoomedIn() {
                System.out.println( "horizontal zoom in" );
            }
        } );
        zc2.addZoomListener( new ZoomListener() {
            public void zoomedOut() {
                System.out.println( "vertical out" );
            }

            public void zoomedIn() {
                System.out.println( "vertical in" );
            }
        } );
    }
}