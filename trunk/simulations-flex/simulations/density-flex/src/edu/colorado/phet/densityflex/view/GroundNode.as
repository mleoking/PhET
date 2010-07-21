package edu.colorado.phet.densityflex.view {
import away3d.core.base.Vertex;

import away3d.materials.ShadingColorMaterial;

import edu.colorado.phet.densityflex.model.DensityModel;

public class GroundNode extends MyMesh {
    public function GroundNode(model:DensityModel) {
        super();

        var poolWidth:Number = model.getPoolWidth() * DensityModel.DISPLAY_SCALE;
        var poolDepth:Number = model.getPoolDepth() * DensityModel.DISPLAY_SCALE;

        var far:Number = DensityView.far;

        this.material = new ShadingColorMaterial(0x00AA00);

        // pool vertices
        v(-poolWidth / 2, 0, 0);
        v(-poolWidth / 2, 0, poolDepth);
        v(poolWidth / 2, 0, poolDepth);
        v(poolWidth / 2, 0, 0);

        // far ground vertices
        v(far, 0, 0);
        v(far, 0, far);
        v(-far, 0, far);
        v(-far, 0, 0);

        for each (var vertex:Vertex in getVertexArray()) {
            vertex.y = vertex.y + DensityView.verticalGroundOffset;
        }

        function addUV(v:Vertex):void {
            var minX = -poolWidth / 2;
            var maxX = poolWidth / 2;
            var minZ = 0;
            var maxZ = far;

            uv((v.x - minX) / (maxX - minX), (v.z - minZ) / (maxZ - minZ));

        }

        for each (var vertex:Vertex in getVertexArray()) {
            addUV(vertex);
        }

        face(3, 4, 5);
        face(3, 5, 2);
        face(2, 5, 6);
        face(1, 2, 6);
        face(0, 1, 6);
        face(0, 6, 7);

        type = "GroundNode";
        url = "density";
    }


}
}