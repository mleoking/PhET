// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.CrystallizationMatch;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.TargetConfiguration;
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

                //Get the list of targets for each crystal
                ArrayList<TargetConfiguration<SphericalParticle>> matches = model.getAllBondingSites();
                for ( TargetConfiguration<SphericalParticle> matchesForCrystal : matches ) {

                    //If there was a match, display it
                    if ( matchesForCrystal != null ) {
                        for ( CrystallizationMatch<SphericalParticle> match : matchesForCrystal.getMatches() ) {

                            //For each of the crystals match lists, show the bonding sites
                            Color baseColor = Color.green;
                            addChild( new PhetPPath( transform.modelToView( match.getTargetShape() ), new BasicStroke( 2 ), new Color( baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 200 ) ) );
                        }
                    }
                }

                moveToFront();
            }
        } );
    }
}