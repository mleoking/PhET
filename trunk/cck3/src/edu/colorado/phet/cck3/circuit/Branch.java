/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.particles.Electron;
import edu.colorado.phet.cck3.debug.SimpleObservableDebug;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:32:58 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class Branch extends SimpleObservableDebug {
//    public static final double WIRE_RESISTANCE_MIN = 0.0001;
    double resistance = CCK3Module.MIN_RESISTANCE;//WIRE_RESISTANCE_MIN;
    double current;
    double voltageDrop;
    private Junction startJunction;
    private Junction endJunction;
    private String label;
    private static int indexCounter = 0;

    private CompositeKirkhoffListener compositeKirkhoffListener = new CompositeKirkhoffListener();
    private ArrayList ivListeners = new ArrayList();
    private boolean isSelected = false;
    private boolean kirkhoffEnabled = true;

    protected Branch( KirkhoffListener listener ) {
        label = toLabel( indexCounter++ );
        setResistance( CCK3Module.MIN_RESISTANCE );
        addKirkhoffListener( listener );
    }

    public Branch( KirkhoffListener listener, Junction startJunction, Junction endJunction ) {
        this( listener );
        this.startJunction = startJunction;
        this.endJunction = endJunction;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected( boolean selected ) {
        isSelected = selected;
        notifyObservers();
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
        if( resistance < 0 ) {
            throw new RuntimeException( "Resistance was < 0, value=" + resistance );
        }
        if( resistance < CCK3Module.MIN_RESISTANCE ) {
            throw new RuntimeException( "Resistance was less than MIN, res=" + resistance + ", min=" + CCK3Module.MIN_RESISTANCE );
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
            compositeKirkhoffListener.circuitChanged();
        }
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

    public String getLabel() {
        return label;
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

    public void addAttributes( XMLElement branchElement ) {
    }

    public static Branch parseXML( IXMLElement xml, Junction startJunction, Junction endJunction, KirkhoffListener kl ) {
        return new Branch( kl, startJunction, endJunction );
    }

    public void delete() {
        removeAllObservers();
    }

    public void setKirkhoffEnabled( boolean kirkhoffEnabled ) {
        this.kirkhoffEnabled = kirkhoffEnabled;
    }
}
