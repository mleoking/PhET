package edu.colorado.phet.mazegame;

//edu.colorado.phet.mazegame.ArrowA.class  M.Dubson 5/30/02  Draws a nice arrow.

import java.awt.*;

public class ArrowA {

    private double L;  	//length of arrow
    private double thta;//angle of of arrow
    private int x0, y0;			//origin of arrow
    private int xFinal, yFinal;	//head of arrow
    private double w;		//width of arrow
    private double h;		//length of arrow head
    private double pi = Math.PI;
    private int[] xInt = new int[8];	//positions of arrow corners for polygon()
    private int[] yInt = new int[8];


    public ArrowA() //default constructor
    {
        this.x0 = 0;
        this.y0 = 0;
        this.xFinal = 0;
        this.yFinal = 0;
    }

    //set arrow position arrow to head:(xFinal, yFinal), tail:(x0, y0)

    //formula for width of arrow line
    public double computeWidth() {
        return (this.L / 8.0);
    }

    public double getLength() {
        double x1 = xFinal - x0;  	//x-component of arrow
        double y1 = yFinal - y0;	//y-component of arrow
        this.L = Math.pow((x1 * x1 + y1 * y1), 0.5);
        return this.L;
    }

    public void setPosition(int x0, int y0, int xFinal, int yFinal) {
        this.x0 = x0;
        this.y0 = y0;
        this.xFinal = xFinal;
        this.yFinal = yFinal;
        double x1 = xFinal - x0;  	//x-component of arrow
        double y1 = yFinal - y0;	//y-component of arrow
        this.L = Math.pow((x1 * x1 + y1 * y1), 0.5);
        //this.thta = Math.asin((yFinal - y0)/L);  //not necessary
        this.w = this.computeWidth();
        this.h = 4 * w * Math.sqrt(3) / 2.0;

        double[] x = new double[]{(w / 2) * (-y1 / L),
                                  (w / 2) * (-y1 / L) + (L - h) * (x1 / L),
                                  (w) * (-y1 / L) + (L - h) * (x1 / L),
                                  (L) * (x1 / L),
                                  (w) * (y1 / L) + (L - h) * (x1 / L),
                                  (w / 2) * (y1 / L) + (L - h) * (x1 / L),
                                  (w / 2) * (y1 / L),
                                  (w / 2) * (-y1 / L)};

        double[] y = new double[]{(w / 2) * x1 / L,
                                  (w / 2) * (x1 / L) + (L - h) * (y1 / L),
                                  (w) * (x1 / L) + (L - h) * (y1 / L),
                                  (L) * (y1 / L),
                                  (w) * (-x1 / L) + (L - h) * (y1 / L),
                                  (w / 2) * (-x1 / L) + (L - h) * (y1 / L),
                                  (w / 2) * (-x1 / L),
                                  (w / 2) * (x1 / L)};

        for (int i = 0; i < x.length; i++) {
            xInt[i] = x0 + (int) (x[i]);
            yInt[i] = y0 + (int) (y[i]);
        }

    }//end of position()


    public void paint(Graphics g) {
        //System.out.println("deltaX = " + (xFinal - x0) + "  deltaY = " + (yFinal - y0));
        g.setColor(Color.green);
        g.fillPolygon(xInt, yInt, xInt.length);
        g.setColor(Color.red);
        g.drawPolygon(xInt, yInt, xInt.length);
    }//end of paint method

}//end of public class