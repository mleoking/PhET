/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.PNode;
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
        else {
            switch( e.getKeyCode() ) {
                case KeyEvent.VK_RIGHT:
                    panRight.start();
                    break;
                case KeyEvent.VK_LEFT:
                    panLeft.start();
                    break;
                case KeyEvent.VK_UP:
                    panUp.start();
                    break;
                case KeyEvent.VK_DOWN:
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
            }
        }
    }

    private void zoomWorld( double scale ) {
        PNode worldNode = phetPCanvas.getWorldNode();
        Point2D point = phetPCanvas.getCamera().getBounds().getCenter2D();
        phetPCanvas.getCamera().localToGlobal( point );
        worldNode.globalToLocal( point );
        worldNode.scaleAboutPoint( scale, point );
    }

    private void translateWorld( int dx, int dy ) {
        PNode worldNode = phetPCanvas.getWorldNode();
        worldNode.translate( dx, dy );
    }

    public void keyReleased( KeyEvent e ) {
        switch( e.getKeyCode() ) {
            case KeyEvent.VK_RIGHT:
                panRight.stop();
                break;
            case KeyEvent.VK_LEFT:
                panLeft.stop();
                break;
            case KeyEvent.VK_UP:
                panUp.stop();
                zoomIn.stop();
                break;
            case KeyEvent.VK_DOWN:
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
