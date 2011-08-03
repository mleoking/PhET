// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.CrystallizationMatch;
import edu.umd.cs.piccolo.PNode;

/**
 * Debugging utility that shows the sites where a crystal lattice may be grown
 *
 * @author Sam Reid
 */
public class BindingSiteDebugger extends PNode {
    public BindingSiteDebugger( final ModelViewTransform transform, final MicroModel model ) {
        model.stepFinishedListeners.add( new VoidFunction0() {
            public void apply() {

                //Clear all children and repopulate
                removeAllChildren();

                //Get one list of matches per crystal in the model
                ArrayList<ArrayList<CrystallizationMatch<SphericalParticle>>> matches = model.getAllBondingSites();
                for ( ArrayList<CrystallizationMatch<SphericalParticle>> matchesForCrystal : matches ) {

                    //For each of the crystals match lists, show the bonding sites
                    for ( int i = matchesForCrystal.size() - 1; i >= 0; i-- ) {
                        CrystallizationMatch match = matchesForCrystal.get( i );
                        Color baseColor = i == 0 ? Color.black : Color.yellow;
                        addChild( new PhetPPath( transform.modelToView( match.getTargetShape() ), new Color( baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 50 ) ) );
                    }
                }

                moveToFront();
            }
        } );
    }
}