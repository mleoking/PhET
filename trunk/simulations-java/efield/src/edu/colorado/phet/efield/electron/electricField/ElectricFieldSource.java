package edu.colorado.phet.efield.electron.electricField;

import phys2d_efield.DoublePoint;

public interface ElectricFieldSource {
    public DoublePoint getField(double x, double y);
}
