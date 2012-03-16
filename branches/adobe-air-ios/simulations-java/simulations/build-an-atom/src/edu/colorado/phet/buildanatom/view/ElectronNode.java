// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.*;

import edu.colorado.phet.buildanatom.model.SphericalParticle;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * @author Sam Reid
 */
public class ElectronNode extends SubatomicParticleNode {
    public ElectronNode( ModelViewTransform mvt, SphericalParticle subatomicParticle ) {
        super( mvt, subatomicParticle, Color.blue );
    }
}