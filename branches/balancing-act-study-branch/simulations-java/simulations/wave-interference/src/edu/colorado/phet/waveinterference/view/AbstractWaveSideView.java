// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: May 17, 2006
 * Time: 11:08:02 AM
 */

public abstract class AbstractWaveSideView extends PNode {
    public abstract void setSpaceBetweenCells( double dim );

    public abstract void update();
}
