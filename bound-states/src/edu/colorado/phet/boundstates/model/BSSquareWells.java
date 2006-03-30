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
import java.util.Iterator;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.test.schmidt_lee.PotentialFunction;
import edu.colorado.phet.boundstates.test.schmidt_lee.Wavefunction;


/**
 * BSSquareWell is the model of a potential composed of one or more Square wells.
 * <p>
 * Our model supports these parameters:
 * <ul>
 * <li>number of wells
 * <li>spacing
 * <li>offset
 * <li>width
 * <li>depth
 * </ul>
 * Offset, width, depth and spacing are identical for each well.
 * Spacing is irrelevant if the number of wells is 1.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareWells extends BSAbstractPotential {
   
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _width;
    private double _depth;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSSquareWells( BSParticle particle, int numberOfWells ) {
        this( particle, 
                numberOfWells, 
                BSConstants.DEFAULT_SQUARE_SPACING, 
                BSConstants.DEFAULT_SQUARE_WIDTH, 
                BSConstants.DEFAULT_SQUARE_DEPTH, 
                BSConstants.DEFAULT_SQUARE_OFFSET, 
                BSConstants.DEFAULT_WELL_CENTER );
    }
    
    public BSSquareWells( BSParticle particle, int numberOfWells, double spacing, double width, double depth, double offset, double center ) {
        super( particle, numberOfWells, spacing, offset, center );
        setWidth( width );
        setDepth( depth );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public double getWidth() {
        return _width;
    }

    public void setWidth( double width ) {
        if ( width <= 0 ) {
            throw new IllegalArgumentException( "invalid width: " + width );
        }
        if ( width != _width ) {
            _width = width;
            notifyObservers();
        }
    }

    public double getDepth() {
        return _depth;
    }

    public void setDepth( double depth ) {
        if ( depth < 0 ) {
            throw new IllegalArgumentException( "invalid depth: " + depth );
        }
        if ( depth != _depth ) {
            _depth = depth;
            notifyObservers();
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractPotential implementation
    //----------------------------------------------------------------------------
    
    public BSWellType getWellType() {
        return BSWellType.SQUARE;
    }
    
    public int getStartingIndex() {
        return 1;
    }
    
    public double getEnergyAt( double position ) {
        
        final int n = getNumberOfWells();
        final double offset = getOffset();
        final double c = getCenter();
        final double s = getSpacing();
        final double w = getWidth();
        final double d = getDepth();
        
        double energy = offset;
        
        for ( int i = 1; i <= n; i++ ) {
            final double xi = s * ( i - ( ( n + 1 ) / 2.0 ) );
            if ( ( ( position - c ) >= xi - ( w / 2 ) ) && ( ( position - c ) <= xi + ( w / 2 ) ) ) {
                energy = offset - d;
                break;
            }
        }

        return energy;
    }
    
    public BSEigenstate[] getEigenstates() {
        System.out.println( "BSSquareWells.getEigenestates, numberOfWells=" + getNumberOfWells() );//XXX
        
        ArrayList eigenstates = new ArrayList();

        final double minX = BSConstants.POSITION_RANGE.getLowerBound();
        final double maxX = BSConstants.POSITION_RANGE.getUpperBound();
        final double maxE = getOffset();
        final double hb = ( BSConstants.HBAR * BSConstants.HBAR ) / ( 2 * getParticle().getMass() );
        final int numberOfPoints = (int)( (maxX - minX) / getDx() ) + 1;
        
        int nodes = 0;
        boolean done = false;
        while ( !done ) {
            try {
                PotentialFunction function = new PotentialFunctionAdapter( this );
                Wavefunction wavefunction = new Wavefunction( hb, minX, maxX, numberOfPoints, nodes, function );
                double E = wavefunction.getE();
                if ( E <= maxE ) {
                    eigenstates.add( new BSEigenstate( E ) );
                }
                else {
                    done = true;
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
                done = true;
            }
            nodes++;
        }
        
        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
}
