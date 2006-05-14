/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.module;

import org.jfree.data.Range;

import edu.colorado.phet.boundstates.util.DoubleRange;

/**
 * BSWellRangeSpec is the specification of ranges for a well type.
 * It contains the union of all well attribute; irrelevant attributes are set to null.
 * Use the inner classes to create a specification for a specific well type.
 * Instances are immutable.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSWellRangeSpec {

    private Range _energyRange;
    private DoubleRange _offsetRange;
    private DoubleRange _spacingRange;
    private DoubleRange _widthRange;
    private DoubleRange _heightRange;
    private DoubleRange _separationRange;
    private DoubleRange _angularFrequencyRange;
    
    public static class Asymmetric extends BSWellRangeSpec {
        public Asymmetric( Range energyRange, DoubleRange offsetRange, DoubleRange widthRange, DoubleRange heightRange ) {
            super( energyRange, offsetRange, null, widthRange, heightRange, null, null );
        }
    }
    
    public static class Coulomb1D extends BSWellRangeSpec {
        public Coulomb1D( Range energyRange, DoubleRange offsetRange, DoubleRange spacingRange ) {
            super( energyRange, offsetRange, spacingRange, null, null, null, null );
        }
    }
    
    public static class Coulomb3D extends BSWellRangeSpec {
        public Coulomb3D( Range energyRange, DoubleRange offsetRange ) {
            super( energyRange, offsetRange, null, null, null, null, null );
        }
    }
    
    public static class HarmonicOscillator extends BSWellRangeSpec {
        public HarmonicOscillator( Range energyRange, DoubleRange offsetRange, DoubleRange angularFrequencyRange ) {
            super( energyRange, offsetRange, null, null, null, null, angularFrequencyRange );
        }
    }
    
    public static class Square extends BSWellRangeSpec {
        public Square( Range energyRange, DoubleRange offsetRange, DoubleRange widthRange, DoubleRange heightRange, DoubleRange separationRange ) {
            super( energyRange, offsetRange, null, widthRange, heightRange, separationRange, null );
        }
    }
    
    /* Do not instantiate, use inner classes */
    private BSWellRangeSpec( 
            Range energyRange,
            DoubleRange offsetRange, DoubleRange spacingRange, 
            DoubleRange widthRange, DoubleRange heightRange,
            DoubleRange separationRange, DoubleRange angularFrequencyRange ) 
    {
        _energyRange = energyRange;
        _offsetRange = offsetRange;
        _spacingRange = spacingRange;
        _widthRange = widthRange;
        _heightRange = heightRange;
        _separationRange = separationRange;
        _angularFrequencyRange = angularFrequencyRange;
    }

    public Range getEnergyRange() {
        return _energyRange;
    }

    public DoubleRange getHeightRange() {
        return _heightRange;
    }

    public DoubleRange getOffsetRange() {
        return _offsetRange;
    }
    
    public DoubleRange getWidthRange() {
        return _widthRange;
    }
    
    public DoubleRange getSpacingRange() {
        return _spacingRange;
    }
    
    public DoubleRange getAngularFrequencyRange() {
        return _angularFrequencyRange;
    }
    
    public DoubleRange getSeparationRange() {
        return _separationRange;
    }
}
