package edu.colorado.phet.fractionsintro.buildafraction.model;

import fj.F;
import fj.Ord;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.common.util.DefaultP2;

/**
 * @author Sam Reid
 */
public @Data class DraggableFraction {
    public final FractionID id;
    public final DraggableObject draggableObject;

    //Double is the time attached.
    //TODO: Convert this DefaultP2 to a lombok named data class
    public final Option<DefaultP2<DraggableNumberID, Double>> numerator;
    public final Option<DefaultP2<DraggableNumberID, Double>> denominator;
    public static final F<DraggableFraction, ObjectID> ID = new F<DraggableFraction, ObjectID>() {
        @Override public ObjectID f( final DraggableFraction draggableFraction ) {
            return draggableFraction.getID();
        }
    };

    public FractionID getID() { return id; }

    public DraggableFraction translate( final Vector2D delta ) { return withDraggableObject( draggableObject.translate( delta ) ); }

    public DraggableFraction withDraggableObject( final DraggableObject draggableObject ) { return new DraggableFraction( id, draggableObject, numerator, denominator ); }

    public DraggableFraction withNumerator( final Option<DefaultP2<DraggableNumberID, Double>> numerator ) { return new DraggableFraction( id, draggableObject, numerator, denominator ); }

    public DraggableFraction withDenominator( final Option<DefaultP2<DraggableNumberID, Double>> denominator ) { return new DraggableFraction( id, draggableObject, numerator, denominator ); }

    public DraggableFraction withDragging( final boolean b ) { return withDraggableObject( draggableObject.withDragging( b ) ); }

    //Determine whether the draggable fraction has the same ID as the one specified (i.e. that they are referring to the same model instance).
    public boolean equalsID( final FractionID id ) { return this.id.equals( id ); }

    public Vector2D getPosition() { return draggableObject.position; }

    public Option<Double> getLastConnectionTime() {
        List<Double> times = List.nil();
        if ( numerator.isSome() ) { times = times.cons( numerator.some()._2() ); }
        if ( denominator.isSome() ) { times = times.cons( denominator.some()._2() ); }
        return times.length() > 0 ? Option.some( times.maximum( Ord.<Double>comparableOrd() ) ) : Option.<Double>none();
    }
}