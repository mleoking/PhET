/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.colorvision3.event;

import java.util.EventListener;

/**
 * VisibleColorChangeListener listens for VisibleColorChangeEvent.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface VisibleColorChangeListener extends EventListener
{
  public void colorChanged( VisibleColorChangeEvent event );
}


/* end of file */