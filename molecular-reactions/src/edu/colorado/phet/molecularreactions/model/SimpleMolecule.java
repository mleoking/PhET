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
import java.awt.geom.Rectangle2D;

/**
 * SimpleMolecule
 * <p>
 * A molecule that is a single sphere.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleMolecule extends Molecule {

    private double radius;
    private Rectangle2D boundingBox = new Rectangle2D.Double();

    /**
     * A molecule that has no component molecules, and whose mass is the square of
     * its radius
     *
     * @param radius
     */
    public SimpleMolecule( double radius ) {
        super();
        this.radius = radius;
        setMass( radius * radius );
    }

    public SimpleMolecule( double radius, Point2D location, Vector2D velocity, Vector2D acceleration, double mass, double charge ) {
        super( location, velocity, acceleration, mass, charge );
        this.radius = radius;
    }

    public Molecule[] getComponentMolecules() {
        return new Molecule[]{ this };
    }

    public Rectangle2D getBoundingBox() {
        boundingBox.setRect( getPosition().getX() - radius,
                                    getPosition().getY() - radius,
                                    radius * 2,
                                    radius * 2);
        return boundingBox;
    }

    public double getRadius() {
        return radius;
    }

    public Point2D getCM() {
        return getPosition();
    }

    public double getMomentOfInertia() {
        return radius * radius * getMass() / 2;
    }
}
