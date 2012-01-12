// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.test.sprites;

import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.umd.cs.piccolo.PNode;

public abstract class TestSprite extends PNode {

    private Point3D position;

    private List<Listener> listeners;

    protected TestLandscape landscape;

    protected TestSprite( TestLandscape landscape, Point3D position ) {
        this.landscape = landscape;
        this.position = position;
        listeners = new LinkedList<Listener>();
    }

    //----------------------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------------------

    public Point3D getPosition() {
        return position;
    }

    public void setPosition( Point3D position ) {
        double old = position.getZ();
        this.position = position;
        notifyMoved( old != position.getZ() );
    }

    //----------------------------------------------------------------------------
    // Event stuff
    //----------------------------------------------------------------------------

    public void addSpriteListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeSpriteListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void notifyMoved( boolean zChanged ) {
        for ( Listener listener : listeners ) {
            listener.spriteMoved( this, zChanged );
        }
    }

    public interface Listener {
        public void spriteMoved( TestSprite testSprite, boolean zChanged );
    }
}
