/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.basicgraphics;

/**
 * BasicGraphicListener
 *
 * @author Sam Reid
 * @version $Revision$
 */
public interface BasicGraphicListener {
    void boundsChanged( BasicGraphic basicGraphic );

    void appearanceChanged( BasicGraphic basicGraphic );
}
