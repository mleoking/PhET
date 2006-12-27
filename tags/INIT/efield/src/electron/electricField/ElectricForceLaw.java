package electron.electricField;

import electron.gui.vectorChooser.VectorListener;
import phys2d.DoublePoint;
import phys2d.Law;
import phys2d.Particle;
import phys2d.System2D;

public class ElectricForceLaw implements Law, VectorListener, ElectricFieldSource {
    DoublePoint field;

    public ElectricForceLaw(DoublePoint field) {
        this.field = field;
    }

    public void vectorChanged(DoublePoint field) {
        this.field = field;
    }

    public DoublePoint getField(double x, double y) {
        return field;
    }

    public void iterate(double dt, System2D sys) {
        for (int i = 0; i < sys.numParticles(); i++) {
            Particle p = sys.particleAt(i);
            DoublePoint force = field.multiply(p.getCharge());//f=qE
            //f=ma
            double mass = p.getMass();
            DoublePoint fOverM = force.multiply(1.0 / mass);
            DoublePoint oldAcc = p.getAcceleration();
            DoublePoint newAcc = oldAcc.add(fOverM);
            //util.Debug.traceln("oldacc="+oldAcc+", newacc="+newAcc);
            p.setAcceleration(newAcc);
        }
    }
}
