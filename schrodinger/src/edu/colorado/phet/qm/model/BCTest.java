package edu.colorado.phet.qm.model;

// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 6/17/2005 5:52:20 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   t_d_quant.java
///http://www.fen.bilkent.edu.tr/~yalabik/applets/t_d_quant.class

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;

public class BCTest extends Applet
        implements Runnable {
    Thread animatorThread;
    Dimension offDimension;
    Image offImage;
    Graphics offGraphics;
    private Button clear_button;
    private Choice state_choices;
    private int rw;
    private int rh;
    private int rx;
    private int ry;
    private int mode;
    private int x_sm;
    private int x_bg;
    private int last_x;
    double cfo[][];
    private static double buf[][] = new double[2][256];
    double psi[][];
    double cpot[][];
    double cenerg[][];
    double bfou[][];
    double bsi[][];
    double csi[][];
    double xsi[][];
    double cc[][][];
    double bksq[][];
    int pot[];
    double dt;
    double xnorm;
    double bnorm;
    double scale;
    int nn;
    int n_bound;
    int n_bound2;
    int nbo;
    double pi;
    int i0;
    double xk0;
    double width;
    int mu;
    int linen;
    int looper;

    public void init() {
        setBackground( Color.black );
        setForeground( Color.white );
        clear_button = new Button( "Reset" );
        clear_button.setForeground( Color.black );
        clear_button.setBackground( Color.lightGray );
        add( clear_button );
        state_choices = new Choice();
        state_choices.addItem( "packet" );
        state_choices.addItem( "injected" );
        state_choices.setForeground( Color.black );
        state_choices.setBackground( Color.lightGray );
        add( new Label( "Type of wave:" ) );
        add( state_choices );
        mode = 1;
        Rectangle rectangle = new Rectangle( 600, 600 );
        rh = rectangle.height;
        rx = rectangle.x;
        ry = rectangle.y;
        scale = 1.2D / (double)rh;
        pi = 4D * Math.atan( 1.0D );
        n_bound2 = n_bound + n_bound;
        nbo = n_bound / 2;
        i0 = 30;
        xk0 = 0.25D;
        width = 10D;
        for( int i = 0; i < nn; i++ ) {
            if( i > 100 && i < 105 || i > 115 && i < 120 ) {
                pot[i] = ( 4 * rh ) / 5;
            }
            else {
                pot[i] = rh - 20;
            }
        }

        for( int j = 0; j <= nn; j++ ) {
            double d = ( (double)( 2 * j ) * pi ) / (double)nn;
            cfo[0][j] = Math.cos( d );
            cfo[1][j] = Math.sin( d );
        }

        for( int k = 0; k <= n_bound2; k++ ) {
            double d1 = ( (double)( 2 * k ) * pi ) / (double)n_bound2;
            bfou[0][k] = Math.cos( d1 );
            bfou[1][k] = Math.sin( d1 );
        }

        for( int l = 0; l < n_bound; l++ ) {
            for( int k1 = 0; k1 < n_bound; k1++ ) {
                cc[0][l][k1] = 1.0D;
                cc[1][l][k1] = 0.0D;
                for( int l1 = 0; l1 < n_bound; l1++ ) {
                    if( k1 != l1 ) {
                        double d2 = -bfou[0][l] - bfou[0][l1];
                        double d5 = -bfou[1][l] - bfou[1][l1];
                        double d6 = bfou[0][k1] - bfou[0][l1];
                        double d7 = bfou[1][k1] - bfou[1][l1];
                        double d9 = d6 * d6 + d7 * d7;
                        double d8 = ( d2 * d6 + d5 * d7 ) / d9;
                        d9 = ( d5 * d6 - d2 * d7 ) / d9;
                        d2 = cc[0][l][k1];
                        d5 = cc[1][l][k1];
                        cc[0][l][k1] = d2 * d8 - d5 * d9;
                        cc[1][l][k1] = d2 * d9 + d5 * d8;
                    }
                }

            }

        }

        xnorm = 1.0D / (double)nn;
        bnorm = 1.0D / (double)n_bound2;
        for( int i1 = 0; i1 < n_bound2; i1++ ) {
            double d3 = Math.sin( ( pi * (double)i1 ) / (double)n_bound2 );
            d3 = 4D * d3 * d3 * dt;
            bksq[0][i1] = Math.cos( d3 ) * bnorm;
            bksq[1][i1] = -Math.sin( d3 ) * bnorm;
        }

        for( int j1 = 0; j1 < nn; j1++ ) {
            double d4 = Math.sin( ( pi * (double)j1 ) / (double)nn );
            d4 = 4D * d4 * d4 * dt;
            cenerg[0][j1] = Math.cos( d4 ) * xnorm;
            cenerg[1][j1] = -Math.sin( d4 ) * xnorm;
        }

        animatorThread = new Thread( this );
        animatorThread.start();
    }

//    public boolean mouseDrag( Event event, int i, int j ) {
//        Graphics g = getGraphics();
//        if( j < ( rh * 4 ) / 5 ) {
//            j = ( rh * 4 ) / 5;
//        }
//        if( j >= rh ) {
//            j = rh - 1;
//        }
//        if( i + 1 >= rw || i < 1 ) {
//            return true;
//        }
//        if( animatorThread != null ) {
//            animatorThread.stop();
//            animatorThread = null;
//        }
//        if( last_x < i ) {
//            x_sm = last_x + 1;
//            x_bg = i + 1;
//        }
//        else if( last_x == i ) {
//            x_sm = i;
//            x_bg = i + 1;
//        }
//        else {
//            x_bg = last_x;
//            x_sm = i;
//        }
//        if( x_sm > 0 ) {
//            g.setColor( getBackground() );
//            g.drawLine( x_sm - 1, pot[x_sm - 1], x_sm, pot[x_sm] );
//        }
//        for( int k = x_sm; k < x_bg; k++ ) {
//            if( k + 1 < rw ) {
//                g.setColor( getBackground() );
//                g.drawLine( k, pot[k], k + 1, pot[k + 1] );
//            }
//            if( i == last_x ) {
//                pot[k] = j;
//            }
//            else {
//                pot[k] = pot[last_x] + ( ( j - pot[last_x] ) * ( k - last_x ) ) / ( i - last_x );
//            }
//            if( k > 0 ) {
//                g.setColor( Color.red );
//                g.drawLine( k - 1, pot[k - 1], k, pot[k] );
//            }
//        }
//
//        if( x_bg > 0 ) {
//            g.setColor( Color.red );
//            g.drawLine( x_bg - 1, pot[x_bg - 1], x_bg, pot[x_bg] );
//        }
//        last_x = i;
//        last_y = j;
//        return true;
//    }

//    public boolean mouseDown( Event event, int i, int j ) {
//        Graphics g = getGraphics();
//        if( j < ( rh * 4 ) / 5 ) {
//            j = ( rh * 4 ) / 5;
//        }
//        if( j >= rh ) {
//            j = rh - 1;
//        }
//        if( i >= rw || i < 0 ) {
//            return true;
//        }
//        if( animatorThread != null ) {
//            animatorThread.stop();
//            animatorThread = null;
//        }
//        if( i + 1 < rw ) {
//            g.setColor( getBackground() );
//            g.drawLine( i, pot[i], i + 1, pot[i + 1] );
//            g.setColor( Color.red );
//            g.drawLine( i, j, i + 1, pot[i + 1] );
//        }
//        if( i > 1 ) {
//            g.setColor( getBackground() );
//            g.drawLine( i - 1, pot[i - 1], i, pot[i] );
//            g.setColor( Color.red );
//            g.drawLine( i - 1, pot[i - 1], i, j );
//        }
//        pot[i] = j;
//        last_x = i;
//        last_y = j;
//        return true;
//    }

//    public boolean mouseUp( Event event, int i, int j ) {
//        if( animatorThread != null ) {
//            animatorThread.stop();
//            animatorThread = null;
//        }
//        animatorThread = new Thread( this );
//        animatorThread.start();
//        return true;
//    }

//    public boolean action( Event event, Object obj ) {
//        if( animatorThread != null ) {
//            animatorThread.stop();
//            animatorThread = null;
//        }
//        if( event.target == clear_button ) {
//            Graphics g = getGraphics();
//            bounds();
//            g.setColor( getBackground() );
//            g.fillRect( rx, ry, rw, rh );
//            for( int i = 0; i < nn; i++ ) {
//                if( i > 100 && i < 105 || i > 115 && i < 120 ) {
//                    pot[i] = ( 4 * rh ) / 5;
//                }
//                else {
//                    pot[i] = rh - 20;
//                }
//            }
//
//            g.setColor( Color.red );
//            for( int j = 1; j < rw; j++ ) {
//                g.drawLine( j - 1, pot[j - 1], j, pot[j] );
//            }
//
//            animatorThread = new Thread( this );
//            animatorThread.start();
//            return true;
//        }
//        if( event.target == state_choices ) {
//            String _tmp = (String)obj;
//            if( obj.equals( "packet" ) ) {
//                mode = 1;
//            }
//            else if( obj.equals( "injected" ) ) {
//                mode = 2;
//            }
//            if( animatorThread == null ) {
//                animatorThread = new Thread( this );
//                animatorThread.start();
//            }
//            return true;
//        }
//        else {
//            return super.action( event, obj );
//        }
//    }

    public void start() {
        if( animatorThread != null ) {
            animatorThread.stop();
            animatorThread = null;
        }
        animatorThread = new Thread( this );
        animatorThread.start();
    }

    public void stop() {
        if( animatorThread != null ) {
            animatorThread.stop();
            animatorThread = null;
        }
        offGraphics = null;
        offImage = null;
    }

    public void run() {
        int k5 = ( 2 * rh ) / 5;
        Graphics g = getGraphics();
        Dimension dimension = size();
        dimension = new Dimension( 600, 600 );
        System.out.println( "dimension=" + dimension );
//        Dimension dimension = new
        if( offGraphics == null ) {
            offDimension = dimension;
            offImage = createImage( dimension.width, dimension.height );
            offGraphics = offImage.getGraphics();
        }
        while( Thread.currentThread() == animatorThread ) {
            double d11 = initModel();

            double d10 = 0.0D;
            int j5 = 0;
            do {
                if( j5 == looper ) {
                    g.drawString( "Psi[" + mu + "]=" + psi[0][mu] + " " + psi[1][mu] + " at start" + nn, 10, linen += 10 );
                }
                for( int k = 0; k < nn; k++ ) {
                    double d2 = psi[0][k];
                    double d6 = psi[1][k];
                    psi[0][k] = d2 * cpot[0][k] - d6 * cpot[1][k];
                    psi[1][k] = d2 * cpot[1][k] + d6 * cpot[0][k];
                }

                linen = 80;
                if( j5 == looper ) {
                    g.drawString( "cpot[" + mu + "]=" + cpot[0][mu] + " " + cpot[1][mu] + " at start", 10, linen += 10 );
                    g.drawString( "Psi[" + mu + "]=" + psi[0][mu] + " " + psi[1][mu] + " after cpot", 10, linen += 10 );
                }
                loopA();

                loopB();

                FFT1();
                loopC();

                FFT2();
                if( j5 == looper ) {
                    g.drawString( "Psi[" + mu + "]=" + psi[0][mu] + " " + psi[1][mu] + " after fft", 10, linen += 10 );
                }
                loopD();

                if( j5 == looper ) {
                    g.drawString( "Psi[" + mu + "]=" + psi[0][mu] + " " + psi[1][mu] + " after cenerg", 10, linen += 10 );
                }
                fft( nn, cfo, psi, 1 );
                if( j5 == looper ) {
                    g.drawString( "Psi[" + mu + "]=" + psi[0][mu] + " " + psi[1][mu] + " after i-fft", 10, linen += 10 );
                }
                loopE();

                d10 += dt;
                mu = ( mu + 1 ) % nn;
                offGraphics.setColor( getBackground() );
                offGraphics.fillRect( 0, 0, rw, rh );
                offGraphics.setColor( Color.red );
                for( int i3 = 1; i3 < rw; i3++ ) {
                    offGraphics.drawLine( i3 - 1, pot[i3 - 1], i3, pot[i3] );
                }

                double d12 = 0.0D;
                loopF( d12 );

                offGraphics.setColor( Color.yellow );
                int i5 = k5 - (int)( d11 * psi[0][0] );
                for( int k3 = 1; k3 < nn; k3++ ) {
                    int j4 = k5 - (int)( d11 * psi[0][k3] );
                    offGraphics.drawLine( k3 - 1, i5, k3, j4 );
                    i5 = j4;
                }

                offGraphics.setColor( Color.green );
                i5 = k5 - (int)( d11 * psi[1][0] );
                for( int l3 = 1; l3 < nn; l3++ ) {
                    int k4 = k5 - (int)( d11 * psi[1][l3] );
                    offGraphics.drawLine( l3 - 1, i5, l3, k4 );
                    i5 = k4;
                }

                offGraphics.setColor( Color.white );
                i5 = k5 - (int)( d11 * Math.sqrt( psi[0][0] * psi[0][0] + psi[1][0] * psi[1][0] ) );
                for( int i4 = 1; i4 < nn; i4++ ) {
                    int l4 = k5 - (int)( d11 * Math.sqrt( psi[0][i4] * psi[0][i4] + psi[1][i4] * psi[1][i4] ) );
                    offGraphics.drawLine( i4 - 1, i5, i4, l4 );
                    i5 = l4;
                }

                offGraphics.drawString( "t = " + d10, 10, ( 4 * rh ) / 5 );
                g.drawImage( offImage, 0, 0, this );
                if( j5 == looper ) {
                    g.drawString( "Psi[" + mu + "]=" + psi[0][mu] + " " + psi[1][mu] + " end of loop", 10, linen += 10 );
                    g.drawString( "psi_scale=" + d11, 10, linen += 10 );
                }
                j5++;
            } while( true );
        }
    }

    private void loopF( double d12 ) {
        for( int j3 = 0; j3 < nn; j3++ ) {
            double d1 = psi[0][j3] * psi[0][j3] + psi[1][j3] * psi[1][j3];
            if( d12 < d1 ) {
                d12 = d1;
            }
            d1 = Math.abs( psi[0][j3] );
            if( d12 < d1 ) {
                d12 = d1;
            }
            d1 = Math.abs( psi[1][j3] );
            if( d12 < d1 ) {
                d12 = d1;
            }
        }
    }

    private void loopE() {
        for( int l1 = 0; l1 < nbo; l1++ ) {
            for( int k2 = 0; k2 < 2; k2++ ) {
                psi[k2][( nn - nbo ) + l1] = bsi[k2][nbo + l1];
                if( mode == 1 ) {
                    psi[k2][l1] = csi[k2][l1];
                }
                else {
                    psi[k2][l1] = csi[k2][l1] + xsi[k2][l1];
                }
            }

        }
    }

    private void loopD() {
        for( int k1 = 0; k1 < nn; k1++ ) {
            double d5 = psi[0][k1];
            double d9 = psi[1][k1];
            psi[0][k1] = d5 * cenerg[0][k1] - d9 * cenerg[1][k1];
            psi[1][k1] = d5 * cenerg[1][k1] + d9 * cenerg[0][k1];
        }
    }

    private void FFT2() {
        fft( n_bound2, bfou, bsi, 1 );
        fft( n_bound2, bfou, csi, 1 );
        if( mode != 1 ) {
            fft( n_bound2, bfou, xsi, 1 );
        }
        fft( nn, cfo, psi, -1 );
    }

    private void loopC() {
        for( int j1 = 0; j1 < n_bound2; j1++ ) {
            double d3 = bsi[0][j1];
            double d7 = bsi[1][j1];
            bsi[0][j1] = d3 * bksq[0][j1] - d7 * bksq[1][j1];
            bsi[1][j1] = d3 * bksq[1][j1] + d7 * bksq[0][j1];
            d3 = csi[0][j1];
            d7 = csi[1][j1];
            csi[0][j1] = d3 * bksq[0][j1] - d7 * bksq[1][j1];
            csi[1][j1] = d3 * bksq[1][j1] + d7 * bksq[0][j1];
            if( mode != 1 ) {
                double d4 = xsi[0][j1];
                double d8 = xsi[1][j1];
                xsi[0][j1] = d4 * bksq[0][j1] - d8 * bksq[1][j1];
                xsi[1][j1] = d4 * bksq[1][j1] + d8 * bksq[0][j1];
            }
        }
    }

    private void FFT1() {
        fft( n_bound2, bfou, bsi, -1 );
        fft( n_bound2, bfou, csi, -1 );
        if( mode != 1 ) {
            fft( n_bound2, bfou, xsi, -1 );
        }
    }

    private void loopB() {
        for( int i1 = 0; i1 < n_bound; i1++ ) {
            for( int j2 = 0; j2 < 2; j2++ ) {
                bsi[j2][n_bound + i1] = 0.0D;
                csi[j2][n_bound + i1] = 0.0D;
            }

            for( int l2 = 0; l2 < n_bound; l2++ ) {
                bsi[0][n_bound + i1] = ( bsi[0][n_bound + i1] + bsi[0][l2] * cc[0][i1][l2] ) - bsi[1][l2] * cc[1][i1][l2];
                bsi[1][n_bound + i1] = bsi[1][n_bound + i1] + bsi[0][l2] * cc[1][i1][l2] + bsi[1][l2] * cc[0][i1][l2];
                csi[0][n_bound + i1] = csi[0][n_bound + i1] + csi[0][l2] * cc[0][i1][l2] + csi[1][l2] * cc[1][i1][l2];
                csi[1][n_bound + i1] = ( csi[1][n_bound + i1] - csi[0][l2] * cc[1][i1][l2] ) + csi[1][l2] * cc[0][i1][l2];
            }

        }
    }

    private void loopA() {
        for( int l = 0; l < n_bound; l++ ) {
            for( int i2 = 0; i2 < 2; i2++ ) {
                bsi[i2][l] = psi[i2][( nn - n_bound ) + l];
                if( mode == 1 ) {
                    csi[i2][l] = psi[i2][l];
                }
                else {
                    csi[i2][l] = psi[i2][l] - xsi[i2][l];
                }
            }

        }
    }

    private double initModel() {
        double d11;
        double d13;
        if( mode == 1 ) {
            d13 = xk0;
            d11 = (double)rh * 0.29999999999999999D;
        }
        else {
            d13 = ( (double)(int)( ( xk0 * 8D ) / pi + 0.5D ) * pi ) / 8D;
            d11 = (double)rh * 0.20000000000000001D;
        }
        for( int i = 0; i < nn; i++ ) {
            double d = Math.exp( (double)( -( i - i0 ) * ( i - i0 ) ) / ( width * width ) );
            if( mode != 1 ) {
                if( i < i0 ) {
                    d = 1.0D;
                }
                if( i < n_bound2 ) {
                    xsi[0][i] = Math.cos( d13 * (double)i );
                    xsi[1][i] = Math.sin( d13 * (double)i );
                }
            }
            psi[0][i] = d * Math.cos( d13 * (double)i );
            psi[1][i] = d * Math.sin( d13 * (double)i );
        }

        for( int j = 0; j < nn; j++ ) {
            cpot[0][j] = Math.cos( dt * scale * (double)( rh - pot[j] - 20 ) );
            cpot[1][j] = -Math.sin( dt * scale * (double)( rh - pot[j] - 20 ) );
        }
        return d11;
    }

    public static void fft( int i, double ad[][], double ad1[][], int j ) {
        for( int k = 0; k < i; k++ ) {
            for( int i1 = 0; i1 < 2; i1++ ) {
                buf[i1][k] = ad1[i1][k];
            }

        }

        int j4;
        int k4;
        if( j == 1 ) {
            j4 = 0;
            k4 = i;
        }
        else {
            j4 = i;
            k4 = -i;
        }
        int l3 = 1;
        int l4 = i / 2;
        for( int i2 = l4; i2 > 0; i2 /= 2 ) {
            k4 /= 2;
            int i4 = j4;
            int k2 = 0;
            int j3 = 0;
            for( int l1 = 0; l1 < l3; l1++ ) {
                double d = ad[0][i4];
                double d1 = ad[1][i4];
                for( int j1 = 0; j1 < i2; j1++ ) {
                    int j2 = j1 + k2;
                    int l2 = j2 + l4;
                    int i3 = j1 + j3;
                    int k3 = i3 + i2;
                    double d2 = buf[0][i3];
                    double d3 = buf[1][i3];
                    double d4 = buf[0][k3];
                    double d5 = buf[1][k3];
                    ad1[0][j2] = ( d2 + d * d4 ) - d1 * d5;
                    ad1[1][j2] = d3 + d1 * d4 + d * d5;
                    ad1[0][l2] = ( d2 - d * d4 ) + d1 * d5;
                    ad1[1][l2] = d3 - d1 * d4 - d * d5;
                }

                i4 += k4;
                k2 += i2;
                j3 = j3 + i2 + i2;
            }

            l3 += l3;
            if( i2 != 1 ) {
                for( int l = 0; l < i; l++ ) {
                    for( int k1 = 0; k1 < 2; k1++ ) {
                        buf[k1][l] = ad1[k1][l];
                    }

                }

            }
        }

    }

    public BCTest() {
        setSize( 600, 600 );

        rw = 256;
        rh = 200;
        cfo = new double[2][257];
        psi = new double[2][256];
        cpot = new double[2][256];
        cenerg = new double[2][256];
        bfou = new double[2][17];
        bsi = new double[2][16];
        csi = new double[2][16];
        xsi = new double[2][16];
        cc = new double[2][16][16];
        bksq = new double[2][16];
        pot = new int[256];
        dt = 0.5D;
        nn = 256;
        n_bound = 8;
        mu = 20;
        linen = 80;
        looper = -1;
    }

    public static void main( String[] args ) {
        Applet a = new BCTest();

        JFrame f = new JFrame();
        f.setContentPane( a );
        f.pack();
        f.show();
        a.init();
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.setSize( 600, 600 );
    }

}