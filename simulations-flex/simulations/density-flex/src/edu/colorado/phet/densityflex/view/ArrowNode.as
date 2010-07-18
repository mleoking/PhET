package edu.colorado.phet.densityflex.view {
import away3d.containers.ObjectContainer3D;
import away3d.core.base.Object3D;
import away3d.materials.ShadingColorMaterial;
import away3d.primitives.RegularPolygon;
import away3d.primitives.Triangle;

public class ArrowNode extends ObjectContainer3D {
    public function ArrowNode() {
        super();
        var triangle = new Triangle({ bothsides: true, edge: 200, yUp: false, width: 20, height: 20, depth: 20, segmentsW: 10, segmentsH: 10, material: new ShadingColorMaterial(0xFFFFFF) })
        addChild( triangle );
    }
}
}