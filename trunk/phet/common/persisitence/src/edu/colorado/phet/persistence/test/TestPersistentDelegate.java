/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.persistence.test;

import edu.colorado.phet.common.util.persistence.*;

import java.awt.geom.*;
import java.awt.*;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

/**
 * TestPersistentDelegate
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestPersistentDelegate {

    public static void main( String[] args ) {
//        testAffineTransform();
//        testRectangle2D();
//        testGeneralPath();
        testGradientPaint();
    }

    static void testAffineTransform() {

        XMLEncoder e = new XMLEncoder( System.out );
//        XMLEncoder e2 = TestSaveState.getTestEncoder();

        // AffineTransform
        AffineTransform atx = AffineTransform.getRotateInstance( Math.PI / 4 );
        e.setPersistenceDelegate( AffineTransform.class, new AffineTransformPersistenceDelegate() );
        e.writeObject( atx );
        e.close();
//        e2.setPersistenceDelegate( AffineTransform.class, new AffineTransformPersistenceDelegate() );
//        e2.writeObject( atx );
//        e2.close();
//
//        XMLDecoder d = TestSaveState.getTestDecoder();
//        AffineTransform atx2 = (AffineTransform)d.readObject();
//        System.out.println( "atx = " + atx );
//        System.out.println( "atx2 = " + atx2 );
    }

    static void testPoint2D() {

        Point2D p = new Point2D.Double( 10, 20 );
//        XMLEncoder e = new XMLEncoder( System.out );
        XMLEncoder e2 = TestSaveState.getTestEncoder();
        XMLEncoder e = new XMLEncoder( System.out );
        e.setPersistenceDelegate( Point2D.class, new Point2DPersistenceDelegate() );
        e.writeObject( p );
        e.close();
//
        e2.setPersistenceDelegate( Point2D.class, new Point2DPersistenceDelegate() );
        e2.writeObject( p );
        e2.close();

        XMLDecoder d = TestSaveState.getTestDecoder();
        Point2D p2 = (Point2D)d.readObject();
        d.close();

        System.out.println( "p = " + p );
        System.out.println( "p2 = " + p2 );
    }

    static void testEllipse2D() {

        Ellipse2D ellipse = new Ellipse2D.Double( 10,20,30,40 );

//        XMLEncoder e = new XMLEncoder( System.out );
//        e.setPersistenceDelegate( Ellipse2D.class, new Ellipse2DPersistenceDelegate() );
//        e.writeObject( ellipse );
//        e.close();

        XMLEncoder e2 = TestSaveState.getTestEncoder();
        e2.setPersistenceDelegate( Ellipse2D.class, new Ellipse2DPersistenceDelegate() );
        e2.writeObject( ellipse );
        e2.close();

        XMLDecoder d = TestSaveState.getTestDecoder();
        Ellipse2D ellipse2 = (Ellipse2D)d.readObject();
        d.close();

        System.out.println( "ellipse = " + ellipse );
        System.out.println( "ellipse2 = " + ellipse2 );
    }

    static void testRectangle2D() {

//        Rectangle2D rect = new Rectangle2D.Float( 10,20,30,40 );
        Rectangle2D rect = new Rectangle2D.Double( 10,20,30,40 );

//        XMLEncoder e = new XMLEncoder( System.out );
//        e.setPersistenceDelegate( Rectangle2D.Double.class, new Rectangle2DPersistenceDelegate() );
//        e.setPersistenceDelegate( Rectangle2D.Float.class, new Rectangle2DPersistenceDelegate() );
//        e.writeObject( rect );
//        e.close();

        XMLEncoder e2 = TestSaveState.getTestEncoder();
        e2.setPersistenceDelegate( Rectangle2D.Double.class, new Rectangle2DPersistenceDelegate() );
        e2.writeObject( rect );
        e2.close();

        XMLDecoder d = TestSaveState.getTestDecoder();
        Rectangle2D rect2 = (Rectangle2D)d.readObject();
        d.close();

        System.out.println( "rect = " + rect );
        System.out.println( "rect2 = " + rect2 );
    }

    static void testGeneralPath() {

        GeneralPath path = new GeneralPath( );
        path.moveTo( 10,20 );
        path.lineTo( 30, 40 );
        path.quadTo( 50,60,70, 80);
        path.curveTo( 1,2,3,4,5,6);
        path.closePath();

//        XMLEncoder e = new XMLEncoder( System.out );
//        e.setPersistenceDelegate( GeneralPath.class, new GeneralPathPersistenceDelegate() );
//        e.writeObject( path );
//        e.close();

        XMLEncoder e2 = TestSaveState.getTestEncoder();
        e2.setPersistenceDelegate( GeneralPath.class, new GeneralPathPersistenceDelegate() );
        e2.writeObject( path );
        e2.close();

        XMLDecoder d = TestSaveState.getTestDecoder();
        GeneralPath rect2 = (GeneralPath)d.readObject();
        d.close();

        System.out.println( "path = " + path );
        System.out.println( "rect2 = " + rect2 );
    }

    static void testGradientPaint() {

        GradientPaint pObj = new GradientPaint( 1,2,Color.red,3,4,Color.green, true );

        XMLEncoder e = new XMLEncoder( System.out );
        e.setPersistenceDelegate( Point2D.class, new Point2DPersistenceDelegate() );
        e.setPersistenceDelegate( GradientPaint.class, new GradientPaintPersistenceDelegate() );
        e.writeObject( pObj );
        e.close();

//        XMLEncoder e2 = TestSaveState.getTestEncoder();
//        e2.setPersistenceDelegate( GeneralPath.class, new GeneralPathPersistenceDelegate() );
//        e2.writeObject( path );
//        e2.close();
//
//        XMLDecoder d = TestSaveState.getTestDecoder();
//        GeneralPath rect2 = (GeneralPath)d.readObject();
//        d.close();

        System.out.println( "pObj = " + pObj );
//        System.out.println( "rect2 = " + rect2 );
    }
}
