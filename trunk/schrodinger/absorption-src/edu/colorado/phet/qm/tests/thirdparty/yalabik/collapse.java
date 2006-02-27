package edu.colorado.phet.qm.tests.thirdparty.yalabik;

import java.applet.Applet;
import java.awt.*;

/* Cemal Yalabik yalabik@fen.bilkent.edu.tr  */

public class collapse extends Applet implements Runnable {

    Thread animatorThread;

    Dimension offDimension;
    Image offImage;
    Graphics offGraphics;

    private Button clear_button;
    private Choice run_choices;
    private Choice sil_choices;
    private Choice mag_choices;
    private Choice det_choices;

    private int rw;   /* width of drawing area. Set by size of rectangle */
    private int rh;       /* height ditto. */

    private int n1, n2, m1, m2, sil_size, j_end;
    private int rx, ry;

    static private double[][] buf = new double[2][256];


    public void init() {
        int i, j, k;
        double a, b, c, d, e, f;

        // Set the background color
        this.setBackground( Color.black );
        this.setForeground( Color.white );

        // Create a button and add it to the applet.
        // Also, set the button's colors
/*  clear_button = new Button("Reset");
    clear_button.setForeground(Color.black);
    clear_button.setBackground(Color.lightGray);
    this.add(clear_button);
*/

        // Create a menu of start/stop/reset and add it to the applet.
        // Also set the menus's colors and add a label.
        run_choices = new Choice();
        run_choices.addItem( "Restart" );
        run_choices.addItem( "Stop" );
        run_choices.setForeground( Color.black );
        run_choices.setBackground( Color.lightGray );
        this.add( run_choices );
        run_choices.select( 1 );

        // Create a menu of slit size and add it to the applet.
        // Also set the menus's colors and add a label.
        mag_choices = new Choice();
        mag_choices.addItem( "10" );
        mag_choices.addItem( "15" );
        mag_choices.addItem( "20" );
        mag_choices.addItem( "25" );
        mag_choices.addItem( "30" );
        mag_choices.addItem( "35" );
        mag_choices.setForeground( Color.black );
        mag_choices.setBackground( Color.lightGray );
        this.add( new Label( "slit size:" ) );
        this.add( mag_choices );
        mag_choices.select( 2 );
        sil_size = 20; // This is necessary, the "select" does not interrupt

        // Create a menu of slit numbers and add it to the applet.
        // Also set the menus's colors and add a label.
        sil_choices = new Choice();
        sil_choices.addItem( "1" );
        sil_choices.addItem( "2" );
        sil_choices.setForeground( Color.black );
        sil_choices.setBackground( Color.lightGray );
        this.add( new Label( "No of slits:" ) );
        this.add( sil_choices );
        sil_choices.select( 1 );
        n2 = 57;
        n1 = n2 - sil_size;
        m1 = 68;
        m2 = m1 + sil_size;  // This is necessary, the "select" does not interrupt

        // Create a menu for detector positions and add it to the applet.
        // Also set the menus's colors and add a label.
        det_choices = new Choice();
        det_choices.addItem( "-25" );
        det_choices.addItem( "15" );
        det_choices.addItem( "35" );
        det_choices.addItem( "50" );
        det_choices.addItem( "75" );
        det_choices.setForeground( Color.black );
        det_choices.setBackground( Color.lightGray );
        this.add( new Label( "detector pos:" ) );
        this.add( det_choices );
        det_choices.select( 4 );
        j_end = 200;


        Rectangle r = this.bounds();

        rw = r.width;
        rh = r.height;
        rx = r.x;
        ry = r.y;

        animatorThread = new Thread( this );
        animatorThread.start();
    }


