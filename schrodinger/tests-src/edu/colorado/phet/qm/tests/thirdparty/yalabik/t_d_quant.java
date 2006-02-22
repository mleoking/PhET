package edu.colorado.phet.qm.tests.thirdparty.yalabik;

import java.applet.Applet;
import java.awt.*;

/* Cemal Yalabik yalabik@fen.bilkent.edu.tr  */

/* This is the java version of the dev.f program, conversion was carried
out with the intent of porting the program eventually to Java.
This version is for a 1-d system. */

public class t_d_quant extends Applet implements Runnable {

    Thread animatorThread;

    Dimension offDimension;
    Image offImage;
    Graphics offGraphics;

    private Button clear_button;
    private Choice state_choices;

    private static int LATTICE_SIZE = 256;
//    private static int LATTICE_SIZE = 1024;
    private int rw = LATTICE_SIZE;
    private int rh = 200;
    private int rx, ry;
    private int mode = PACKET;
    private int last_x;

    static private double[][] FFTBuffer = new double[2][LATTICE_SIZE];

    double[][] cfo = new double[2][LATTICE_SIZE + 1];

    double[][] psi = new double[2][LATTICE_SIZE];
    double[][] cpot = new double[2][LATTICE_SIZE];
    double[][] cenerg = new double[2][LATTICE_SIZE];
    double[][] bfou = new double[2][17];
    double[][] bsi = new double[2][16];
    double[][] csi = new double[2][16];
    double[][] xsi = new double[2][16];
    double[][][] cc = new double[2][16][16];
    double[][] bksq = new double[2][16];
    int[] pot = new int[LATTICE_SIZE];
    double dt = 0.5;
    double xnorm, bnorm, scale;
    int nn = LATTICE_SIZE;
    int n_bound = 8;
    int n_bound2;
    int nbo;
    double pi;
    int i0;
    double xk0, width;
    int mu = 20;
    int looper = -1;
    private static final int PACKET = 1;
    private static final int INJECTED = 2;
    private static final long LOOP_DELAY = 30;
    private double t;
    private int loop;

    public void init() {
        // Set the background color
        this.setBackground( Color.black );
        this.setForeground( Color.white );

        // Create a button and add it to the applet.
        // Also, set the button's colors
        clear_button = new Button( "Reset" );
        clear_button.setForeground( Color.black );
        clear_button.setBackground( Color.lightGray );
        this.add( clear_button );

        // Create a menu of colors and add it to the applet.
        // Also set the menus's colors and add a label.
        state_choices = new Choice();
        state_choices.addItem( "packet" );
        state_choices.addItem( "injected" );
        state_choices.setForeground( Color.black );
        state_choices.setBackground( Color.lightGray );
        this.add( new Label( "Type of wave:" ) );
        this.add( state_choices );
        mode = PACKET;

        initParameters();
        initPotential();
        initFFT();
        initFFTInjected();
        initLagrangeExtensionPolynomial();

        animatorThread = new Thread( this );
        animatorThread.start();
    }

    private void initParameters() {
        Rectangle r = this.getBounds();
        rh = r.height;
        rx = r.x;
        ry = r.y;
        scale = 1.2 / rh;
        pi = 4. * Math.atan( 1. );

        n_bound2 = n_bound + n_bound;
        nbo = n_bound / 2;

/* Initial wavepacket parameters  */
        i0 = 30;
        xk0 = 0.25;
        width = 10.;
    }

    private void initLagrangeExtensionPolynomial() {
        /* initialization of the Lagrange extension polynomial */

        for( int i = 0; i < n_bound; i++ ) {
            for( int j = 0; j < n_bound; j++ ) {
                cc[0][i][j] = 1.;
                cc[1][i][j] = 0.;
                for( int k = 0; k < n_bound; k++ ) {
                    if( j != k ) {
                        /*  implementing  cc(i,j)=cc(i,j)*(-bfou(i)-bfou(k))/(bfou(j)-bfou(k)) */
                        double a = -bfou[0][i] - bfou[0][k];
                        double b = -bfou[1][i] - bfou[1][k];
                        double c = bfou[0][j] - bfou[0][k];
                        double d = bfou[1][j] - bfou[1][k];
                        double f = c * c + d * d;
                        double e = ( a * c + b * d ) / f;
                        f = ( b * c - a * d ) / f;
                        a = cc[0][i][j];
                        b = cc[1][i][j];
                        cc[0][i][j] = a * e - b * f;
                        cc[1][i][j] = a * f + b * e;
                    }  /* if    */
                }    /* for k */
            }      /* for j */
        }        /* for i */

        xnorm = 1. / nn;
        bnorm = 1. / n_bound2;

        for( int i = 0; i < n_bound2; i++ ) {
            double a = Math.sin( pi * i / n_bound2 );
            a = 4. * a * a * dt;
            bksq[0][i] = Math.cos( a ) * bnorm;
            bksq[1][i] = -Math.sin( a ) * bnorm;
        }

        for( int i = 0; i < nn; i++ ) {
            double a = Math.sin( pi * i / nn );
            a = 4. * a * a * dt;
            cenerg[0][i] = Math.cos( a ) * xnorm;
            cenerg[1][i] = -Math.sin( a ) * xnorm;
        }
    }

