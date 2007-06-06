/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: C:/Java/cvs/root/SelfDrivenParticles/phetcommon/src/edu/colorado/phet/common/view/phetgraphics/PhetGraphicListener.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: Sam Reid $
 * Revision : $Revision: 1.1.1.1 $
 * Date modified : $Date: 2005/08/10 08:22:03 $
 */
package edu.colorado.phet.common.view.phetgraphics;

/**
 * PhetGraphicListener
 *
 * @author ?
 * @version $Revision: 1.1.1.1 $
 */
public interface PhetGraphicListener {
    public void phetGraphicChanged( PhetGraphic phetGraphic );

    public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic );
}
