package edu.colorado.phet.batteryvoltage.man;

import java.util.Vector;

import edu.colorado.phet.batteryvoltage.Action;
import edu.colorado.phet.batteryvoltage.common.electron.man.Man;
import edu.colorado.phet.batteryvoltage.common.electron.man.Motion;
import edu.colorado.phet.batteryvoltage.common.electron.man.laws.MotionChooser;
import edu.colorado.phet.batteryvoltage.common.phys2d.PropagatingParticle;

public class VoltMan implements MotionChooser {
    Action action;
    Action declaredAction;

    Action goToHome;
    Get get;

    Vector dir;

    public VoltMan( Action goToHome, Get get ) {
        dir = new Vector();
        this.get = get;
        this.goToHome = goToHome;
    }

    public void addDirectional( Directional d ) {
        dir.add( d );
    }

    public void goHomeAndStayThere() {
        setAction( goToHome );
    }

    public void setGoingRight( boolean t ) {
        /*Make sure we're going right.*/
        for ( int i = 0; i < dir.size(); i++ ) {
            Directional d = (Directional) dir.get( i );
            d.setCarryRight( t );
        }
    }

    public void carryElectronRight( PropagatingParticle p ) {
        setGoingRight( true );
        get.setTarget( p );
        setAction( get );
    }

    public void carryElectronLeft( PropagatingParticle p ) {
        /*Make sure we're going left.*/
        setGoingRight( false );
        get.setTarget( p );
        setAction( get );
    }

    public boolean isAvailable() {
        return ( this.declaredAction == goToHome );
    }

    public Action getAction() {
        return action;
    }

    public void setAction( Action a ) {
        this.declaredAction = a;
        this.action = a;
    }

    public Motion getMotion() {
        Action old = action;
        this.action = action.act();
        //o.O.p(action);
        if ( action == null ) {
            throw new RuntimeException( "Returned null action: " + old );
            //util.Debug.traceln("Action returned null: "+old);
        }
        //Motion m=action.getMotion();
        //o.O.p(m);
        return action.getMotion();
    }
}
