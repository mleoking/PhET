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

import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.beans.*;
import java.util.Iterator;

/**
 * Point2DPersistenceDelegate
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Ellipse2DPersistenceDelegate extends DefaultPersistenceDelegate {

    protected void initialize( Class type, Object oldInstance, Object newInstance, Encoder out ) {
        Ellipse2D ellipse = (Ellipse2D)oldInstance;
        out.writeStatement( new Statement( oldInstance,
                                           "setFrame",
                                           new Object[]{new Double( ellipse.getX() ),
                                                        new Double( ellipse.getY() ),
                                                        new Double( ellipse.getWidth() ),
                                                        new Double( ellipse.getHeight() )} ) );
    }
}
