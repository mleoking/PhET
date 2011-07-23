// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows all components of a molecule, used in bar chart legends, but not used in the beaker play area--in that case each atom is a top-level node.
 *
 * @author Sam Reid
 */
public class CompositeParticleNode extends PNode {
    public CompositeParticleNode( ModelViewTransform transform, Iterable<Constituent> molecule, ObservableProperty<Boolean> showChargeColor ) {
        for ( Constituent constituent : molecule ) {
            addChild( new SphericalParticleNode( transform, (SphericalParticle) constituent.particle, showChargeColor ) );
        }
    }
}
