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

    public static enum SurfaceType {NONE, ELECTROSTATIC_POTENTIAL, ELECTRON_DENSITY}

    public final Property<SurfaceType> isosurfaceType;
    public final Property<Boolean> bondDipolesVisible;
    public final Property<Boolean> molecularDipoleVisible;
    public final Property<Boolean> partialChargesVisible;
    public final Property<Boolean> bondTypeVisible;
    public final Property<Boolean> atomLabelsVisible;

    public ViewProperties( SurfaceType isosurfaceType, boolean bondDipolesVisible, boolean molecularDipoleVisible,
                           boolean partialChargesVisible, boolean bondTypeVisible, boolean atomLabelsVisible ) {
        this.isosurfaceType = new Property<SurfaceType>( isosurfaceType );
        this.bondDipolesVisible = new Property<Boolean>( bondDipolesVisible );
        this.molecularDipoleVisible = new Property<Boolean>( molecularDipoleVisible );
        this.partialChargesVisible = new Property<Boolean>( partialChargesVisible );
        this.bondTypeVisible = new Property<Boolean>( bondTypeVisible );
        this.atomLabelsVisible = new Property<Boolean>( atomLabelsVisible );
    }

    public void reset() {
        isosurfaceType.reset();
        bondDipolesVisible.reset();
        molecularDipoleVisible.reset();
        partialChargesVisible.reset();
        bondTypeVisible.reset();
        atomLabelsVisible.reset();
    }
}
