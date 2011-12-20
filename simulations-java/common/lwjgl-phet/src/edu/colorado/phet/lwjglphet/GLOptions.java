// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet;

public class GLOptions implements Cloneable {
    /*
     * IMPORTANT NOTE: this class is cloneable and so shallow copies of all fields
     * are taken for modification of these settings.
     */

    // whether we are just drawing for "picking" purposes.
    public boolean forSelection = false;
    public boolean forWireframe = false;

    public boolean shouldSendNormals() {
        return !forSelection && !forWireframe;
    }

    public boolean shouldSendTexture() {
        return !forSelection && !forWireframe;
    }

    public GLOptions getCopy() {
        try {
            return (GLOptions) clone();
        }
        catch ( CloneNotSupportedException e ) {
            throw new RuntimeException( e );
        }
    }
}
