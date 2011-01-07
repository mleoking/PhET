// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.*;

import edu.colorado.phet.buildanatom.model.SubatomicParticle;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;

/**
 * @author Sam Reid
 */
public class NeutronNode extends SubatomicParticleNode {
    public NeutronNode( ModelViewTransform2D mvt, SubatomicParticle subatomicParticle ) {
        super( mvt, subatomicParticle, Color.gray);
    }
}