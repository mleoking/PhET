/**
 * Class: StripChart
 * Package: edu.colorado.phet.util
 * Author: Another Guy
 * Date: Aug 8, 2003
 */
package edu.colorado.phet.radiowaves.util;

import edu.colorado.phet.common_1200.model.clock.AbstractClock;
import edu.colorado.phet.common_1200.model.clock.ClockTickListener;
import edu.colorado.phet.common_1200.model.clock.SwingTimerClock;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class StripChart extends JPanel {

    private int width;
    private int height;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private Rectangle2D plotArea;
    private Color plotBackground = Color.WHITE;
    private double[] data;
    private int numData = 0;
    private double timeStep;
    // Upper left corner of plot area
    private Point plotAreaULC;
    private double yScale;
    private int yOffset;

    /**
     * @param width    Display width of the plot area, in pixels
     * @param height   Display height of the plot area, in pixels
     * @param minX     Minimum x value to be plotted
     * @param maxX     Maximum x value to be plotted
     * @param minY     Minimum y value to be plotted
     * @param maxY     Maximum y value to be plotted
     * @param timeStep Time step between data points
     */
    public StripChart( int width, int height, double minX, double maxX, double minY, double maxY, double timeStep ) {
        this.width = width;
        this.height = height;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.timeStep = timeStep;
        data = new double[width];

        yScale = height / ( maxY - minY );
        yOffset = (int)( minY * yScale );
        this.setLayout( null );

        plotAreaULC = new Point( 10, 10 );
        this.setPreferredSize( new Dimension( plotAreaULC.x * 2 + width, plotAreaULC.y * 2 + height ) );

        plotArea = new Rectangle2D.Double( plotAreaULC.x, plotAreaULC.y, width, height );
    }

    private int tickLoc = 0;
    private int tickSpace = 20;
    private Color tickColor = new Color( 128, 128, 128 );

    protected void paintComponent( Graphics g ) {

        super.paintComponent( g );

        Graphics2D g2 = (Graphics2D)g;
        g2.setColor( plotBackground );
        g2.fill( plotArea );
        g2.setColor( Color.BLACK );
        g2.draw( plotArea );
        g2.setColor( Color.RED );

        // Draw horizontal lines
        g2.setColor( tickColor );
        g2.drawLine( plotAreaULC.x, plotAreaULC.y + ( height / 2 ),
                     plotAreaULC.x + width, plotAreaULC.y + ( height / 2 ) );

        for( int i = 1; i < data.length; i++ ) {
//        for( int i = 1; i < numData; i++ ) {
            int d0 = (int)( data[i - 1] * yScale );
            int d1 = (int)( data[i] * yScale );

            // Draw vertical lines
            if( ( i % tickSpace ) == tickLoc ) {
                g2.setColor( tickColor );
                g2.drawLine( i + plotAreaULC.x, plotAreaULC.y,
                             i + plotAreaULC.x, plotAreaULC.y + height );
            }

            // Draw data point
            g2.setColor( Color.RED );
            g2.drawLine( i - 1 + plotAreaULC.x, d0 + plotAreaULC.y - yOffset,
                         i + plotAreaULC.x, d1 + plotAreaULC.y - yOffset );
        }
    }

    public Color getPlotBackground() {
        return plotBackground;
    }

    public void setPlotBackground( Color plotBackground ) {
        this.plotBackground = plotBackground;
    }

    public void addDatum( double datum, double dt ) {

        // Move the vertical tick location
        tickLoc = ( tickLoc + 1 ) % tickSpace;

        // Move all data one spot to the right
        for( int i = data.length - 1; i > 0; i-- ) {
            data[i] = data[i - 1];
        }
        data[0] = datum;
        numData = numData < data.length ? numData + 1 : numData;
        this.repaint( (int)plotArea.getX(), (int)plotArea.getY(), (int)plotArea.getWidth(), (int)plotArea.getHeight() );
    }


    /**
     * Tests the strip chart class by creating a strip chart that tracks
     * a sine function
     *
     * @param args
     */
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        final StripChart sc = new StripChart( 200, 80, 0, 100, -1.5, 1.5, 1 );
        sc.setPlotBackground( new Color( 250, 250, 200 ) );
        sc.setBackground( new Color( 200, 250, 200 ) );
        frame.getContentPane().add( sc );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );

        SwingTimerClock clock = new SwingTimerClock( 1, 20, true );
        clock.start();
        clock.addClockTickListener( new ClockTickListener() {
            double d = 0;

            public void clockTicked( AbstractClock iClock, double v ) {
                d = ( d + 0.1 ) % ( Math.PI * 2 );
                sc.addDatum( Math.sin( d ), 1 );
            }
        } );

    }
}