    private void initFFTInjected() {
        /* initialization of fft variables for the injected wave: */

        for( int i = 0; i <= n_bound2; i++ ) {
            double xx = 2 * i * pi / n_bound2;
            bfou[0][i] = Math.cos( xx );
            bfou[1][i] = Math.sin( xx );
        }
    }

    private void initFFT() {
        for( int i = 0; i <= nn; i++ ) {
            double xx = 2 * i * pi / nn;
            cfo[0][i] = Math.cos( xx );
            cfo[1][i] = Math.sin( xx );
        }
    }

    private void initPotential() {
        int i;
        for( i = 0; i < nn; i++ ) {
            if( ( ( i > 100 ) && ( i < 105 ) ) || ( ( i > 115 ) && ( i < 120 ) ) ) {
                pot[i] = 4 * rh / 5;
            }
            else {
                pot[i] = rh - 20;
            }
        }
    }


    // Called when the user drags the mouse to scribble
    public boolean mouseDrag( Event e, int x, int y ) {

        int z;
        Graphics g = this.getGraphics();

        if( y < rh * 4 / 5 ) {y = rh * 4 / 5;}
        if( y >= rh ) { y = rh - 1;}
        if( x + 1 >= rw || x < 1 ) {return true; }

        stopAnimatorThread();

        int x_sm;
        int x_bg;
        if( last_x < x ) {
            x_sm = last_x + 1;
            x_bg = x + 1;
        }
        else if( last_x == x ) {
            x_sm = x;
            x_bg = x + 1;
        }
        else {
            x_bg = last_x;
            x_sm = x;
        }

        if( x_sm > 0 ) {
            g.setColor( this.getBackground() );
            g.drawLine( x_sm - 1, pot[x_sm - 1], x_sm, pot[x_sm] );
        }

        for( z = x_sm; z < x_bg; z++ ) {

            if( z + 1 < rw ) {
                g.setColor( this.getBackground() );
                g.drawLine( z, pot[z], z + 1, pot[z + 1] );
            }

            if( x == last_x ) {
                pot[z] = y;
            }
            else {
                pot[z] = pot[last_x] + ( y - pot[last_x] ) * ( z - last_x ) / ( x - last_x );
            }

            if( z > 0 ) {
                g.setColor( Color.red );
                g.drawLine( z - 1, pot[z - 1], z, pot[z] );
            }

        }
        if( x_bg > 0 ) {
            g.setColor( Color.red );
            g.drawLine( x_bg - 1, pot[x_bg - 1], x_bg, pot[x_bg] );
        }

        last_x = x;
        return true;
    }

    // Called when the user pushes the mouse button down
    public boolean mouseDown( Event e, int x, int y ) {

        Graphics g = this.getGraphics();

        if( y < rh * 4 / 5 ) {y = rh * 4 / 5;}
        if( y >= rh ) {y = rh - 1;}
        if( x >= rw || x < 0 ) {return true; }

        stopAnimatorThread();

        if( x + 1 < rw ) {
            g.setColor( this.getBackground() );
            g.drawLine( x, pot[x], x + 1, pot[x + 1] );
            g.setColor( Color.red );
            g.drawLine( x, y, x + 1, pot[x + 1] );
        }
        if( x > 1 ) {
            g.setColor( this.getBackground() );
            g.drawLine( x - 1, pot[x - 1], x, pot[x] );
            g.setColor( Color.red );
            g.drawLine( x - 1, pot[x - 1], x, y );
        }
        pot[x] = y;

        last_x = x;

        return true;
    }

