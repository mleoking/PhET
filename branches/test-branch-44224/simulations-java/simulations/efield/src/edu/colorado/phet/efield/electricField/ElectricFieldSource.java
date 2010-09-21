package edu.colorado.phet.efield.electricField;

import edu.colorado.phet.efield.phys2d_efield.DoublePoint;

public interface ElectricFieldSource {
    public DoublePoint getField( double x, double y );
}
