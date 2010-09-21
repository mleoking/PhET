/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import java.util.ArrayList;

import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.SchmidtLeeSolver.SchmidtLeeException;


/**
 * BSAsymmetricPotential is the model of a potential composed of one Asymmetric well.
 * <p>
 * Our model supports these parameters:
 * <ul>
 * <li>offset (at the bottom of the well)
 * <li>width
 * <li>height
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSAsymmetricPotential extends BSAbstractPotential {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _width;
    private double _height;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param particle
     * @param offset
     * @param height 
     * @param width
     */
    public BSAsymmetricPotential( BSParticle particle, double offset, double height, double width ) {
        super( particle, 1 /* numberOfWells */, offset );
        setHeight( height );
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
     * Gets the height.
     * Height is the same for all wells.
     * 
     * @return the height, in eV
     */
    public double getHeight() {
        return _height;
    }

    /**
     * Sets the height.
     * Height is the same for all wells.
     * 
     * @param height the height, >= 0, in eV
     */
    public void setHeight( double height ) {
        if ( height < 0 ) {
            throw new IllegalArgumentException( "invalid height: " + height );
        }
        if ( height != _height ) {
            _height = height;
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
     * @return false
     */
    public boolean supportsMultipleWells() {
        return false;
    }
    
    /**
     * The ground state is E1.
     * @return 1
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
        final double h = getHeight();
        
        double energy = offset + h;
        if ( Math.abs( position - c ) <= w / 2 ) {
            energy = offset + ( h - ( Math.abs( c + w/2 - position ) * h / w ) );
        }
        
        return energy;
    }

    /*
     * Calculates eigenstates, starting from the ground state (node=0) and 
     * continuing until we find an energy value that is higher than the offset + height.
     */
    protected BSEigenstate[] calculateEigenstates() {

        SchmidtLeeSolver solver = getEigenstateSolver();
        ArrayList eigenstates = new ArrayList();
        final double maxE = getOffset() + _height;
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
