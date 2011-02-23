// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.colorado.phet.lightreflectionandrefraction.modules.intro.LightWaveNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public abstract class LaserView {
    public static final LaserView RAY = new LaserView() {
        public PNode createNode( ModelViewTransform transform, LightRay lightRay ) {
            return new LightRayNode( transform, lightRay );
        }

        @Override
        public PNode getLayer( PNode lightRayLayer, PNode lightWaveLayer ) {
            return lightRayLayer;
        }
    };
    public static final LaserView WAVE = new LaserView() {
        public PNode createNode( ModelViewTransform transform, LightRay lightRay ) {
            return new LightWaveNode( transform, lightRay );
        }

        @Override
        public PNode getLayer( PNode lightRayLayer, PNode lightWaveLayer ) {
            return lightWaveLayer;
        }
    };

    public abstract PNode createNode( ModelViewTransform transform, LightRay lightRay );

    /**
     * Determine which layer to put the PNode in.
     *
     * @param lightRayLayer
     * @param lightWaveLayer
     * @return
     */
    public abstract PNode getLayer( PNode lightRayLayer, PNode lightWaveLayer );
}
