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

    // whether bonds should be colorized in the real molecule view
    public static final Property<Boolean> useColoredBonds = new Property<Boolean>( false );

    // number of samples (both directions) that are taken on spheres
    public static final Property<Integer> sphereSamples = new Property<Integer>( 32 );

    // number of samples taken radially on the cylinders (bonds)
    public static final Property<Integer> cylinderSamples = new Property<Integer>( 16 );

    // FOR DEV, not enabled: controls the threshold of brightness (0 to 1) where the bond angle's visibility will fade from visible to invisible
    public static final Property<Double> minimumBrightnessFade = new Property<Double>( 0.5 );
    public static final Property<Double> maximumBrightnessFade = new Property<Double>( 0.7 );

    // whether to disable showing the bond angles when it is not applicable
    public static final Property<Boolean> disableNAShowBondAngles = new Property<Boolean>( true );
}
