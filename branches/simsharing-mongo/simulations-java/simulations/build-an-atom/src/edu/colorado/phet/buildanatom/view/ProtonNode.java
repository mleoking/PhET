// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import edu.colorado.phet.buildanatom.model.SphericalParticle;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * @author Sam Reid
 */
public class ProtonNode extends SubatomicParticleNode {
    public ProtonNode( ModelViewTransform mvt, SphericalParticle subatomicParticle ) {
        super( mvt, subatomicParticle, PhetColorScheme.RED_COLORBLIND );
    }
}