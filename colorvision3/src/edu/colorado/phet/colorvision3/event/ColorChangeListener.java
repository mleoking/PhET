/* ColorChangeListener.java, Copyright 2004 University of Colorado */

package edu.colorado.phet.colorvision3.event;

import java.util.EventListener;

/**
 * ColorChangeListener listens for ColorChangeEvent.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public interface ColorChangeListener extends EventListener
{
  public void colorChanged( ColorChangeEvent event );
}


/* end of file */