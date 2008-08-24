package edu.colorado.phet.forces1d.view;

/**
 * Created by: Sam
 * Aug 23, 2008 at 7:27:43 PM
 */
public interface OffsetManager {//workaround for poor scene graph

    double getOffset();

    void addListener( Listener listener );

    public static interface Listener {
        void offsetChanged();
    }
}
