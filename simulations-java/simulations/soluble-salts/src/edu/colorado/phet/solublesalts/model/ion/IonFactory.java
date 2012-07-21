// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.solublesalts.model.ion;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;

/**
 * IonFactory
 * <p/>
 * Creates instances of concrete Ion classes, given the class of Ion for
 * which an instance is desired
 *
 * @author Ron LeMaster
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
        catch ( InstantiationException e ) {
            e.printStackTrace();
        }
        catch ( IllegalAccessException e ) {
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
    public Ion create( Class ionClass, Point2D position, MutableVector2D velocity, MutableVector2D acceleration ) {
        Ion ion = create( ionClass );
        ion.setPosition( position );
        ion.setVelocity( velocity );
        ion.setAcceleration( acceleration );
        return ion;
    }
}
