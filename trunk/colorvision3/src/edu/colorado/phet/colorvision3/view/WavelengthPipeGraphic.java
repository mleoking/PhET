/* WavelengthPipeGraphic.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.Component;

/**
 * WavelengthPipeGraphic
 *
 * @author cmalley
 * @revision $Id$
 */
public class WavelengthPipeGraphic extends PipeGraphic
{  
  /**
   * Sole constructor.
   */
  public WavelengthPipeGraphic( Component parent )
  {    
    super( parent );
    
    super.setThickness( 5 );
    super.addSegment( PipeGraphic.HORIZONTAL, 0, 0, 100 );
    super.addSegment( PipeGraphic.VERTICAL,   0, 0, 215 );
    super.addSegment( PipeGraphic.HORIZONTAL, 0, 210, 100 );
  }

}


/* end of file */