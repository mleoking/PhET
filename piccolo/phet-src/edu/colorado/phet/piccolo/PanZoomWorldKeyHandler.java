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

    public PanZoomWorldKeyHandler( PhetPCanvas phetPCanvas ) {
        this.phetPCanvas = phetPCanvas;
        panRight = new PanEvent( -translateDX, 0 );
        panLeft = new PanEvent( translateDX, 0 );
        panUp = new PanEvent( 0, translateDX );
        panDown = new PanEvent( 0, -translateDX );
    }

    private class PanEvent extends PActivity {
        private int dx;
        private int dy;

        public PanEvent( int dx, int dy ) {
            super( -1 );
            this.dx = dx;
            this.dy = dy;
        }

        protected void activityStep( long elapsedTime ) {
            super.activityStep( elapsedTime );
            translateWorld( dx, dy );
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

    public void keyPressed( KeyEvent e ) {
        if( e.isShiftDown() ) {
            switch( e.getKeyCode() ) {
                case KeyEvent.VK_UP:
                    zoomWorld( zoomScale );
                    break;
                case KeyEvent.VK_DOWN:
                    zoomWorld( 1.0 / zoomScale );
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
                break;
            case KeyEvent.VK_DOWN:
                panDown.stop();
                break;
            default:
                break;
        }
    }

    public void keyTyped( KeyEvent e ) {
    }
}
