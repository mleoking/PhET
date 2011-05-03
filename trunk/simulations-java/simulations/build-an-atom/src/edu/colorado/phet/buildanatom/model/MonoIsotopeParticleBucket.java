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
     * Add an isotope to the first open location in the bucket.
     *
     * @param isotope
     * @param moveImmediately
     */
    public void addIsotopeInstanceFirstOpen( MovableAtom isotope, boolean moveImmediately ){
        if ( isIsotopeAllowed( isotope.getAtomConfiguration() )){
            addParticleFirstOpen( isotope, moveImmediately );
        }
    }

    /**
     * Add an isotope to the nearest open location in the bucket.
     *
     * @param isotope
     * @param moveImmediately
     */
    public void addIsotopeInstanceNearestOpen( MovableAtom isotope, boolean moveImmediately ){
        if ( isIsotopeAllowed( isotope.getAtomConfiguration() )){
            addParticleNearestOpen( isotope, moveImmediately );
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
}
