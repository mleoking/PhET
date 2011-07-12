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

    // Choices for visual representations of the model.
    public enum ModelRepresentation {
        BALL_AND_STICK,
        ELECTROSTATIC_POTENTIAL,
        ELECTRON_DENSITY
    }

    public final Property<ModelRepresentation> modelRepresentation = new Property<ModelRepresentation>( ModelRepresentation.BALL_AND_STICK );
    public final Property<Boolean> bondDipolesVisible = new Property<Boolean>( false );
    public final Property<Boolean> moleculeDipoleVisible = new Property<Boolean>( false );
    public final Property<Boolean> partialChargesVisible = new Property<Boolean>( false );

    public void reset() {
        modelRepresentation.reset();
        bondDipolesVisible.reset();
        moleculeDipoleVisible.reset();
        partialChargesVisible.reset();
    }
}
