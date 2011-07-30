// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.view.barchart.BarItem;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.RemoveSoluteButtonNode;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Calcium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ethanol.Ethanol;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.Nitrate;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.Sucrose;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.*;

/**
 * List of the kits the user can choose from, for showing the appropriate bar charts + controls
 *
 * @author Sam Reid
 */
public class KitList {
    private final ArrayList<Kit> kits = new ArrayList<Kit>();

    public KitList( final MicroModel model, ModelViewTransform transform ) {
        //This is the logic for which components are present within each kit.  If kits change, this will need to be updated
        //Put the positive ions to the left of the negative ions
        kits.add( new Kit( new RemoveSoluteButtonNode[] {
                new RemoveSoluteButtonNode( "Remove Sodium Chloride", model.isAnySaltToRemove(), new VoidFunction0() {
                    public void apply() {
                        model.removeSalt();
                    }
                } ),
                new RemoveSoluteButtonNode( "Remove Sucrose", model.isAnySugarToRemove(), new VoidFunction0() {
                    public void apply() {
                        model.removeSugar();
                    }
                } ) },
                           new BarItem( model.sodiumConcentration, model.sodiumColor, SODIUM, new Some<PNode>( new SphericalParticleNode( transform, new Sodium(), model.showChargeColor ) ) ),
                           new BarItem( model.chlorideConcentration, model.chlorideColor, CHLORIDE, new Some<PNode>( new SphericalParticleNode( transform, new Chloride(), model.showChargeColor ) ) ),
                           new BarItem( model.sucroseConcentration, model.sucroseColor, SUCROSE, new Some<PNode>( new CompositeParticleNode<SphericalParticle>( transform, new Sucrose( ZERO ), model.showChargeColor ) ) ) ) );

        kits.add( new Kit( new RemoveSoluteButtonNode[] { },
                           new BarItem( model.sodiumConcentration, model.sodiumColor, SODIUM, new Some<PNode>( new SphericalParticleNode( transform, new Sodium(), model.showChargeColor ) ) ),
                           new BarItem( model.calciumConcentration, model.calciumColor, CALCIUM, new Some<PNode>( new SphericalParticleNode( transform, new Calcium(), model.showChargeColor ) ) ),
                           new BarItem( model.chlorideConcentration, model.chlorideColor, CHLORIDE, new Some<PNode>( new SphericalParticleNode( transform, new Chloride(), model.showChargeColor ) ) ) ) );

        kits.add( new Kit( new RemoveSoluteButtonNode[] { },
                           new BarItem( model.sodiumConcentration, model.sodiumColor, SODIUM, new Some<PNode>( new SphericalParticleNode( transform, new Sodium(), model.showChargeColor ) ) ),
                           new BarItem( model.chlorideConcentration, model.chlorideColor, CHLORIDE, new Some<PNode>( new SphericalParticleNode( transform, new Chloride(), model.showChargeColor ) ) ),
                           new BarItem( model.nitrateConcentration, model.nitrateColor, NITRATE, new Some<PNode>( new CompositeParticleNode<Particle>( transform, new Nitrate( 0, ImmutableVector2D.ZERO ), model.showChargeColor ) ) ) ) );

        kits.add( new Kit( new RemoveSoluteButtonNode[] { },
                           new BarItem( model.sucroseConcentration, model.sucroseColor, SUCROSE, new Some<PNode>( new CompositeParticleNode<SphericalParticle>( transform, new Sucrose(), model.showChargeColor ) ) ),
                           new BarItem( model.ethanolConcentration, model.ethanolColor, ETHANOL, new Some<PNode>( new CompositeParticleNode<SphericalParticle>( transform, new Ethanol(), model.showChargeColor ) ) ) ) );
    }

    public Kit getKit( int kit ) {
        return kits.get( kit );
    }
}
