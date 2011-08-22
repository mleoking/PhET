// Copyright 2002-2011, University of Colorado
package org.reid.scenic;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

    public ScenicPanel( VoidFunction2<T, Graphics2D> painter, Function2<T, MouseEvent, T> mousePressHandler ) {
        this.painter = painter;
        this.mousePressHandler = mousePressHandler;
        addMouseListener( new MouseAdapter() {
            @Override public void mousePressed( MouseEvent e ) {
                model = ScenicPanel.this.mousePressHandler.apply( model, e );
            }
        } );
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