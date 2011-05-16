// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroSugar;

/**
 * Graphical representation of a sugar crystal
 *
 * @author Sam Reid
 */
public class SugarNode extends CrystalNode {
    public SugarNode( final ModelViewTransform transform, final MacroSugar sugar, ObservableProperty<Color> color ) {
        super( transform, sugar, color );
    }
}
