// Copyright 2002-2011, University of Colorado
package org.reid.scenic;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;

/**
 * This project is an attempt at a functional library for Java2D programming, see readme
 *
 * @author Sam Reid
 */
public class ScenicPanel<T> extends JPanel {

    private T model;
    private VoidFunction2<T, Graphics2D> painter;
    private Function2<T, MouseEvent, T> mousePressHandler;
    private Function2<T, MouseEvent, T> mouseReleasedHandler;
    private Function2<T, MouseEvent, T> mouseMovedHandler;

    public ScenicPanel( T model,
                        VoidFunction2<T, Graphics2D> painter,
                        Function2<T, MouseEvent, T> mousePressHandler,
                        Function2<T, MouseEvent, T> mouseReleasedHandler,
                        Function2<T, MouseEvent, T> mouseMovedHandler ) {
        this.model = model;
        this.painter = painter;
        this.mousePressHandler = mousePressHandler;
        this.mouseReleasedHandler = mouseReleasedHandler;
        this.mouseMovedHandler = mouseMovedHandler;
        addMouseListener( new MouseAdapter() {
            @Override public void mousePressed( MouseEvent e ) {
                ScenicPanel.this.model = ScenicPanel.this.mousePressHandler.apply( ScenicPanel.this.model, e );
            }

            @Override public void mouseReleased( MouseEvent e ) {
                ScenicPanel.this.model = ScenicPanel.this.mouseReleasedHandler.apply( ScenicPanel.this.model, e );
            }
        } );
        addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged( MouseEvent e ) {
            }

            public void mouseMoved( MouseEvent e ) {
                ScenicPanel.this.model = ScenicPanel.this.mouseMovedHandler.apply( ScenicPanel.this.model, e );
            }
        } );
    }

    public T getModel() {
        return model;
    }

    @Override protected void paintComponent( Graphics g ) {
        super.paintComponent( g );    //To change body of overridden methods use File | Settings | File Templates.
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        if ( model != null ) {
            painter.apply( model, g2 );
        }
    }

    public void setModel( T model ) {
        this.model = model;
    }
}