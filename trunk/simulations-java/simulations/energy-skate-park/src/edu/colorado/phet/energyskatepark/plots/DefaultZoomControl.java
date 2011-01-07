// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.common.piccolophet.nodes.ZoomControlNode;
import org.jfree.chart.axis.ValueAxis;

/**
 * Author: Sam Reid
 * May 22, 2007, 3:56:28 AM
 */
public abstract class DefaultZoomControl extends ZoomControlNode {
    private int zoom = 0;
    private ValueAxis axis;

    public DefaultZoomControl( int orientation, ValueAxis axis ) {
        super( orientation );
        this.axis = axis;

        addZoomListener( new ZoomListener() {
            public void zoomedOut() {
                zoom++;
                updateZoom();
            }

            public void zoomedIn() {
                zoom--;
                updateZoom();
            }
        } );
        updateZoom();
    }


    public ValueAxis getAxis() {
        return axis;
    }

    public int getZoom() {
        return zoom;
    }

    protected abstract void updateZoom();

    public void setZoom( int zoom ) {
        this.zoom = zoom;
    }
}
