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

import java.awt.geom.AffineTransform;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;

/**
 * AffineTransformPersistenceDelegate
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class AffineTransformPersistenceDelegate extends PersistenceDelegate {

    protected Expression instantiate( Object oldInstance, Encoder out ) {
        AffineTransform tx = (AffineTransform)oldInstance;
        double[] coeffs = new double[6];
        tx.getMatrix( coeffs );
        return new Expression( oldInstance,
                               oldInstance.getClass(),
                               "new",
                               new Object[]{coeffs} );
    }
}
