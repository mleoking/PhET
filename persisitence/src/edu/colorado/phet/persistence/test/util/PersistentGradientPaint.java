/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.persistence.test.util;

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
public class PersistentGradientPaint extends GradientPaint {
    private GradientPaint gradientPaint;
    private StateDescriptor stateDescriptor;

    public PersistentGradientPaint() {
        super( 0, 0, Color.black, 0, 0, Color.black );
    }

    public PersistentGradientPaint( GradientPaint gradientPaint ) {
        super( 0, 0, Color.black, 0, 0, Color.black );
        this.gradientPaint = gradientPaint;
        stateDescriptor = new StateDescriptor( gradientPaint );
    }

    //////////////////////////////////////
    // Perisistence setters and getters
    public StateDescriptor getStateDescriptor() {
        return stateDescriptor;
    }

    public void setStateDescriptor( StateDescriptor stateDescriptor ) {
        this.stateDescriptor = stateDescriptor;
        gradientPaint = stateDescriptor.generate();
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

    ////////////////////////////////////////
    // Inner classes
    //
    public static class StateDescriptor {
        private Color color1;
        private Color color2;
        private PersistentPoint2D point1;
        private PersistentPoint2D point2;
        private boolean isCyclic;

        public StateDescriptor() {
        }

        StateDescriptor( GradientPaint gradientPaint ) {
            color1 = gradientPaint.getColor1();
            color2 = gradientPaint.getColor2();
            point1 = new PersistentPoint2D( gradientPaint.getPoint1() );
            point2 = new PersistentPoint2D( gradientPaint.getPoint2() );
            isCyclic = gradientPaint.isCyclic();
        }

        //////////////////////////////////
        // Generator
        //
        GradientPaint generate() {
            GradientPaint gradientPaint = new GradientPaint( point1, color1, point2, color2, isCyclic );
            return gradientPaint;
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
