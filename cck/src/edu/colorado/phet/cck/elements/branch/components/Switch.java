/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch.components;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.xml.BranchData;
import edu.colorado.phet.cck.elements.xml.SwitchData;
import edu.colorado.phet.common.util.SimpleObserver;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 2:36:37 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class Switch extends Branch implements HasResistance {
    boolean open = true;
    ArrayList switchListeners = new ArrayList();
    private double resistance;

    public Switch( Circuit parent, double x1, double y1, double x2, double y2 ) {
        super( parent, x1, y1, x2, y2 );
        chooseResistance();
    }

    public Branch copy() {
        return new Switch( parent, getX1(), getY1(), getX2(), getY2() );
    }

    public BranchData toBranchData() {
        return new SwitchData( this );
    }

    public boolean isOpen() {
        return open;
    }

    public void addSwitchListener( SimpleObserver so ) {
        switchListeners.add( so );
    }

    public void setOpen( boolean open ) {
        this.open = open;
        chooseResistance();
        for( int i = 0; i < switchListeners.size(); i++ ) {
            SimpleObserver simpleObserver = (SimpleObserver)switchListeners.get( i );
            simpleObserver.update();
        }
        parent.fireConnectivityChanged();
    }

    private void chooseResistance() {
        if( open ) {
            resistance = Double.POSITIVE_INFINITY;
        }
        else {
            resistance = new Wire( parent, getX1(), getY1(), getX2(), getY2() ).getResistance();
        }
    }

    public double getResistance() {
        return resistance;
    }

    public void setResistance( double resistance ) {
        throw new RuntimeException( "Not supported" );
    }

}
