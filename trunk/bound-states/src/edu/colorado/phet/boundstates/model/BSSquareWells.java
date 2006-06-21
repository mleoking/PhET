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
import java.util.Collections;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.SchmidtLeeSolver.SchmidtLeeException;


/**
 * BSSquareWell is the model of a potential composed of one or more Square wells.
 * <p>
 * Our model supports these parameters:
 * <ul>
 * <li>number of wells
 * <li>spacing
 * <li>offset (at the bottom of the wells)
 * <li>width
 * <li>height
 * </ul>
 * Offset, width, height and spacing are identical for each well.
 * Spacing is irrelevant if the number of wells is 1.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareWells extends BSAbstractPotential {
   
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _height; // height in eV
    private double _width; // width in nm
    private double _separation; // separation in nm

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param particle
     * @param numberOfWells
     * @param offset
     * @param height
     * @param width
     * @param separation
     */
    public BSSquareWells( BSParticle particle, int numberOfWells, double offset, double height, double width, double separation ) {
        super( particle, numberOfWells, offset );
        setHeight( height );
        setWidth( width );
        setSeparation( separation );
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
     * @param width the width, > 0, in nm
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
     * @param heigth the height, >= 0, in eV
     */
    public void setHeight( double height ) {
        if ( height < 0 ) {
            throw new IllegalArgumentException( "invalid heigth: " + height );
        }
        if ( height != _height ) {
            _height = height;
            markEigenstatesDirty();
            notifyObservers();
        }
    }
    
    /**
     * Gets the separation between wells.
     * Separation is the distance between the walls of adjacent wells.
     * 
     * @return separation the separation, in nm
     */
    public double getSeparation() {
        return _separation;
    }

    /**
     * Sets the separation between wells.
     * Separation is the distance between the walls of adjacent wells,
     * and is the same for each pair of wells.
     * Separation of zero is OK.
     * 
     * @param separation the separation, >= 0, in nm
     */
    public void setSeparation( double separation ) {
        if ( separation < 0 ) {
                throw new IllegalArgumentException( "invalid separation: " + separation );
        }
        if ( separation != _separation ) {
            _separation = separation;
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
     * 
     * @return BSWellType.SQUARE
     */
    public BSWellType getWellType() {
        return BSWellType.SQUARE;
    }
    
    /**
     * Multiple wells are supported.
     * @returns true
     */
    public boolean supportsMultipleWells() {
        return true;
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
        
        final int n = getNumberOfWells();
        final double offset = getOffset();
        final double c = getCenter();
        final double s = getWidth() + getSeparation(); // spacing between well centers
        final double w = getWidth();
        final double h = getHeight();
        
        double energy = offset + h;
        
        for ( int i = 1; i <= n; i++ ) {
            final double xi = s * ( i - ( ( n + 1 ) / 2.0 ) );
            if ( ( ( position - c ) >= xi - ( w / 2 ) ) && ( ( position - c ) <= xi + ( w / 2 ) ) ) {
                energy = offset;
                break;
            }
        }

        return energy;
    }
    
    /*
     * Calculates the eigenstates.
     * Start at the ground state and continue up to the offset + height.
     */
    protected BSEigenstate[] calculateEigenstates() {
        
        SchmidtLeeSolver solver = getEigenstateSolver();
        ArrayList eigenstates = new ArrayList();
        final double maxE = getOffset() + _height;
        int nodes = 0;
        
        boolean done = false;
        while ( !done ) {
            try {
                double E = solver.getEnergy( nodes );
                if ( E <= maxE ) {
                    int subscript = nodes + 1; // subscripts start at 1
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
