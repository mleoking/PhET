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
//    private TextField textField;
//    private TextArea textArea;

    private int rw = 256;
    private int rh = 200;
    private int rx, ry, mode;
    private int x, y, x_sm, x_bg, last_x, last_y;


    double[][] cfo = new double[2][257];
    static private double[][] buf = new double[2][256];
    double[][] psi = new double[2][256];
    double[][] cpot = new double[2][256];
    double[][] cenerg = new double[2][256];
    double[][] bfou = new double[2][17];
    double[][] bsi = new double[2][16];
    double[][] csi = new double[2][16];
    double[][] xsi = new double[2][16];
    double[][][] cc = new double[2][16][16];
    double[][] bksq = new double[2][16];
    int[] pot = new int[256];
    double dt = 0.5, xnorm, bnorm, scale;
    int nn = 256;
    int n_bound = 8;
    int n_bound2;
    int nbo;
    double pi;
    int i0;
    double xk0, width;
    int mu = 20;
    int linen = 80;
    int looper = -1;

    public void init() {
        int i, j, k;
        double xx, a, b, c, d, e, f;

        // Set the background color
        this.setBackground( Color.black );
        this.setForeground( Color.white );

        //Create a text area
//      textField = new TextField(10);
//      textArea = new TextArea(1,10);
//      textArea.setEditable(false);

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
        mode = 1;

        Rectangle r = this.bounds();

//      rw=r.width;
        rh = r.height;
        rx = r.x;
        ry = r.y;
        scale = 1.2 / rh;
        pi = 4. * Math.atan( 1. );

        n_bound2 = n_bound + n_bound;
        nbo = n_bound / 2;

/* Initial wavepacket parameters  */
        i0 = 30;
        xk0 = -0.25;
        width = 10.;

//      for(i=0;i<nn;i++){
//        if( ( (i>100)&&(i<105) ) || ( (i>115)&&(i<120) ) ){
//          pot[i]=4*rh/5;
//        }
//        else{
//          pot[i]=rh-20;
//        }
//      }

/* initialization of fft variables */

        for( i = 0; i <= nn; i++ ) {
            xx = 2 * i * pi / nn;
            cfo[0][i] = Math.cos( xx );
            cfo[1][i] = Math.sin( xx );
        }

/* initialization of fft variables for the injected wave: */

        for( i = 0; i <= n_bound2; i++ ) {
            xx = 2 * i * pi / n_bound2;
            bfou[0][i] = Math.cos( xx );
            bfou[1][i] = Math.sin( xx );
        }

/* initialization of the Lagrange extension polynomial */

        for( i = 0; i < n_bound; i++ ) {
            for( j = 0; j < n_bound; j++ ) {
                cc[0][i][j] = 1.;
                cc[1][i][j] = 0.;
                for( k = 0; k < n_bound; k++ ) {
                    if( j != k ) {
/*  implementing  cc(i,j)=cc(i,j)*(-bfou(i)-bfou(k))/(bfou(j)-bfou(k)) */
                        a = -bfou[0][i] - bfou[0][k];
                        b = -bfou[1][i] - bfou[1][k];
                        c = bfou[0][j] - bfou[0][k];
                        d = bfou[1][j] - bfou[1][k];
                        f = c * c + d * d;
                        e = ( a * c + b * d ) / f;
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

        for( i = 0; i < n_bound2; i++ ) {
            a = Math.sin( pi * i / n_bound2 );
            a = 4. * a * a * dt;
            bksq[0][i] = Math.cos( a ) * bnorm;
            bksq[1][i] = -Math.sin( a ) * bnorm;
        }

        for( i = 0; i < nn; i++ ) {
            a = Math.sin( pi * i / nn );
            a = 4. * a * a * dt;
            cenerg[0][i] = Math.cos( a ) * xnorm;
            cenerg[1][i] = -Math.sin( a ) * xnorm;
        }

        animatorThread = new Thread( this );
        animatorThread.start();
    }


    // Called when the user drags the mouse to scribble
    public boolean mouseDrag( Event e, int x, int y ) {

        int z;
        Graphics g = this.getGraphics();

        if( y < rh * 4 / 5 ) {y = rh * 4 / 5;}
        if( y >= rh ) { y = rh - 1;}
        if( x + 1 >= rw || x < 1 ) {return true; }

        if( animatorThread != null ) {
            animatorThread.stop();
            animatorThread = null;
        }

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
        last_y = y;
        return true;
    }

    // Called when the user pushes the mouse button down
    public boolean mouseDown( Event e, int x, int y ) {

        Graphics g = this.getGraphics();

        if( y < rh * 4 / 5 ) {y = rh * 4 / 5;}
        if( y >= rh ) {y = rh - 1;}
        if( x >= rw || x < 0 ) {return true; }

        if( animatorThread != null ) {
            animatorThread.stop();
            animatorThread = null;
        }


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
        last_y = y;

        return true;
    }

    // Called when the user releases the mouse button
    public boolean mouseUp( Event e, int x, int y ) {


        if( animatorThread != null ) {
            animatorThread.stop();
            animatorThread = null;
        }
        animatorThread = new Thread( this );
        animatorThread.start();

        return true;
    }

    // Called when the user clicks the button or chooses a color
    public boolean action( Event event, Object arg ) {
        int i, k;

        if( animatorThread != null ) {
            animatorThread.stop();
            animatorThread = null;
        }

        // If the Reset button was clicked on, handle it.
        if( event.target == clear_button ) {
            Graphics g = this.getGraphics();
            Rectangle r = this.bounds();
            g.setColor( this.getBackground() );
            g.fillRect( rx, ry, rw, rh );

            for( i = 0; i < nn; i++ ) {
                if( ( ( i > 100 ) && ( i < 105 ) ) || ( ( i > 115 ) && ( i < 120 ) ) ) {
                    pot[i] = 4 * rh / 5;
                }
                else {
                    pot[i] = rh - 20;
                }
            }


            g.setColor( Color.red );
            for( k = 1; k < rw; k++ ) {
                g.drawLine( k - 1, pot[k - 1], k, pot[k] );
            }

            animatorThread = new Thread( this );
            animatorThread.start();

            return true;
        }
        // Otherwise if a color was chosen, handle that
        else if( event.target == state_choices ) {
            String statename = (String)arg;
            if( arg.equals( "packet" ) ) {
                mode = 1;
            }
            else if( arg.equals( "injected" ) ) {
                mode = 2;
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
//      else{
//        String text = textField.getText();
//        textArea.appendText(text + "\n");
//        textField.selectAll();
//        return true;
//      }
    }

    public void start() {
        if( animatorThread != null ) {
            animatorThread.stop();
            animatorThread = null;
        }
        animatorThread = new Thread( this );
        animatorThread.start();

    }

    public void stop() {
        //Stop the animating thread.
        if( animatorThread != null ) {
            animatorThread.stop();
            animatorThread = null;
        }
        //Get rid of the objects necessary for double buffering.
        offGraphics = null;
        offImage = null;
//  System.exit(0);
    }

    public void run() {
        int i, j, k, ii, old_psi, loop, axis;
        double xx, a, b, t, psi_scale, psi_big, xk00;

        axis = 2 * rh / 5;

        Graphics g = this.getGraphics();
        Dimension d = size();

        if( offGraphics == null ) {
            offDimension = d;
            offImage = createImage( d.width, d.height );
            offGraphics = offImage.getGraphics();
        }

//                                                   days* millisec/day
//    if(System.currentTimeMillis() > 868887506157d + 45d*86400000d){
//    if( Date.after( deadline ) ){
//    if(System.currentTimeMillis() > Date.parse("15 February 2007") ){
        if( System.currentTimeMillis() > 1171490400000d ) {

            g.setColor( Color.white );
            g.drawString( "This applet has expired. Please reload from", 10, 60 );
            g.drawString( "http://www.fen.bilkent.edu.tr/~yalabik/applets/t_d_quant.html"
                    , 10, 90 );
            g.drawString( "or inform me through e-mail: yalabik@fen.bilkent.edu.tr"
                    , 10, 120 );
            if( animatorThread != null ) {
                animatorThread.stop();
                animatorThread = null;
            }
            return;
        }


        while( Thread.currentThread() == animatorThread ) {

            if( mode == 1 ) {
                xk00 = xk0;
                psi_scale = rh * 0.3;
            }
            else {
                xk00 = ( (int)( xk0 * 8. / pi + .5 ) ) * pi / 8.;
                psi_scale = rh * 0.2;
            }

            for( i = 0; i < nn; i++ ) {
                xx = Math.exp( -( i - i0 ) * ( i - i0 ) / ( width * width ) );
                if( mode != 1 ) {
                    if( i < i0 ) {
                        xx = 1.;
                    }
                    if( i < n_bound2 ) {
                        xsi[0][i] = Math.cos( xk00 * i );
                        xsi[1][i] = Math.sin( xk00 * i );
                    }
                }
                psi[0][i] = xx * Math.cos( xk00 * i );
                psi[1][i] = xx * Math.sin( xk00 * i );
            }

            for( i = 0; i < nn; i++ ) {
                cpot[0][i] = Math.cos( dt * scale * ( rh - pot[i] - 20 ) );
                cpot[1][i] = -Math.sin( dt * scale * ( rh - pot[i] - 20 ) );
            }

/*
c
c
c ************* start the computation loop **************
c
*/
            t = 0.;
            for( loop = 0; ; loop++ ) {

                if( loop == looper ) {
                    g.drawString( "Psi[" + mu + "]=" + psi[0][mu] + " " + psi[1][mu] +
                                  " at start" + nn, 10, linen += 10 );
                }

                for( i = 0; i < nn; i++ ) {
                    a = psi[0][i];
                    b = psi[1][i];
                    psi[0][i] = a * cpot[0][i] - b * cpot[1][i];
                    psi[1][i] = a * cpot[1][i] + b * cpot[0][i];
                }
                linen = 80;
                if( loop == looper ) {
                    g.drawString( "cpot[" + mu + "]=" + cpot[0][mu] + " " + cpot[1][mu] +
                                  " at start", 10, linen += 10 );
                    g.drawString( "Psi[" + mu + "]=" + psi[0][mu] + " " + psi[1][mu] +
                                  " after cpot", 10, linen += 10 );
                }

/*  form the bc vector bsi for i~nn and csi for i~1  */

                for( i = 0; i < n_bound; i++ ) {
                    for( j = 0; j < 2; j++ ) {
                        bsi[j][i] = psi[j][nn - n_bound + i];
                        if( mode == 1 ) {
                            csi[j][i] = psi[j][i];
                        }
                        else {
                            csi[j][i] = psi[j][i] - xsi[j][i];
                        }
                    }
                }

/* extend this vector using the Lagrange extarpolation with positive ft: */

                for( i = 0; i < n_bound; i++ ) {
                    for( j = 0; j < 2; j++ ) {
                        bsi[j][n_bound + i] = 0;
                        csi[j][n_bound + i] = 0;
//        xsi[j][n_bound+i]=0;
                    }
                    for( k = 0; k < n_bound; k++ ) {
/* implementing    bsi(n_bound+i)=bsi(n_bound+i)+bsi(k)*(cc(i,k))  */
                        bsi[0][n_bound + i] = bsi[0][n_bound + i] + bsi[0][k] * cc[0][i][k]
                                              - bsi[1][k] * cc[1][i][k];
                        bsi[1][n_bound + i] = bsi[1][n_bound + i] + bsi[0][k] * cc[1][i][k]
                                              + bsi[1][k] * cc[0][i][k];
/* implementing     csi(nn_bound+i)=csi(n_bound+i)+csi(k)*conjg(cc(i,k))  */
                        csi[0][n_bound + i] = csi[0][n_bound + i] + csi[0][k] * cc[0][i][k]
                                              + csi[1][k] * cc[1][i][k];
                        csi[1][n_bound + i] = csi[1][n_bound + i] - csi[0][k] * cc[1][i][k]
                                              + csi[1][k] * cc[0][i][k];
//        if(mode != 1){
//        xsi[0][n_bound+i]=xsi[0][n_bound+i]+xsi[0][k]*cc[0][i][k]
//                                           +xsi[1][k]*cc[1][i][k] ;
//        xsi[1][n_bound+i]=xsi[1][n_bound+i]-xsi[0][k]*cc[1][i][k]
//                                           +xsi[1][k]*cc[0][i][k] ;
//        }
                    }  /* for k */
                }    /* for i */

/*  develop this part of wf (using periodic bc)  */

                fft( n_bound2, bfou, bsi, -1 );
                fft( n_bound2, bfou, csi, -1 );
                if( mode != 1 ) {
                    fft( n_bound2, bfou, xsi, -1 );
                }
/*
   do the momentum space updating and normalization
*/
                for( i = 0; i < n_bound2; i++ ) {
                    a = bsi[0][i];
                    b = bsi[1][i];
                    bsi[0][i] = a * bksq[0][i] - b * bksq[1][i];
                    bsi[1][i] = a * bksq[1][i] + b * bksq[0][i];

                    a = csi[0][i];
                    b = csi[1][i];
                    csi[0][i] = a * bksq[0][i] - b * bksq[1][i];
                    csi[1][i] = a * bksq[1][i] + b * bksq[0][i];

                    if( mode != 1 ) {
                        a = xsi[0][i];
                        b = xsi[1][i];
                        xsi[0][i] = a * bksq[0][i] - b * bksq[1][i];
                        xsi[1][i] = a * bksq[1][i] + b * bksq[0][i];
                    }
                }

/*   inverse transform to position space   */

                fft( n_bound2, bfou, bsi, 1 );
                fft( n_bound2, bfou, csi, 1 );
                if( mode != 1 ) {
                    fft( n_bound2, bfou, xsi, 1 );
                }

/*   fourier transform the wavefunction:  */

                fft( nn, cfo, psi, -1 );

                if( loop == looper ) {
                    g.drawString( "Psi[" + mu + "]=" + psi[0][mu] + " " + psi[1][mu] +
                                  " after fft", 10, linen += 10 );
                }

/*   do the momentum space updating and normalization  */

                for( i = 0; i < nn; i++ ) {
                    a = psi[0][i];
                    b = psi[1][i];
                    psi[0][i] = a * cenerg[0][i] - b * cenerg[1][i];
                    psi[1][i] = a * cenerg[1][i] + b * cenerg[0][i];
                }

                if( loop == looper ) {
                    g.drawString( "Psi[" + mu + "]=" + psi[0][mu] + " " + psi[1][mu] +
                                  " after cenerg", 10, linen += 10 );
                }

/*   inverse transform to position space  */

                fft( nn, cfo, psi, 1 );

                if( loop == looper ) {
                    g.drawString( "Psi[" + mu + "]=" + psi[0][mu] + " " + psi[1][mu] +
                                  " after i-fft", 10, linen += 10 );
                }

/* interpolate the boundary region between the two forms of solutions  */


                for( i = 0; i < nbo; i++ ) {
                    for( j = 0; j < 2; j++ ) {
                        psi[j][nn - nbo + i] = bsi[j][nbo + i];
                        if( mode == 1 ) {
                            psi[j][i] = csi[j][i];
                        }
                        else {
                            psi[j][i] = csi[j][i] + xsi[j][i];
                        }
                    }
                }

/*
c simulation is over only for one tour...
c time to make the increments...
c and plot ...
c  */

                t = t + dt;
                mu = ( mu + 1 ) % nn;

                offGraphics.setColor( getBackground() );
                offGraphics.fillRect( 0, 0, rw, rh );
                offGraphics.setColor( Color.red );
                for( k = 1; k < rw; k++ ) {
                    offGraphics.drawLine( k - 1, pot[k - 1], k, pot[k] );
                }

                psi_big = 0.;
                for( k = 0; k < nn; k++ ) {
                    xx = psi[0][k] * psi[0][k] + psi[1][k] * psi[1][k];
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
//    psi_scale=rh*0.3/psi_big;
//    psi_scale=rh*0.3;
                offGraphics.setColor( Color.yellow );
                old_psi = axis - (int)( psi_scale * psi[0][0] );
                for( k = 1; k < nn; k++ ) {
                    ii = axis - (int)( psi_scale * psi[0][k] );
                    offGraphics.drawLine( k - 1, old_psi, k, ii );
                    old_psi = ii;
                }
                offGraphics.setColor( Color.green );
                old_psi = axis - (int)( psi_scale * psi[1][0] );
                for( k = 1; k < nn; k++ ) {
                    ii = axis - (int)( psi_scale * psi[1][k] );
                    offGraphics.drawLine( k - 1, old_psi, k, ii );
                    old_psi = ii;
                }
                offGraphics.setColor( Color.white );
                old_psi = axis - (int)( psi_scale * Math.sqrt( psi[0][0] * psi[0][0] + psi[1][0] * psi[1][0] ) );
                for( k = 1; k < nn; k++ ) {
                    ii = axis - (int)( psi_scale * Math.sqrt( psi[0][k] * psi[0][k] + psi[1][k] * psi[1][k] ) );
                    offGraphics.drawLine( k - 1, old_psi, k, ii );
                    old_psi = ii;
                }

//    g.setColor(Color.white);
                offGraphics.drawString( "t = " + t, 10, 4 * rh / 5 );

                g.drawImage( offImage, 0, 0, this );

                if( loop == looper ) {
                    g.drawString( "Psi[" + mu + "]=" + psi[0][mu] + " " + psi[1][mu] + " end of loop", 10, linen += 10 );
                    g.drawString( "psi_scale=" + psi_scale, 10, linen += 10 );
                }


            }  /* closes for - loop  */
        }   /*  closes while  */
    }


    static public void fft( int n, double[][] cfou,
                            double[][] vect, int mode )
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
                buf[j][i] = vect[j][i];
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
//        System.out.println("nh="+nh+" length="+length+" k="+k+
//                           " j="+j+" J="+J+" K="+K+
//                           " L="+L+" M="+M+
//                           " cfr= "+cfr+" cfi= "+cfi);
                    buf1r = buf[0][L];
                    buf1i = buf[1][L];
                    buf2r = buf[0][M];
                    buf2i = buf[1][M];
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
                        buf[j][i] = vect[j][i];
                    }
                }
            }

        }   //  for - nh
        return;
    }


}
