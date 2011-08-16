// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.Sucrose;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.view.CompositeParticleNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.model.property.Not.not;

/**
 * Draws a sucrose crystal which contains molecules (which contains atoms), shown in the sugar bucket for dragging into the play area
 *
 * @author Sam Reid
 */
public class SucroseCrystalNode extends PNode {
    public SucroseCrystalNode( ModelViewTransform transform, SucroseCrystal crystal, ObservableProperty<Boolean> showSugarAtoms ) {

        //Add a node for each constituent in the crystal
        for ( Constituent<Sucrose> constituent : crystal ) {
            CompositeParticleNode<SphericalParticle> node = new CompositeParticleNode<SphericalParticle>( transform, constituent.particle, not( showSugarAtoms ) );

            //Set it at the right relative location to the origin of the crystal
            node.setOffset( transform.modelToView( constituent.relativePosition.getX(), constituent.relativePosition.getY() ) );
            addChild( node );
        }
    }
}
