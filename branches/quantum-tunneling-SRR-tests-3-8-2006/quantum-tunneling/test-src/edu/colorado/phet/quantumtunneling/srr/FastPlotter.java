/* Copyright 2004, Sam Reid */
package edu.colorado.phet.quantumtunneling.srr;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Mar 10, 2006
 * Time: 2:53:35 PM
 * Copyright (c) Mar 10, 2006 by Sam Reid
 */

public class FastPlotter {
    private JFrame frame;
    private PPath path;
    private PCanvas pCanvas;

    public FastPlotter( String title ) {
        frame = new JFrame( title );
        frame.setSize( 800, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        pCanvas = new PCanvas();
        frame.setContentPane( pCanvas );
        path = new PPath();
        path.setStroke( new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER ) );
        pCanvas.getLayer().addChild( path );
    }

    public void setVisible( boolean visible ) {
        frame.setVisible( visible );
    }

    public void setData( Point2D.Double []objects ) {
        GeneralPath path = new GeneralPath();
        path.reset();
        path.moveTo( (float)objects[0].x, (float)objects[0].y );
        float sy = 250;
        float dy = pCanvas.getHeight() / 2;
        for( int i = 1; i < objects.length; i++ ) {
            Point2D.Double object = objects[i];
            path.lineTo( (float)object.x / 2, ( (float)object.y * sy ) + dy );
        }
        this.path.setPathTo( path );
    }

}