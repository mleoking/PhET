//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.away3d {
import Box2D.Dynamics.b2Body;

import edu.colorado.phet.densityandbuoyancy.model.Cuboid;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;
import edu.colorado.phet.densityandbuoyancy.view.*;

public class CuboidNode extends DensityObjectNode implements Pickable {

    private var cuboid: Cuboid;

    public function CuboidNode( cuboid: Cuboid, canvas: AbstractDBCanvas ): void {
        super( cuboid, canvas );
        this.cuboid = cuboid;
        this.x = cuboid.getX() * DensityModel.DISPLAY_SCALE;
        this.y = cuboid.getY() * DensityModel.DISPLAY_SCALE;
        this.z = cuboid.getZ() * DensityModel.DISPLAY_SCALE;
        cuboid.addShapeChangeListener( updateGeometry );
    }

    public override function setPosition( x: Number, y: Number ): void {
        cuboid.setPosition( x / DensityModel.DISPLAY_SCALE, y / DensityModel.DISPLAY_SCALE );
    }

    public function getCuboid(): Cuboid {
        return cuboid;
    }

    public override function getBody(): b2Body {
        return cuboid.getBody();
    }

    public override function updateGeometry(): void {
        this.x = cuboid.getX() * DensityModel.DISPLAY_SCALE;
        this.y = cuboid.getY() * DensityModel.DISPLAY_SCALE;
        this.z = cuboid.getZ() * DensityModel.DISPLAY_SCALE;
        frontZProperty.value = -(cuboid.getDepth() * DensityModel.DISPLAY_SCALE) / 2.0;
    }
}
}