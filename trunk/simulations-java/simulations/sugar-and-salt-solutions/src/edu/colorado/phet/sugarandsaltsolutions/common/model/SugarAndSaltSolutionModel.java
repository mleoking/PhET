// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.sugarandsaltsolutions.common.view.FaucetMetrics;
import edu.colorado.phet.sugarandsaltsolutions.common.view.VerticalRangeContains;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.canvasSize;

/**
 * Base class model for Sugar and Salt Solutions, which keeps track of the physical model as well as the MVC model for view components (such as whether certain components are enabled).
 *
 * @author Sam Reid
 */
public abstract class SugarAndSaltSolutionModel extends AbstractSugarAndSaltSolutionsModel {

    //Use the same aspect ratio as the view to minimize insets with blank regions
    private final double aspectRatio = canvasSize.getWidth() / canvasSize.getHeight();

    //Model for input and output flows
    public final Property<Double> inputFlowRate = new Property<Double>( 0.0 );//rate that water flows into the beaker, between 0 and 1
    public final Property<Double> outputFlowRate = new Property<Double>( 0.0 );//rate that water flows out of the beaker, between 0 and 1

    //Flow controls vary between 0 and 1, this scales it down to a good model value
    public final double faucetFlowRate;

    public final double drainPipeBottomY;
    public final double drainPipeTopY;

    //The amount to scale model translations so that micro tab emits solute at the appropriate time.  Without this factor, the tiny (1E-9 meters) drag motion in the Micro tab wouldn't be enough to emit solute
    public final double distanceScale;

    //Which dispenser the user has selected
    public final Property<DispenserType> dispenserType = new Property<DispenserType>( SALT );

    //True if the values should be shown in the user interface
    public final Property<Boolean> showConcentrationValues = new Property<Boolean>( false );

    //volume in SI (m^3).  Start at 1 L (halfway up the 2L beaker).  Note that 0.001 cubic meters = 1L
    public final DoubleProperty waterVolume;

    //Beaker model
    public final Beaker beaker;

    //Max amount of water before the beaker overflows
    public final double maxWater;

    //Flag to indicate whether there is enough solution to flow through the lower drain.
    public final VerticalRangeContains lowerFaucetCanDrain;

    //User setting: whether the concentration bar chart should be shown
    public final Property<Boolean> showConcentrationBarChart;

    //Part of the model that must be visible within the view
    public final ImmutableRectangle2D visibleRegion;

    //Observable flag which determines whether the beaker is full of solution, for purposes of preventing overflow
    public final ObservableProperty<Boolean> beakerFull;

    //Model location (in meters) of where water will flow out the drain (both toward and away from drain faucet), set by the view since view locations are chosen first for consistency across tabs
    private FaucetMetrics drainFaucetMetrics = new FaucetMetrics( this, ZERO, ZERO, 0 );
    private FaucetMetrics inputFaucetMetrics = new FaucetMetrics( this, ZERO, ZERO, 0 );

    //The shape of the input and output water.  The Shape of the water draining out the output faucet is also needed for purposes of determining whether there is an electrical connection for the conductivity tester
    public final Property<Shape> inputWater = new Property<Shape>( new Area() );
    public final Property<Shape> outputWater = new Property<Shape>( new Area() );

    //True if there are any solutes (i.e., if moles of salt or moles of sugar is greater than zero).  This is used to show/hide the "remove solutes" button
    public abstract ObservableProperty<Boolean> getAnySolutes();

    //Models for dispensers that can be used to add solute to the beaker solution
    public final ArrayList<Dispenser> dispensers;

    //Rate at which liquid (but no solutes) leaves the model
    public final SettableProperty<Integer> evaporationRate = new Property<Integer>( 0 );//Between 0 and 100

    //Rate at which liquid evaporates
    public final double evaporationRateScale;

    //The elapsed running time of the model
    protected double time;

