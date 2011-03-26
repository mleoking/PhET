//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.away3d {
import Box2D.Dynamics.b2Body;

import edu.colorado.phet.densityandbuoyancy.model.Cuboid;
import edu.colorado.phet.densityandbuoyancy.model.DensityAndBuoyancyModel;
import edu.colorado.phet.densityandbuoyancy.view.*;

/**
 * The CuboidNode renders graphics and provides interactivity for Cuboids.
 */
public class CuboidNode extends DensityObjectNode implements Pickable {

    private var cuboid: Cuboid;

    public function CuboidNode( cuboid: Cuboid, canvas: AbstractDBCanvas ): void {
        super( cuboid, canvas );
        this.cuboid = cuboid;
        this.x = cuboid.getX() * DensityAndBuoyancyModel.DISPLAY_SCALE;
        this.y = cuboid.getY() * DensityAndBuoyancyModel.DISPLAY_SCALE;
        this.z = cuboid.getZ() * DensityAndBuoyancyModel.DISPLAY_SCALE;
        cuboid.addShapeChangeListener( updateGeometry );
    }

    public override function setPosition( x: Number, y: Number ): void {
        cuboid.setPosition( x / DensityAndBuoyancyModel.DISPLAY_SCALE, y / DensityAndBuoyancyModel.DISPLAY_SCALE );
    }

    public function getCuboid(): Cuboid {
        return cuboid;
    }

    public override function getBody(): b2Body {
        return cuboid.getBody();
    }

    public override function updateGeometry(): void {
        this.x = cuboid.getX() * DensityAndBuoyancyModel.DISPLAY_SCALE;
        this.y = cuboid.getY() * DensityAndBuoyancyModel.DISPLAY_SCALE;
        this.z = cuboid.getZ() * DensityAndBuoyancyModel.DISPLAY_SCALE;
        frontZProperty.value = -(cuboid.getDepth() * DensityAndBuoyancyModel.DISPLAY_SCALE) / 2.0;
    }
}
}