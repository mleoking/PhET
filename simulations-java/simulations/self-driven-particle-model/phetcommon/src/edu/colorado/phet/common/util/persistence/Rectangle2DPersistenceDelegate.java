/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/util/persistence/Rectangle2DPersistenceDelegate.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
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
 * @version $Revision: 1.1.1.1 $
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
