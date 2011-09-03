// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Global properties. Of particular interest, properties that are modified by developer controls
 */
public class MoleculeShapesProperties {
    // whether existing atoms (when behind the molecule center) are dragged around the back. If false, they pop to the front-most mouse position
    public static final Property<Boolean> allowDraggingBehind = new Property<Boolean>( true );

    // sets the cursor to a "move" cursor when rotating a molecule
    public static final Property<Boolean> useRotationCursor = new Property<Boolean>( true );

    // whether bond angles should be shown
    public static final Property<Boolean> showBondAngles = new Property<Boolean>( false );

    // whether we should show bond angles between lone pairs
    public static final Property<Boolean> allowAnglesBetweenLonePairs = new Property<Boolean>( false );

    // whether bonds should be colorized in the real molecule view
    public static final Property<Boolean> useColoredBonds = new Property<Boolean>( false );

    // number of samples (both directions) that are taken on spheres
    public static final Property<Integer> sphereSamples = new Property<Integer>( 32 );

    // number of samples taken radially on the cylinders (bonds)
    public static final Property<Integer> cylinderSamples = new Property<Integer>( 16 );
}
