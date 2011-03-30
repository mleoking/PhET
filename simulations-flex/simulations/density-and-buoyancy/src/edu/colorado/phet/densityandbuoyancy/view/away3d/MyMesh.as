//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.densityandbuoyancy.view.away3d {
import away3d.core.base.*;
import away3d.materials.ITriangleMaterial;

/**
 * Provides convenience methods for creating custom Mesh instances.
 */
public class MyMesh extends Mesh {
    private var varr: Array = [];
    private var uvarr: Array = [];

    /**
     * Add a vertex
     * @param x
     * @param y
     * @param z
     * @return The index of the vertex. Should be used later with the face functions
     */
    public function v( x: Number, y: Number, z: Number ): Number {
        varr.push( new Vertex( x, y, z ) );
        return varr.length - 1;
    }

    /**
     * Adds a UV-coordinate, which are used for texture mapping.
     * @param u
     * @param v
     */
    public function uv( u: Number, v: Number ): void {
        uvarr.push( new UV( u, v ) );
    }

    /**
     * Add a face, without texture
     *
     * This face should reference already-defined vertices and UV-coordinates
     * @param vn0 Vertex index 0
     * @param vn1 Vertex index 1
     * @param vn2 Vertex index 2
     * @param uvn0 UV coordinates index 0
     * @param uvn1 UV coordinates index 1
     * @param uvn2 UV coordinates index 2
     */
    public function f( vn0: int, vn1: int, vn2: int, uvn0: int, uvn1: int, uvn2: int ): void {
        addFace( new Face( varr[vn0], varr[vn1], varr[vn2], null, uvarr[uvn0], uvarr[uvn1], uvarr[uvn2] ) );
    }

    /**
     * Add a face, with an optional texture
     *
     * @param vn0 Vertex index 0
     * @param vn1 Vertex index 1
     * @param vn2 Vertex index 2
     */
    public function face( vn0: int, vn1: int, vn2: int, texture: ITriangleMaterial = null ): void {
        addFace( new Face( varr[vn0], varr[vn1], varr[vn2], texture, uvarr[vn0], uvarr[vn1], uvarr[vn2] ) );
    }

    /**
     * Add a quad-face from 4 planar vertices
     * @param a Vertex index 0
     * @param b Vertex index 1
     * @param c Vertex index 2
     * @param d Vertex index 3
     * @param texture Optional texture
     */
    public function plane( a: int, b: int, c: int, d: int, texture: ITriangleMaterial = null ): void {
        face( a, b, c, texture );
        face( a, c, d, texture );
    }

    /**
     * Convenience method to combine maps since Away3D uses them
     * @param a Map a
     * @param b Map b
     * @return A copy of map a with all entries in b overwritten
     */
    public function combine( a: Object, b: Object ): Object {
        var obj: Object = new Object();
        if ( a != null ) {
            for ( var key: String in a ) {
                obj[key] = a[key];
            }
        }
        if ( b != null ) {
            for ( var key2: String in b ) {
                obj[key2] = b[key2];
            }
        }
        return obj;
    }

    public function MyMesh( init: Object = null ) {
        super( init );
        build();
    }

    protected function build(): void {
    }

    protected function getVertexArray(): Array {
        return varr;
    }
}
}