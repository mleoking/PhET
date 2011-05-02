// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property2.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * @author Sam Reid
 */
public class SugarAndSaltSolutionModel {
    public final double width = 1.04;//visible width in meters
    public final double height = 0.7;//visible height in meters

    //Center the beaker's base at x=0 and have it go halfway up the screen
    public final double beakerWidth = width * 0.6;
    public final double beakerX = -beakerWidth / 2;
    public final double beakerHeight = height * 0.5;

    public final Beaker beaker = new Beaker( beakerX, 0, beakerWidth, beakerHeight );//The beaker into which you can add water, salt and sugar.
    public final Water water = new Water( beaker );
    public final Property<Double> inputFlowRate = new Property<Double>( 0.0 );//rate that water flows into the beaker in m^3/s
    public final Property<Double> outputFlowRate = new Property<Double>( 0.0 );//rate that water flows out of the beaker in m^3/s
    public final ConstantDtClock clock;

    //Sugar and its listeners
    public final ArrayList<Sugar> sugarList = new ArrayList<Sugar>();//The sugar crystals that haven't been dissolved
    public final ArrayList<VoidFunction1<Sugar>> sugarAddedListeners = new ArrayList<VoidFunction1<Sugar>>();//Listeners for when sugar crystals are added

    //Salt and its listeners
    public final ArrayList<Salt> saltList = new ArrayList<Salt>();//The salt crystals that haven't been dissolved
    public final ArrayList<VoidFunction1<Salt>> saltAddedListeners = new ArrayList<VoidFunction1<Salt>>();//Listeners for when salt crystals are added

    private ImmutableVector2D gravity = new ImmutableVector2D( 0, -9.8 );//Force due to gravity near the surface of the earth

    private static final double FLOW_SCALE = 0.02;//Flow controls vary between 0 and 1, this scales it down to a good model value

    public SugarAndSaltSolutionModel() {
        clock = new ConstantDtClock( 30 );

        //Wire up to the clock so we can update when it ticks
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateModel( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    //Adds the specified Sugar crystal to the model
    public void addSugar( Sugar sugar ) {
        sugarList.add( sugar );
        for ( VoidFunction1<Sugar> listener : sugarAddedListeners ) {
            listener.apply( sugar );
        }
    }

    //Adds the specified salt crystal to the model
    public void addSalt( Salt salt ) {
        this.saltList.add( salt );
        for ( VoidFunction1<Salt> listener : saltAddedListeners ) {
            listener.apply( salt );
        }
    }

    //Update the model when the clock ticks
    private void updateModel( double dt ) {
        //Change the water volume based on input and output flow
        double newVolume = water.volume.getValue() + dt * ( inputFlowRate.getValue() - outputFlowRate.getValue() ) * FLOW_SCALE;

        //Turn off the input flow if the beaker would overflow
        if ( newVolume >= beaker.getMaxFluidVolume() ) {
            inputFlowRate.setValue( 0.0 );
        }

        //Update the water volume
        water.volume.setValue( MathUtil.clamp( 0, newVolume, beaker.getMaxFluidVolume() ) );

        //Move about the sugar and salt crystals
        ArrayList<Salt> toRemove = new ArrayList<Salt>();
        for ( Salt salt : saltList ) {
            //slow the motion down a little bit or it moves too fast
            salt.stepInTime( gravity.times( salt.mass ), dt / 10 );

            //If the salt hits the water, absorb it
            if ( water.getShape().getBounds2D().contains( salt.position.getValue().toPoint2D() ) ) {
                toRemove.add( salt );
            }
        }
        //Remove the salt crystals that hit the water
        for ( Salt salt : toRemove ) {
            salt.remove();
            saltList.remove( salt );
            //TODO: increase salt concentration in the water
        }
    }

    public void addSaltAddedListener( VoidFunction1<Salt> e ) {
        saltAddedListeners.add( e );
    }

    public void addSugarAddedListener( VoidFunction1<Sugar> e ) {
        sugarAddedListeners.add( e );
    }
}