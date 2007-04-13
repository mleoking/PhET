package edu.colorado.phet.efield.electron.electricField;

import edu.colorado.phet.efield.electron.phys2d_efield.DoublePoint;

public interface ElectricFieldSource {
    public DoublePoint getField(double x, double y);
}
