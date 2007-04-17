/** Sam Reid*/
package edu.colorado.phet.cck.model.components;

import edu.colorado.phet.cck.common.SimpleObservableDebug;
import edu.colorado.phet.cck.model.*;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.CompositeCircuitChangeListener;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common_cck.util.SimpleObserver;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:32:58 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public abstract class Branch extends SimpleObservableDebug {
    private double resistance = CCKModel.MIN_RESISTANCE;
    private double current;
    private double voltageDrop;
    private Junction startJunction;
    private Junction endJunction;
    private CompositeCircuitChangeListener circuitChangeListeners = new CompositeCircuitChangeListener();
    private ArrayList ivListeners = new ArrayList();
    private boolean isSelected = false;
    private boolean kirkhoffEnabled = true;
    private ArrayList flameListeners = new ArrayList();
    private boolean isOnFire = false;
    private boolean editing = false;
    private String name;/*For purposes of debugging.*/
//    private boolean currentDirectionForward = true;

    private static int indexCounter = 0;

    protected Branch( CircuitChangeListener listener ) {
        name = toLabel( indexCounter++ );
        setResistance( CCKModel.MIN_RESISTANCE );
        addKirkhoffListener( listener );
    }

    public Branch( CircuitChangeListener listener, Junction startJunction, Junction endJunction ) {
        this( listener );
        this.startJunction = startJunction;
        this.endJunction = endJunction;
        startJunction.addObserver( so );
        endJunction.addObserver( so );
    }

    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Sets a name which could be used for debugging.
     *
     * @param label
     */
    public void setName( String label ) {
        this.name = label;
    }

    //todo this could be awkward
    //if a junction changes, this branch has also changed.
    SimpleObserver so = new SimpleObserver() {
        public void update() {
            notifyObservers();
        }
    };

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected( boolean selected ) {
        isSelected = selected;
        notifyObservers();
    }

    public void removeFlameListener( FlameListener flameListener ) {
        flameListeners.remove( flameListener );
    }

    public static interface FlameListener {
        void flameStarted();

        void flameFinished();
    }

    public void addFlameListener( FlameListener flameListener ) {
        flameListeners.add( flameListener );
    }

    public void addCurrentVoltListener( CurrentVoltListener currentListener ) {
        ivListeners.add( currentListener );
    }

    public void removeCurrentVoltListener( CurrentVoltListener currentListener ) {
        ivListeners.remove( currentListener );
    }

    public void addKirkhoffListener( CircuitChangeListener circuitChangeListener ) {
        circuitChangeListeners.addCircuitChangeListener( circuitChangeListener );
    }

    public void addObserver( SimpleObserver so ) {
        super.addObserver( so );
    }

    public String toString() {
        return "Branch_" + name + "[" + startJunction.getLabel() + "," + endJunction.getLabel() + "] <" + getClass() + ">";
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
        if( resistance < 0 ) {
            throw new RuntimeException( "Resistance was < 0, value=" + resistance );
        }
        if( resistance < CCKModel.MIN_RESISTANCE ) {
            throw new RuntimeException( "Resistance was less than MIN, res=" + resistance + ", min=" + CCKModel.MIN_RESISTANCE );
        }
        if( resistance != this.resistance ) {
            this.resistance = resistance;
            notifyObservers();
            fireKirkhoffChange();
        }
    }

    public void notifyObservers() {
        SimpleObserver[] so = getObservers();
        for( int i = 0; i < so.length; i++ ) {
            SimpleObserver simpleObserver = so[i];
            if( simpleObserver instanceof Electron.Observer ) {
                Electron.Observer e = (Electron.Observer)simpleObserver;
                if( e.isDeleted() ) {
                    removeObserver( simpleObserver );
                }
            }
        }
        super.notifyObservers();
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
        boolean shouldBeOnFire = Math.abs( current ) > 10.0;
        if( shouldBeOnFire != isOnFire ) {
            this.isOnFire = shouldBeOnFire;
            if( isOnFire ) {
                for( int i = 0; i < flameListeners.size(); i++ ) {
                    FlameListener flameListener = (FlameListener)flameListeners.get( i );
                    flameListener.flameFinished();
                }
            }
            else {
                for( int i = 0; i < flameListeners.size(); i++ ) {
                    FlameListener flameListener = (FlameListener)flameListeners.get( i );
                    flameListener.flameStarted();
                }
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
        if( kirkhoffEnabled ) {
            circuitChangeListeners.circuitChanged();
        }
    }

    public Point2D getStartPoint() {
        return startJunction.getPosition();
    }

    public Point2D getEndPoint() {
        return endJunction.getPosition();
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
        if( startJunction != null ) {
            startJunction.removeObserver( so );
        }
        this.startJunction = newJunction;
        startJunction.addObserver( so );
    }

    public void translate( double dx, double dy ) {
        getStartJunction().translate( dx, dy );
        getEndJunction().translate( dx, dy );
    }

    public void setEndJunction( Junction newJunction ) {
        if( endJunction != null ) {
            endJunction.removeObserver( so );
        }
        this.endJunction = newJunction;
        endJunction.addObserver( so );
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
        if( getLength() == 0 ) {
            return getStartJunction().getPosition();
        }
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

    public String getName() {
        return name;
    }

    public boolean containsScalarLocation( double x ) {
        return x >= 0 && x <= getLength();
    }

    public double getAngle() {
        return new ImmutableVector2D.Double( getStartJunction().getPosition(), getEndJunction().getPosition() ).getAngle();
    }

    public Point2D getCenter() {
        return new Vector2D.Double( getStartJunction().getPosition(), getEndJunction().getPosition() ).getScaledInstance( .5 ).getDestination( getStartJunction().getPosition() );
    }

    public void delete() {
        removeAllObservers();
        endJunction.removeObserver( so );
        startJunction.removeObserver( so );
    }

    public void setKirkhoffEnabled( boolean kirkhoffEnabled ) {
        this.kirkhoffEnabled = kirkhoffEnabled;
    }

    public abstract Shape getShape();

    public boolean isOnFire() {
        return isOnFire;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing( boolean editing ) {
        if( this.editing != editing ) {
            this.editing = editing;
            notifyObservers();
        }
    }

    public boolean isEditable() {
        return true;
    }

//    /**
//     * Sets whether the arbitrary "direction of current" is forward or backwards.
//     *
//     * @param currentDirectionForward
//     */
//    public void setCurrentDirectionForward( boolean currentDirectionForward ) {
//        this.currentDirectionForward = currentDirectionForward;
//    }
//
//    /**
//     * Gets the current in the arbitrarily chosen "direction of current"
//     *
//     * @return
//     */
//    public double getForwardCurrent() {
//        return getCurrent() * ( currentDirectionForward ? 1.0 : -1.0 );
//    }

}