    //Solution model, the fluid + any dissolved solutes
    public final Solution solution;

    public SugarAndSaltSolutionModel( final ConstantDtClock clock,

                                      //Dimensions of the beaker
                                      final BeakerDimension beakerDimension,
                                      double faucetFlowRate,
                                      final double drainPipeBottomY,
                                      final double drainPipeTopY,

                                      //Scale to help accommodate micro tab, for Macro tab the scale is 1.0
                                      double distanceScale ) {
        super( clock );
        this.faucetFlowRate = faucetFlowRate;
        this.drainPipeBottomY = drainPipeBottomY;
        this.drainPipeTopY = drainPipeTopY;
        this.distanceScale = distanceScale;
        this.evaporationRateScale = faucetFlowRate / 300.0;//Scaled down since the evaporation control rate is 100 times bigger than flow scales

        //Start the water halfway up the beaker
        waterVolume = new DoubleProperty( beakerDimension.getVolume() / 2 );

        //Inset so the beaker doesn't touch the edge of the model bounds
        final double inset = beakerDimension.width * 0.1;
        final double modelWidth = beakerDimension.width + inset * 2;

        //Beaker model
        beaker = new Beaker( beakerDimension.x, 0, beakerDimension.width, beakerDimension.height, beakerDimension.depth, beakerDimension.wallThickness );

        //Visible model region: a bit bigger than the beaker, used to set the stage aspect ratio in the canvas
        visibleRegion = new ImmutableRectangle2D( -modelWidth / 2, -inset, modelWidth, modelWidth / aspectRatio );

        //Set a max amount of water that the user can add to the system so they can't overflow it
        maxWater = beaker.getMaxFluidVolume();

        //User setting: whether the concentration bar chart should be shown
        showConcentrationBarChart = new Property<Boolean>( true );

        //Create the list of dispensers
        dispensers = new ArrayList<Dispenser>();

        //Sets the shape of the water into the beaker
        inputFlowRate.addObserver( new VoidFunction1<Double>() {
            public void apply( Double rate ) {
                double width = rate * inputFaucetMetrics.faucetWidth;
                double height = inputFaucetMetrics.outputPoint.getY();//assumes beaker floor is at y=0
                inputWater.set( new Rectangle2D.Double( inputFaucetMetrics.outputPoint.getX() - width / 2, inputFaucetMetrics.outputPoint.getY() - height, width, height ) );
            }
        } );

        //Sets the shape of the water flowing out of the beaker, changing the shape updates the brightness of the conductivity tester in the macro tab
        outputFlowRate.addObserver( new VoidFunction1<Double>() {
            public void apply( Double rate ) {
                double width = rate * drainFaucetMetrics.faucetWidth;
                double height = beakerDimension.height * 2;
                outputWater.set( new Rectangle2D.Double( drainFaucetMetrics.outputPoint.getX() - width / 2, drainFaucetMetrics.outputPoint.getY() - height, width, height ) );
            }
        } );

        //Create the solution, which sits atop the solid precipitate (if any)
        solution = new Solution( waterVolume, beaker );

        //Convenience composite properties for determining whether the beaker is full or empty so we can shut off the faucets when necessary
        beakerFull = solution.volume.greaterThanOrEqualTo( maxWater );

        //Determine if the lower faucet is allowed to let fluid flow out.  It can if any part of the fluid overlaps any part of the pipe range.
        //This logic is used in the model update step to determine if water can flow out, as well as in the user interface to determine if the user can turn on the output faucet
        lowerFaucetCanDrain = new VerticalRangeContains( solution.shape, drainPipeBottomY, drainPipeTopY );
    }

    //Callback when water has evaporated from the solution
    protected void waterEvaporated( double evaporatedWater ) {
        //Nothing to do in the base class
    }

    //Reset the model state
    public void reset() {
        resetWater();
        for ( Dispenser dispenser : dispensers ) {
            dispenser.reset();
        }
        dispenserType.reset();
        showConcentrationValues.reset();
        showConcentrationBarChart.reset();

        notifyReset();
    }

