// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Properties that control things in the view.
 * Since this sim has a small number of controls, this is a union of all such properties in all modules.
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
    public final Property<Boolean> bondCharacterVisible;
    public final Property<Boolean> atomLabelsVisible;
    public final Property<Boolean> electronegativityTableVisible;

    public ViewProperties( SurfaceType surfaceType, boolean bondDipolesVisible, boolean molecularDipoleVisible,
                           boolean partialChargesVisible, boolean bondCharacterVisible, boolean atomLabelsVisible,
                           boolean electronegativityTableVisible ) {

        //Give property description for use in Sim data collection/processing
        this.surfaceType = new Property<SurfaceType>( "Surface type", surfaceType );
        this.bondDipolesVisible = new Property<Boolean>( "Bond dipoles visible", bondDipolesVisible );
        this.molecularDipoleVisible = new Property<Boolean>( "Molecular dipole visible", molecularDipoleVisible );
        this.partialChargesVisible = new Property<Boolean>( "Partial Charges Visible", partialChargesVisible );
        this.bondCharacterVisible = new Property<Boolean>( "Bond character visible", bondCharacterVisible );
        this.atomLabelsVisible = new Property<Boolean>( "Atom labels visible", atomLabelsVisible );
        this.electronegativityTableVisible = new Property<Boolean>( "Electronegativity table visible", electronegativityTableVisible );
    }

    public void reset() {
        surfaceType.reset();
        bondDipolesVisible.reset();
        molecularDipoleVisible.reset();
        partialChargesVisible.reset();
        bondCharacterVisible.reset();
        atomLabelsVisible.reset();
        electronegativityTableVisible.reset();
    }
}
