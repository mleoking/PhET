// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.ethanol;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Dispenser;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.view.EthanolDropperNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.model.property.Property.property;

/**
 * Model for the ethanol dropper.
 *
 * @author Sam Reid
 */
public class EthanolDropper extends Dispenser<MicroModel> {

    //True if the button has been pressed by the user and the dropper is emitting ethanol
    public final Property<Boolean> pressing = property( false );

    //Model height of the dropper in meters
    private double dropperHeight;

    //Number of time steps in which the dropper has emitted ethanol, so that it can be shut off after a short burst
    private int pressCounts;

    public EthanolDropper( double x, double y, double angle, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, final DispenserType type, MicroModel model ) {
        super( x, y, angle, beaker, moreAllowed, name, distanceScale, selectedType, type, model );
    }

    //If the user is pressing the dropper and it hasn't reached the limit, emit ethanol
    @Override public void updateModel() {
        if ( pressing.get() && moreAllowed.get() ) {
            model.addEthanol( center.get().plus( 0, dropperHeight / 2 ) );

            //Keep track of the number of particles emitted and shut off after a short burst
            pressCounts++;
            if ( pressCounts > 20 ) {
                pressing.set( false );
            }
        }
    }

    //Sets the model value of the dropper height in meters so the drops can be placed at the end of the dropper
    public void setDropperHeight( double dropperHeight ) {
        this.dropperHeight = dropperHeight;
    }

    //Create the graphic for this model element
    @Override public PNode createNode( ModelViewTransform transform, double beakerHeight ) {
        return new EthanolDropperNode( transform, this, beakerHeight );
    }

    //Start the dropper emitting ethanol fluid
    public void startDropping() {
        pressing.set( true );
        pressCounts = 0;
    }
}