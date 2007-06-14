/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/util/persistence/BasicStrokePersistenceDelegate.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.5 $
 * Date modified : $Date: 2006/01/03 23:37:18 $
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
 * @version $Revision: 1.5 $
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
