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

import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.math.Vector2D;

import java.util.Random;

/**
 * Shaker
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Shaker extends Particle {
    private Random random = new Random( System.currentTimeMillis() );
    private SolubleSaltsModel model;

    public Shaker( SolubleSaltsModel model ) {
        this.model = model;
    }

    public void shake( double dy ) {
        setPosition( getPosition().getX(), getPosition().getY() + dy );

        Ion ion = null;
        double theta = Math.PI/4 + random.nextDouble() * Math.PI / 3;
        Vector2D v = new Vector2D.Double( 5, 0 );
        v.rotate( theta );
        if( random.nextBoolean() ) {
            ion = new Chloride( getPosition(), v, new Vector2D.Double() );
//            ion = new Chloride( getPosition(), new Vector2D.Double( 0, 5 ), new Vector2D.Double() );
        }
        else {
            ion = new Sodium( getPosition(), v, new Vector2D.Double() );
//            ion = new Sodium( getPosition(), new Vector2D.Double( 0, 5 ), new Vector2D.Double() );
        }
        model.addModelElement( ion );
    }
}
