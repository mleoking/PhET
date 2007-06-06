/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/util/persistence/BasicStrokePersistenceDelegate.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.util.persistence;

import java.awt.*;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;

/**
 * GradientPaintPersistenceDelegate
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1.1.1 $
 */
public class BasicStrokePersistenceDelegate extends PersistenceDelegate {

    protected Expression instantiate( Object oldInstance, Encoder out ) {
        BasicStroke stroke = (BasicStroke)oldInstance;
        StrokeDescriptor desc = new StrokeDescriptor( stroke );
        return desc.generate( stroke );
    }


    //////////////////////////////////////////
    // Inner classes
    //
    class StrokeDescriptor {
        private float dashPhase;
        private float lineWidth;
        private float miterLimit;
        private int endCap;
        private int lineJoin;
        private float[] dashArray;

        StrokeDescriptor( BasicStroke stroke ) {
            dashPhase = stroke.getDashPhase();
            lineWidth = stroke.getLineWidth();
            miterLimit = stroke.getMiterLimit();
            endCap = stroke.getEndCap();
            lineJoin = stroke.getLineJoin();
            dashArray = stroke.getDashArray();
        }

        ///////////////////////////////////
        // Generator
        //
        public Expression generate( Object oldInstance ) {
            return new Expression( oldInstance,
                                   oldInstance.getClass(),
                                   "new",
                                   new Object[]{new Float( lineWidth ), new Integer( endCap ), new Integer( lineJoin ),
                                                new Float( miterLimit ), dashArray, new Float( dashPhase )} );
        }

    }
}
