// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class can be used to display a Pie Chart in Piccolo.  See main() for sample usage and behavior.
 * //todo: create addPieValue() method instead of having to set all data at once
 */
public class PieChartNode extends PNode {
    private PieValue[] slices;//The values to show in the pie
    private Rectangle area;//The area which the pie should take up
    private double initialAngle = 0; // Angle, from the middle of the right side, from which to start drawing.

    /*
     * Creates a PieChartNode with the specified slices and area
     */
    public PieChartNode( PieChartNode.PieValue[] slices, Rectangle area ) {
        this.slices = slices;
        this.area = area;
        update();
    }

    public void setPieValues( PieChartNode.PieValue[] values ) {
        this.slices = values;
        update();
    }

    public void setArea( Rectangle area ) {
        this.area = area;
        update();
    }

    /**
     * Set the initial angle for drawing the pie slices.  Zero (the default)
     * means that the first slice will start at the right middle.  A value
     * of PI/2 would start at the top of the pie.  And so on.
     *
     * @param initialAngle - In radians.
     */
    public void setInitialAngle( double initialAngle ) {
        this.initialAngle = initialAngle;
        update();
    }

    private void update() {
        removeAllChildren();
        // Get total value of all slices
        double total = getTotal();

        // Draw each pie slice
        double initialAngleInDegrees = Math.toDegrees( initialAngle );
        double curValue = 0.0;
        for ( int i = 0; i < slices.length; i++ ) {
            // Compute the start and stop angles
            double startAngle = curValue * 360 / total + initialAngleInDegrees;
            double arcAngle = slices[i].value * 360 / total;

            // Ensure that rounding errors do not leave a gap between the first and last slice
            if ( i == slices.length - 1 ) {
                arcAngle = 360 - startAngle + initialAngleInDegrees;
            }

            // If the slice has a non-zero value, set the color and draw a filled arc.
            if ( slices[i].getValue() > 0 ) {
                PPath path;
                if ( slices[i].getValue() == total ) {
                    // This slice represents the entire pie, so just draw it as a circle.
                    path = new PPath( new Ellipse2D.Double( area.x, area.y, area.width, area.height ) );
                }
                else {
                    // Draw the pie slice as an arc.
                    path = new PPath( new Arc2D.Double( area.x, area.y, area.width, area.height, startAngle, arcAngle, Arc2D.Double.PIE ) );
                }
                path.setPaint( slices[i].color );
                addChild( path );
            }
            curValue += slices[i].value;
        }
    }

    public double getTotal() {
        double total = 0.0D;
        for ( int i = 0; i < slices.length; i++ ) {
            total += slices[i].value;
        }
        return total;
    }

    // Class to hold a value for a slice
    public static class PieValue {
        double value;
        Color color;

        public PieValue( double value, Color color ) {
            this.value = value;
            this.color = color;
        }

        public double getValue() {
			return value;
		}

        public void setValue(double value) {
			this.value = value;
		}

        public Color getColor() {
			return color;
		}

        public void setColor(Color color) {
			this.color = color;
		}
    }

    /**
     * Sample usage of PieChartNode
     */
    public static void main( String[] args ) {
        PieChartNode.PieValue[] values = new PieValue[]{
                new PieChartNode.PieValue( 25, Color.red ),
                new PieChartNode.PieValue( 33, Color.green ),
                new PieChartNode.PieValue( 20, Color.pink ),
                new PieChartNode.PieValue( 15, Color.blue )};
        PieChartNode n = new PieChartNode( values, new Rectangle( 100, 100 ) );
        JFrame frame = new JFrame();
        PCanvas contentPane = new PCanvas();
        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        contentPane.getLayer().addChild( n );

        frame.setSize( 400, 400 );
        frame.setVisible( true );
    }


}
