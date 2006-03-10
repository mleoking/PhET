/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enum.WellType;


/**
 * BSHarmonicOscillatorWell is the model of a potential composed of one Harmonic Oscillator well.
 * <p>
 * Our model supports these parameters:
 * <ul>
 * <li>offset
 * <li>angular frequency
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHarmonicOscillatorWell extends BSAbstractPotential implements Observer {

    private double _angularFrequency;
    private BSParticle _particle;
    
    public BSHarmonicOscillatorWell( BSParticle particle ) {
        this( particle, BSConstants.DEFAULT_OSCILLATOR_OFFSET, BSConstants.DEFAULT_OSCILLATOR_ANGULAR_FREQUENCY );
    }
    
    public BSHarmonicOscillatorWell( BSParticle particle, double offset, double angularFrequency ) {
        super( 1, 0, offset, BSConstants.DEFAULT_WELL_CENTER );
        setAngularFrequency( angularFrequency );
        setParticle( particle );
    }

    public double getAngularFrequency() {
        return _angularFrequency;
    }

    public void setAngularFrequency( double angularFrequency ) {
        _angularFrequency = angularFrequency;
        notifyObservers();
    }

    public BSParticle getParticle() {
        return _particle;
    }
    
    public void setParticle( BSParticle particle ) {
        if ( _particle != null ) {
            _particle.deleteObserver( this );
        }
        _particle = particle;
        _particle.addObserver( this );
        notifyObservers();
    }
    
    public void setNumberOfWells( int numberOfWells ) {
        if ( numberOfWells != 1 ) {
            throw new UnsupportedOperationException( "mutiple wells not supported for harmonic oscillator well" );
        }
        else {
            super.setNumberOfWells( numberOfWells );
        }
    }
    
    public WellType getWellType() {
        return WellType.HARMONIC_OSCILLATOR;
    }

    public int getStartingIndex() {
        return 0;
    }
    
    //HACK 15 dummy eigenstates
    public BSEigenstate[] getEigenstates() {
        final int numberOfEigenstates = 15;
        BSEigenstate[] eigenstates = new BSEigenstate[ numberOfEigenstates ];
        final double maxEnergy = getOffset();
        final double minEnergy = getOffset() - 5;
        final double deltaEnergy = ( maxEnergy - minEnergy ) / numberOfEigenstates;
        for ( int i = 0; i < eigenstates.length; i++ ) {
            eigenstates[i] = new BSEigenstate( minEnergy + ( i * deltaEnergy ) );
        }
        return eigenstates;
    }

    public Point2D[] getPoints( double minX, double maxX, double dx ) {
        ArrayList points = new ArrayList();
        for ( double x = minX; x <= maxX; x += dx ) {
            points.add( new Point2D.Double( x, U(x) ) );
        }
        // Convert to an array...
        return (Point2D[]) points.toArray( new Point2D.Double[points.size()] );
    }
    
    private double U( final double x ) {
        assert( getNumberOfWells() == 1 );
        
        final double offset = getOffset();
        final double c = getCenter();
        final double m = _particle.getMass();
        final double w = getAngularFrequency();
        
        return offset + ( 0.5 * m * w * w * (x-c) * (x-c) );
    }

    public void update( Observable o, Object arg ) {
        if ( o == _particle ) {
            notifyObservers();
        }   
    }
}
