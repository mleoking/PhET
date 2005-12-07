/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * IonFactory 
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonFactory {

    public Ion create( Class ionClass ) {
        Ion ion = null;
        if( ionClass == Sodium.class ) {
            ion = new Sodium();
        }
        if( ionClass == Chloride.class ) {
            ion = new Chloride();
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

        if( ion == null ) {
            throw new RuntimeException( "Ion class not recognized ");
        }
        return ion;
    }

    public Ion create( Class ionClass, Point2D position, Vector2D velocity, Vector2D acceleration ) {
        Ion ion = create( ionClass );
        ion.setPosition( position );
        ion.setVelocity( velocity );
        ion.setAcceleration( acceleration );
        return ion;
    }
}
