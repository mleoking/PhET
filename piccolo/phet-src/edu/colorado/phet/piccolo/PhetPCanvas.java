/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.colorado.phet.piccolo.pswing.PSwingCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.util.PDebug;

import java.awt.*;
import java.awt.event.*;

/**
 * Piccolo canvas extension that provides support for maintenance of aspect ratio,
 * and convenience methods for usage.
 */

public class PhetPCanvas extends PSwingCanvas {
    private Dimension renderingSize = null;
    private ComponentAdapter resizeAdapter;

    public PhetPCanvas() {
        removeInputEventListener( getZoomEventHandler() );
        removeInputEventListener( getPanEventHandler() );

        resizeAdapter = new ResizeAdapter();
        addComponentListener( resizeAdapter );
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );
        addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
            }

            public void keyReleased( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_D ) {
                    PDebug.debugRegionManagement = !PDebug.debugRegionManagement;
                }
                else if( e.getKeyCode() == KeyEvent.VK_S ) {
                    identityScale();
                }
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
    }

    protected class ResizeAdapter extends ComponentAdapter {
        public void componentResized( ComponentEvent e ) {
            //use renderingSize to set zoom.
            updateScale();
        }

        public void componentShown( ComponentEvent e ) {
            //if first time shown, set rendering size.
            if( renderingSize == null ) {
                setRenderingSize();
            }
        }
    }

    protected void updateScale() {
        if( renderingSize == null ) {
            if( isVisible() ) {
                setRenderingSize();
            }
            else {
                return;
            }
        }
        double sx = getScaleX();
        double sy = getScaleY();
        //use the smaller

        double scale = sx < sy ? sx : sy;
//        System.out.println( "sx = " + sx + ", sy=" + sy + ", scale=" + scale );
        double cameraViewScale = getCamera().getViewScale();
        System.out.println( "scale=" + scale );
        getCamera().scaleView( scale / cameraViewScale );
    }

    public void identityScale() {
        double cameraViewScale = getCamera().getViewScale();
        System.out.println( "scale=" + 1.0 );
        getCamera().scaleView( 1.0 / cameraViewScale );
    }

    private double getScaleY() {
        return ( (double)getHeight() ) / renderingSize.height;
    }

    private double getScaleX() {
        return ( (double)getWidth() ) / renderingSize.width;
    }

    private void setRenderingSize() {
        setRenderingSize( getSize() );
    }

    public void setRenderingSize( Dimension dim ) {
        this.renderingSize = new Dimension( dim );
    }

    /*
    Convenience methods.
    */
    public void addChild( int layer, PNode graphic ) {
        getLayer().addChild( layer, graphic );
    }

    public void addChild( PNode graphic ) {
        getLayer().addChild( graphic );
    }

    public void removeChild( PNode graphic ) {
        getLayer().removeChild( graphic );
    }

    public void setDebugRegionManagement( boolean debugRegionManagement ) {
        PDebug.debugRegionManagement = debugRegionManagement;
    }

    public void setDebugFrameRateToConsole( boolean frameRateToConsole ) {
        PDebug.debugPrintFrameRate = frameRateToConsole;
    }

    public void setDebugFullBounds( boolean debugFullBounds ) {
        PDebug.debugFullBounds = debugFullBounds;
    }

    public void addActivity( PActivity activity ) {
        getRoot().addActivity( activity );
    }

    public void removeActivity( PActivity activity ) {
        getRoot().getActivityScheduler().removeActivity( activity );
    }

}
