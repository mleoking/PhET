// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Debugging utility to show the location of the faucet where the particles will drain out
 *
 * @author Sam Reid
 */
public class DrainFaucetNodeLocationDebugger extends PNode {

    //Flag to indicate whether the debugging is enabled
    private static final boolean enabled = true;

    public DrainFaucetNodeLocationDebugger( final ModelViewTransform transform, final SugarAndSaltSolutionModel model ) {
        if ( enabled ) {
            double length = 4;

            //Show the location where particles enter the drain faucet
            addChild( new PhetPPath( new Rectangle2D.Double( -length, -length, length * 2, length * 2 ), Color.red ) {{

                //Whenever the model solution changes, the drain point could change, so watch it for updates
                model.solution.shape.addObserver( new SimpleObserver() {
                    public void update() {
                        setOffset( transform.modelToView( model.getDrainFaucetMetrics().getInputPoint().toPoint2D() ) );
                    }
                } );
            }} );

            //Show the location where particles leave through the drain faucet
            addChild( new PhetPPath( new Rectangle2D.Double( -length, -length, length * 2, length * 2 ), Color.red ) {{
                setOffset( transform.modelToView( model.getDrainFaucetMetrics().outputPoint.toPoint2D() ) );
            }} );
        }
    }
}