// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Properties that control things in the view.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewProperties implements Resettable {

    public static enum IsosurfaceType {NONE, ELECTROSTATIC_POTENTIAL, ELECTRON_DENSITY}

    ;

    public final Property<IsosurfaceType> isosurfaceType = new Property<IsosurfaceType>( IsosurfaceType.NONE );
    public final Property<Boolean> bondDipolesVisible = new Property<Boolean>( false );
    public final Property<Boolean> molecularDipoleVisible = new Property<Boolean>( false );
    public final Property<Boolean> partialChargesVisible = new Property<Boolean>( false );
    public final Property<Boolean> bondTypeVisible = new Property<Boolean>( false );
    public final Property<Boolean> atomLabelsVisible = new Property<Boolean>( true );

    public void reset() {
        isosurfaceType.reset();
        bondDipolesVisible.reset();
        molecularDipoleVisible.reset();
        partialChargesVisible.reset();
        bondTypeVisible.reset();
        atomLabelsVisible.reset();
    }
}
