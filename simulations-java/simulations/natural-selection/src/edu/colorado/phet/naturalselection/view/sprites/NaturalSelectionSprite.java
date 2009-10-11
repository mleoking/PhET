/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.view.sprites;

import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.naturalselection.view.LandscapeNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Abstract class for all piccolo nodes to be displayed in and around the bunnies
 *
 * @author Jonathan Olson
 */
public abstract class NaturalSelectionSprite extends PNode implements Comparable {

    private Point3D position;

    protected LandscapeNode landscapeNode;

    private List<Listener> listeners;

    public NaturalSelectionSprite( LandscapeNode landscapeNode, Point3D position ) {
        this.landscapeNode = landscapeNode;
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
        double old = this.position.getZ();
        this.position = position;
        notifyMoved( old != position.getZ() );
    }

    /**
     * Allow the sprites to be sorted quickly for display depth
     *
     * @param otherObject Another sprite to be compared to
     * @return The usual compare function return values
     */
    public int compareTo( Object otherObject ) {
        NaturalSelectionSprite other = (NaturalSelectionSprite) otherObject;
        if ( position.getZ() == other.getPosition().getZ() ) {
            return 0;
        }

        if ( position.getZ() > other.getPosition().getZ() ) {
            return -1;
        }
        else {
            return 1;
        }
    }

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

        public void spriteMoved( NaturalSelectionSprite sprite, boolean zChanged );

    }

}
