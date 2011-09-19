// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Properties that control things in the view.
 * This is a union of all such properties in all modules.
 * Properties that are irrelevant for a module are simply ignored by that module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewProperties implements Resettable {

    public static enum SurfaceType {NONE, ELECTROSTATIC_POTENTIAL, ELECTRON_DENSITY}

    public final Property<SurfaceType> surfaceType;
    public final Property<Boolean> bondDipolesVisible;
    public final Property<Boolean> molecularDipoleVisible;
    public final Property<Boolean> partialChargesVisible;
    public final Property<Boolean> bondTypeVisible;
    public final Property<Boolean> atomLabelsVisible;
    public final Property<Boolean> electronegativityTableVisible;

    public ViewProperties( SurfaceType surfaceType, boolean bondDipolesVisible, boolean molecularDipoleVisible,
                           boolean partialChargesVisible, boolean bondTypeVisible, boolean atomLabelsVisible,
                           boolean electronegativityTableVisible ) {
        this.surfaceType = new Property<SurfaceType>( surfaceType );
        this.bondDipolesVisible = new Property<Boolean>( bondDipolesVisible );
        this.molecularDipoleVisible = new Property<Boolean>( molecularDipoleVisible );
        this.partialChargesVisible = new Property<Boolean>( partialChargesVisible );
        this.bondTypeVisible = new Property<Boolean>( bondTypeVisible );
        this.atomLabelsVisible = new Property<Boolean>( atomLabelsVisible );
        this.electronegativityTableVisible = new Property<Boolean>( electronegativityTableVisible );
    }

    public void reset() {
        surfaceType.reset();
        bondDipolesVisible.reset();
        molecularDipoleVisible.reset();
        partialChargesVisible.reset();
        bondTypeVisible.reset();
        atomLabelsVisible.reset();
        electronegativityTableVisible.reset();
    }
}
