/* IntensityChangeListener.java */

package edu.colorado.phet.colorvision3.event;

import java.util.EventListener;

/**
 * IntensityChangeListener listens for IntensityChangeEvent.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public interface IntensityChangeListener extends EventListener
{
  public void intensityChanged( IntensityChangeEvent event );
}


/* end of file */