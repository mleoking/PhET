// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water;

import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * @author Sam Reid
 */
public class Sucrose implements Removable {
    public final Property<ImmutableVector2D> position;

    public Sucrose( World world, final ModelViewTransform transform, final double x, final double y, double vx, double vy, final double theta, VoidFunction1<VoidFunction0> addUpdateListener, double charge, double radius ) {
        position = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
    }

    public void addRemovalListener( VoidFunction0 voidFunction0 ) {
    }
}
