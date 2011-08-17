// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import java.awt.geom.Point2D;

import static edu.colorado.phet.sugarandsaltsolutions.water.view.Element.CHLORINE_RADIUS;
import static edu.colorado.phet.sugarandsaltsolutions.water.view.Element.SODIUM_RADIUS;

/**
 * Code that creates a single salt crystal, used when dragged from the bucket or created in the beaker
 *
 * @author Sam Reid
 */
public class SaltCrystal {
    private final double horizontalSeparation = CHLORINE_RADIUS + SODIUM_RADIUS;
    private final double verticalSeparation = horizontalSeparation * 0.85;

    public final DefaultParticle sodium;
    public final DefaultParticle chloride;

    public final DefaultParticle sodium2;
    public final DefaultParticle chloride2;

    public SaltCrystal( WaterModel waterModel, Point2D location ) {
        sodium = new DefaultParticle( waterModel.world, waterModel.modelToBox2D, location.getX(), location.getY(), 0, 0, 0, waterModel.addFrameListener, waterModel.ionCharge, SODIUM_RADIUS );
        chloride = new DefaultParticle( waterModel.world, waterModel.modelToBox2D, location.getX() + horizontalSeparation, location.getY(), 0, 0, 0, waterModel.addFrameListener, waterModel.ionCharge.times( -1 ), CHLORINE_RADIUS );

        sodium2 = new DefaultParticle( waterModel.world, waterModel.modelToBox2D, location.getX() + horizontalSeparation, location.getY() + verticalSeparation, 0, 0, 0, waterModel.addFrameListener, waterModel.ionCharge, SODIUM_RADIUS );
        chloride2 = new DefaultParticle( waterModel.world, waterModel.modelToBox2D, location.getX(), location.getY() + verticalSeparation, 0, 0, 0, waterModel.addFrameListener, waterModel.ionCharge.times( -1 ), CHLORINE_RADIUS );
    }
}
