// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import edu.colorado.phet.bendinglight.model.LightRay;
import edu.colorado.phet.bendinglight.modules.intro.LightWaveNode;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;

/**
 * Display type for the rays, can be shown as rays (nonmoving lines) or waves (animating).
 *
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
