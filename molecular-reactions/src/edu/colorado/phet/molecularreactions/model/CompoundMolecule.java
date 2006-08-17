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

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * CompoundMolecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompoundMolecule extends Molecule {
    private Molecule[] components;
    private Rectangle2D boundingBox = new Rectangle2D.Double();
    private Point2D cm = new Point2D.Double();

    public CompoundMolecule( Molecule[] components ) {
        super();
        this.components = components;
    }

    public CompoundMolecule( Molecule[] components, Point2D location, Vector2D velocity, Vector2D acceleration, double mass, double charge ) {
        super( location, velocity, acceleration, mass, charge );
        this.components = components;
    }

    public Molecule[] getComponentMolecules() {
        return components;
    }

    public Rectangle2D getBoundingBox() {
        boundingBox.setRect( components[0].getBoundingBox() );
        for( int i = 1; i < components.length; i++ ) {
            boundingBox.createUnion( components[i].getBoundingBox() );
        }
        return boundingBox;
    }

    public Point2D getCM() {
        double xSum = 0;
        double ySum = 0;
        double massSum = 0;
        for( int i = 0; i < components.length; i++ ) {
            Molecule component = components[i];
            double mass = component.getMass();
            xSum += mass * component.getCM().getX();
            ySum += mass * component.getCM().getY();
            massSum += mass;
        }
        cm.setLocation( xSum / massSum, ySum / massSum );
        return cm;
    }

    public double getMomentOfInertia() {
        double moi = 0;
        Point2D cm = this.getCM();
        for( int i = 0; i < components.length; i++ ) {
            Molecule component = components[i];
            double dist = cm.distance( component.getCM() );
            double mOfIComponent = component.getMomentOfInertia() + component.getMass()
                                                                    * dist * dist;
            moi += mOfIComponent;
        }
        return moi;
    }
}
