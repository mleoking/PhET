

package com.pixelzoom.model3d;



import java.awt.*;

import java.awt.event.*;

import java.io.ByteArrayInputStream;

import java.io.IOException;

import java.io.InputStream;

import java.text.DecimalFormat;



import javax.swing.*;



/**

 * Tests the Model3D and Matrix3D classes that Sun distributes

 * with it's Wireframe applet example.

 * <p>

 * Displays an animated standing wave on a ring.

 * The wave can be rotated in 3D space.

 */

public class TestModel3D extends JPanel {



    //----------------------------------------------------------------------------

    // Data structures

    //----------------------------------------------------------------------------

    

    private static class Point3D {

        double x, y, z;

        public Point3D( double x, double y, double z ) {

            this.x = x;

            this.y = y;

            this.z = z;

        }

    }

    

    //----------------------------------------------------------------------------

    // Class data

    //----------------------------------------------------------------------------

    

    private static final float SCALE_FUDGE = 1;

    private static final int WAVE_POINTS = 200;

    private static final double WAVE_RADIUS = 10;

    private static final Dimension PANEL_SIZE = new Dimension( 600, 600 );

    

    //----------------------------------------------------------------------------

    // Instance data

    //----------------------------------------------------------------------------

    

    private Model3D model3D;

    private float xfac;

    private int prevx, prevy;

    private Matrix3D amat;

    private Matrix3D tmat;

    private int count;

    private int frequency;

    

    //----------------------------------------------------------------------------

    // Constructors

    //----------------------------------------------------------------------------

    

    /**

     * Constructor.

     */

    public TestModel3D() throws IOException {

        super();

        

        setSize( PANEL_SIZE );



        count = 0;

        frequency = 6;

        amat = new Matrix3D();

        amat.yrot( 20 );

        amat.xrot( 20 );

        tmat = new Matrix3D();

        

        updateModel3D();

        xfac = xfac();

        

        // Remember where the user last clicked on the model

        addMouseListener( new MouseAdapter() {

            public void mousePressed( MouseEvent e ) {

                prevx = e.getX();

                prevy = e.getY();

            }

        } );

        

        // Drag on the model to rotate

        addMouseMotionListener( new MouseMotionAdapter() {

            public void mouseDragged( MouseEvent e ) {

                tmat.unit();

                float xtheta = ( prevy - e.getY() ) * 360.0f / getWidth();

                float ytheta = ( e.getX() - prevx ) * 360.0f / getHeight();

                tmat.xrot( xtheta );

                tmat.yrot( ytheta );

                amat.mult( tmat );

                repaint();

                prevx = e.getX();

                prevy = e.getY();

            }

        } );

        

        // Periodically advances the standing wave

        Timer timer = new Timer( 30, new ActionListener() {

            public void actionPerformed( ActionEvent e ) {

                try {

                    updateModel3D();

                }

                catch ( IOException ioe ) {

                    ioe.printStackTrace();

                }

            }

        } );

        timer.start();

    }

    

    //----------------------------------------------------------------------------

    // Public interface

    //----------------------------------------------------------------------------



    /**

     * Draws the 3D model.

     */

    public void paintComponent( Graphics g ) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setPaint( Color.WHITE );

