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

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

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
     * @return
     */
    public Ion create( Class ionClass ) {
        Ion ion = null;
        if( ionClass == ConfigurableAnion.class ) {
            ion = new ConfigurableAnion();
        }
        if( ionClass == ConfigurableCation.class ) {
            ion = new ConfigurableCation();
        }
        if( ionClass == Sodium.class ) {
            ion = new Sodium();
        }
        if( ionClass == Chlorine.class ) {
            ion = new Chlorine();
        }
        if( ionClass == Lead.class ) {
            ion = new Lead();
        }
        if( ionClass == Silver.class ) {
            ion = new Silver();
        }
        if( ionClass == Iodine.class ) {
            ion = new Iodine();
        }
        if( ionClass == Copper.class ) {
            ion = new Copper();
        }
        if( ionClass == Hydroxide.class ) {
            ion = new Hydroxide();
        }
        if( ionClass == Chromium.class ) {
            ion = new Chromium();
        }
        if( ionClass == Strontium.class ) {
            ion = new Strontium();
        }
        if( ionClass == Phosphate.class ) {
            ion = new Phosphate();
        }
        if( ionClass == Bromine.class ) {
            ion = new Bromine();
        }
        if( ionClass == Mercury.class ) {
            ion = new Mercury();
        }

        if( ion == null ) {
            throw new RuntimeException( "Ion class not recognized " );
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
     * @return
     */
    public Ion create( Class ionClass, Point2D position, Vector2D velocity, Vector2D acceleration ) {
        Ion ion = create( ionClass );
        ion.setPosition( position );
        ion.setVelocity( velocity );
        ion.setAcceleration( acceleration );
        return ion;
    }
}
