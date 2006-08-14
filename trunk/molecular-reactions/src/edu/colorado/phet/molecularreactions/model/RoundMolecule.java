/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollidableAdapter;
import edu.colorado.phet.collision.SolidSphere;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * RoundMolecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RoundMolecule extends SolidSphere {

    private CollidableAdapter collidableAdapter = new CollidableAdapter( this );
    private double mass;

    public RoundMolecule( double radius, double mass ) {
        super( radius );
        super.setMass( mass );
    }
}
