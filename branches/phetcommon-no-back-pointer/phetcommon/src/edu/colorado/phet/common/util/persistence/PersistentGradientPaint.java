/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util.persistence;

import java.awt.*;
import java.awt.image.ColorModel;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;

/**
 * PersistentGradientPaint
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PersistentGradientPaint extends GradientPaint implements Persistent {
    private GradientPaint gradientPaint;

    public PersistentGradientPaint() {
        super( 0, 0, Color.black, 0, 0, Color.black );
        gradientPaint = new GradientPaint( 0, 0, Color.black, 0, 0, Color.black );
    }

    public PersistentGradientPaint( GradientPaint gradientPaint ) {
        super( 0, 0, Color.black, 0, 0, Color.black );
        this.gradientPaint = gradientPaint;
    }

    public PersistentGradientPaint( float x1, float y1, Color color1, float x2, float y2, Color color2 ) {
        super( 0, 0, Color.black, 0, 0, Color.black );
        gradientPaint = new GradientPaint( x1, y1, color1, x2, y2, color2 );
    }

    public PersistentGradientPaint( float x1, float y1, Color color1, float x2, float y2, Color color2, boolean cyclic ) {
        super( 0, 0, Color.black, 0, 0, Color.black );
        gradientPaint = new GradientPaint( x1, y1, color1, x2, y2, color2, cyclic );
    }

    public PersistentGradientPaint( Point2D pt1, Color color1, Point2D pt2, Color color2 ) {
        super( 0, 0, Color.black, 0, 0, Color.black );
        gradientPaint = new GradientPaint( pt1, color1, pt2, color2 );
    }

    public PersistentGradientPaint( Point2D pt1, Color color1, Point2D pt2, Color color2, boolean cyclic ) {
        super( 0, 0, Color.black, 0, 0, Color.black );
        gradientPaint = new GradientPaint( pt1, color1, pt2, color2, cyclic );
    }

    private void setPaint( GradientPaint paint ) {
        gradientPaint = paint;
    }

    //////////////////////////////////////
    // Perisistence setters and getters
    public StateDescriptor getState() {
        return new GradientPaintDescriptor( this );
    }

    public void setState( StateDescriptor stateDescriptor ) {
        stateDescriptor.setState( this );
    }

    //////////////////////////////////////
    // Wrapper methods
    //
    public int getTransparency() {
        return gradientPaint.getTransparency();
    }

    public boolean isCyclic() {
        return gradientPaint.isCyclic();
    }

    public Color getColor1() {
        return gradientPaint.getColor1();
    }

    public Color getColor2() {
        return gradientPaint.getColor2();
    }

    public Point2D getPoint1() {
        return gradientPaint.getPoint1();
    }

    public Point2D getPoint2() {
        return gradientPaint.getPoint2();
    }

    public PaintContext createContext( ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds,
                                       AffineTransform xform, RenderingHints hints ) {
        return gradientPaint.createContext( cm, deviceBounds, userBounds, xform, hints );
    }

    //////////////////////////////////////
    // Inner classes
    //
    public static class GradientPaintDescriptor implements StateDescriptor {
        private Color color1;
        private Color color2;
        private PersistentPoint2D point1;
        private PersistentPoint2D point2;
        private boolean isCyclic;

        public GradientPaintDescriptor() {
        }

        GradientPaintDescriptor( GradientPaint gradientPaint ) {
            color1 = gradientPaint.getColor1();
            color2 = gradientPaint.getColor2();
            point1 = new PersistentPoint2D( gradientPaint.getPoint1() );
            point2 = new PersistentPoint2D( gradientPaint.getPoint2() );
            isCyclic = gradientPaint.isCyclic();
        }

        //////////////////////////////////
        // Generator
        //
        public void setState( Persistent persistentObject ) {
            PersistentGradientPaint persistentPaint = (PersistentGradientPaint)persistentObject;
            GradientPaint gradientPaint = new GradientPaint( point1, color1, point2, color2, isCyclic );
            persistentPaint.setPaint( gradientPaint );
        }

        //////////////////////////////////
        // Persistence setters and getters
        //
        public Color getColor1() {
            return color1;
        }

        public void setColor1( Color color1 ) {
            this.color1 = color1;
        }

        public Color getColor2() {
            return color2;
        }

        public void setColor2( Color color2 ) {
            this.color2 = color2;
        }

        public PersistentPoint2D getPoint1() {
            return point1;
        }

        public void setPoint1( PersistentPoint2D point1 ) {
            this.point1 = point1;
        }

        public PersistentPoint2D getPoint2() {
            return point2;
        }

        public void setPoint2( PersistentPoint2D point2 ) {
            this.point2 = point2;
        }

        public boolean isCyclic() {
            return isCyclic;
        }

        public void setCyclic( boolean cyclic ) {
            isCyclic = cyclic;
        }
    }
}
