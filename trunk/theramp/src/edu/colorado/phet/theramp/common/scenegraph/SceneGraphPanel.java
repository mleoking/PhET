/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphics2D;
import edu.colorado.phet.common.view.util.GraphicsState;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 8:19:18 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class SceneGraphPanel extends JPanel {
    private GraphicLayerNode rootGraphic;
    private MouseHandler mouseHandler;
    private Cursor cursor;
    private ArrayList dirtyRegions = new ArrayList();
    private boolean drawDirtyRegions = false;
    private KeyHandler keyHandler;

    public SceneGraphPanel() {
        rootGraphic = new GraphicLayerNode();
        rootGraphic.setName( "Root (in " + this + ")" );
        rootGraphic.setSceneGraphPanel( this );
        mouseHandler = new MouseHandler();
        keyHandler = new KeyHandler();
        addKeyListener( keyHandler );
        addMouseListener( mouseHandler );
        addMouseMotionListener( mouseHandler );
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                double width = SceneGraphPanel.this.getWidth();
                double height = SceneGraphPanel.this.getHeight();

                double renderWidth = 600;
                double renderHeight = 600;

                double sw = width / renderWidth;
                double sh = height / renderHeight;
                double min = Math.min( sw, sh );
                rootGraphic.setTransform( AffineTransform.getScaleInstance( min, min ) );
            }
        } );
    }

    public void addDirtyRegion( Shape dirty ) {
        if( dirty != null ) {
            Rectangle bounds = dirty.getBounds();
//        System.out.println( "SceneGraphPanel.addDirtyRegion, shape=" + bounds );
//        new Exception( "SceneGraphPanel.addDirtyRegion, shape=" + bounds ).printStackTrace( );
            RepaintManager.currentManager( this ).addDirtyRegion( this, bounds.x, bounds.y, bounds.width, bounds.height );
            dirtyRegions.add( bounds );
        }
    }

    private MouseListener requestFocusListener = new MouseAdapter() {
        public void mousePressed( MouseEvent e ) {
            requestFocus();
        }
    };

    public void setRequestFocusOnMousePress( boolean requestFocusOnMousePress ) {
        while( Arrays.asList( getMouseListeners() ).contains( requestFocusListener ) ) {
            removeMouseListener( requestFocusListener );
        }
        if( requestFocusOnMousePress ) {
            addMouseListener( requestFocusListener );
        }
    }

    private class KeyHandler implements KeyListener {

        public void keyPressed( KeyEvent e ) {
            rootGraphic.keyPressed( e );
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void keyReleased( KeyEvent e ) {
            rootGraphic.keyReleased( e );
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void keyTyped( KeyEvent e ) {
            rootGraphic.keyTyped( e );
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private class MouseHandler implements MouseInputListener {
//        private AbstractGraphic handler = new NullGraphic();
        private MouseEvent lastEvent = null;

        public void mouseClicked( MouseEvent e ) {
            rootGraphic.mouseClicked( toSceneGraphMouseEvent( e ) );//todo not implemented yet.
            lastEvent = e;
        }

        public void mouseEntered( MouseEvent e ) {
            lastEvent = e;
        }

        public void mouseExited( MouseEvent e ) {
            lastEvent = e;
        }

        public void mousePressed( MouseEvent e ) {
            rootGraphic.mousePressed( toSceneGraphMouseEvent( e ) );
            AbstractGraphic graphic = rootGraphic.getHandler( toSceneGraphMouseEvent( e ) );
            if( graphic == null ) {
                rootGraphic.disableKeyFocusTree();
            }
            lastEvent = e;
        }

        public void mouseReleased( MouseEvent e ) {
            rootGraphic.mouseReleased( toSceneGraphMouseEvent( e ) );
            lastEvent = e;
        }

        public void mouseDragged( MouseEvent e ) {
            rootGraphic.mouseDragged( toSceneGraphMouseEvent( e ) );
            lastEvent = e;
        }

        public void mouseMoved( MouseEvent e ) {
            rootGraphic.handleEntranceAndExit( toSceneGraphMouseEvent( e ) );
            AbstractGraphic graphic = rootGraphic.getHandler( toSceneGraphMouseEvent( e ) );

//            System.out.println( "handler= " + graphic );
            Cursor cursor = graphic == null ? null : graphic.getCursor();
            if( cursor == null ) {
                cursor = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
            }
            setCursor( cursor );
            lastEvent = e;
        }

        private SceneGraphMouseEvent toSceneGraphMouseEvent( MouseEvent e ) {
            return new SceneGraphMouseEvent( e, getPreviousMouseEvent( e ) );
        }

        public MouseEvent getPreviousMouseEvent( MouseEvent currentEvent ) {
            if( lastEvent == null ) {
                return currentEvent;
            }
            else {
                return lastEvent;
            }
        }
    }

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        PhetGraphics2D phetGraphics2D = new PhetGraphics2D( (Graphics2D)g );
        GraphicsState state = new GraphicsState( phetGraphics2D );
        rootGraphic.paint( phetGraphics2D );
        state.restoreGraphics();
        state = new GraphicsState( phetGraphics2D );
        debugDirtyRegions( phetGraphics2D );
        state.restoreGraphics();
    }

    private void debugDirtyRegions( Graphics2D graphics2D ) {
        if( drawDirtyRegions ) {
            graphics2D.setStroke( new BasicStroke( 1 ) );
            for( int i = 0; i < dirtyRegions.size(); i++ ) {
                Rectangle rectangle = (Rectangle)dirtyRegions.get( i );
                graphics2D.setColor( randomColor() );
                graphics2D.drawRect( rectangle.x, rectangle.y, rectangle.width - 1, rectangle.height - 1 );
            }
        }
        dirtyRegions.clear();
    }

    private Color randomColor() {
        return new Color( (float)Math.random(), (float)Math.random(), (float)Math.random() );
    }

    public void addGraphic( AbstractGraphic graphic ) {
        rootGraphic.addGraphic( graphic );
    }

    public void addGraphic( AbstractGraphic graphic, double layer ) {
        rootGraphic.addGraphic( graphic, layer );
    }

    public GraphicLayerNode getGraphic() {
        return rootGraphic;
    }

    public boolean isDrawDirtyRegions() {
        return drawDirtyRegions;
    }

    public void setDrawDirtyRegions( boolean drawDirtyRegions ) {
        this.drawDirtyRegions = drawDirtyRegions;
    }

}
