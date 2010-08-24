/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.ion;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2DInterface;

/**
 * IonFactory
 * <p/>
 * Creates instances of concrete Ion classes, given the class of Ion for
 * which an instance is desired
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonFactory {

    /**
     * Returns an instance of a specified Ion class. All attributes are
     * set to defaults.
     *
     * @param ionClass
     * @return an Ion
     */
    public Ion create( Class ionClass ) {
        Ion ion = null;
        try {
            ion = (Ion) ionClass.newInstance();
        }
        catch( InstantiationException e ) {
            e.printStackTrace();
        }
        catch( IllegalAccessException e ) {
            e.printStackTrace();
        }
        return ion;
    }

    /**
     * Returns an instance of a specified Ion class, with attributes set to
     * specified parameters.
     *
     * @param ionClass
     * @param position
     * @param velocity
     * @param acceleration
     * @return an Ion
     */
    public Ion create( Class ionClass, Point2D position, Vector2DInterface velocity, Vector2DInterface acceleration ) {
        Ion ion = create( ionClass );
        ion.setPosition( position );
        ion.setVelocity( velocity );
        ion.setAcceleration( acceleration );
        return ion;
    }
}
