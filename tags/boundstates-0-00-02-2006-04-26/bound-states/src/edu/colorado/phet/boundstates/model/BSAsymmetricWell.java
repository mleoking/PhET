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
import java.util.Collections;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.SchmidtLeeSolver.SchmidtLeeException;
import edu.colorado.phet.boundstates.test.schmidt_lee.PotentialFunction;
import edu.colorado.phet.boundstates.test.schmidt_lee.Wavefunction;


/**
 * BSAsymmetricWell is the model of a potential composed of one Asymmetric well.
 * <p>
 * Our model supports these parameters:
 * <ul>
 * <li>offset
 * <li>width
 * <li>depth
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSAsymmetricWell extends BSAbstractPotential {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _width;
    private double _depth;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSAsymmetricWell( BSParticle particle ) {
        this( particle,
              BSConstants.DEFAULT_ASYMMETRIC_WIDTH, 
              BSConstants.DEFAULT_ASYMMETRIC_DEPTH, 
              BSConstants.DEFAULT_ASYMMETRIC_OFFSET, 
              BSConstants.DEFAULT_WELL_CENTER );
    }
    
    public BSAsymmetricWell( BSParticle particle, double width, double depth, double offset, double center ) {
        super( particle, 1, 0, offset, center );
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
            markEigenstatesDirty();
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
            markEigenstatesDirty();
            notifyObservers();
        }
    }

    //----------------------------------------------------------------------------
    // AbstractPotential implementation
    //----------------------------------------------------------------------------
    
    public BSWellType getWellType() {
        return BSWellType.ASYMMETRIC;
    }
    
    public boolean supportsMultipleWells() {
        return false;
    }
    
    public int getStartingIndex() {
        return 1;
    }

    public double getEnergyAt( double position ) {
        assert( getNumberOfWells() == 1 );
        
        final double offset = getOffset();
        final double c = getCenter();
        final double w = getWidth();
        
        double energy = offset;
        if ( Math.abs( position - c ) <= w / 2 ) {
            energy = offset - Math.abs( c + w/2 - position ) * getDepth() / w;
        }
        return energy;
    }

    protected BSEigenstate[] calculateEigenstates() {

        SchmidtLeeSolver solver = getEigenstateSolver();
        ArrayList eigenstates = new ArrayList();
        final double maxE = getOffset();
        int nodes = 0;
        
        boolean done = false;
        while ( !done ) {
            try {
                int subscript = nodes + 1; // subscripts start at 1
                double E = solver.getEnergy( nodes );
                if ( E <= maxE ) {
                    eigenstates.add( new BSEigenstate( subscript, E ) );
                }
                else {
                    done = true;
                }
            }
            catch ( SchmidtLeeException sle ) {
                System.err.println( sle.getClass() + ": " + sle.getMessage() );//XXX
                done = true;
            }
            nodes++;
        }
        
        // Ensure that they appear in ascending order...
        Collections.sort( eigenstates );
        
        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
}
