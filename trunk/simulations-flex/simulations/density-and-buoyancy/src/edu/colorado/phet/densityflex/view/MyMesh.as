package edu.colorado.phet.densityflex.view {
import away3d.core.base.*;
import away3d.materials.ITriangleMaterial;

/**
 * Creates a sea turtle model (exported using asAS3Class).
 *
 * @see away3d.core.Mesh#asAS3Class()
 */
public class MyMesh extends Mesh {
    private var varr:Array = [];
    private var uvarr:Array = [];

    public function v(x:Number, y:Number, z:Number):void {
        varr.push(new Vertex(x, y, z));
    }

    public function uv(u:Number, v:Number):void {
        uvarr.push(new UV(u, v));
    }

    public function f(vn0:int, vn1:int, vn2:int, uvn0:int, uvn1:int, uvn2:int):void {
        addFace(new Face(varr[vn0], varr[vn1], varr[vn2], null, uvarr[uvn0], uvarr[uvn1], uvarr[uvn2]));
    }

    public function face(x:int, y:int, z:int, texture:ITriangleMaterial = null):void {
        addFace(new Face(varr[x], varr[y], varr[z], texture, uvarr[x], uvarr[y], uvarr[z]));
    }

    public function combine(a:Object, b:Object):Object {
        var obj:Object = new Object();
        if (a != null) {
            for (var key:String in a) {
                obj[key] = a[key];
            }
        }
        if (b != null) {
            for (var key2:String in b) {
                obj[key2] = b[key2];
            }
        }
        return obj;
    }

    public function MyMesh(init:Object = null) {
        super(init);
        build();
    }

    protected function build():void {
    }

    protected function getVertexArray():Array {
        return varr;
    }
}
}