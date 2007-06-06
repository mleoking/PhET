/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/util/persistence/Ellipse2DPersistenceDelegate.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.util.persistence;

import java.awt.geom.Ellipse2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;

/**
 * Point2DPersistenceDelegate
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1.1.1 $
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
