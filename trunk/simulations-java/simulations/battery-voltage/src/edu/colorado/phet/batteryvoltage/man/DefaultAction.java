// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryvoltage.man;

import java.util.Vector;

import edu.colorado.phet.batteryvoltage.Action;
import edu.colorado.phet.batteryvoltage.common.electron.man.Motion;

public class DefaultAction implements Action {
    Vector conditions = new Vector();
    Vector actions = new Vector();

    Motion m;

    public DefaultAction( Motion m ) {
        this.m = m;
    }

    public void addClause( Condition c, Action a ) {
        conditions.add( c );
        actions.add( a );
    }

    public Action act() {
//        if (ac != null)
//            ac.act();
        for ( int i = 0; i < conditions.size(); i++ ) {
            Condition c = (Condition) conditions.get( i );
            if ( c.isSatisfied() ) {
                return ( (Action) actions.get( i ) );
            }
        }
        return this;
    }

    public Motion getMotion() {
        return m;
    }
}
