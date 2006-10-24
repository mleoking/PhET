package edu.colorado.phet.qm.tests;

import edu.colorado.phet.qm.util.ImageDebugFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Laplace {

    public static void main( String[] args ) {
        int N = 300;
//        int N = Integer.parseInt(args[0]);
        BufferedImage image = new BufferedImage( N + 1, N + 1, BufferedImage.TYPE_INT_RGB );
        ImageDebugFrame imageDebugFrame = new ImageDebugFrame( image );
        imageDebugFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        imageDebugFrame.setVisible( true );
//        StdDraw.create(N+1, N+1);
        double[][] V = new double[N + 1][N + 1];

        // precompute colors
        Color[] colors = new Color[101];
        for( int i = 0; i <= 100; i++ ) {
            int red = ( 255 * i ) / 100;
            int green = 0;
            int blue = 255 - red;
            colors[i] = new Color( red, green, blue );
        }
        for( int i = 0; i <= 100; i = i + 10 ) {
            colors[i] = Color.white;
        }

        // initialize all points with reasonable starting values for the potential
        for( int i = 1; i < N; i++ ) {
            for( int j = 1; j < N; j++ ) {
                V[i][j] = 51.0;
            }
        }

        // boundary conditions
        for( int j = 0; j <= N; j++ ) {
            V[0][j] = 100;   // left
        }
        for( int j = 0; j <= N; j++ ) {
            V[N][j] = 100;   // right
        }
        for( int i = 0; i <= N; i++ ) {
            V[i][0] = 0;   // bottom
        }
        for( int i = 0; i <= N; i++ ) {
            V[i][N] = 0;   // top
        }


        Graphics2D graphics2D = image.createGraphics();
        // numerically solve Laplace's equation
        for( int t = 0; true; t++ ) {

            // draw
            for( int i = 1; i < N; i++ ) {
                for( int j = 1; j < N; j++ ) {
                    int c = (int)Math.round( V[i][j] );
//                    System.out.println( "c = " + c );
                    graphics2D.setColor( new Color( c, 0, 0 ) );
                    graphics2D.fillRect( i, j, 1, 1 );
//                    StdDraw.setColor(colors[c]);
//                    StdDraw.go(i, j);
//                    StdDraw.spot(0);
                }
            }
            imageDebugFrame.setImage( image );
            try {
                Thread.sleep( 20 );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
//	    StdDraw.show();

            // repeat 100 times before drawing to screen
            for( int r = 0; r < 1; r++ ) {
                for( int i = 1; i < N; i++ ) {
                    for( int j = 1; j < N; j++ ) {
                        V[i][j] = 0.25 * ( V[i - 1][j] + V[i + 1][j] + V[i][j - 1] + V[i][j + 1] );
                    }
                }
                V[Math.abs( sourceX ) % N][Math.abs( sourceY ) % N] = 255;
                int n = 2;
                sourceX += random.nextBoolean() ? random.nextInt( n ) : -random.nextInt( n );
                sourceY += random.nextBoolean() ? random.nextInt( n ) : -random.nextInt( n );
                if( sourceX <= 0 ) {
                    sourceX = 0;
                }

            }

        }
    }

    static int sourceX = 100;
    static int sourceY = 100;
    static Random random = new Random();
}

