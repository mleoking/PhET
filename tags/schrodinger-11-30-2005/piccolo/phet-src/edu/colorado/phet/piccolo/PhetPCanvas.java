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
    //it's very difficult to have things point to each other when there is a camera layer between them.   
    private PhetRootPNode phetRootNode;

    public PhetPCanvas() {
        this.phetRootNode = new PhetRootPNode();
        getLayer().addChild( phetRootNode );
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
                    setWorldScaleIdentity();
                }
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        addKeyListener( new PanZoomWorldKeyHandler( this ) );
        addKeyListener( new ShowControlsKeyHandler( this ) );
        requestFocus();
    }

    public PNode getWorldNode() {
        return phetRootNode.getWorldNode();
    }

    public PNode getScreenNode() {
        return phetRootNode.getScreenNode();
    }

    public void setScreenNode( PNode screenNode ) {
        phetRootNode.setScreenNode( screenNode );
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
        setWorldScale( scale );
    }

    private void setWorldScale( double scale ) {
        phetRootNode.setWorldScale( scale );
    }

    public void setWorldScaleIdentity() {
        setWorldScale( 1.0 );
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

    public void setRenderingSize( int width, int height ) {
        setRenderingSize( new Dimension( width, height ) );
    }

    /*
    Convenience methods.
    */
    public void addWorldChild( int layer, PNode graphic ) {
        phetRootNode.addWorldChild( layer, graphic );
    }

    public void addScreenChild( PNode node ) {
        phetRootNode.addScreenChild( node );
    }

    public void removeScreenChild( PNode node ) {
        phetRootNode.removeScreenChild( node );
    }

    public void addWorldChild( PNode graphic ) {
        phetRootNode.addWorldChild( graphic );
    }

    public void removeWorldChild( PNode graphic ) {
        phetRootNode.removeWorldChild( graphic );
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

    public PhetRootPNode getPhetRootNode() {
        return phetRootNode;
    }
}
