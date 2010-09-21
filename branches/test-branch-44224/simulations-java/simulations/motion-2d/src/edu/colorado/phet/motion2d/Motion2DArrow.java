package edu.colorado.phet.motion2d;

import java.awt.*;

public class Motion2DArrow {

    protected double L;      //length of arrow
    protected double w;        //width of arrow
    protected double h;        //length of arrow head
    protected int[] xInt = new int[8];    //positions of arrow corners for polygon()
    protected int[] yInt = new int[8];

    //set arrow position arrow to head:(xFinal, yFinal), tail:(x0, y0)

    //formula for width of arrow line

    public double computeWidth() {
        return ( Math.min( 6.0, this.L / 10.0 ) );
    }

    public void setPosition( double x0, double y0, double xFinal, double yFinal ) {
        double x1 = xFinal - x0;      //x-component of arrow
        double y1 = yFinal - y0;    //y-component of arrow
        this.L = Math.pow( ( x1 * x1 + y1 * y1 ), 0.5 );
        //this.thta = Math.asin((yFinal - y0)/L);  //not necessary
        this.w = this.computeWidth();
        this.h = 4 * w * Math.sqrt( 3 ) / 2.0;

        double[] x = new double[]{( w / 2 ) * ( -y1 / L ),
                ( w / 2 ) * ( -y1 / L ) + ( L - h ) * ( x1 / L ),
                ( w ) * ( -y1 / L ) + ( L - h ) * ( x1 / L ),
                ( L ) * ( x1 / L ),
                ( w ) * ( y1 / L ) + ( L - h ) * ( x1 / L ),
                ( w / 2 ) * ( y1 / L ) + ( L - h ) * ( x1 / L ),
                ( w / 2 ) * ( y1 / L ),
                ( w / 2 ) * ( -y1 / L )};

        double[] y = new double[]{( w / 2 ) * x1 / L,
                ( w / 2 ) * ( x1 / L ) + ( L - h ) * ( y1 / L ),
                ( w ) * ( x1 / L ) + ( L - h ) * ( y1 / L ),
                ( L ) * ( y1 / L ),
                ( w ) * ( -x1 / L ) + ( L - h ) * ( y1 / L ),
                ( w / 2 ) * ( -x1 / L ) + ( L - h ) * ( y1 / L ),
                ( w / 2 ) * ( -x1 / L ),
                ( w / 2 ) * ( x1 / L )};

        for ( int i = 0; i < x.length; i++ ) {
            xInt[i] = (int) x0 + (int) ( x[i] );
            yInt[i] = (int) y0 + (int) ( y[i] );
        }

    }//end of position()


    public void paint( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        g.fillPolygon( xInt, yInt, xInt.length );
    }//end of paint method

}//end of public class