

package com.pixelzoom.dave3d;



import java.awt.*;

import java.awt.event.*;

import java.io.ByteArrayInputStream;

import java.io.IOException;

import java.io.InputStream;

import java.text.DecimalFormat;



import javax.swing.*;



/**

 * Test the Model3D and Matrix3D classes from

 * http://www.cs.cf.ac.uk/Dave/JAVA/3d/3d.html

 */

public class TestModel3D extends JPanel {



    private static final float SCALE_FUDGE = 1;

    private static final int WAVE_POINTS = 200;

    private static final double WAVE_RADIUS = 10;

    private static final Dimension PANEL_SIZE = new Dimension( 600, 600 );

    

    private Model3D model3d;

    private float xfac;

    private int prevx, prevy;

    private Matrix3D amat;

    private Matrix3D tmat;

    private int count;

    private int frequency;



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

        

        addMouseListener( new MouseAdapter() {

            public void mousePressed( MouseEvent e ) {

                prevx = e.getX();

                prevy = e.getY();

            }

        } );

        

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

    

    private float xfac() {

        float xw = model3d.xmax - model3d.xmin;

        float yw = model3d.ymax - model3d.ymin;

        float zw = model3d.zmax - model3d.zmin;

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

        model3d = new Model3D( is );



        model3d.compress();

        model3d.findBB();

        

        count++;

        repaint();

    }

    

    /*

     * Gets the points that approximate a standing wave.

     */

    private Point3D[] getPoints( int numPts, double radius, double zFreq, double zAmp, double zPhase ) {

        Point3D[] pts = new Point3D[numPts];

        for ( int i = 0; i < numPts; i++ ) {

            double frac = i / ( (double) numPts - 1 );

            double angle = Math.PI * 2 * frac;

            double x = radius * Math.cos( angle );

            double y = radius * Math.sin( angle );

            double z = zAmp * Math.sin( angle * zFreq + zPhase );

            Point3D pt = new Point3D( x, y, z );

            pts[i] = pt;

        }

        return pts;

    }

    

    /*

     * Converts an array of 3D points to an input stream,

     * required by the Model3D constructor.

     */

    private InputStream pointsToStream( Point3D[] pts ) {

        StringBuffer buf = new StringBuffer();

        DecimalFormat de = new DecimalFormat( "0.0000" );

        for ( int i = 0; i < pts.length; i++ ) {

            Point3D pt = pts[i];

            buf.append( "v " );

            buf.append( de.format( pt.getX() ) );

            buf.append( " " );

            buf.append( de.format( pt.getY() ) );

            buf.append( " " );

            buf.append( de.format( pt.getZ() ) );

            buf.append( System.getProperty( "line.separator" ) );

        }

        for ( int i = 1; i <= pts.length; i++ ) {

            buf.append( "l " );

            buf.append( i );

            buf.append( " " );

            buf.append( i + 1 );

            buf.append( System.getProperty( "line.separator" ) );

        }

        buf.append( "l " );

        buf.append( pts.length - 1 );

        buf.append( " 1" );

        InputStream is = new ByteArrayInputStream( buf.toString().getBytes() );

        return is;

    }



    public void paintComponent( Graphics g ) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setPaint( Color.WHITE );

        g2.fillRect( 0, 0, getWidth(), getHeight() );

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        if ( model3d != null ) {

            model3d.mat.unit();

            model3d.mat.translate( -( model3d.xmin + model3d.xmax ) / 2, -( model3d.ymin + model3d.ymax ) / 2, -( model3d.zmin + model3d.zmax ) / 2 );

            model3d.mat.mult( amat );

            model3d.mat.scale( xfac, -xfac, 16 * xfac / getWidth() );

            model3d.mat.translate( getWidth() / 2, getHeight() / 2, 8 );

            model3d.transformed = false;

            model3d.paint( g );

        }

    }



    public static void main( String[] args ) throws IOException {

        

        final TestModel3D pane = new TestModel3D();

        

        JButton increaseButton = new JButton( "Increase Frequency" );

        increaseButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {

                pane.frequency++;

                pane.repaint();

            }

        } );



        JButton decreaseButton = new JButton( "Decrease Frequency" );

        decreaseButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {

                pane.frequency--;

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

