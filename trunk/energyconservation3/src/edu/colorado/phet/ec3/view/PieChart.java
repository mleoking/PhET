/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Oct 25, 2005
 * Time: 9:49:41 AM
 * Copyright (c) Oct 25, 2005 by Sam Reid
 */

public class PieChart extends PNode {
// Class to hold a value for a slice
    public static class PieValue {
        double value;
        Color color;

        public PieValue( double value, Color color ) {
            this.value = value;
            this.color = color;
        }
    }

    // slices is an array of values that represent the size of each slice.
    public void drawPie( Graphics2D g, Rectangle area, PieValue[] slices ) {
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
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
            g.setColor( slices[i].color );
            g.fillArc( area.x, area.y, area.width, area.height, startAngle, arcAngle );

            curValue += slices[i].value;
        }
    }

    public static void main( String[] args ) {
        // Show the component in a frame
        new PieChart().test();
    }

    private void test() {

        JFrame frame = new JFrame();
        frame.getContentPane().add( new MyComponent() );
        frame.setSize( 300, 200 );
        frame.setVisible( true );
    }

    class MyComponent extends JComponent {
        PieValue[] slices = new PieValue[4];

        MyComponent() {
            slices[0] = new PieValue( 25, Color.red );
            slices[1] = new PieValue( 33, Color.green );
            slices[2] = new PieValue( 20, Color.pink );
            slices[3] = new PieValue( 15, Color.blue );
        }

        // This method is called whenever the contents needs to be painted
        public void paint( Graphics g ) {
            // Draw the pie
            drawPie( (Graphics2D)g, new Rectangle( 100, 100 ), slices );
        }
    }


}
