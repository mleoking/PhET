/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.CCKModule;
import edu.colorado.phet.cck3.circuit.CircuitChangeListener;
import edu.colorado.phet.cck3.circuit.DynamicBranch;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.common_cck.math.AbstractVector2D;
import edu.colorado.phet.common_cck.math.Vector2D;
import net.n3.nanoxml.IXMLElement;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 16, 2006
 * Time: 12:50:58 AM
 * Copyright (c) Jun 16, 2006 by Sam Reid
 */

public class Inductor extends CircuitComponent implements DynamicBranch {
//    private static final double DEFAULT_INDUCTANCE = 1.0;
    private static final double DEFAULT_INDUCTANCE = 10.0;
    private ArrayList listeners = new ArrayList();
    private double inductance = DEFAULT_INDUCTANCE;

    public Inductor( Point2D start, AbstractVector2D dir, double length, double height, CircuitChangeListener kl ) {
        super( kl, start, dir, length, height );
        setKirkhoffEnabled( false );
        setResistance( CCKModule.MIN_RESISTANCE );
        setKirkhoffEnabled( true );
    }

    public Inductor( CircuitChangeListener kl, Junction startJunction, Junction endjJunction, double length, double height ) {
        super( kl, startJunction, endjJunction, length, height );
    }

    public Inductor( double resistance ) {
        this( new Point2D.Double(), new Vector2D.Double(), 1, 1, new CircuitChangeListener() {
            public void circuitChanged() {
            }
        } );
        setKirkhoffEnabled( false );
        setResistance( resistance );
        setKirkhoffEnabled( true );
    }

    public void addAttributes( IXMLElement xml ) {
        xml.setAttribute( "resistance", getResistance() + "" );
    }

    public void setInductance( double inductance ) {
        this.inductance = inductance;
        notifyObservers();
        fireKirkhoffChange();
        notifyChargeChanged();
    }

    public void setVoltageDrop( double voltageDrop ) {
        super.setVoltageDrop( voltageDrop );
        notifyChargeChanged();
    }

    public void stepInTime( double dt ) {
    }

    public void resetDynamics() {
        setKirkhoffEnabled( false );
        setVoltageDrop( 0.0 );
        setCurrent( 0.0 );
        setKirkhoffEnabled( true );
    }

    public void setTime( double time ) {
    }

    public double getInductance() {
        return inductance;
    }

    public void discharge() {
        resetDynamics();
    }

    public static interface Listener {
        public void chargeChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyChargeChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.chargeChanged();
        }
    }

//    ArrayList currentHistory = new ArrayList();
//
//    public void setCurrent( double current ) {
//        currentHistory.add( new Double( current ) );
//        while( currentHistory.size() > 100 ) {
//            currentHistory.remove( 0 );
//        }
//        System.out.println( "currentHistory = " + currentHistory );
//        if( currentHistory.size() >= 50 && isHistoryBad() ) {
//            System.out.println( "Bad = "  );
//            super.setCurrent( 0.0 );
//            setVoltageDrop( 0.0 );
//        }
//        else {
//            super.setCurrent( current );
//        }
//    }
//
//    private boolean isHistoryBad() {
//        double a = ( (Double)currentHistory.get( 0 ) ).doubleValue();
//        double b = ( (Double)currentHistory.get( 1 ) ).doubleValue();
//        double c = ( (Double)currentHistory.get( 2 ) ).doubleValue();
//        double d = ( (Double)currentHistory.get( 3 ) ).doubleValue();
//        if( MathUtil.isApproxEqual( a, -c, 0.0001 ) && MathUtil.isApproxEqual( b, -d, 0.0001 ) ) {
//            return true;
//        }
//        return false;
//    }
}