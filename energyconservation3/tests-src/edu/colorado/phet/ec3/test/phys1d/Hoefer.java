package edu.colorado.phet.ec3.test.phys1d;

//This applet implements interpolation using relaxed cubic splines
//The user can click on six points and move them around and the
//spline will be redrawn in real time

import java.applet.Applet;
import java.awt.*;

public class Hoefer extends Applet {
    Point[] points;    //points to be interpolated
    Point[] control;    //control points
    int numpoints;
    Image offscreenImg;    //used in double buffering
    Graphics offscreenG;
    double t;    //time variable
    static final double k = .05; //partition length
    int moveflag;    //point movement
    Button polygon;
    boolean poly = false;
    int ltnumpoints = 6;
    private int lt3 = 6;

    //this method initializes the applet
    public void init() {
//start off with 6 points
        points = new Point[6];
        control = new Point[6];
        numpoints = 6;
        moveflag = numpoints;
        int increment = (int)( ( size().width - 60 ) / ( numpoints - 1 ) );
        for( int i = 0; i < ltnumpoints; i++ ) {
            points[i] = new Point( ( i * increment ) + 30, (int)( size().height / 2 ) );
        }
//create offscreen buffer
        offscreenImg = createImage( size().width, size().height );
        offscreenG = offscreenImg.getGraphics();
//put "Polygon" button on screen
        polygon = new Button( "Polygon" );
        add( polygon );
    }

    //this method is called by the repaint() method
    public void update( Graphics g ) {
        paint( g );
    }

