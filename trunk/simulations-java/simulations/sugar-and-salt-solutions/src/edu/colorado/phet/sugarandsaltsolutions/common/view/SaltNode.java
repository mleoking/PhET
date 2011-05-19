// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroSalt;

/**
 * Graphical representation of a salt crystal
 *
 * @author Sam Reid
 */
public class SaltNode extends CrystalNode {
    public SaltNode( final ModelViewTransform transform, final MacroSalt salt, ObservableProperty<Color> color ) {
        super( transform, salt, color, transform.modelToViewDeltaX( salt.length ) );
    }
}
