package edu.colorado.phet.theramp.v2.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

public class RampModel {
    private ArrayList tracks = new ArrayList();
    private ArrayList objects = new ArrayList();

    public RampModel() {
        objects.add( new RampObject( 0, new ImmutableVector2D.Double(), new ImmutableVector2D.Double(), new ImmutableVector2D.Double(), 0, 0, 0 ) );
    }

    private RampModel( ArrayList tracks, ArrayList objects ) {
        this.tracks = tracks;
        this.objects = objects;
    }

    public RampModel update( double dt ) {
        ArrayList newObjects = new ArrayList();
        ArrayList newTracks = new ArrayList();
        for ( int i = 0; i < objects.size(); i++ ) {
            newObjects.add( updateObject( ( (RampObject) objects.get( i ) ), dt ) );
        }
        for ( int i = 0; i < tracks.size(); i++ ) {
            newTracks.add( tracks.get( i ) );
        }
        return new RampModel( newTracks, newObjects );
    }

    private RampObject updateObject( RampObject orig, double dt ) {
        return orig.translate( 1, 1 );
    }

    public int getObjectCount() {
        return objects.size();
    }

    public RampObject getObject( int i ) {
        return (RampObject) objects.get( i );
    }
}
