package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import java.awt.*;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

/**
 * A single molecule of sugar.
 *
 * @author Sam Reid
 */
public class SugarMolecule extends Particle {
    private final double radius;

    public SugarMolecule( ImmutableVector2D position, double radius ) {
        super( position );
        this.radius = radius;
    }

    //TODO: fix the sugar shape
    @Override public Shape getShape() {
        return new Ellipse2D.Double( position.get().getX() - radius, position.get().getY() - radius, radius * 2, radius * 2 );
    }
}
