/* VisibleColorChangeListener.java, Copyright 2004 University of Colorado PhET */

package edu.colorado.phet.colorvision3.event;

import java.util.EventListener;

/**
 * VisibleColorChangeListener listens for VisibleColorChangeEvent.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Id$ $Name$
 */
public interface VisibleColorChangeListener extends EventListener
{
  public void colorChanged( VisibleColorChangeEvent event );
}


/* end of file */