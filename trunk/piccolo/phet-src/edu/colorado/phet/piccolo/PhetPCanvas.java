/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.colorado.phet.piccolo.pswing.PSwingCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDebug;

import java.awt.*;
import java.awt.event.*;

/**
 * User: Sam Reid
 * Date: Aug 1, 2005
 * Time: 7:55:07 AM
 * Copyright (c) Aug 1, 2005 by Sam Reid
 * <p/>
 * Adds support for maintenance of aspect ratio,
 * and convenience methods for usage.
 */

public class PhetPCanvas extends PSwingCanvas {
    private Dimension renderingSize = null;
    private ComponentAdapter l;

    public PhetPCanvas() {
        removeInputEventListener( getZoomEventHandler() );
        removeInputEventListener( getPanEventHandler() );

        l = new ResizeAdapter();
        addComponentListener( l );
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
//        System.out.println( "scale=" + scale );
        getCamera().scaleView( scale / cameraViewScale );
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

//    public void paintComponent( Graphics g ) {
//        PDebug.startProcessingOutput();
//
//        Graphics2D g2 = (Graphics2D)g.create();
//        g2.setColor( getBackground() );
//        g2.fillRect( 0, 0, getWidth(), getHeight() );
//
//        // create new paint context and set render quality to lowest common
//        // denominator render quality.
//        PPaintContext paintContext = new PPaintContext( g2 );
////        if( getInteracting() || getAnimating() ) {
////            if( interactingRenderQuality < animatingRenderQuality ) {
////                paintContext.setRenderQuality( interactingRenderQuality );
////            }
////            else {
////                paintContext.setRenderQuality( animatingRenderQuality );
////            }
////        }
////        else {
////            paintContext.setRenderQuality( defaultRenderQuality );
////        }
//
//        // paint piccolo
//        boolean doubleBufferMe = false;
//        if( doubleBufferMe ) {
//            Image image = getCamera().toImage( getWidth(), getHeight(), getBackground() );
//            BufferedImage bufferedImage = BufferedImageUtils.toBufferedImage( image );
//            PPaintContext bufferedContext = new PPaintContext( bufferedImage.createGraphics() );
//            getCamera().fullPaint( bufferedContext );
//            g2.drawRenderedImage( bufferedImage, new AffineTransform() );
//        }
//        else {
//            getCamera().fullPaint( paintContext );
//        }
//
////        // if switched state from animating to not animating invalidate the entire
////        // screen so that it will be drawn with the default instead of animating
////        // render quality.
////        if( !getAnimating() && animatingOnLastPaint ) {
////            repaint();
////        }
////        animatingOnLastPaint = getAnimating();
//
//        PDebug.endProcessingOutput( g2 );
//    }

//    public void repaint( long tm, int x, int y, int width, int height ) {
////        super.repaint( tm, x, y, width, height );
//    }
//
//    public void repaint( PBounds bounds ) {
////        super.repaint( bounds );
//    }
//
//    public void repaint( Rectangle r ) {
////        super.repaint( r );
//    }
//
//    public void repaint() {
////        super.repaint();
//    }
//
//    public void repaint( int x, int y, int width, int height ) {
////        super.repaint( x, y, width, height );
//    }
//
//    public void repaint( long tm ) {
////        super.repaint( tm );
//    }


}