    // Called when the user releases the mouse button
    public boolean mouseUp( Event e, int x, int y ) {
        stopAnimatorThread();
        animatorThread = new Thread( this );
        animatorThread.start();

        return true;
    }

    // Called when the user clicks the button or chooses a color
    public boolean action( Event event, Object arg ) {
        stopAnimatorThread();

        // If the Reset button was clicked on, handle it.
        if( event.target == clear_button ) {
            Graphics g = this.getGraphics();
            g.setColor( this.getBackground() );
            g.fillRect( rx, ry, rw, rh );

            for( int i = 0; i < nn; i++ ) {
                if( ( ( i > 100 ) && ( i < 105 ) ) || ( ( i > 115 ) && ( i < 120 ) ) ) {
                    pot[i] = 4 * rh / 5;
                }
                else {
                    pot[i] = rh - 20;
                }
            }


            g.setColor( Color.red );
            for( int k = 1; k < rw; k++ ) {
                g.drawLine( k - 1, pot[k - 1], k, pot[k] );
            }

            animatorThread = new Thread( this );
            animatorThread.start();

            return true;
        }
        // Otherwise if a color was chosen, handle that
        else if( event.target == state_choices ) {
            if( arg.equals( "packet" ) ) {
                mode = PACKET;
            }
            else if( arg.equals( "injected" ) ) {
                mode = INJECTED;
            }

            if( animatorThread == null ) {
                animatorThread = new Thread( this );
                animatorThread.start();
            }

            return true;
        }
        // Otherwise, let the superclass handle it.
        else {
            return super.action( event, arg );
        }
    }

    public void start() {
        stopAnimatorThread();
        animatorThread = new Thread( this );
        animatorThread.start();
    }

    public void stop() {
        //Stop the animating thread.
        stopAnimatorThread();
        //Get rid of the objects necessary for double buffering.
        offGraphics = null;
        offImage = null;
    }

    private void stopAnimatorThread() {
        if( animatorThread != null ) {
            animatorThread.stop();
            animatorThread = null;
        }
    }

    public double getXK00() {
        return mode == PACKET ? xk0 : ( (int)( xk0 * 8. / pi + .5 ) ) * pi / 8.;
    }

    public double getPsiScale() {
        return mode == PACKET ? rh * 0.3 : rh * 0.2;
    }

