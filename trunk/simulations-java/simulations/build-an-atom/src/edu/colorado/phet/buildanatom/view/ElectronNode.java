package edu.colorado.phet.buildanatom.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.Electron;
import edu.colorado.phet.buildanatom.model.SubatomicParticle;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;

/**
 * @author Sam Reid
 */
public class ElectronNode extends SubatomicParticleNode {
    public static final double ELECTRON_DIAMETER = Electron.ELECTRON_RADIUS*2;
    public static final Color ELECTRON_COLOR = Color.BLUE;
    public static final Color ELECTRON_HILITE_COLOR = Color.CYAN;
    public static final Paint ELECTRON_ROUND_GRADIENT = new RoundGradientPaint( -ELECTRON_DIAMETER / 3,
                                                                                -ELECTRON_DIAMETER / 3, ELECTRON_HILITE_COLOR, new Point2D.Double( ELECTRON_DIAMETER / 2, ELECTRON_DIAMETER / 2 ),
                                                                                ELECTRON_COLOR );
    public ElectronNode( ModelViewTransform2D mvt, SubatomicParticle subatomicParticle ) {
        super( mvt, subatomicParticle, ELECTRON_ROUND_GRADIENT );
    }
}