    public void paint( Graphics g ) {
//points to be plotted
        int x1, y1, x2, y2;
//Clear screen and set colors
        setBackground( Color.white );
        offscreenG.setColor( Color.white );
        offscreenG.fillRect( 0, 0, size().width, size().height );
        offscreenG.setColor( Color.black );
//check if user wants control polygon and points drawn

        if( poly ) {
//draw control polygon and control points
            for( int i = 0; i < ltnumpoints - 1; i++ ) {
                offscreenG.fillOval( points[i].x - 2, points[i].y - 2, 4, 4 );
                offscreenG.fillOval( (int)( .6667 * control[i].x + .3333 * control[i + 1].x )
                                     - 2,

                                     (int)( .6667 * control[i].y + .3333 * control[i + 1].y )
                                     - 2, 4, 4 );
                offscreenG.fillOval( (int)( .3333 * control[i].x + .6667 * control[i + 1].x )
                                     - 2,

                                     (int)( .3333 * control[i].y + .6667 * control[i + 1].y )
                                     - 2, 4, 4 );
                offscreenG.fillOval( points[i + 1].x - 2, points[i + 1].y - 2, 4, 4 );
                offscreenG.drawLine( points[i].x, points[i].y, (int)( .6667 * control[i]
                        .x +

                           .3333 * control[i + 1].x ), (int)( .6667 * control[i].y
                                                              +
                                                              .3333 * control[i + 1].y ) );
                offscreenG.drawLine( (int)( .6667 * control[i].x + .3333 * control[i + 1].x )
                        ,

                                     (int)( .6667 * control[i].y + .3333 * control[i + 1].y )
                        ,

                                     (int)( .3333 * control[i].x + .6667 * control[i + 1].x )
                        ,

                                     (int)( .3333 * control[i].y + .6667 * control[i + 1].y )
                );
                offscreenG.drawLine( (int)( .3333 * control[i].x + .6667 * control[i + 1].x )
                        ,

                                     (int)( .3333 * control[i].y + .6667 * control[i + 1].y )
                        ,
                                     points[i + 1].x, points[i + 1].y );
            }
        }
//Change interpolating points into control points
        control[0] = new Point( points[0].x, points[0].y );
        control[numpoints - 1] = new Point( points[numpoints - 1].x,
                                            points[numpoints - 1].y );
        x1 = (int)( 1.6077 * (double)points[1].x - .26794 * (double)points[0].x -
                    .43062 * (double)points[2].x + .11483 * (double)points[3].x -
                                                                                .028708 * (double)points[4].x + .004785 * (double)points[5].x );
        y1 = (int)( 1.6077 * (double)points[1].y - .26794 * (double)points[0].y -
                    .43062 * (double)points[2].y + .11483 * (double)points[3].y -

                                                                                .028708 * (double)points[4].y + .004785 * (double)points[5].y );

        control[1] = new Point( x1, y1 );
        x1 = (int)( -.43062 * (double)points[1].x + .07177 * (double)points[0].x +
                    1.7225 * (double)points[2].x - .45933 * (double)points[3].x +
                                                                                .11483 * (double)points[4].x - .019139 * (double)points[3].x );

        y1 = (int)( -.43062 * (double)points[1].y + .07177 * (double)points[0].y +
                    1.7225 * (double)points[2].y - .45933 * (double)points[3].y +
                                                                                .11483 * (double)points[4].y - .019139 * (double)points[3].y );

        control[2] = new Point( x1, y1 );
        x1 = (int)( .11483 * (double)points[1].x - .019139 * (double)points[0].x -
                    .45933 * (double)points[2].x + 1.7225 * (double)points[3].x -
                                                                                .43062 * (double)points[4].x + .07177 * (double)points[5].x );

        y1 = (int)( .11483 * (double)points[1].y - .019139 * (double)points[0].y -
                    .45933 * (double)points[2].y + 1.7225 * (double)points[3].y -
                                                                                .43062 * (double)points[4].y + .07177 * (double)points[5].y );

        control[3] = new Point( x1, y1 );
        x1 = (int)( -.028708 * (double)points[1].x + .004785 * (double)points[0].x +
                    .114835 * (double)points[2].x - .43062 * (double)points[3].x +
                                                                                 1.6077 * (double)points[4].x - .26794 * (double)points[5].x );

        y1 = (int)( -.028708 * (double)points[1].y + .004785 * (double)points[0].y +
                    .114835 * (double)points[2].y - .43062 * (double)points[3].y +
                                                                                 1.6077 * (double)points[4].y - .26794 * points[5].y );

        control[4] = new Point( x1, y1 );
//Plot points
        for( int i = 0; i < ltnumpoints; i++ ) {
            offscreenG.fillOval( points[i].x - 2, points[i].y - 2, 4, 4 );
        }

//draw n bezier curves using Bernstein Polynomials
        x1 = points[0].x;
        y1 = points[0].y;
        for( int i = 1; i < ltnumpoints; i++ ) {
            for( t = i - 1; t <= i; t += k ) {
                x2 = (int)( (double)points[i - 1].x + ( t - ( i - 1 ) ) * ( -3 * (double)points[i - 1].x
                                                                            +
                                                                            3 * ( .6667 * (double)control[i - 1].x + .3333 * (double)control[i].x ) +
                                                                            ( t - ( i - 1 ) ) * ( 3 * (double)points[i - 1].x - 6 * ( .6667 * (double)control[i - 1
                                                                                    ].x +
                                                                                        .3333 * (double)control[i].x ) + 3 * ( .3333 * (double)control[i - 1].x +
                                                                                                                               .6667 * (double)control[i].x ) + ( -(double)points[i - 1].x +
                                                                                                                                                                  3 * ( .6667 * (double)control[i - 1].x + .3333 * (double)control[i].x ) -
                                                                                                                                                                                                                                          3 * ( .3333 * (double)control[i - 1].x + .6667 * (double)control[i].x ) +
                                                                                                                                                                                                                                                                                                                  (double)points[i].x ) * ( t - ( i - 1 ) ) ) ) );
                y2 = (int)( (double)points[i - 1].y + ( t - ( i - 1 ) ) * ( -3 * (double)points[i - 1].y
                                                                            +
                                                                            3 * ( .6667 * (double)control[i - 1].y + .3333 * (double)control[i].y ) +
                                                                            ( t - ( i - 1 ) ) * ( 3 * (double)points[i - 1].y - 6 * ( .6667 * (double)control[i - 1
                                                                                    ].y +
                                                                                        .3333 * (double)control[i].y ) + 3 * ( .3333 * (double)control[i - 1].y +
                                                                                                                               .6667 * (double)control[i].y ) + ( -(double)points[i - 1].y +
                                                                                                                                                                  3 * ( .6667 * (double)control[i - 1].y + .3333 * (double)control[i].y ) -

                                                                                                                                                                                                                                          3 * ( .3333 * (double)control[i - 1].y + .6667 * (double)control[i].y ) +
                                                                                                                                                                                                                                                                                                                  (double)points[i].y ) * ( t - ( i - 1 ) ) ) ) );
                offscreenG.drawLine( x1, y1, x2, y2 );
                x1 = x2;
                y1 = y2;
            }
        }
//draw buffered image to screen
        g.drawImage( offscreenImg, 0, 0, this );
    }

    //check if user has pushed polygon button
    public boolean action( Event e, Object o ) {
        if( e.target == polygon ) {
            if( poly ) {
                poly = false;
            }
            else {
                poly = true;
            }
            repaint();
            return true;
        }
        return false;
    }

    //Check if user has clicked on point
    public boolean mouseDown( Event evt, int x, int y ) {
        Point p = new Point( x, y );
        for( int i = 0; i < ltnumpoints; i++ ) {
            for( int j = -2; j < lt3; j++ ) {
                for( int l = -2; l < lt3; l++ ) {
                    if( p.equals( new Point( points[i].x + j,
                                             points[i].y + l ) ) )
//set moveflag to the ith point
                    {
                        moveflag = i;
                    }
                }
            }
        }
        return true;
    }

    public boolean mouseDrag( Event evt, int x, int y ) {
//check if user is trying to drag an old point
        if( moveflag < numpoints ) {
//move the point and redraw screen
            points[moveflag].move( x, y );
            repaint();
        }
        return true;
    }

    //if user unclicks mouse, reset moveflag
    public boolean mouseUp( Event evt, int x, int y ) {
        moveflag = 6;
        return true;
    }

}

