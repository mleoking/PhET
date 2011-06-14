// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.mazegame;

import java.awt.*;

//M.Dubson 5/30/02  Draws a nice arrow.
public class ArrowA {

    private double arrowLength;      //length of arrow
    private int[] arrowCornersX = new int[8];    //positions of arrow corners for polygon()
    private int[] arrowCornersY = new int[8];

    //set arrow position arrow to head:(xFinal, yFinal), tail:(x0, y0)

    //formula for width of arrow line

    public double computeWidth() {
        return ( this.arrowLength / 8.0 );
    }

    public void setPosition( int x0, int y0, int xFinal, int yFinal ) {
        double x1 = xFinal - x0;      //x-component of arrow
        double y1 = yFinal - y0;    //y-component of arrow
        this.arrowLength = Math.pow( ( x1 * x1 + y1 * y1 ), 0.5 );
        //this.thta = Math.asin((yFinal - y0)/L);  //not necessary
        double w = this.computeWidth();
        double h = 4 * w * Math.sqrt( 3 ) / 2.0;

        double[] x = new double[]{( w / 2 ) * ( -y1 / arrowLength ),
                ( w / 2 ) * ( -y1 / arrowLength ) + ( arrowLength - h ) * ( x1 / arrowLength ),
                ( w ) * ( -y1 / arrowLength ) + ( arrowLength - h ) * ( x1 / arrowLength ),
                ( arrowLength ) * ( x1 / arrowLength ),
                ( w ) * ( y1 / arrowLength ) + ( arrowLength - h ) * ( x1 / arrowLength ),
                ( w / 2 ) * ( y1 / arrowLength ) + ( arrowLength - h ) * ( x1 / arrowLength ),
                ( w / 2 ) * ( y1 / arrowLength ),
                ( w / 2 ) * ( -y1 / arrowLength )};

        double[] y = new double[]{( w / 2 ) * x1 / arrowLength,
                ( w / 2 ) * ( x1 / arrowLength ) + ( arrowLength - h ) * ( y1 / arrowLength ),
                ( w ) * ( x1 / arrowLength ) + ( arrowLength - h ) * ( y1 / arrowLength ),
                ( arrowLength ) * ( y1 / arrowLength ),
                ( w ) * ( -x1 / arrowLength ) + ( arrowLength - h ) * ( y1 / arrowLength ),
                ( w / 2 ) * ( -x1 / arrowLength ) + ( arrowLength - h ) * ( y1 / arrowLength ),
                ( w / 2 ) * ( -x1 / arrowLength ),
                ( w / 2 ) * ( x1 / arrowLength )};

        for ( int i = 0; i < x.length; i++ ) {
            arrowCornersX[i] = x0 + (int) ( x[i] );
            arrowCornersY[i] = y0 + (int) ( y[i] );
        }
    }

    public void paint( Graphics g ) {
        g.setColor( Color.green );
        g.fillPolygon( arrowCornersX, arrowCornersY, arrowCornersX.length );
        g.setColor( Color.red );
        g.drawPolygon( arrowCornersX, arrowCornersY, arrowCornersX.length );
    }

}