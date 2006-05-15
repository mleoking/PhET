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

import java.text.DecimalFormat;

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

    private BSAxisSpec _energyAxisSpec;
    private DoubleRange _offsetRange;
    private DoubleRange _spacingRange;
    private DoubleRange _widthRange;
    private DoubleRange _heightRange;
    private DoubleRange _separationRange;
    private DoubleRange _angularFrequencyRange;
    
    public static class Asymmetric extends BSWellRangeSpec {
        public Asymmetric( BSAxisSpec energyAxisSpec, DoubleRange offsetRange, DoubleRange widthRange, DoubleRange heightRange ) {
            super( energyAxisSpec, offsetRange, null, widthRange, heightRange, null, null );
        }
    }
    
    public static class Coulomb1D extends BSWellRangeSpec {
        public Coulomb1D( BSAxisSpec energyAxisSpec, DoubleRange offsetRange, DoubleRange spacingRange ) {
            super( energyAxisSpec, offsetRange, spacingRange, null, null, null, null );
        }
    }
    
    public static class Coulomb3D extends BSWellRangeSpec {
        public Coulomb3D( BSAxisSpec energyAxisSpec, DoubleRange offsetRange ) {
            super( energyAxisSpec, offsetRange, null, null, null, null, null );
        }
    }
    
    public static class HarmonicOscillator extends BSWellRangeSpec {
        public HarmonicOscillator( BSAxisSpec energyAxisSpec, DoubleRange offsetRange, DoubleRange angularFrequencyRange ) {
            super( energyAxisSpec, offsetRange, null, null, null, null, angularFrequencyRange );
        }
    }
    
    public static class Square extends BSWellRangeSpec {
        public Square( BSAxisSpec energyAxisSpec, DoubleRange offsetRange, DoubleRange widthRange, DoubleRange heightRange, DoubleRange separationRange ) {
            super( energyAxisSpec, offsetRange, null, widthRange, heightRange, separationRange, null );
        }
    }
    
    /* Do not instantiate, use inner classes */
    private BSWellRangeSpec( 
            BSAxisSpec energyAxisSpec,
            DoubleRange offsetRange, DoubleRange spacingRange, 
            DoubleRange widthRange, DoubleRange heightRange,
            DoubleRange separationRange, DoubleRange angularFrequencyRange ) 
    {
        _energyAxisSpec = energyAxisSpec;
        _offsetRange = offsetRange;
        _spacingRange = spacingRange;
        _widthRange = widthRange;
        _heightRange = heightRange;
        _separationRange = separationRange;
        _angularFrequencyRange = angularFrequencyRange;
    }

    public BSAxisSpec getEnergyAxisSpec() {
        return _energyAxisSpec;
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
