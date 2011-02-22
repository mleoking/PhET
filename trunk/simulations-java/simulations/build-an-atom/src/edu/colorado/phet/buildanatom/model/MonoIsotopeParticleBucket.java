/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.modules.isotopemixture.model.MovableAtom;

/**
 * A particle bucket that can only contain one configuration of isotope,
 * though it may contain multiple instances of that isotope.
 *
 * @author John Blanco
 */
public class MonoIsotopeParticleBucket extends ParticleBucket {

    private int numProtons = 0;
    private int numNeutrons = 0;

    /**
     * Constructor.
     */
    public MonoIsotopeParticleBucket( Point2D position, Dimension2D size, Color baseColor, String caption,
            double particleRadius, int numProtons, int numNeutrons ) {
        super( position, size, baseColor, caption, particleRadius );
        this.numProtons = numProtons;
        this.numNeutrons = numNeutrons;
    }

    public void addIsotopeInstance( MovableAtom isotope ){
        if ( isIsotopeAllowed( isotope.getAtomConfiguration() )){
            addParticle( isotope, true );
        }

    }

    public boolean isIsotopeAllowed( int numProtons, int numNeutrons ){
        return this.numProtons == numProtons && this.numNeutrons == numNeutrons;
    }

    public boolean isIsotopeAllowed( ImmutableAtom isotopeConfig ){
        return isIsotopeAllowed( isotopeConfig.getNumProtons(), isotopeConfig.getNumNeutrons() );
    }
}
