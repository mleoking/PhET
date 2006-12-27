package edu.colorado.phet.batteryvoltage.man;

import edu.colorado.phet.batteryvoltage.Action;
import edu.colorado.phet.batteryvoltage.Battery;
import electron.man.Man;
import electron.man.Motion;
import phys2d.PropagatingParticle;
import phys2d.Propagator;

import java.util.Hashtable;
import java.util.Vector;

public class Release implements Action, Directional {
    Vector carried;
    Propagator freePropagator;
    Hashtable carrierMap;
    Get get;
    Vector targeted;
    Battery b;
    boolean right;
    VoltMan vm;

    public void setCarryRight( boolean right ) {
        this.right = right;
    }

    public Release( Man m, Vector carried, Propagator freePropagator, Hashtable carrierMap, Get get, Vector targeted, Battery b, boolean right ) {
        this.get = get;
        this.targeted = targeted;
        this.carrierMap = carrierMap;
        this.freePropagator = freePropagator;
        this.carried = carried;
        this.b = b;
    }

    public void setVoltMan( VoltMan vm ) {
        this.vm = vm;
    }

    public void setPropagator( Propagator freePropagator ) {
        this.freePropagator = freePropagator;
    }

    public Action act() {
        vm.goHomeAndStayThere();
        PropagatingParticle target = get.getTarget();
        carrierMap.put( target, new Object() );
        carried.remove( target );
        targeted.remove( target );
        target.setPropagator( freePropagator );
        if( right ) {
            b.setRight( target );
        }
        else {
            b.setLeft( target );
        }
        return vm.getAction();
    }

    public Motion getMotion() {
        return null;
    }
}
