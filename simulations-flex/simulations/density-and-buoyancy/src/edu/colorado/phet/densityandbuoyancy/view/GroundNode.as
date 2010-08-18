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
        v(-poolWidth / 2, 0, 0);
        v(-poolWidth / 2, 0, poolDepth);
        v(poolWidth / 2, 0, poolDepth);
        v(poolWidth / 2, 0, 0);

        // far ground vertices
        v(far, 0, 0);
        v(far, 0, poolDepth);
        v(-far, 0, poolDepth);
        v(-far, 0, 0);

        // front earth vertices
        v(-far, -far, 0);
        v(far, -far, 0);
        v(poolWidth / 2, -poolHeight, 0);
        v(-poolWidth / 2, -poolHeight, 0);

        // at the back of the pool
        v(-poolWidth / 2, -poolHeight, poolDepth);
        v(poolWidth / 2, -poolHeight, poolDepth);

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

        face(3, 4, 5, grassMaterial);
        face(3, 5, 2, grassMaterial);
        //face(2, 5, 6, grassMaterial);
        //face(1, 2, 6, grassMaterial);
        face(0, 1, 6, grassMaterial);
        face(0, 6, 7, grassMaterial);

        face(0, 7, 11, earthMaterial);
        face(7, 8, 11, earthMaterial);
        face(8, 9, 11, earthMaterial);
        face(9, 10, 11, earthMaterial);
        face(10, 9, 4, earthMaterial);
        face(4, 3, 10, earthMaterial);

        // right side of pool
        face(10, 3, 2, poolMaterial);
        face(10, 2, 13, poolMaterial);

        // back of pool
        face(1, 12, 13, poolMaterial);
        face(13, 2, 1, poolMaterial);

        // left side of pool
        face(11, 12, 1, poolMaterial);
        face(11, 1, 0, poolMaterial);

        // bottom of pool
        face(10, 13, 12, poolMaterial);
        face(10, 12, 11, poolMaterial);

        type = "GroundNode";
        url = "density";
    }


}
}