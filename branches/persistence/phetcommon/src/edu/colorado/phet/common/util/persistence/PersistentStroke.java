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

/**
 * PersistentStrok<p>
 * Todo: may need to implement hashCode() and equals() differently
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PersistentStroke extends BasicStroke {
    private PersistentStroke.StateDescriptor stateDescriptor;
    private BasicStroke stroke;


    public PersistentStroke() {
    }

    public PersistentStroke( BasicStroke stroke ) {
        this.stroke = stroke;
        stateDescriptor = new StateDescriptor( stroke );
    }

    public StateDescriptor getStateDescriptor() {
        return stateDescriptor;
    }

    public void setStateDescriptor( StateDescriptor stateDescriptor ) {
        this.stateDescriptor = stateDescriptor;
        stroke = stateDescriptor.generate();
    }


    ////////////////////////////
    // Wrapper methods
    //
    public float getDashPhase() {
        return this.stroke.getDashPhase();
    }

    public float getLineWidth() {
        return this.stroke.getLineWidth();
    }

    public float getMiterLimit() {
        return this.stroke.getMiterLimit();
    }

    public int getEndCap() {
        return this.stroke.getEndCap();
    }

    public int getLineJoin() {
        return this.stroke.getLineJoin();
    }

    public float[] getDashArray() {
        return this.stroke.getDashArray();
    }

    public Shape createStrokedShape( Shape s ) {
        return this.stroke.createStrokedShape( s );
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals( Object obj ) {
        return super.equals( obj );
    }

    //////////////////////////////////////////
    // Inner classes
    //
    public static class StateDescriptor {
        private float dashPhase;
        private float lineWidth;
        private float miterLimit;
        private int endCap;
        private int lineJoin;
        private float[] dashArray;

        public StateDescriptor() {
        }

        StateDescriptor( BasicStroke stroke ) {
            dashPhase = stroke.getDashPhase();
            lineWidth = stroke.getLineWidth();
            miterLimit = stroke.getMiterLimit();
            endCap = stroke.getEndCap();
            lineJoin = stroke.getLineJoin();
            dashArray = stroke.getDashArray();
        }


        ////////////////////////////////////
        // Setters and getters
        //
        public float getDashPhase() {
            return dashPhase;
        }

        public void setDashPhase( float dashPhase ) {
            this.dashPhase = dashPhase;
        }

        public float getLineWidth() {
            return lineWidth;
        }

        public void setLineWidth( float lineWidth ) {
            this.lineWidth = lineWidth;
        }

        public float getMiterLimit() {
            return miterLimit;
        }

        public void setMiterLimit( float miterLimit ) {
            this.miterLimit = miterLimit;
        }

        public int getEndCap() {
            return endCap;
        }

        public void setEndCap( int endCap ) {
            this.endCap = endCap;
        }

        public int getLineJoin() {
            return lineJoin;
        }

        public void setLineJoin( int lineJoin ) {
            this.lineJoin = lineJoin;
        }

        public float[] getDashArray() {
            return dashArray;
        }

        public void setDashArray( float[] dashArray ) {
            this.dashArray = dashArray;
        }

        ///////////////////////////////////
        // Generator
        //
        BasicStroke generate() {
            BasicStroke stroke = new BasicStroke( lineWidth, endCap, lineJoin, miterLimit, dashArray, dashPhase );
            return stroke;
        }
    }
}

