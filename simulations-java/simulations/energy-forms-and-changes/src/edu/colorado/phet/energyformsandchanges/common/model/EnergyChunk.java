// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common.model;

import javafx.beans.property.BooleanPropertyBase;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.energyformsandchanges.intro.model.FadeState;

/**
 * Class that represents a chunk of energy in the view.
 *
 * @author John Blanco
 */
public class EnergyChunk {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double FADE_RATE = 1; // Proportion per second.
    private static final int TIMER_DELAY = 30; // In milliseconds.
    private static final BooleanProperty ALWAYS_VISIBLE = new BooleanProperty( true );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Position in model space.
    public final Property<Vector2D> position;
    public final Property<Double> zPosition = new Property<Double>( 0.0 ); // Used for some simple 3D layering effects.

    // TODO: Note to self - I (jblanco) was not sure if having a velocity here made sense, since in many cases it is not used to position the chunk.  Decide eventually if it's better to keep this externally (in a map or something).
    // Velocity.  At the time of this writing, this is only used in the
    // algorithms that distribute energy chunks in a container.
    private Vector2D velocity = new Vector2D( 0, 0 ); // In meters/sec.

    // Property that controls visibility in view.
    public final ObservableProperty<Boolean> visible;

    // Strength of existence, used for fading in and out.  Range is from 0 to 1.
    private final Property<Double> existenceStrength = new Property<Double>( 1.0 );

    // Fade state, so that we know which way it is going.
    private FadeState fadeState = FadeState.FULLY_FADED_IN;

    // Energy type.  This can change during the life of the energy chunk.
    public Property<EnergyType> energyType = new Property<EnergyType>( null );

    // Timer for fade in/out.
    private Timer fadeTimer;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public EnergyChunk( EnergyType initialEnergyType ) {
        this( initialEnergyType, new Vector2D( 0, 0 ), ALWAYS_VISIBLE );
    }

    public EnergyChunk( EnergyType initialEnergyType, double x, double y, ObservableProperty<Boolean> visibilityControl ) {
        this( initialEnergyType, new Vector2D( x, y ), visibilityControl );
    }

    public EnergyChunk( EnergyType initialEnergyType, Vector2D initialPosition, ObservableProperty<Boolean> visibilityControl ) {
        this( initialEnergyType, initialPosition, new Vector2D( 0, 0 ), visibilityControl );
    }

    public EnergyChunk( EnergyType initialEnergyType, Vector2D initialPosition, Vector2D initialVelocity, ObservableProperty<Boolean> visibilityControl ) {
        this.position = new Property<Vector2D>( initialPosition );
        this.energyType.set( initialEnergyType );
        this.visible = visibilityControl;
        this.velocity = initialVelocity;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    private void updateFadeState( double dt ) {
        switch( fadeState ) {
            case FADING_IN:
                existenceStrength.set( Math.min( existenceStrength.get() + FADE_RATE * dt, 1 ) );
                if ( existenceStrength.get() == 1 ) {
                    fadeState = FadeState.FULLY_FADED_IN;
                    fadeTimer.stop();
                }
                break;
            case FADING_OUT:
                existenceStrength.set( Math.max( existenceStrength.get() - FADE_RATE * dt, 0 ) );
                if ( existenceStrength.get() == 0 ) {
                    fadeState = FadeState.FULLY_FADED_OUT;
                    fadeTimer.stop();
                }
                break;
            case FULLY_FADED_IN:
                // State consistency checking.
                assert existenceStrength.get() == 1;
                break;
            case FULLY_FADED_OUT:
                // State consistency checking.
                assert existenceStrength.get() == 0;
                break;
        }
    }

    public void translate( Vector2D movement ) {
        position.set( position.get().plus( movement ) );
    }

    public void translateBasedOnVelocity( double time ) {
        translate( velocity.times( time ) );
    }

    public Vector2D getVelocity() {
        return new Vector2D( velocity );
    }

    public void setVelocity( double x, double y ) {
        velocity = new Vector2D( x, y );
    }

    public void setVelocity( Vector2D newVelocity ) {
        setVelocity( newVelocity.x, newVelocity.y );
    }

    public ObservableProperty<Double> getExistenceStrength() {
        return existenceStrength;
    }

    public void startFadeOut() {
        fadeState = FadeState.FADING_OUT;
        fadeTimer = new Timer( TIMER_DELAY, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateFadeState( (double) TIMER_DELAY / 1000 );
            }
        } );
        fadeTimer.restart();
    }
}
