package electron.electricField;

import phys2d.DoublePoint;

public interface ElectricFieldSource {
    public DoublePoint getField(double x, double y);
}
