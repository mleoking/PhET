// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Model for a particle, including mappings between box2d physics engine and SI coordinates
 *
 * @author Sam Reid
 */
public class DefaultParticle extends Molecule {
    public final double radius;
    public final Property<ImmutableVector2D> position;
    public final Atom atom;

    public DefaultParticle( World world, final ModelViewTransform transform, final double x, final double y, double vx, double vy, final double theta, VoidFunction1<VoidFunction0> addUpdateListener, ObservableProperty<Double> charge, double radius ) {
        super( world, transform, x, y, vx, vy, theta, addUpdateListener );
        atom = new Atom( x, y, transform, radius, body, 0, 0, charge, true );
        initAtoms( atom );
        this.radius = radius;
        position = atom.position;
    }
}