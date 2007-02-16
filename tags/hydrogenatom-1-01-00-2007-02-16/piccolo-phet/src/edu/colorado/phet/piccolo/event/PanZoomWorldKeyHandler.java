/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo.event;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.activities.PActivity;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 13, 2005
 * Time: 1:20:04 AM
 * Copyright (c) Sep 13, 2005 by Sam Reid
 */

public class PanZoomWorldKeyHandler implements KeyListener {
    private PhetPCanvas phetPCanvas;
    private int translateDX = 7;
    private double zoomScale = 1.1;
    private PanEvent panRight;
    private PanEvent panLeft;
    private PanEvent panUp;
    private PanEvent panDown;
    private ZoomEvent zoomIn;
    private ZoomEvent zoomOut;
    private static final int right = KeyEvent.VK_NUMPAD6;
    private static final int left = KeyEvent.VK_NUMPAD4;
    private static final int up = KeyEvent.VK_NUMPAD8;
    private static final int down = KeyEvent.VK_NUMPAD5;

    public PanZoomWorldKeyHandler( PhetPCanvas phetPCanvas ) {
        this.phetPCanvas = phetPCanvas;
        panRight = new PanEvent( -translateDX, 0 );
        panLeft = new PanEvent( translateDX, 0 );
        panUp = new PanEvent( 0, translateDX );
        panDown = new PanEvent( 0, -translateDX );
        zoomIn = new ZoomEvent( zoomScale );
        zoomOut = new ZoomEvent( 1.0 / zoomScale );
    }

    private class PanZoomActivity extends PActivity {
        public PanZoomActivity() {
            super( -1 );
        }

        public void start() {
            if( !phetPCanvas.getRoot().getActivityScheduler().getActivitiesReference().contains( this ) ) {
                phetPCanvas.getRoot().addActivity( this );
            }
        }

        public void stop() {
            while( phetPCanvas.getRoot().getActivityScheduler().getActivitiesReference().contains( this ) ) {
                phetPCanvas.removeActivity( this );
            }
        }
    }

    private class PanEvent extends PanZoomActivity {
        private int dx;
        private int dy;

        public PanEvent( int dx, int dy ) {
            this.dx = dx;
            this.dy = dy;
        }

        protected void activityStep( long elapsedTime ) {
            super.activityStep( elapsedTime );
            translateWorld( dx, dy );
        }

    }

    private class ZoomEvent extends PanZoomActivity {
        private double scale;

        public ZoomEvent( double scale ) {
            this.scale = scale;
        }

        protected void activityStep( long elapsedTime ) {
            super.activityStep( elapsedTime );
            zoomWorld( scale );
        }
    }

    public void keyPressed( KeyEvent e ) {
        if( e.isShiftDown() ) {
            switch( e.getKeyCode() ) {
                case KeyEvent.VK_UP:
                    zoomIn.start();
                    break;
                case KeyEvent.VK_DOWN:
                    zoomOut.start();
                    break;
                default:
                    break;
            }
        }
//        else if( true) {
        switch( e.getKeyCode() ) {
            case right:
                panRight.start();
                break;
            case left:
                panLeft.start();
                break;
            case up:
                panUp.start();
                break;
            case down:
                panDown.start();
                break;
            case KeyEvent.VK_PAGE_UP:
                zoomIn.start();
                break;
            case KeyEvent.VK_PAGE_DOWN:
                zoomOut.start();
                break;
            default:
                break;
//            }
        }
    }

    private void zoomWorld( double scale ) {
        //todo zooming is disabled
        Point2D point = phetPCanvas.getCamera().getBounds().getCenter2D();
        phetPCanvas.getCamera().localToGlobal( point );
        phetPCanvas.getPhetRootNode().globalToWorld( point );
        phetPCanvas.getPhetRootNode().scaleWorldAboutPoint( scale, point );
    }

    private void translateWorld( int dx, int dy ) {
        phetPCanvas.getPhetRootNode().translateWorld( dx, dy );
    }

    public void keyReleased( KeyEvent e ) {
        switch( e.getKeyCode() ) {
            case right:
                panRight.stop();
                break;
            case left:
                panLeft.stop();
                break;
            case up:
                panUp.stop();
                zoomIn.stop();
                break;
            case down:
                panDown.stop();
                zoomOut.stop();
                break;
            case KeyEvent.VK_PAGE_UP:
                zoomIn.stop();
                break;
            case KeyEvent.VK_PAGE_DOWN:
                zoomOut.stop();
                break;
            default:
                break;
        }
    }

    public void keyTyped( KeyEvent e ) {
    }
}