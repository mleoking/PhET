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
import java.awt.geom.Point2D;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;

/**
 * GradientPaintPersistenceDelegate
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GradientPaintPersistenceDelegate extends PersistenceDelegate {

    protected Expression instantiate( Object oldInstance, Encoder out ) {
        GradientPaint gradientPaint = (GradientPaint)oldInstance;
        Color color1 = gradientPaint.getColor1();
        Color color2 = gradientPaint.getColor2();
        Point2D point1 = gradientPaint.getPoint1();
        Point2D point2 = gradientPaint.getPoint2();
        Boolean isCyclic = new Boolean( gradientPaint.isCyclic() );

        return new Expression( oldInstance,
                               oldInstance.getClass(),
                               "new",
                               new Object[]{point1, color1, point2, color2, isCyclic} );
    }
}
