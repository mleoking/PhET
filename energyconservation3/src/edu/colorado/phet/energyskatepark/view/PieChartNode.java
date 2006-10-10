/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.view;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

/**
 * User: Sam Reid
 * Date: Oct 25, 2005
 * Time: 9:49:41 AM
 * Copyright (c) Oct 25, 2005 by Sam Reid
 */

public class PieChartNode extends PNode {
    private PieValue[] slices;
    private Rectangle area;

    // Class to hold a value for a slice
    public static class PieValue {
        double value;
        Color color;

        public PieValue( double value, Color color ) {
            this.value = value;
            this.color = color;
        }
    }

    public PieChartNode( PieValue[] slices, Rectangle area ) {
        this.slices = slices;
        this.area = area;
        update();
    }

    public void setPieValues( PieValue[] values ) {
        this.slices = values;
        update();
    }

    public void setArea( Rectangle area ) {
        this.area = area;
        update();
    }

    private void update() {
        removeAllChildren();
        // Get total value of all slices
        double total = 0.0D;
        for( int i = 0; i < slices.length; i++ ) {
            total += slices[i].value;
        }

        // Draw each pie slice
        double curValue = 0.0D;
        int startAngle = 0;
        for( int i = 0; i < slices.length; i++ ) {
            // Compute the start and stop angles
            startAngle = (int)( curValue * 360 / total );
            int arcAngle = (int)( slices[i].value * 360 / total );

            // Ensure that rounding errors do not leave a gap between the first and last slice
            if( i == slices.length - 1 ) {
                arcAngle = 360 - startAngle;
            }

            // Set the color and draw a filled arc
            PPath path = new PPath( new Arc2D.Double( area.x, area.y, area.width, area.height, startAngle, arcAngle, Arc2D.Double.PIE ) );
            path.setPaint( slices[i].color );
            addChild( path );
            curValue += slices[i].value;
        }
    }


    public static void main( String[] args ) {
        // Show the component in a frame
        PieChartNode.PieValue[] values = new PieValue[]{new PieChartNode.PieValue( 25, Color.red ), new PieChartNode.PieValue( 33, Color.green ), new PieChartNode.PieValue( 20, Color.pink ), new PieChartNode.PieValue( 15, Color.blue )};
        PieChartNode n = new PieChartNode( values, new Rectangle( 100, 100 ) );
        JFrame frame = new JFrame();
        PCanvas contentPane = new PCanvas();
        frame.setContentPane( contentPane );
        contentPane.getLayer().addChild( n );

        frame.setSize( 400, 400 );
        frame.setVisible( true );
    }


}
