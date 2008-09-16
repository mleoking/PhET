package edu.colorado.phet.electrichockey;

//edu.colorado.phet.ehockey.ElectricHockeyArrow.class  M.Dubson 5/30/02  Draws a nice arrow.

import java.awt.*;

public class ElectricHockeyArrow {

    protected double L;      //length of arrow
    protected double thta;//angle of of arrow
    protected int x0, y0;            //origin of arrow
    protected int xFinal, yFinal;    //head of arrow
    protected double w;        //width of arrow
    protected double h;        //length of arrow head
    protected double pi = Math.PI;
    protected int[] xInt = new int[8];    //positions of arrow corners for polygon()
    protected int[] yInt = new int[8];


    public ElectricHockeyArrow() //default constructor
    {
        this.x0 = 0;
        this.y0 = 0;
        this.xFinal = 30;
        this.yFinal = 30;
    }

    //set arrow position arrow to head:(xFinal, yFinal), tail:(x0, y0)

    //formula for width of arrow line

    public double computeWidth() {
        return ( Math.min( 6.0, this.L / 10.0 ) );
    }

    public double getLength() {
        double x1 = xFinal - x0;      //x-component of arrow
        double y1 = yFinal - y0;    //y-component of arrow
        this.L = Math.pow( ( x1 * x1 + y1 * y1 ), 0.5 );
        return this.L;
    }

    public void setPosition( int x0, int y0, int xFinal, int yFinal ) {
        this.x0 = x0;
        this.y0 = y0;
        this.xFinal = xFinal;
        this.yFinal = yFinal;
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
            xInt[i] = x0 + (int) ( x[i] );
            yInt[i] = y0 + (int) ( y[i] );
        }

    }//end of position()


    public void paint( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.fillPolygon( xInt, yInt, xInt.length );
    }//end of paint method

}//end of public class