    public void run() {
        Graphics g = this.getGraphics();
        Dimension d = getSize();

        if( offGraphics == null ) {
            offDimension = d;
            offImage = createImage( d.width, d.height );
            offGraphics = offImage.getGraphics();
        }

        while( Thread.currentThread() == animatorThread ) {
            initAlgorithm();

/*
c
c
c ************* start the computation loop **************
c
*/
            t = 0.0;
            for( loop = 0; ; loop++ ) {
                try {
                    Thread.sleep( LOOP_DELAY );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
                updateWavefunction();

/*
c simulation is over only for one tour...
c time to make the increments...
c and plot ...
c  */

                this.t = t + dt;
                mu = ( mu + 1 ) % nn;

                plot( g );
            }  /* closes for - loop  */
        }   /*  closes while  */
    }

    private void updateWavefunction() {

        for( int i = 0; i < nn; i++ ) {
            double a = psi[0][i];
            double b = psi[1][i];
            psi[0][i] = a * cpot[0][i] - b * cpot[1][i];
            psi[1][i] = a * cpot[1][i] + b * cpot[0][i];
        }

/*  form the bc vector bsi for i~nn and csi for i~1  */

        for( int i = 0; i < n_bound; i++ ) {
            for( int j = 0; j < 2; j++ ) {
                bsi[j][i] = psi[j][nn - n_bound + i];
                if( mode == PACKET ) {
                    csi[j][i] = psi[j][i];
                }
                else {
                    csi[j][i] = psi[j][i] - xsi[j][i];
                }
            }
        }

/* extend this vector using the Lagrange extarpolation with positive ft: */

        for( int i = 0; i < n_bound; i++ ) {
            for( int j = 0; j < 2; j++ ) {
                bsi[j][n_bound + i] = 0;
                csi[j][n_bound + i] = 0;
            }
            for( int k = 0; k < n_bound; k++ ) {
                /* implementing    bsi(n_bound+i)=bsi(n_bound+i)+bsi(k)*(cc(i,k))  */
                bsi[0][n_bound + i] = bsi[0][n_bound + i] + bsi[0][k] * cc[0][i][k] - bsi[1][k] * cc[1][i][k];
                bsi[1][n_bound + i] = bsi[1][n_bound + i] + bsi[0][k] * cc[1][i][k] + bsi[1][k] * cc[0][i][k];
                /* implementing     csi(nn_bound+i)=csi(n_bound+i)+csi(k)*conjg(cc(i,k))  */
                csi[0][n_bound + i] = csi[0][n_bound + i] + csi[0][k] * cc[0][i][k] + csi[1][k] * cc[1][i][k];
                csi[1][n_bound + i] = csi[1][n_bound + i] - csi[0][k] * cc[1][i][k] + csi[1][k] * cc[0][i][k];
            }  /* for k */
        }    /* for i */

/*  develop this part of wf (using periodic bc)  */

        fft( n_bound2, bfou, bsi, -1 );
        fft( n_bound2, bfou, csi, -1 );
        if( mode != PACKET ) {
            fft( n_bound2, bfou, xsi, -1 );
        }
/*
   do the momentum space updating and normalization
*/
        for( int i = 0; i < n_bound2; i++ ) {
            double a = bsi[0][i];
            double b = bsi[1][i];
            bsi[0][i] = a * bksq[0][i] - b * bksq[1][i];
            bsi[1][i] = a * bksq[1][i] + b * bksq[0][i];

            a = csi[0][i];
            b = csi[1][i];
            csi[0][i] = a * bksq[0][i] - b * bksq[1][i];
            csi[1][i] = a * bksq[1][i] + b * bksq[0][i];

            if( mode != PACKET ) {
                a = xsi[0][i];
                b = xsi[1][i];
                xsi[0][i] = a * bksq[0][i] - b * bksq[1][i];
                xsi[1][i] = a * bksq[1][i] + b * bksq[0][i];
            }
        }

/*   inverse transform to position space   */

        fft( n_bound2, bfou, bsi, 1 );
        fft( n_bound2, bfou, csi, 1 );
        if( mode != PACKET ) {
            fft( n_bound2, bfou, xsi, 1 );
        }

/*   fourier transform the wavefunction:  */

        fft( nn, cfo, psi, -1 );

/*   do the momentum space updating and normalization  */

        for( int i = 0; i < nn; i++ ) {
            double a = psi[0][i];
            double b = psi[1][i];
            psi[0][i] = a * cenerg[0][i] - b * cenerg[1][i];
            psi[1][i] = a * cenerg[1][i] + b * cenerg[0][i];
        }

/*   inverse transform to position space  */

        fft( nn, cfo, psi, 1 );

/* interpolate the boundary region between the two forms of solutions  */


        for( int i = 0; i < nbo; i++ ) {
            for( int j = 0; j < 2; j++ ) {
                psi[j][nn - nbo + i] = bsi[j][nbo + i];
                if( mode == PACKET ) {
                    psi[j][i] = csi[j][i];
                }
                else {
                    psi[j][i] = csi[j][i] + xsi[j][i];
                }
            }
        }
    }

    private void initAlgorithm() {
        for( int i = 0; i < nn; i++ ) {
            double xx = Math.exp( -( i - i0 ) * ( i - i0 ) / ( width * width ) );
            if( mode != PACKET ) {
                if( i < i0 ) {
                    xx = 1.;
                }
                if( i < n_bound2 ) {
                    xsi[0][i] = Math.cos( getXK00() * i );
                    xsi[1][i] = Math.sin( getXK00() * i );
                }
            }
            psi[0][i] = xx * Math.cos( getXK00() * i );
            psi[1][i] = xx * Math.sin( getXK00() * i );
        }

        for( int i = 0; i < nn; i++ ) {
            cpot[0][i] = Math.cos( dt * scale * ( rh - pot[i] - 20 ) );
            cpot[1][i] = -Math.sin( dt * scale * ( rh - pot[i] - 20 ) );
        }
    }

    private void plot( Graphics g ) {
        offGraphics.setColor( getBackground() );
        offGraphics.fillRect( 0, 0, rw, rh );
        offGraphics.setColor( Color.red );
        for( int k = 1; k < rw; k++ ) {
            offGraphics.drawLine( k - 1, pot[k - 1], k, pot[k] );
        }

        double psi_big = 0.;
        for( int k = 0; k < nn; k++ ) {
            double xx = psi[0][k] * psi[0][k] + psi[1][k] * psi[1][k];
            if( psi_big < xx ) {
                psi_big = xx;
            }
            xx = Math.abs( psi[0][k] );
            if( psi_big < xx ) {
                psi_big = xx;
            }
            xx = Math.abs( psi[1][k] );
            if( psi_big < xx ) {
                psi_big = xx;
            }
        }

        offGraphics.setColor( Color.yellow );
        double psi_scale = getPsiScale();
        int old_psi = getAxis() - (int)( psi_scale * psi[0][0] );
        int axis = getAxis();
        for( int k = 1; k < nn; k++ ) {
            int ii = axis - (int)( psi_scale * psi[0][k] );
            offGraphics.drawLine( k - 1, old_psi, k, ii );
            old_psi = ii;
        }
        offGraphics.setColor( Color.green );
        old_psi = axis - (int)( psi_scale * psi[1][0] );
        for( int k = 1; k < nn; k++ ) {
            int ii = axis - (int)( psi_scale * psi[1][k] );
            offGraphics.drawLine( k - 1, old_psi, k, ii );
            old_psi = ii;
        }
        offGraphics.setColor( Color.white );
        old_psi = axis - (int)( psi_scale * Math.sqrt( psi[0][0] * psi[0][0] + psi[1][0] * psi[1][0] ) );
        for( int k = 1; k < nn; k++ ) {
            int ii = axis - (int)( psi_scale * Math.sqrt( psi[0][k] * psi[0][k] + psi[1][k] * psi[1][k] ) );
            offGraphics.drawLine( k - 1, old_psi, k, ii );
            old_psi = ii;
        }

        offGraphics.drawString( "t = " + t, 10, 4 * rh / 5 );
        g.drawImage( offImage, 0, 0, this );

    }

    private int getAxis() {
        return 2 * rh / 5;
    }


    static public void fft( int n, double[][] cfou, double[][] vect, int mode )
/*
Cemal Yalabik, 1987 (Temple University-Physics) Fast Fourier Transform
c Vectorization may be forced on all inner do loops.
c To fourier transform an array of the form psi(nx,ny)
c call fft(nx,1+(j-1)*nx,1,psi,cfoux,ibtrvx,1,buf) for all j.le.ny
c call fft(ny,i,nx,psi,cfouy,ibtrvy,1,buf) for all i.le.nx
*/ {
        int i, j, k, nh, J, J0, K, L, L0, M, length, index, index_0, index_incr;
        int no2;
        double cfr, cfi, buf1r, buf1i, buf2r, buf2i;

        for( i = 0; i < n; i++ ) {
            for( j = 0; j < 2; j++ ) {
                FFTBuffer[j][i] = vect[j][i];
            }
        }

        if( mode == 1 ) {
            index_0 = 0;
            index_incr = n;
        }
        else {
            index_0 = n;
            index_incr = -n;
        }
        length = 1;
        no2 = n / 2;

        for( nh = no2; nh > 0; nh = nh / 2 ) {  //   nh = no of ft to be computed at this step
            index_incr /= 2;
            index = index_0;
            J0 = 0;
            L0 = 0;
            for( k = 0; k < length; k++ ) {   //   length = length of the ft at this step
                cfr = cfou[0][index];
                cfi = cfou[1][index];
                for( j = 0; j < nh; j++ ) {     //   go over all the ft's at this step
                    J = j + J0;
                    K = J + no2;
                    L = j + L0;
                    M = L + nh;
                    buf1r = FFTBuffer[0][L];
                    buf1i = FFTBuffer[1][L];
                    buf2r = FFTBuffer[0][M];
                    buf2i = FFTBuffer[1][M];
                    vect[0][J] = buf1r + cfr * buf2r - cfi * buf2i;
                    vect[1][J] = buf1i + cfi * buf2r + cfr * buf2i;
                    vect[0][K] = buf1r - cfr * buf2r + cfi * buf2i;
                    vect[1][K] = buf1i - cfi * buf2r - cfr * buf2i;
                }   // for - j

                index += index_incr;
                J0 = J0 + nh;
                L0 = L0 + nh + nh;
            }    //  for - k

            length = length + length;

            if( nh != 1 ) {
                for( i = 0; i < n; i++ ) {
                    for( j = 0; j < 2; j++ ) {
                        FFTBuffer[j][i] = vect[j][i];
                    }
                }
            }

        }   //  for - nh
        return;
    }


}