    //Reset the water volume to the initial value and stop the flow rate for input and output faucets
    protected void resetWater() {
        waterVolume.reset();
        inputFlowRate.reset();
        outputFlowRate.reset();
    }

    //Determine if any salt can be removed for purposes of displaying a "remove salt" button
    public abstract ObservableProperty<Boolean> isAnySaltToRemove();

    //Determine if any sugar can be removed for purposes of displaying a "remove sugar" button
    public abstract ObservableProperty<Boolean> isAnySugarToRemove();

    //Gets the elapsed time of the model in seconds
    public double getTime() {
        return time;
    }

    //Get the location of the drain where particles will flow toward and out, in absolute coordinates, in meters
    public FaucetMetrics getDrainFaucetMetrics() {
        return drainFaucetMetrics;
    }

    //Set the location where particles will flow out the drain, set by the view since view locations are chosen first for consistency across tabs
    public void setDrainFaucetMetrics( FaucetMetrics faucetMetrics ) {
        this.drainFaucetMetrics = faucetMetrics;
    }

    //Set the location where particles will flow out the drain, set by the view since view locations are chosen first for consistency across tabs
    public void setInputFaucetMetrics( FaucetMetrics faucetMetrics ) {
        this.inputFaucetMetrics = faucetMetrics;
    }

    //Update the model when the clock ticks, and return the amount of drained water (in meters cubed) so that subclasses like MacroModel can decrease the amount of dissolved solutes
    protected double updateModel( double dt ) {
        time += dt;

        //Add any new crystals from the salt & sugar dispensers
        for ( Dispenser dispenser : dispensers ) {
            dispenser.updateModel();
        }

        //Change the water volume based on input and output flow
        double inputWater = dt * inputFlowRate.get() * faucetFlowRate;
        double drainedWater = dt * outputFlowRate.get() * faucetFlowRate;
        double evaporatedWater = dt * evaporationRate.get() * evaporationRateScale;

        //Compute the new water volume, but making sure it doesn't overflow or underflow.
        //If we rewrite the model to account for solute volume displacement, this computation should account for the solution volume, not the water volume
        double newVolume = waterVolume.get() + inputWater - drainedWater - evaporatedWater;
        if ( newVolume > maxWater ) {
            inputWater = maxWater + drainedWater + evaporatedWater - waterVolume.get();
        }
        //Only allow drain to use up all the water if user is draining the liquid
        else if ( newVolume < 0 && outputFlowRate.get() > 0 ) {
            drainedWater = inputWater + waterVolume.get();
        }
        //Conversely, only allow evaporated water to use up all remaining water if the user is evaporating anything
        else if ( newVolume < 0 && evaporationRate.get() > 0 ) {
            evaporatedWater = inputWater + waterVolume.get();
        }
        //Note that the user can't be both evaporating and draining fluid at the same time, since the controls are one-at-a-time controls.
        //This simplifies the logic here.

        //Set the true value of the new volume based on clamped inputs and outputs
        newVolume = waterVolume.get() + inputWater - drainedWater - evaporatedWater;

        //Turn off the input flow if the beaker would overflow
        if ( newVolume >= maxWater ) {
            inputFlowRate.set( 0.0 );
        }

        //Turn off the output flow if no water is adjacent to it
        if ( !lowerFaucetCanDrain.get() ) {
            outputFlowRate.set( 0.0 );
        }

        //Turn off evaporation if beaker is empty of water
        if ( newVolume <= 0 ) {
            evaporationRate.set( 0 );
        }

        //Update the water volume
        waterVolume.set( newVolume );

        //Notify subclasses that water evaporated in case they need to update anything
        if ( evaporatedWater > 0 ) {
            waterEvaporated( evaporatedWater );
        }

        return drainedWater;
    }
}