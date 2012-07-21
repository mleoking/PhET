// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import edu.colorado.phet.beerslawlab.common.model.Movable;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Model of the dropper, contains solute in solution form (stock solution).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Dropper extends Movable {

    public final Property<Solute> solute;
    public final Property<Boolean> visible;
    public final Property<Boolean> on; // true if the dropper is dispensing solution
    public final Property<Boolean> enabled;
    public final Property<Boolean> empty;
    public final CompositeProperty<Double> flowRate; // L/sec

    public Dropper( Vector2D location, PBounds dragBounds, Property<Solute> solute, final double maxFlowRate ) {
        super( location, dragBounds );
        assert ( dragBounds.contains( location.toPoint2D() ) );

        this.solute = solute;
        this.visible = new Property<Boolean>( true );
        this.on = new Property<Boolean>( false );
        this.enabled = new Property<Boolean>( true );
        this.empty = new Property<Boolean>( false );

        // turn off the dropper when it's disabled
        this.enabled.addObserver( new SimpleObserver() {
            public void update() {
                if ( !Dropper.this.enabled.get() ) {
                    on.set( false );
                }
            }
        } );

        // Toggle the flow rate when the dropper is turned on/off.
        this.flowRate = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                return ( Dropper.this.on.get() ? maxFlowRate : 0d );
            }
        }, this.on );

        // when the dropper becomes empty, disable it.
        this.empty.addObserver( new SimpleObserver() {
            public void update() {
                if ( empty.get() ) {
                    enabled.set( false );
                }
            }
        } );
    }
}
