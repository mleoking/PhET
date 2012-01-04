// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.model.PrecipitateParticle;
import edu.colorado.phet.beerslawlab.model.PrecipitateParticle.PrecipitateParticleListener;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Visual representation of a precipitate particle.
 * We use the same representation for all solutes, but vary the size and orientation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PrecipitateParticleNode extends PPath {

    public interface PrecipitateParticleNodeListener {
        // Instructs listeners to remove the specified node from the scenegraph.
        public void removeNode( PrecipitateParticleNode node );
    }

    private final ArrayList<PrecipitateParticleNodeListener> listeners;

    public PrecipitateParticleNode( PrecipitateParticle particle ) {

        setPaint( particle.getColor() );
        setStrokePaint( particle.getColor().darker() );
        setPathTo( new Rectangle2D.Double( 0, 0, particle.getSize(), particle.getSize() ) ); // square
        setRotation( Math.random() * 2 * Math.PI );
        setOffset( particle.getLocation() );

        listeners = new ArrayList<PrecipitateParticleNodeListener>();

        particle.addListener( new PrecipitateParticleListener() {
            public void particleDeleted( PrecipitateParticle particle ) {
                fireRemoveNode();
            }
        } );
    }

    public void addListener( PrecipitateParticleNodeListener listener ) {
        listeners.add( listener );
    }

    private void removeListener( PrecipitateParticleNodeListener listener ) {
        listeners.remove( listener );
    }

    private void fireRemoveNode() {
        for ( PrecipitateParticleNodeListener listener : new ArrayList<PrecipitateParticleNodeListener>( listeners ) ) {
            listener.removeNode( this );
            removeListener( listener ); // automatically remove the listener, since the node is being removed from the scenegraph
        }
    }
}
