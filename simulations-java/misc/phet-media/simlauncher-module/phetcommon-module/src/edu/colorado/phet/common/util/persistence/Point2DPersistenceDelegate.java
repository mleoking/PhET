/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/util/persistence/Point2DPersistenceDelegate.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: samreid $
 * Revision : $Revision: 1.4 $
 * Date modified : $Date: 2005/11/11 23:09:49 $
 */
package edu.colorado.phet.common.util.persistence;

import java.awt.geom.Point2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;

/**
 * Point2DPersistenceDelegate
 *
 * @author Ron LeMaster
 * @version $Revision: 1.4 $
 */
public class Point2DPersistenceDelegate extends DefaultPersistenceDelegate {

    protected void initialize( Class type, Object oldInstance, Object newInstance, Encoder out ) {
        Point2D point = (Point2D)oldInstance;
        out.writeStatement( new Statement( oldInstance,
                                           "setLocation",
                                           new Object[]{new Double( point.getX() ), new Double( point.getY() )} ) );
    }
}
