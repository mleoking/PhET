/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/BasicGraphicsSetup.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:02 $
 */
package edu.colorado.phet.common.view;

import java.awt.*;

/**
 * Convenience class for common rendering options.
 * 
 * @author ?
 * @version $Revision: 1.1.1.1 $
 */
public class BasicGraphicsSetup implements GraphicsSetup {
    RenderingHints renderingHints;

    public BasicGraphicsSetup() {
        this.renderingHints = new RenderingHints( null );
        setAntialias( true );
        setBicubicInterpolation();
    }

    public void setAntialias( boolean antialias ) {
        if( antialias ) {
            renderingHints.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        }
        else {
            renderingHints.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        }
    }

    public void setBicubicInterpolation() {
        renderingHints.put( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
    }

    public void setNearestNeighborInterpolation() {
        renderingHints.put( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
    }

    public void setBilinearInterpolation() {
        renderingHints.put( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
    }

    public void setRenderQuality( boolean quality ) {
        if( quality ) {
            renderingHints.put( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        }
        else {
            renderingHints.put( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );
        }
    }

    public void setup( Graphics2D graphics ) {
        graphics.setRenderingHints( renderingHints );
    }

}
