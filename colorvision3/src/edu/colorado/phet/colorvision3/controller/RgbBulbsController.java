/* RgbBulbsController.java */

package edu.colorado.phet.colorvision3.controller;

import java.awt.Color;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.colorvision3.event.IntensityChangeEvent;
import edu.colorado.phet.colorvision3.event.IntensityChangeListener;
import edu.colorado.phet.colorvision3.model.Person2D;
import edu.colorado.phet.colorvision3.model.Spotlight2D;
import edu.colorado.phet.colorvision3.view.PhotonBeamGraphic;


/**
 * RgbBulbsController is the controller for the "RGB Bulbs" simulation module.
 * It manages how the color produced by the RGB photon beams is perceived
 * by the person viewing the spotlights.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @revision $Id$
 */
public class RgbBulbsController implements IntensityChangeListener
{
  private PhotonBeamGraphic _redBeam, _greenBeam, _blueBeam;
  private Person2D _person;
  private EventListenerList _listenerList;
  
  /**
   * Sole constructor.
   * 
   * @param redBeam the red photon beam
   * @param greenBeam the green photon beam
   * @param blueBeam the blue photon beam
   * @param person the person model
   */
  public RgbBulbsController( PhotonBeamGraphic redBeam, PhotonBeamGraphic greenBeam, PhotonBeamGraphic blueBeam, Person2D person )
  {
    _redBeam = redBeam;
    _greenBeam = greenBeam;
    _blueBeam = blueBeam;
    _person = person;
    _listenerList = new EventListenerList();
  }

  /**
   * Handles an IntensityChangeEvent.
   * The new perceived color is calculated by consulting all of the
   * photon beams, and a ColorChangeEvent is fired.
   * 
   * @param event the event
   */
  public void intensityChanged( IntensityChangeEvent event )
  {
		Color color = getPerceivedColor();
		_person.setColor( color ); 
  }

  /**
   * Gets the perceived color produced by the combination of photon beams.
   * Each photon beam contributes one color component (RGB).
   * Alpha is scaled to match the intensity of the maximum component value.
   * 
   * @return the perceived color
   */
  private Color getPerceivedColor()
  {
    double maxIntensity = Spotlight2D.INTENSITY_MAX;
    
    // Each beam contributes one color component.
    int red = (int) ((_redBeam.getPerceivedIntensity() / maxIntensity) * 255 );
    int green = (int) ((_greenBeam.getPerceivedIntensity() / maxIntensity) * 255 );
    int blue = (int) ((_blueBeam.getPerceivedIntensity() / maxIntensity) * 255 );
    int alpha = Math.max( red, Math.max(green, blue) );

		return new Color( red, green, blue, alpha );
  }
}


/* end of file */