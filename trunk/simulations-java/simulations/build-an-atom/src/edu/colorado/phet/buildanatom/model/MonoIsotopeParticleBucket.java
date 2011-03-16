/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.buildanatom.modules.isotopemixture.model.MovableAtom;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A particle bucket that can only contain one configuration of isotope,
 * though it may contain multiple instances of that isotope.
 *
 * @author John Blanco
 */
public class MonoIsotopeParticleBucket extends ParticleBucket {

    private final Dimension2D size = new PDimension();
    private int numProtonsInIsotope = 0;
    private int numNeutronsInIsotope = 0;

    /**
     * Constructor.
     */
    public MonoIsotopeParticleBucket( Point2D position, Dimension2D size, Color baseColor, String caption,
            double particleRadius, int numProtonsInIsotope, int numNeutronsInIsotope ) {
        super( position, size, baseColor, caption, particleRadius );
        this.size.setSize( size );
        this.numProtonsInIsotope = numProtonsInIsotope;
        this.numNeutronsInIsotope = numNeutronsInIsotope;
    }

    /**
     * Create a bucket based on a previously created bucket state.  This is
     * generally used when restoring a bucket that had previously existed.
     */
    public MonoIsotopeParticleBucket( State state ){
        this( state.position, state.size, state.baseColor, state.captionText, state.particleRadius, state.numProtonsInIsotope, state.numNeutronsInIsotope );
        for ( MovableAtom isotope : state.containedIsotopes ){
            addIsotopeInstance( isotope );
        }
    }

    public void addIsotopeInstance( MovableAtom isotope ){
        if ( isIsotopeAllowed( isotope.getAtomConfiguration() )){
            addParticle( isotope, true );
        }
    }

    public boolean isIsotopeAllowed( int numProtons, int numNeutrons ){
        return this.numProtonsInIsotope == numProtons && this.numNeutronsInIsotope == numNeutrons;
    }

    public boolean isIsotopeAllowed( ImmutableAtom isotopeConfig ){
        return isIsotopeAllowed( isotopeConfig.getNumProtons(), isotopeConfig.getNumNeutrons() );
    }

    public MovableAtom removeArbitraryIsotope(){
        MovableAtom isotopeToRemove = null;
        if ( getParticleList().size() > 0 ){
            isotopeToRemove = (MovableAtom)getParticleList().get( getParticleList().size() - 1 );
            removeParticle( isotopeToRemove );
        }
        else{
            System.err.println(getClass().getName() + " - Warning: Ignoring attempt to remove particle from empty bucket.");
        }
        return isotopeToRemove;
    }

    /**
     * Get a list of all isotopes contained within this bucket.
     */
    public List<MovableAtom> getContainedIsotopes(){
        List<MovableAtom> containedIsotopes = new ArrayList<MovableAtom>();
        for ( SphericalParticle isotope : getParticleList() ){
            assert isotope instanceof MovableAtom;
            containedIsotopes.add( (MovableAtom)isotope );
        }
        return containedIsotopes;
    }

    public State getState(){
        return new State( this );
    }

    /**
     * A class used for saving and restoring a bucket's state.
     *
     * @author John Blanco
     */
    public static class State {
        protected final List<MovableAtom> containedIsotopes;
        protected final Dimension2D size;
        protected final Point2D position = new Point2D.Double();
        protected final Color baseColor;
        protected final String captionText;
        protected final double particleRadius;
        protected final int numProtonsInIsotope;
        protected final int numNeutronsInIsotope;

        public State( MonoIsotopeParticleBucket monoIsotopeParticleBucket ){
            containedIsotopes = monoIsotopeParticleBucket.getContainedIsotopes();
            size = new PDimension( monoIsotopeParticleBucket.size );
            position.setLocation( monoIsotopeParticleBucket.getPosition() );
            baseColor = monoIsotopeParticleBucket.getBaseColor();
            captionText = new String( monoIsotopeParticleBucket.getCaptionText() );
            particleRadius = monoIsotopeParticleBucket.getParticleRadius();
            numProtonsInIsotope = monoIsotopeParticleBucket.numProtonsInIsotope;
            numNeutronsInIsotope = monoIsotopeParticleBucket.numNeutronsInIsotope;
        }

        public List<MovableAtom> getContainedIsotopes() {
            return containedIsotopes;
        }
    }
}
