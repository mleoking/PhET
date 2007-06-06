/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/graphics/transforms/TransformListener.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:03 $
 */
package edu.colorado.phet.common.view.graphics.transforms;


/**
 * Listens for changes in the model or view viewport.
 *
 * @author ?
 * @version $Revision: 1.1.1.1 $
 */
public interface TransformListener {
    public void transformChanged( ModelViewTransform2D mvt );
}