    // Called when the user clicks the button or chooses a friction value
    public boolean action( Event event, Object arg ) {
        int i, j;

        // If the Reset button was clicked on, handle it.
//  if (event.target == clear_button) {

        if( event.target == run_choices ) {
            if( animatorThread != null ) {
                animatorThread.stop();
                animatorThread = null;
            }
            String statename = (String)arg;
            if( arg.equals( "Restart" ) ) {
                Graphics g = this.getGraphics();
                Rectangle r = this.bounds();
                g.setColor( this.getBackground() );
                g.fillRect( rx, ry, rw, rh );

                // reset coordinates, etc.

                animatorThread = new Thread( this );
                animatorThread.start();
                run_choices.select( 1 );
                return true;
            }
            else if( arg.equals( "Stop" ) ) {
                run_choices.select( 0 );
                return true;
            }
        }

        // Otherwise if a friction value was chosen, handle that
        if( event.target == sil_choices ) {

            String statename = (String)arg;
            if( arg.equals( "1" ) ) {m2 = m1 - 1;}
            else if( arg.equals( "2" ) ) {m2 = m1 + sil_size;}

            return true;

        }
        else if( event.target == mag_choices ) {

            String statename = (String)arg;
            if( arg.equals( "10" ) ) {
                sil_size = 10;
            }
            else if( arg.equals( "15" ) ) {
                sil_size = 15;
            }
            else if( arg.equals( "20" ) ) {
                sil_size = 20;
            }
            else if( arg.equals( "25" ) ) {
                sil_size = 25;
            }
            else if( arg.equals( "30" ) ) {
                sil_size = 30;
            }
            else if( arg.equals( "35" ) ) {
                sil_size = 35;
            }
            n1 = n2 - sil_size;
            if( m2 > m1 ) {
                m2 = m1 + sil_size;
            }

            return true;

        }
        else if( event.target == det_choices ) {

            String statename = (String)arg;
            if( arg.equals( "-25" ) ) {
                j_end = 100;
            }
            else if( arg.equals( "15" ) ) {
                j_end = 140;
            }
            else if( arg.equals( "35" ) ) {
                j_end = 165;
            }
            else if( arg.equals( "50" ) ) {
                j_end = 180;
            }
            else if( arg.equals( "75" ) ) {
                j_end = 200;
            }
//    j_end values are approximately 127 larger than the choices to shift
//     the reference to the slits.

            if( animatorThread != null ) {
                animatorThread.stop();
                animatorThread = null;
            }
            animatorThread = new Thread( this );
            animatorThread.start();

            return true;

        }

        // Otherwise, let the superclass handle it.
        else {
            return super.action( event, arg );
        }
    }

/* ----- this part did not seem to work, so removing it!
  public boolean handleEvent(Event e){
    int i,j;
    switch(e.id){
      case Event.WINDOW_ICONIFY:
        if (animatorThread != null) {
          animatorThread.stop();
          animatorThread=null;
        }
        break;
      case Event.WINDOW_DEICONIFY:
        // reset coordinates, etc.

        animatorThread = new Thread(this);
        animatorThread.start();
        break;
      case Event.WINDOW_DESTROY:
        System.exit(0);
        break;
      }
      return super.handleEvent(e);
    }
//}
      --------  end of removed part.. */

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
        int i, j, x, y, xbef, ybef, ipos, jpos, j_start;
        int m, mm, n, nn, n_low, n_left, pen, side;
        double momentum, dd, delta_t, a_r, a_i, t_r, t_i, pi, xx, p_now, p_total, prob;
        int[] maxes = new int[512];
        double[][] reel = new double[127][255];
        double[][] imag = new double[127][255];
        double[] p_left = new double[127];
        double[] p_right = new double[127];


        double[][] cfo = new double[2][257];
        double[][] psi = new double[2][256];
        double[][] cenerg = new double[2][256];

        Graphics g = this.getGraphics();
        Dimension d = size();

        Thread.currentThread().setPriority( Thread.MIN_PRIORITY );

        if( offGraphics == null ) {
            offDimension = d;
            offImage = createImage( d.width, d.height );
            offGraphics = offImage.getGraphics();
        }

//  if(System.currentTimeMillis() > Date.parse("15 February 2007") ){
        if( System.currentTimeMillis() > 1171490400000d ) {

            g.setColor( Color.white );
            g.drawString( "This applet has expired. Please reload from", 10, 60 );
            g.drawString( "http://www.fen.bilkent.edu.tr/~yalabik/applets/collapse.html", 10, 90 );
            g.drawString( "or inform me through e-mail: yalabik@fen.bilkent.edu.tr", 10, 120 );
            if( animatorThread != null ) {
                animatorThread.stop();
                animatorThread = null;
            }
            return;
        }