        g2.fillRect( 0, 0, getWidth(), getHeight() );

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        if ( model3D != null ) {

            model3D.mat.unit();

            model3D.mat.translate( -( model3D.xmin + model3D.xmax ) / 2, -( model3D.ymin + model3D.ymax ) / 2, -( model3D.zmin + model3D.zmax ) / 2 );

            model3D.mat.mult( amat );

            model3D.mat.scale( xfac, -xfac, 16 * xfac / getWidth() );

            model3D.mat.translate( getWidth() / 2, getHeight() / 2, 8 );

            model3D.transformed = false;

            model3D.paint( g );

        }

    }

    

    /**

     * Sets the standing wave's frequency.

     * @param frequency

     */

    public void setFrequency( int frequency ) {

        this.frequency = frequency;

    }

    

    /**

     * Gets the standing wave's frequency.

     * @return

     */

    public int getFrequency() {

        return frequency;

    }

    

    //----------------------------------------------------------------------------

    // Private methods

    //----------------------------------------------------------------------------

    

    /*

     * ??

     */

    private float xfac() {

        float xw = model3D.xmax - model3D.xmin;

        float yw = model3D.ymax - model3D.ymin;

        float zw = model3D.zmax - model3D.zmin;

        if ( yw > xw ) {

            xw = yw;

        }

        if ( zw > xw ) {

            xw = zw;

        }

        float f1 = getWidth() / xw;

        float f2 = getHeight() / xw;

        return ( 0.7f * ( f1 < f2 ? f1 : f2 ) * SCALE_FUDGE );

    }

    

    /*

     * Updates the 3D model.

     */

    private void updateModel3D() throws IOException {

        

        double amplitude = 3 * Math.sin( count / 10.0 );

        double phase = count / 100.0;



        Point3D[] pts = getPoints( WAVE_POINTS, WAVE_RADIUS, frequency, amplitude, phase );

        InputStream is = pointsToStream( pts );

        model3D = new Model3D( is );

        model3D.compress();

        model3D.findBB();

        

        count++;

        repaint();

    }

    

    /*

     * Gets the points that approximate a standing wave.

     */

    private Point3D[] getPoints( int numberOfPoints, double radius, double zFreq, double zAmp, double zPhase ) {

        Point3D[] points = new Point3D[numberOfPoints];

        for ( int i = 0; i < numberOfPoints; i++ ) {

            double frac = i / ( (double) numberOfPoints - 1 );

            double angle = Math.PI * 2 * frac;

            double x = radius * Math.cos( angle );

            double y = radius * Math.sin( angle );

            double z = zAmp * Math.sin( angle * zFreq + zPhase );

            Point3D pt = new Point3D( x, y, z );

            points[i] = pt;

        }

        return points;

    }

    

    /*

     * Converts an array of 3D points to an input stream.

     * The Model3D constructor is expecting to read an input stream,

     * most likely from a file that contains info about the model.

     * The input stream format is reportedly the same as the Wavefront OBJ file format.

     * Tokens found in Dave's sample data files:

     * v = vertex

     * l = line

     * f = face

     * fo = ?

     * g = ?

     */

    private InputStream pointsToStream( Point3D[] points ) {

        StringBuffer buf = new StringBuffer();

        DecimalFormat formatter = new DecimalFormat( "0.0000" );

        for ( int i = 0; i < points.length; i++ ) {

            Point3D p = points[i];

            buf.append( "v " );

            buf.append( formatter.format( p.x ) );

            buf.append( " " );

            buf.append( formatter.format( p.y ) );

            buf.append( " " );

            buf.append( formatter.format( p.z ) );

            buf.append( System.getProperty( "line.separator" ) );

        }

        for ( int i = 1; i <= points.length; i++ ) {

            buf.append( "l " );

            buf.append( i );

            buf.append( " " );

            buf.append( i + 1 );

            buf.append( System.getProperty( "line.separator" ) );

        }

        buf.append( "l " );

        buf.append( points.length - 1 );

        buf.append( " 1" );

        InputStream is = new ByteArrayInputStream( buf.toString().getBytes() );

        return is;

    }



    //----------------------------------------------------------------------------

    // main

    //----------------------------------------------------------------------------

    

    /**

     * Builds the user interface.

     * The 3D model is in the left panel, controls are in the right panel.

     */

    public static void main( String[] args ) throws IOException {

        

        final TestModel3D pane = new TestModel3D();

        

        JButton increaseButton = new JButton( "Increase Frequency" );

        increaseButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {

                pane.setFrequency( pane.getFrequency() + 1 );

                pane.repaint();

            }

        } );



        JButton decreaseButton = new JButton( "Decrease Frequency" );

        decreaseButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {

                pane.setFrequency( pane.getFrequency() - 1 );

                pane.repaint();

            }

        } );

        

        JPanel controlPanel = new JPanel();

        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.Y_AXIS ) );

        controlPanel.add( increaseButton );

        controlPanel.add( decreaseButton );



        JPanel contentPane = new JPanel( new BorderLayout() );

        contentPane.add( pane, BorderLayout.CENTER );

        contentPane.add( controlPanel, BorderLayout.EAST );

        

        JFrame frame = new JFrame();

        frame.setContentPane( contentPane );

        frame.setSize( 600, 600 );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.setVisible( true );

    }

}

