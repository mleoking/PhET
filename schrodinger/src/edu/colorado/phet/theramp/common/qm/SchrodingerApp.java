/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.qm;

import edu.colorado.phet.common.math.Vector2D;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:06:40 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerApp {
    public void run( int Steps ) {
        int show = 1;
        int XMESH = 130;
        int YMESH = 130;

        double deltaTime = 10E-5;

        double k = 10 * Math.PI;
        double LAMBDA = (int)2 * Math.PI / k * XMESH;

        final Complex[][] wavefunction = new Complex[XMESH + 1][YMESH + 1];

//        Potential potential = new ConstantPotential( 0 );
        int barrierWidth = 5;
        int barrierHeight = 5;
        int barrierX = XMESH / 2 - barrierWidth / 2;
        final Rectangle barrierRect = new Rectangle( barrierX, YMESH / 2 - barrierHeight / 2, barrierWidth, barrierHeight );
//        Potential potential = new BarrierPotential( barrierRect, 10E5 );

        final CompositePotential potential = createDoubleSlit( XMESH, YMESH, barrierX, barrierWidth, 10, 10, 10E5 );
//        Potential potential = new SimpleGradientPotential( 30 );

        BoundaryCondition boundaryCondition = new ZeroBoundaryCondition();
//        BoundaryCondition boundaryCondition = new PlaneWave( k, XMESH );

//        new CylinderWave().initialize( wavefunction );
//        new PlaneWave( k, XMESH ).initialize( wavefunction );
//        new GaussianWave( XMESH, YMESH, new Point( (int)( XMESH *0.85 ), YMESH / 2 ), new Vector2D.Double( 5, 0.0 ), 0.02 ).initialize( wavefunction );
//        new GaussianWave( XMESH, YMESH, new Point( (int)( XMESH *0.85 ), YMESH / 2 ), new Vector2D.Double( 5, 0.0 ), 0.001 ).initialize( wavefunction );
//        new GaussianWave( XMESH, YMESH, new Point( (int)( XMESH *0.85 ), YMESH / 2 ), new Vector2D.Double( 25, 0.0 ), 0.01 ).initialize( wavefunction );
//        new GaussianWave( XMESH, YMESH, new Point( (int)( XMESH *0.85 ), YMESH / 2 ), new Vector2D.Double( 20, 0.0 ), 0.01 ).initialize( wavefunction );
//        new GaussianWave( XMESH, YMESH, new Point( (int)( XMESH *0.85 ), YMESH / 2 ), new Vector2D.Double( 100, 0.0 ), 0.01 ).initialize( wavefunction );
//        new GaussianWave( XMESH, YMESH, new Point( (int)( XMESH * 0.85 ), YMESH / 2 ), new Vector2D.Double( 13, 120.0 ), 0.1 ).initialize( wavefunction );
        new GaussianWave( XMESH, YMESH, new Point( (int)( XMESH * 0.85 ), YMESH / 2 ), new Vector2D.Double( -13, 0 ), 0.01 ).initialize( wavefunction );

        CNCPropagator cncPropagator = new CNCPropagator( deltaTime, boundaryCondition, potential );

//        initGaussian( wavefunction );
        ColorGrid colorGrid = new ColorGrid( 600, 600, XMESH, YMESH );
        ColorMap colorMap = new ColorMap() {
            public Paint getPaint( int i, int k ) {

                double h = Math.abs( wavefunction[i][k].getReal() );
                double s = Math.abs( wavefunction[i][k].getImaginary() );
//                double h = 0.7;
//                double s = 0.5;
                double b = 1 - wavefunction[i][k].abs();
                Color color = new Color( Color.HSBtoRGB( (float)h, (float)s, (float)b ) );
                double potval = potential.getPotential( i, k, 0 );
                if( potval > 0 ) {
                    color = new Color( 100, color.getGreen(), color.getBlue() );
                }
//                if( barrierRect.contains( i, k ) ) {
//                    color = new Color( color.getRed(), color.getGreen(), 30 );
//                }
                return color;
            }
        };
        colorGrid.colorize( colorMap );
        ImageDebugFrame frame = new ImageDebugFrame( colorGrid.getBufferedImage() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
        for( int i = 0; i <= Steps; i++ ) {
//            System.out.println( "Running to step: " + i );
            if( i % show == 0 ) {
                colorGrid.colorize( colorMap );
//                try {
////                    ImageIO.write( colorGrid.image, "png",new File("C:/image_"+i+".png"));
//                }
//                catch( IOException e ) {
//                    e.printStackTrace();
//                }
                frame.setImage( colorGrid.getBufferedImage() );
            }
            cncPropagator.propagate( wavefunction );
//            double barrierX = new PositionValue().compute( wavefunction );
//            System.out.println( "barrierX = " + barrierX );
//            System.out.println( "new ProbabilityValue().compute( wavefunction ) = " + new ProbabilityValue().compute( wavefunction ) );

        }
    }

    private CompositePotential createDoubleSlit( int XMESH, int YMESH, int x, int width, int slitHeight, int slitSeparation, double val ) {
        CompositePotential compositePotential = new CompositePotential();
        int barHeight = ( YMESH - 2 * slitHeight - slitSeparation ) / 2;
        Rectangle top = new Rectangle( x, 0, width, barHeight );
        Rectangle mid = new Rectangle( x, barHeight + slitHeight, width, slitSeparation );
        Rectangle bot = new Rectangle( x, barHeight + slitHeight * 2 + slitSeparation, width, barHeight );
        compositePotential.addPotential( new BarrierPotential( top, val ) );
        compositePotential.addPotential( new BarrierPotential( mid, val ) );
        compositePotential.addPotential( new BarrierPotential( bot, val ) );
        return compositePotential;
    }

    public static void main( String[] args ) {
        new SchrodingerApp().run( 15000 );
    }
}
