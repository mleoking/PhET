/**
 * Class: Histogram
 * Class: edu.colorado.phet.graphics
 * User: Ron LeMaster
 * Date: Jan 18, 2004
 * Time: 1:55:47 PM
 */
package edu.colorado.phet.instrumentation;

import javax.swing.*;
import java.awt.*;

public class Histogram extends JPanel {

    private HistogramModel model;
    private int bucketWidth = 20;
    private int displayWidth = 300;
    private int displayHeight = 200;
    private int clippingLevel;
    private Color color;
    private Point plotULC = new Point( 10, 10 );
    private int maxBuckHeight;

    /**
     * @param displayWidth   How wide the histogram will appear on the screen, in pixels
     * @param displayHeight  How high the histogram will appear on the screen, in pixels
     * @param lowerBound     The lower bound of data values to be put in the histogram
     * @param upperBound     The upper bound of data values to be put in the histogram
     * @param numBins        The number of bins in the histogram
     * @param clippingHeight The maximum bin count beyond which the histogram will clip
     * @param color          The color of the histogram bars
     */
    public Histogram( int displayWidth, int displayHeight,
                      double lowerBound, double upperBound,
                      int numBins, int clippingHeight,
                      Color color ) {
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.clippingLevel = clippingHeight;
        model = new HistogramModel();
        model.setBounds( lowerBound, upperBound );
        model.setNumIntervals( numBins );
        this.color = color;
        this.bucketWidth = displayWidth / model.getNumIntervals();
        this.maxBuckHeight = displayHeight;
        setPreferredSize( new Dimension( plotULC.x * 2 + displayWidth, plotULC.y * 2 + displayHeight ) );
    }

    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor( Color.WHITE );
        g2.fillRect( plotULC.x, plotULC.y, displayWidth, displayHeight );
        g2.setColor( color );
        for( int i = 0; i < model.getNumIntervals(); i++ ) {
            int value = model.valueAt( i );
            int bucketHeight = Math.min( value * maxBuckHeight / clippingLevel, maxBuckHeight );

            int i1 = displayHeight + plotULC.y - bucketHeight;
            int i2 = displayHeight + plotULC.y;
            g2.fillRect( plotULC.x + i * bucketWidth, displayHeight + plotULC.y - bucketHeight, bucketWidth, bucketHeight );
        }
        g2.setColor( Color.BLACK );
        g2.drawRect( plotULC.x, plotULC.y, displayWidth - 1, displayHeight - 1 );
    }

    /**
     * Adds a datum to the histogram
     *
     * @param datum
     */
    public void add( double datum ) {
        model.add( datum );
    }

    /**
     * Sets the count within a bin beyond which the histogram clips
     *
     * @param clippingLevel
     */
    public void setClippingLevel( int clippingLevel ) {
        this.clippingLevel = clippingLevel;
    }

    /**
     * Clears all data from the histogram
     */
    public void clear() {
        model.clear();
    }


    public static void main( String[] args ) {
        Histogram histogram = new Histogram( 300, 100, 0, 10, 100, 10, Color.green );

        for( int i = 0; i < 500; i++ ) {
            histogram.add( Math.random() * 10 );
        }
//        histogram.add( .5 );
//        histogram.add( .5 );
//        histogram.add( .5 );
//        histogram.add( 2 );
//        histogram.add( 2 );
//        histogram.add( 4.4 );
//        histogram.add( 4.3 );
//        histogram.add( 6 );
//        histogram.add( 6 );
//        histogram.add( 6 );
//        histogram.add( 6 );

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        JPanel panel = new JPanel();
        panel.setPreferredSize( new Dimension( 400, 300 ) );
        panel.add( histogram );
        frame.getContentPane().add( panel );
        frame.pack();
        frame.setVisible( true );
    }
}
