// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet;

/**
 * Encapsulates a rendering state that is passed along the GLNode hierarchy as it is rendering. Any time a node wants to make a change for its
 * children, it should call getCopy() and modify the copy before passing it.
 */
public class GLOptions implements Cloneable {
    /*
     * IMPORTANT NOTE: this class is cloneable and so shallow copies of all fields
     * are taken for modification of these settings.
     */

    // TODO: can we make this something extensible, or is it an advantage of having fixed passes that we don't have to specify their features?
    public static enum RenderPass {
        REGULAR,

        // transparency is rendered after regular, generally with depth-write disabled
        TRANSPARENCY,
    }

    // whether we are just drawing for "picking" purposes.
    public boolean forSelection = false;
    public boolean forWireframe = false;

    public RenderPass renderPass = RenderPass.REGULAR;

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
        catch( CloneNotSupportedException e ) {
            throw new RuntimeException( e );
        }
    }
}
