// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetgraphics.view.phetgraphics;

/**
 * PhetGraphicListener
 *
 * @author ?
 * @version $Revision$
 */
public interface PhetGraphicListener {
    public void phetGraphicChanged( PhetGraphic phetGraphic );

    public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic );
}
