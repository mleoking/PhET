/*
 * Class: PhetGraphic
 * Package: edu.colorado.phet.graphics
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 11:14:11 AM
 */
package edu.colorado.phet.graphics;

import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.physics.body.PhysicalEntity;

import java.util.Observable;
import java.util.Observer;

/**
 * Abstract base class for Paintable objects that are associated with elements in the
 * PhysicalSystem.
 */
public abstract class PhetGraphic implements Paintable, Observer {

    // The ApparatusPanel that owns this graphic
    private ApparatusPanel apparatusPanel;
    // The object (e.g., Image or Shape) that gets drawn
    private Object rep;
    // The physical body represented by this graphic
    private PhysicalEntity body;

    /**
     *
     * @param body
     */
    public void init( Particle body ) {
        this.body = body;
        this.setPosition( body );
        body.addObserver( this );
    }

    /**
     *
     * @param apparatusPanel The ApparatusPanel this graphic appears on
     */
    public void setApparatusPanel( ApparatusPanel apparatusPanel ) {
        this.apparatusPanel = apparatusPanel;
    }

    /**
     *
     * @return The apparatus panel that this graphic is attached to
     */
    protected ApparatusPanel getApparatusPanel() {
        return apparatusPanel;
    }

    /**
     *
     * @return The entity in the physical system this graphic corresponds to
     */
    public PhysicalEntity getBody() {
        return body;
    }

    public Object getRep() {
        return rep;
    }

    protected void setRep( Object rep ) {
        this.rep = rep;
    }

    /**
     *
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {

        // Have we been told that a the body with which we are
        // associated has been removed from the system?
        if( o instanceof PhysicalEntity
                && arg instanceof Integer
                && arg == Particle.S_REMOVE_BODY ) {
            apparatusPanel.removeBody( (PhysicalEntity)o );
        }
    }

    //
    // Abstract methods
    //
    protected abstract void setPosition( Particle body );

}
