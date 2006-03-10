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

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enum.WellType;


/**
 * BSHarmonicOscillatorWell
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSHarmonicOscillatorWell extends BSAbstractWell {

    private double _angularFrequency;
    
    public BSHarmonicOscillatorWell() {
        this( BSConstants.DEFAULT_OSCILLATOR_OFFSET, BSConstants.DEFAULT_OSCILLATOR_ANGULAR_FREQUENCY );
    }
    
    public BSHarmonicOscillatorWell( double offset, double angularFrequency ) {
        super( 1, 0, offset, BSConstants.DEFAULT_WELL_CENTER );
    }

    public double getAngularFrequency() {
        return _angularFrequency;
    }

    public void setAngularFrequency( double angularFrequency ) {
        _angularFrequency = angularFrequency;
    }

    public WellType getWellType() {
        return WellType.HARMONIC_OSCILLATOR;
    }

    public void setNumberOfWells( int numberOfWells ) {
        if ( numberOfWells != 1 ) {
            throw new UnsupportedOperationException( "mutiple wells not supported for harmonic oscillator well" );
        }
        else {
            super.setNumberOfWells( numberOfWells );
        }
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
        //HACK straight line at offset
        for ( double x = minX; x <= maxX; x += dx ) {
            points.add( new Point2D.Double( x, getOffset() + 1 ) );
        }
        // Convert to an array...
        return (Point2D[]) points.toArray( new Point2D.Double[points.size()] );
    }
}
