// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.model;


/**
 * LookupTableStallForceStrategy uses a lookup table to find the stall force that 
 * is closest to a specific ATP concentration.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LookupTableStallForceStrategy implements IStallForceStrategy {
    
    /**
     * TableEntry is an immutable entry in the lookup table, 
     * and it maps and ATP concentration to a stall force value.
     */
    public static class TableEntry {

        private final double _atpConcentration; // arbitrary units
        private final double _stallForceMagnitude; // pN

        public TableEntry( double atpConcentration, double stallForceMagnitude ) {
            if ( ! ( atpConcentration >= 0 ) ) {
                throw new IllegalArgumentException( "atpConcentration must be >= 0: " + atpConcentration );
            }
            if ( ! ( stallForceMagnitude >= 0 ) ) {
                throw new IllegalArgumentException( "stallForceMagnitude must be >= 0: " + stallForceMagnitude );
            }
            _atpConcentration = atpConcentration;
            _stallForceMagnitude = stallForceMagnitude;
        }

        public double getAtpConcentration() {
            return _atpConcentration;
        }

        public double getStallForceMagnitude() {
            return _stallForceMagnitude;
        }
    }

    private final TableEntry[] _lookupTable;
    
    /**
     * Constructor.
     * 
     * @param lookupTable table that maps ATP concentration to stall force magnitude,
     *      entries must be ordered by ascending ATP concentration
     */
    public LookupTableStallForceStrategy( TableEntry[] lookupTable ) {
        if ( lookupTable == null || lookupTable.length == 0 ) {
            throw new IllegalArgumentException( "invalid lookupTable, null or zero-length" );
        }
        for ( int i = 0; i < lookupTable.length - 1; i++ ) {
            if ( lookupTable[i].getAtpConcentration() >= lookupTable[i + 1].getAtpConcentration() ) {
                throw new IllegalArgumentException( "invalid lookupTable, entries are not ordered by ascending ATP concentration" );
            }
        }
        _lookupTable = lookupTable;
    }
    
    /**
     * @see IDStallForceStrategy.getStallForceMagnitude
     * 
     * Find the closest match for the ATP concentration in the lookup table,
     * and returns the corresponding stall force.
     * 
     * @param atpConcentration ATP concentration (arbitrary units)
     * @return stall force magnitude (pN)
     */
    public double getStallForceMagnitude( final double atpConcentration ) {
        if ( atpConcentration < 0 ) {
            throw new IllegalArgumentException( "atpConcentration must be >= 0: " + atpConcentration );
        }
        int index = -1;
        if ( atpConcentration <= _lookupTable[0].getAtpConcentration() ) {
            index = 0;
        }
        else if ( atpConcentration >= _lookupTable[_lookupTable.length - 1].getAtpConcentration() ) {
            index = _lookupTable.length - 1;
        }
        else {
            for ( int i = 0; i < _lookupTable.length - 1; i++ ) {
                double atpCurrent = _lookupTable[i].getAtpConcentration();
                double atpNext = _lookupTable[i + 1].getAtpConcentration();
                if ( atpConcentration >= atpCurrent && atpConcentration < atpNext ) {
                    double midPoint = atpCurrent + ( ( atpNext - atpCurrent ) / 2 );
                    if ( atpConcentration < midPoint ) {
                        index = i;
                    }
                    else {
                        index = i + 1;
                    }
                    break;
                }
            }
        }
        assert ( index >= 0 && index < _lookupTable.length );
        return _lookupTable[index].getStallForceMagnitude();
    }

}