        while( Thread.currentThread() == animatorThread ) {

            long startTime = System.currentTimeMillis();

            n = 256; // size of fft arrays
            nn = 127; // size of boxes
            mm = 255; // combined size of boxes
            m = 500; // horizontal size; should not exceed d.width
            n_low = d.height - 10;
            n_left = nn / 2;
/*    n1=45;
      n2=57;
      m1=68;
      m2=80; */
            ipos = nn / 2;
            j_start = mm / 4; // starting point of horizontal display
//    j_end=3*mm/4+1;
            jpos = 5 * nn / 8;
            delta_t = 0.5;
            momentum = 0.8;
            p_total = 0.;
            p_now = 0.;

            for( i = 0; i < nn; i++ ) {
                for( j = 0; j < mm; j++ ) {
                    dd = Math.exp( -0.003 * ( ( j - jpos ) * ( j - jpos ) + ( i - ipos ) * ( i - ipos ) ) );
                    reel[i][j] = dd * Math.cos( momentum * j );
                    imag[i][j] = dd * Math.sin( momentum * j );
                }
                p_left[i] = 0.;
                p_right[i] = 0.;
            }

/* initialization of fft variables */

            pi = 4. * Math.atan( 1. );
            for( i = 0; i <= n; i++ ) {
                xx = 2 * i * pi / n;
                cfo[0][i] = Math.cos( xx );
                cfo[1][i] = Math.sin( xx );
            }

            for( i = 0; i < n; i++ ) {
                xx = Math.sin( pi * i / n );
                xx = 4. * xx * xx * delta_t;
                cenerg[0][i] = Math.cos( xx ) / n;
                cenerg[1][i] = -Math.sin( xx ) / n;
            }


            for( ; jpos < 320; jpos++ ) {

//      g.drawString("drawing frame"+jpos,10, 60);

//      re-draw plot for new value
                offGraphics.setColor( getBackground() );
                offGraphics.fillRect( 0, 0, rw, rh );

//      find out probability that the detection occured in previous step:
                side = 0;
                prob = p_now / ( 523. - p_total );
                p_total = p_total + p_now;
                if( 523. - p_total <= 0. || prob > Math.random() ) {
                    xx = p_now * Math.random();
                    for( ipos = 0; ipos < nn; ipos++ ) {
                        xx = xx - p_left[ipos];
                        if( xx < 0. ) {
                            side = -1;
                            break;
                        }
                        xx = xx - p_right[ipos];
                        if( xx < 0. ) {
                            side = 1;
                            break;
                        }
                    }
                }

                if( side == 0 ) {  //  indentless!
//      update wavefunction - evaluate vertical ft - left half:
                    for( j = 0; j < nn; j++ ) {
                        for( i = 0; i < nn; i++ ) {
                            psi[0][i + 1] = reel[i][j];
                            psi[1][i + 1] = imag[i][j];
                            psi[0][n - 1 - i] = -psi[0][i + 1];
                            psi[1][n - 1 - i] = -psi[1][i + 1];
                        }
                        psi[0][0] = 0.;
                        psi[1][0] = 0.;
                        psi[0][nn + 1] = 0.;
                        psi[1][nn + 1] = 0.;
                        fft( n, cfo, psi, 1 );

//        momentum space update:
                        for( i = 0; i < n; i++ ) {
                            a_r = psi[0][i] * cenerg[0][i] - psi[1][i] * cenerg[1][i];
                            a_i = psi[0][i] * cenerg[1][i] + psi[1][i] * cenerg[0][i];
                            psi[0][i] = a_r;
                            psi[1][i] = a_i;
                        }

//        inverse ft:
                        fft( n, cfo, psi, -1 );
                        for( i = 0; i < nn; i++ ) {
                            reel[i][j] = psi[0][i + 1];
                            imag[i][j] = psi[1][i + 1];
                        }
                    }   // the j - for

//      horizontal ft - left half:
                    for( j = 0; j < nn; j++ ) {
                        for( i = 0; i < nn; i++ ) {
                            psi[0][i + 1] = reel[j][i];
                            psi[1][i + 1] = imag[j][i];
                            psi[0][n - 1 - i] = -psi[0][i + 1];
                            psi[1][n - 1 - i] = -psi[1][i + 1];
                        }
                        psi[0][0] = 0.;
                        psi[1][0] = 0.;
                        psi[0][nn + 1] = 0.;
                        psi[1][nn + 1] = 0.;
                        fft( n, cfo, psi, 1 );

//        momentum space update:
                        for( i = 0; i < n; i++ ) {
                            a_r = psi[0][i] * cenerg[0][i] - psi[1][i] * cenerg[1][i];
                            a_i = psi[0][i] * cenerg[1][i] + psi[1][i] * cenerg[0][i];
                            psi[0][i] = a_r;
                            psi[1][i] = a_i;
                        }

//        inverse ft:
                        fft( n, cfo, psi, -1 );
                        for( i = 0; i < nn; i++ ) {
                            reel[j][i] = psi[0][i + 1];
                            imag[j][i] = psi[1][i + 1];
                        }
                    }   // the j - for

// ++++++++++++++++++

//      now repeat for the right half: j changes nn+1 to mm
//      evaluate vertical ft:
                    for( j = 0; j < nn; j++ ) {
                        for( i = 0; i < nn; i++ ) {
                            psi[0][i + 1] = reel[i][j + nn + 1];
                            psi[1][i + 1] = imag[i][j + nn + 1];
                            psi[0][n - 1 - i] = -psi[0][i + 1];
                            psi[1][n - 1 - i] = -psi[1][i + 1];
                        }
                        psi[0][0] = 0.;
                        psi[1][0] = 0.;
                        psi[0][nn + 1] = 0.;
                        psi[1][nn + 1] = 0.;
                        fft( n, cfo, psi, 1 );

//        momentum space update:
                        for( i = 0; i < n; i++ ) {
                            a_r = psi[0][i] * cenerg[0][i] - psi[1][i] * cenerg[1][i];
                            a_i = psi[0][i] * cenerg[1][i] + psi[1][i] * cenerg[0][i];
                            psi[0][i] = a_r;
                            psi[1][i] = a_i;
                        }

//        inverse ft:
                        fft( n, cfo, psi, -1 );
                        for( i = 0; i < nn; i++ ) {
                            reel[i][j + nn + 1] = psi[0][i + 1];
                            imag[i][j + nn + 1] = psi[1][i + 1];
                        }
                    }   // the j - for

//      horizontal ft - right half:
                    for( j = 0; j < nn; j++ ) {
                        for( i = 0; i < nn; i++ ) {
                            psi[0][i + 1] = reel[j][i + nn + 1];
                            psi[1][i + 1] = imag[j][i + nn + 1];
                            psi[0][n - 1 - i] = -psi[0][i + 1];
                            psi[1][n - 1 - i] = -psi[1][i + 1];
                        }
                        psi[0][0] = 0.;
                        psi[1][0] = 0.;
                        psi[0][nn + 1] = 0.;
                        psi[1][nn + 1] = 0.;
                        fft( n, cfo, psi, 1 );

//        momentum space update:
                        for( i = 0; i < n; i++ ) {
                            a_r = psi[0][i] * cenerg[0][i] - psi[1][i] * cenerg[1][i];
                            a_i = psi[0][i] * cenerg[1][i] + psi[1][i] * cenerg[0][i];
                            psi[0][i] = a_r;
                            psi[1][i] = a_i;
                        }

//        inverse ft:
                        fft( n, cfo, psi, -1 );
                        for( i = 0; i < nn; i++ ) {
                            reel[j][i + nn + 1] = psi[0][i + 1];
                            imag[j][i + nn + 1] = psi[1][i + 1];
                        }
                    }   // the j - for

// ++++++++++++++++++

//      combine the two boxes with two slits at n1-n2 and m1-m2:
                    for( i = n1; i < m2; i++ ) {
                        if( ( i >= n1 && i < n2 ) || ( i >= m1 && i < m2 ) ) {
                            a_r = reel[i][nn - 1];
                            a_i = imag[i][nn - 1];
                            reel[i][nn - 1] = reel[i][nn + 1];
                            imag[i][nn - 1] = imag[i][nn + 1];
                            reel[i][nn + 1] = a_r;
                            imag[i][nn + 1] = a_i;
                        }
                    }

//      calculate currents and accumulate probabilities:
                    p_now = 0;
                    if( jpos > 100 ) {
                        for( i = 0; i < nn; i++ ) {
                            p_right[i] = delta_t * (
                                    reel[i][j_end] * ( imag[i][j_end + 1] - imag[i][j_end - 1] ) -
                                    imag[i][j_end] * ( reel[i][j_end + 1] - reel[i][j_end - 1] ) );
                            p_left[i] = delta_t * (
                                    reel[i][j_start] * ( imag[i][j_start - 1] - imag[i][j_start + 1] ) -
                                    imag[i][j_start] * ( reel[i][j_start - 1] - reel[i][j_start + 1] ) );
                            if( p_left[i] > 0. ) {p_now = p_now + p_left[i];}
                            if( p_right[i] > 0. ) {p_now = p_now + p_right[i];}
                        }
                    }

                } // the if(side==0) -- indentless!
                else {  // ditto!
//        collapse the wave function!
                    for( i = 0; i < nn; i++ ) {
                        for( j = 0; j < mm; j++ ) {
                            reel[i][j] = 0.;
                            imag[i][j] = 0.;
                        }
                    }
                    p_total = 523.;
                    p_now = 0.;
                    for( i = 0; i < nn; i++ ) {
                        p_left[i] = 0.;
                        p_right[i] = 0.;
                    }
                    if( side < 0 ) { p_left[ipos] = 0.1;}
                    else { p_right[ipos] = 0.1;}
                }

//      plot the function:

//      plot the probabilities first:
                x = 2 * ( j_end - j_start ) - 1 + n_left;
                if( side == 0 ) {
                    offGraphics.setColor( Color.white );  //the normal plotting color
                    offGraphics.drawString( "Total probability in detectors: " +
                                            (int)( 0.5 + 100. * ( p_total + p_now ) / 523. ) + " percent", 10, 90 );
                }
                else {
                    offGraphics.setColor( Color.pink );  //the collapsed plotting color
                    offGraphics.drawString( "Wavefunction collapsed!", 10, 90 );
                    if( side < 0 ) {
                        offGraphics.drawString( "click!", n_left + ipos - 65, n_low - ipos );
                    }
                    else {
                        offGraphics.drawString( "click!", x + ipos + 35, n_low - ipos );
                    }
                }
                for( i = 0; i < nn; i++ ) {
                    x = x + 1;
                    y = n_low - i;
                    offGraphics.drawLine( i + n_left, y, i + n_left - (int)( 300. * p_left[i] ), y );
                    offGraphics.drawLine( x, y, x + (int)( 300. * p_right[i] ), y );
                }

//      now the graph:
//      offGraphics.setColor(Color.yellow);  //the plotting color
                pen = 1;
                for( j = 0; j < m; j++ ) {maxes[j] = n + n;}
                for( i = 1; i < nn; i = i + 1 ) {
                    ybef = n_low -
                           (int)( i + 100. * ( reel[i][j_start] * reel[i][j_start] +
                                               imag[i][j_start] * imag[i][j_start] ) );
                    xbef = i + n_left;
                    for( j = 1; j + j_start < j_end; j++ ) {
                        x = i + j + j + n_left;
                        y = n_low -
                            (int)( i + 100. * ( reel[i][j + j_start] * reel[i][j + j_start] +
                                                imag[i][j + j_start] * imag[i][j + j_start] ) );
                        if( maxes[x] >= y ) {
                            maxes[x] = y;
                            if( pen == 1 ) {
                                offGraphics.setColor( Color.yellow );  //the plotting color
                                offGraphics.drawLine( xbef, ybef, x, y );
                            }
                            else { pen = 1; }
                            xbef = x;
                            ybef = y;
                        }
                        else { pen = 0;}
                    }
                    if( i < n1 || ( i >= n2 && i < m1 ) || i >= m2 ) {  // for plotting the slit
                        j = nn - j_start;
                        x = i + j + j + n_left;
                        y = n_low - i;
                        if( maxes[x] >= y ) {
                            offGraphics.setColor( Color.red );
                            offGraphics.drawLine( x - 2, y + 1, x, y );
                        }
                    }

                }

                g.drawImage( offImage, 0, 0, this );

//      wait for a while if running too fast
                try {
                    startTime += 30;  // decrease this to increase speed
                    Thread.sleep( Math.max( 0, startTime - System.currentTimeMillis() ) );
                }
                catch( InterruptedException e ) {
                    startTime = System.currentTimeMillis();
                    break;
                }

//      wait a sec (or two!) if wf collapsed:
                if( side != 0 ) {
                    try { Thread.sleep( 2000 );}
                    catch( InterruptedException e ) { }

//        and break apart from the jpos loop to start again:
                    break;
                }

            }     /*  closes jpos loop */


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

