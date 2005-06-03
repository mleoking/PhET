/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 8:19:18 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class SceneGraphPanel extends JPanel {
    private GraphicListNode rootGraphic;
    private MouseHandler mouseHandler;
    private Cursor cursor;

    public SceneGraphPanel() {
        rootGraphic = new GraphicListNode();
        mouseHandler = new MouseHandler();
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

    protected void processMouseMotionEvent( MouseEvent e ) {
        super.processMouseMotionEvent( e );
    }

    private class MouseHandler implements MouseInputListener {
//        private AbstractGraphic handler = new NullGraphic();
        private MouseEvent lastEvent = null;

        public void mouseClicked( MouseEvent e ) {
            rootGraphic.mouseClicked( toSceneGraphMouseEvent( e ) );
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
            System.out.println( "handler= " + graphic );
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
        rootGraphic.paint( (Graphics2D)g );
    }

    public void addGraphic( AbstractGraphic graphic ) {
        rootGraphic.addGraphic( graphic );
    }

    public GraphicListNode getGraphic() {
        return rootGraphic;
    }

}
