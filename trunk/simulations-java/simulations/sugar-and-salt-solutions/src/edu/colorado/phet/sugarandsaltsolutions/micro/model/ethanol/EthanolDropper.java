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
    public Property<Boolean> pressing = property( false );
    private double dropperHeight;

    public EthanolDropper( double x, double y, double angle, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, final DispenserType type ) {
        super( x, y, angle, beaker, moreAllowed, name, distanceScale, selectedType, type );
    }

    //If the user is pressing the dropper, emit ethanol
    @Override public void updateModel( MicroModel model ) {
        if ( pressing.get() ) {
            model.addEthanol( center.get().plus( 0, dropperHeight / 2 ) );
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
}