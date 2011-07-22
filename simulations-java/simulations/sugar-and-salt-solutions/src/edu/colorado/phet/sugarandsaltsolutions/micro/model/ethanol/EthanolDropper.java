// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.ethanol;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Dispenser;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Model for the ethanol dropper.
 *
 * @author Sam Reid
 */
public class EthanolDropper extends Dispenser<MicroModel> {
    public EthanolDropper( double x, double y, double angle, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, final DispenserType type ) {
        super( x, y, angle, beaker, moreAllowed, name, distanceScale, selectedType, type );
    }

    //TODO: implement this method
    @Override public void updateModel( MicroModel model ) {
    }

    @Override public PNode createNode( ModelViewTransform transform, double beakerHeight ) {
        return new EthanolDropperNode( transform, this, beakerHeight );
    }
}
