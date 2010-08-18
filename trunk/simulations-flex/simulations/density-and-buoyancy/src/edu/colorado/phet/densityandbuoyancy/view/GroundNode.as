package edu.colorado.phet.densityandbuoyancy.view {
import away3d.core.base.Vertex;
import away3d.materials.ITriangleMaterial;
import away3d.materials.ShadingColorMaterial;

import edu.colorado.phet.densityandbuoyancy.DensityConstants;
import edu.colorado.phet.densityandbuoyancy.model.DensityModel;

public class GroundNode extends MyMesh {
    public function GroundNode(model:DensityModel) {
        super();

        var poolWidth:Number = model.getPoolWidth() * DensityModel.DISPLAY_SCALE;
        var poolHeight:Number = model.getPoolHeight() * DensityModel.DISPLAY_SCALE;
        var poolDepth:Number = model.getPoolDepth() * DensityModel.DISPLAY_SCALE;

        var far:Number = AbstractDensityModule.far;

        // pool vertices
        var POOL_LEFT_FRONT:Number = v(-poolWidth / 2, 0, 0);
        var POOL_LEFT_BACK:Number =v(-poolWidth / 2, 0, poolDepth);
        var POOL_RIGHT_FRONT:Number =v(poolWidth / 2, 0, poolDepth);
        var POOL_RIGHT_BACK:Number =v(poolWidth / 2, 0, 0);

        // far ground vertices
        var GROUND_LEFT_FRONT:Number=v(far, 0, 0);
        var GROUND_LEFT_BACK:Number=v(far, 0, poolDepth);
        var GROUND_RIGHT_BACK:Number=v(-far, 0, poolDepth);
        var GROUND_RIGHT_FRONT:Number=v(-far, 0, 0);

        // front earth vertices
        var EARTH_LEFT_BASE:Number=v(-far, -far, 0);
        var EARTH_RIGHT_BASE:Number=v(far, -far, 0);
        var POOL_RIGHT_BASE:Number=v(poolWidth / 2, -poolHeight, 0);
        var POOL_LEFT_BASE:Number=v(-poolWidth / 2, -poolHeight, 0);

        // at the back of the pool
        var POOL_LEFT_BACK_BASE:Number=v(-poolWidth / 2, -poolHeight, poolDepth);
        var POOL_RIGHT_BACK_BASE:Number=v(poolWidth / 2, -poolHeight, poolDepth);

        for each (var vertex1:Vertex in getVertexArray()) {
            vertex1.y = vertex1.y + DensityConstants.VERTICAL_GROUND_OFFSET_AWAY_3D;
        }

        function addUV(v:Vertex):void {
            var minX:Number = -poolWidth / 2;
            var maxX:Number = poolWidth / 2;
            var minZ:Number = 0;
            var maxZ:Number = far;

            uv((v.x - minX) / (maxX - minX), (v.z - minZ) / (maxZ - minZ));
        }

        for each (var vertex2:Vertex in getVertexArray()) {
            addUV(vertex2);
        }

        var grassMaterial:ITriangleMaterial = new ShadingColorMaterial(0x00AA00);
        var earthMaterial:ITriangleMaterial = new ShadingColorMaterial(0xAA7733);
        var poolMaterial:ITriangleMaterial = new ShadingColorMaterial(0xAAAAAA);

        face(POOL_RIGHT_BACK, GROUND_LEFT_FRONT, GROUND_LEFT_BACK, grassMaterial);
        face(POOL_RIGHT_BACK, GROUND_LEFT_BACK, POOL_RIGHT_FRONT, grassMaterial);
        //face(2, 5, 6, grassMaterial);
        //face(1, 2, 6, grassMaterial);
        face(POOL_LEFT_FRONT, POOL_LEFT_BACK, GROUND_RIGHT_BACK, grassMaterial);
        face(POOL_LEFT_FRONT, GROUND_RIGHT_BACK, GROUND_RIGHT_FRONT, grassMaterial);

        face(POOL_LEFT_FRONT, GROUND_RIGHT_FRONT, POOL_LEFT_BASE, earthMaterial);
        face(GROUND_RIGHT_FRONT, EARTH_LEFT_BASE, POOL_LEFT_BASE, earthMaterial);
        face(EARTH_LEFT_BASE, EARTH_RIGHT_BASE, POOL_LEFT_BASE, earthMaterial);
        face(EARTH_RIGHT_BASE, POOL_RIGHT_BASE, POOL_LEFT_BASE, earthMaterial);
        face(POOL_RIGHT_BASE, EARTH_RIGHT_BASE, GROUND_LEFT_FRONT, earthMaterial);
        face(GROUND_LEFT_FRONT, POOL_RIGHT_BACK, POOL_RIGHT_BASE, earthMaterial);

        // right side of pool
        face(POOL_RIGHT_BASE, POOL_RIGHT_BACK, POOL_RIGHT_FRONT, poolMaterial);
        face(POOL_RIGHT_BASE, POOL_RIGHT_FRONT, POOL_RIGHT_BACK_BASE, poolMaterial);

        // back of pool
        face(POOL_LEFT_BACK, POOL_LEFT_BACK_BASE, POOL_RIGHT_BACK_BASE, poolMaterial);
        face(POOL_RIGHT_BACK_BASE, POOL_RIGHT_FRONT, POOL_LEFT_BACK, poolMaterial);

        // left side of pool
        face(POOL_LEFT_BASE, POOL_LEFT_BACK_BASE, POOL_LEFT_BACK, poolMaterial);
        face(POOL_LEFT_BASE, POOL_LEFT_BACK, POOL_LEFT_FRONT, poolMaterial);

        // bottom of pool
        face(POOL_RIGHT_BASE, POOL_RIGHT_BACK_BASE, POOL_LEFT_BACK_BASE, poolMaterial);
        face(POOL_RIGHT_BASE, POOL_LEFT_BACK_BASE, POOL_LEFT_BASE, poolMaterial);

        type = "GroundNode";
        url = "density";
    }


}
}