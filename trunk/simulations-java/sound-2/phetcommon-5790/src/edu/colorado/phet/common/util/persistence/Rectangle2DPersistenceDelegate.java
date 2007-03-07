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

import java.awt.geom.Rectangle2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;

/**
 * Rectangle2DPersistenceDelegate
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Rectangle2DPersistenceDelegate extends DefaultPersistenceDelegate {

    protected void initialize( Class type, Object oldInstance, Object newInstance, Encoder out ) {
        Rectangle2D rect = (Rectangle2D)oldInstance;
        out.writeStatement( new Statement( oldInstance,
                                           "setFrame",
                                           new Object[]{new Double( rect.getX() ),
                                                        new Double( rect.getY() ),
                                                        new Double( rect.getWidth() ),
                                                        new Double( rect.getHeight() )} ) );
    }
}
