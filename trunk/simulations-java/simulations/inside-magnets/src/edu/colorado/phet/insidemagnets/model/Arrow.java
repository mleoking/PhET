//package edu.colorado.phet.insidemagnets.model;
//
//import java.awt.geom.Point2D;
//
//import edu.colorado.phet.insidemagnets.model.created.GC;
//import edu.colorado.phet.insidemagnets.model.created.Window;
//
//public class Arrow {
//    Object display = null;
//
///*----------------------------------------------------------------------
//   from testarro.c--Draws spin arrow plots a la wavefunc.pro idl program.
//   Takes the raw values of Xi and plots them as arrows.                */
//
//    void draw_arrow( Window window, GC gc, double x1, double y1,/* tail point of arrow, pixel coords.  */
//                     double x2, double y2,/* head point of arrow, pixel coords.  */
//                     double sz,/* choose color of arrow (turned off). */
//                     int form )/* 0==open head, 1==closed head.       */
///*    subroutine for drawing arrows    */ {
//        int icolor;
//        double head = 0.40;
//        double headb = 0.60;
//        double xlong, ylong;
//        double xa, ya, vxa, vya;
//        double xb, yb, vxb, vyb;
//        if ( sz > 1.0 ) { sz -= 2.0; }
//        if ( sz < -1.0 ) { sz += 2.0; }
//        icolor = (int) ( 15.0 * ( sz + 1.0 ) / 2.0 );   /*  colors from pallette here  */
///*  setcolor(icolor);  */
//
//        if ( form == 1 )   /* line arrow.  */ {
//            XDrawLine( display, window, gc, (int) ( x1 ), (int) ( y1 ), (int) ( x2 ), (int) ( y2 ) );
//        }
//        else    /*   shorter line, open head for form==0.   */ {
//            xa = head * x1 + headb * x2;
//            ya = head * y1 + headb * y2;
//            XDrawLine( display, window, gc, (int) ( x1 ), (int) ( y1 ), (int) ( xa ), (int) ( ya ) );
//        }
//        xlong = x2 - x1;    /* rotate arrow to correct orientation  */
//        ylong = y2 - y1;
//        xa = -head;
//        ya = 0.5 * head;
//        vxa = xa * xlong - ya * ylong;
//        vya = xa * ylong + ya * xlong;
//        XDrawLine( display, window, gc, (int) ( x2 ), (int) ( y2 ), (int) ( x2 + vxa ), (int) ( y2 + vya ) );
///*---------------------------*/
//        xb = -head;
//        yb = -0.5 * head;
//        vxb = xb * xlong - yb * ylong;
//        vyb = xb * ylong + yb * xlong;
//        XDrawLine( display, window, gc, (int) ( x2 ), (int) ( y2 ), (int) ( x2 + vxb ), (int) ( y2 + vyb ) );
///*---------------------------*/
//        if ( form == 0 ) {
///*   shorter line, open head for form = 0.   */
//            xa = head * x1 + headb * x2;
//            ya = head * y1 + headb * y2;
//            XDrawLine( display, window, gc, (int) x1, (int) y1, (int) ( xa ), (int) ( ya ) );
//            XDrawLine( display, window, gc, (int) ( x2 + vxa ), (int) ( y2 + vya ), (int) ( x2 + vxb ), (int) ( y2 + vyb ) );
//        }
//        return;
//    }
//
//    private void XDrawLine( Object display, Window window, GC gc, int i, int i1, int i2, int i3 ) {
//
//    }
//
//    /**
//     * **************************************************
//     */
//    Point2D map_coords( double x, double y/* physical coordinates. */
//    )/*    pixel coordinates. */
///*  Takes coordinates (x,y) which should be from xmin to xmax, and maps
//    them into pixel coordinates (X,Y) ranging appropriately inside the window.  */ {
//        return new Point2D.Double( X0 + ( x - xmin ) * XScale, Y0 - ( y - ymin ) * YScale );
//    }
//
//
//    /**
//     * **************************************************
//     */
//    void spinarrow( Window window, GC gc, double x, double y,/* lattice coordinates */
//                    double sx, double sy, double sz )/* spin components     */
///*  Calculates pixels, draws a spin arrow in desired coord system. */ {
//        int View = 1;    /* spin component projected towards the viewer. *///TODO: extern view
//        double s1, s2, s3;     /* rotated spin components.		      */
//        double x1, y1, x2, y2;  /* physical coordinates for the lattice spins.  */
//        double dx, dy;
//        int form;
//
//        if ( View == 1 ) {
//            s1 = (double) sy;
//            s2 = (double) sz;
//            s3 = (double) sx;
//        }  /* look towards sx  */
//        else {
//            s1 = (double) sx;
//            s2 = (double) sy;
//            s3 = (double) sz;
//        }  /* look towards sz  */
//
//        dx = 0.5 * s1;
//        dy = 0.5 * s2;
//        x1 = x - dx;
//        y1 = y - dy;
//        x2 = x + dx;
//        y2 = y + dy;
//        /* line arrow heads for s3>=0, open for s3<0. (original form).  */
//        /* form=(int)(s3+1.0);  */
//        /* line arrow heads for s3<0, open for s3>=0. (new version). */
//        form = 1 - (int) ( s3 + 1.0 );
//        /* pixel coords for arrows and other objects.   */
//        Point2D m1 = map_coords( x1, y1 );
//        Point2D m2 = map_coords( x2, y2 );
//        draw_arrow( window, gc, m1.getX(), m1.getY(), m2.getX(), m2.getY(), s3, form );
//    }
//
//}