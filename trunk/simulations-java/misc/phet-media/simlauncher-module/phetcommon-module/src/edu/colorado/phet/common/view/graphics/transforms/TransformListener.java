/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/view/graphics/transforms/TransformListener.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: samreid $
 * Revision : $Revision: 1.10 $
 * Date modified : $Date: 2005/11/11 23:09:49 $
 */
package edu.colorado.phet.common.view.graphics.transforms;


/**
 * Listens for changes in the model or view viewport.
 *
 * @author ?
 * @version $Revision: 1.10 $
 */
public interface TransformListener {
    public void transformChanged( ModelViewTransform2D mvt );
}
