/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:32:58 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class Branch extends SimpleObservable {
    double resistance;
    double current;
    double voltageDrop;
    private Junction startJunction;
    private Junction endJunction;
    String label;
    private static int indexCounter = 0;
    private static final double WIRE_RESISTANCE = 0.0001;
    CompositeKirkhoffListener compositeKirkhoffListener = new CompositeKirkhoffListener();
    ArrayList ivListeners = new ArrayList();

    protected Branch( KirkhoffListener listener ) {
        label = toLabel( indexCounter++ );
        setResistance( WIRE_RESISTANCE );
        addKirkhoffListener( listener );
    }

    public Branch( KirkhoffListener listener, Junction startJunction, Junction endJunction ) {
        this( listener );
        this.startJunction = startJunction;
        this.endJunction = endJunction;
    }

    public void addCurrentVoltListener( CurrentVoltListener currentListener ) {
        ivListeners.add( currentListener );
    }

    public void addKirkhoffListener( KirkhoffListener kirkhoffListener ) {
        compositeKirkhoffListener.addKirkhoffListener( kirkhoffListener );
    }

    public void addObserver( SimpleObserver so ) {
        super.addObserver( so );
    }

    public String toString() {
        return "Branch_" + label + "[" + startJunction.getLabel() + "," + endJunction.getLabel() + "]";
    }

    public AbstractVector2D getDirectionVector() {
        return new ImmutableVector2D.Double( startJunction.getPosition(), endJunction.getPosition() );
    }

    public double getResistance() {
        return resistance;
    }

    public double getCurrent() {
        return current;
    }

    public double getVoltageDrop() {
        return voltageDrop;
    }

    public void setResistance( double resistance ) {
        this.resistance = resistance;
        notifyObservers();
        fireKirkhoffChange();
    }

    public void setCurrent( double current ) {
        if( this.current != current ) {
            this.current = current;
            notifyObservers();
            for( int i = 0; i < ivListeners.size(); i++ ) {
                CurrentVoltListener listener = (CurrentVoltListener)ivListeners.get( i );
                listener.currentOrVoltageChanged( this );
            }
        }
    }

    public void setVoltageDrop( double voltageDrop ) {
        if( this.voltageDrop != voltageDrop ) {
            this.voltageDrop = voltageDrop;
            notifyObservers();
            for( int i = 0; i < ivListeners.size(); i++ ) {
                CurrentVoltListener currentVoltListener = (CurrentVoltListener)ivListeners.get( i );
                currentVoltListener.currentOrVoltageChanged( this );
            }
        }
    }

    public void fireKirkhoffChange() {
        compositeKirkhoffListener.circuitChanged();
    }

    public double getX1() {
        return startJunction.getX();
    }

    public double getY1() {
        return startJunction.getY();
    }

    public double getX2() {
        return endJunction.getX();
    }

    public double getY2() {
        return endJunction.getY();
    }

    public Junction getStartJunction() {
        return startJunction;
    }

    public Junction getEndJunction() {
        return endJunction;
    }

    public boolean hasJunction( Junction junction ) {
        return endJunction == junction || startJunction == junction;
    }

    public Junction opposite( Junction a ) {
        if( startJunction == a ) {
            return endJunction;
        }
        else if( endJunction == a ) {
            return startJunction;
        }
        else {
            throw new RuntimeException( "No such junction: " + a );
        }
    }

    public void setStartJunction( Junction newJunction ) {
        this.startJunction = newJunction;
    }

    public void setEndJunction( Junction newJunction ) {
        this.endJunction = newJunction;
    }

    public Junction[] getJunctions() {
        return new Junction[]{startJunction, endJunction};
    }

    public void replaceJunction( Junction junction, Junction newJ ) {
        if( junction == startJunction ) {
            setStartJunction( newJ );
        }
        else if( junction == endJunction ) {
            setEndJunction( newJ );
        }
        else {
            throw new RuntimeException( "No such junction." );
        }
    }

    public double getLength() {
        return getStartJunction().getDistance( getEndJunction() );
    }

    public Point2D getPosition( double x ) {
        AbstractVector2D vec = new Vector2D.Double( getStartJunction().getPosition(), getEndJunction().getPosition() ).getInstanceOfMagnitude( x );
        return vec.getDestination( getStartJunction().getPosition() );
    }

    private static String toLabel( int label ) {
        char ch = 'a';
        ch += label % 26;
        int val = label / 26;
        String out = "";
        if( val == 0 ) {
            out = "" + ch;
        }
        else {
            out = "" + ch + "_" + val;
        }
        if( out == null ) {
            throw new RuntimeException( "Null string." );
        }
        return out;
    }

    public String getLabel() {
        return label;
    }

    public boolean containsScalarLocation( double x ) {
        return x >= 0 && x <= getLength();
    }

    public double getAngle() {
        return new ImmutableVector2D.Double( getStartJunction().getPosition(), getEndJunction().getPosition() ).getAngle();
    }

}
