package edu.colorado.phet.efield.electron.electricField;

import edu.colorado.phet.efield.electron.gui.vectorChooser.VectorListener;
import edu.colorado.phet.efield.electron.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.electron.phys2d_efield.Law;
import edu.colorado.phet.efield.electron.phys2d_efield.Particle;
import edu.colorado.phet.efield.electron.phys2d_efield.System2D;

public class ElectricForceLaw implements Law, VectorListener, ElectricFieldSource {
    DoublePoint field;

    public ElectricForceLaw( DoublePoint field ) {
        this.field = field;
    }

    public void vectorChanged( DoublePoint field ) {
        this.field = field;
    }

    public DoublePoint getField( double x, double y ) {
        return field;
    }

    public void iterate( double dt, System2D sys ) {
        for ( int i = 0; i < sys.numParticles(); i++ ) {
            Particle p = sys.particleAt( i );
            DoublePoint force = field.multiply( p.getCharge() );//f=qE
            //f=ma
            double mass = p.getMass();
            DoublePoint fOverM = force.multiply( 1.0 / mass );
            DoublePoint oldAcc = p.getAcceleration();
            DoublePoint newAcc = oldAcc.add( fOverM );
            //util.Debug.traceln("oldacc="+oldAcc+", newacc="+newAcc);
            p.setAcceleration( newAcc );
        }
    }
}
