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
 * BSAsymmetricWell
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSAsymmetricWell extends BSAbstractWell {

    private double _width;
    private double _depth;

    public BSAsymmetricWell() {
        this( BSConstants.DEFAULT_ASYMMETRIC_WIDTH, 
              BSConstants.DEFAULT_ASYMMETRIC_DEPTH, 
              BSConstants.DEFAULT_ASYMMETRIC_OFFSET, 
              BSConstants.DEFAULT_WELL_CENTER );
    }
    
    public BSAsymmetricWell( double width, double depth, double offset, double center ) {
        super( 1, 0, offset, center );
        setWidth( width );
        setDepth( depth );
    }
    
    public double getWidth() {
        return _width;
    }

    public void setWidth( double width ) {
        if ( width <= 0 ) {
            throw new IllegalArgumentException( "invalid width: " + width );
        }
        _width = width;
        notifyObservers();
    }

    public double getDepth() {
        return _depth;
    }

    public void setDepth( double depth ) {
        if ( depth > 0 ) {
            throw new IllegalArgumentException( "invalid depth: " + depth );
        }
        _depth = depth;
        notifyObservers();
    }
    
    public WellType getWellType() {
        return WellType.ASYMMETRIC;
    }
    
    public void setNumberOfWells( int numberOfWells ) {
        if ( numberOfWells != 1 ) {
            throw new UnsupportedOperationException( "mutiple wells not supported for asymmetric well" );
        }
        else {
            super.setNumberOfWells( numberOfWells );
        }
    }

    //XXX Hack -- creates 20 eigenstates between offset and depth
    public BSEigenstate[] getEigenstates() {
        final int numberOfEigenstates = 20;
        BSEigenstate[] eigenstates = new BSEigenstate[ numberOfEigenstates ];
        final double maxEnergy = getOffset();
        final double minEnergy = getOffset() + getDepth();
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
            points.add( new Point2D.Double( x, getOffset() + 2 ) );
        }
        // Convert to an array...
        return (Point2D[]) points.toArray( new Point2D.Double[points.size()] );
    }
}
