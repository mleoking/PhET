// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Debugging utility to show the location of the faucet where the paricles will drain out
 *
 * @author Sam Reid
 */
public class DrainFaucetNodeLocationDebugger extends PNode {
    public DrainFaucetNodeLocationDebugger( final ModelViewTransform transform, final SugarAndSaltSolutionModel model ) {
        double length = 4;
        addChild( new PhetPPath( new Rectangle2D.Double( -length, -length, length * 2, length * 2 ), Color.red ) {{
            setOffset( transform.modelToView( model.getDrainFaucetLocation().toPoint2D() ) );
        }} );
    }
}
