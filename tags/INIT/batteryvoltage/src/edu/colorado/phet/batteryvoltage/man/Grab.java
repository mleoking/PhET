package edu.colorado.phet.batteryvoltage.man;

import edu.colorado.phet.batteryvoltage.Action;
import electron.man.Man;
import electron.man.Motion;
import electron.man.motions.StandStill;
import phys2d.PropagatingParticle;
import phys2d.Propagator;

import java.util.Hashtable;
import java.util.Vector;

public class Grab implements Action {
    Vector carried;
    Man m;
    Propagator carryPropagator;
    Action carry;
    Hashtable carrierMap;
    Get get;
    Vector targeted;

    public Grab( Man m, Vector carried, Propagator carryPropagator, Hashtable carrierMap, Get get, Action looking, Vector targeted ) {
        this.targeted = targeted;
        this.get = get;
        this.carrierMap = carrierMap;
        this.carryPropagator = carryPropagator;
        this.m = m;
        this.carried = carried;
    }

    public void setCarry( Action carry ) {
        this.carry = carry;
    }

    public Action act() {
        PropagatingParticle target = get.getTarget();
        //get.setTarget(null);
//  	PropagatingParticle target=foundElectron.getTarget();
//  	if (carried.contains(target))
//  	    {
//  		return looking;
//  	    }
        carrierMap.put( target, m );
        carried.add( target );
        targeted.remove( target );
        //util.Debug.traceln(carried);
        target.setPropagator( carryPropagator );
        //carry.setTarget(target);
        return carry;
    }

    public Motion getMotion() {
        return new StandStill();
    }
}
