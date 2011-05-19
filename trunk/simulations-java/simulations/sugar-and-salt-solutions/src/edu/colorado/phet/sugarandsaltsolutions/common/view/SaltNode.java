// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroCrystal;

/**
 * Graphical representation of a salt crystal
 *
 * @author Sam Reid
 */
public class SaltNode extends CrystalNode {

    //If we define each grain of solute as 5g, then the volume is 0.00231L for salt, and 0.00315L for sugar (i.e., the grain size is different for salt and sugar)
    //So the grain sizes should have roughly the same ratio of sizes
    //Note, this uses the volume ratios to depict linear length ratios, so won't be perfectly correct, but should be close enough
    private static final double relativeSize = 1.363636;

    public SaltNode( final ModelViewTransform transform, final MacroCrystal crystal, ObservableProperty<Color> color ) {
        super( transform, crystal, color, SugarNode.SIZE / relativeSize );
    }
}
