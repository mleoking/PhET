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
    
    public BSAsymmetricWell( BSParticle particle, double offset, double depth, double width ) {
        super( particle, 1 /* numberOfWells */, offset );
        setDepth( depth );
        setWidth( width );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the width.
     * Width is the same for all wells.
     * 
     * @return width the width, in nm
     */
    public double getWidth() {
        return _width;
    }

    /**
     * Sets the width.
     * Width is the same for all wells.
     * 
     * param width the width, > 0, in nm
     */
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

    /**
     * Gets the depth.
     * Depth is a positive quantity, and is the same for all wells.
     * 
     * @return the depth, in eV
     */
    public double getDepth() {
        return _depth;
    }

    /**
     * Sets the depth.
     * Depth is a positive quantity, and is the same for all wells.
     * 
     * @param depth the depth, >= 0, in eV
     */
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
    
    /**
     * Gets the type of well used in the potential.
     * Potentials in this simulation are composed of homogeneous well types.
     * @return BSWellType.ASYMMETRIC
     */
    public BSWellType getWellType() {
        return BSWellType.ASYMMETRIC;
    }
    
    /**
     * Multiple wells are not supported.
     * @returns false
     */
    public boolean supportsMultipleWells() {
        return false;
    }
    
    /**
     * The ground state is E1.
     * @returns 1
     */
    public int getGroundStateSubscript() {
        return 1;
    }

    /**
     * Gets the energy at a specified position.
     * 
     * @param position the position, in nm
     */
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

    /*
     * Calculates eigenstates, starting from the ground state (node=0) and 
     * continuing until we find an energy value that is higher than the offset.
     */
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
        
        return (BSEigenstate[]) eigenstates.toArray( new BSEigenstate[ eigenstates.size() ] );
    }
}
