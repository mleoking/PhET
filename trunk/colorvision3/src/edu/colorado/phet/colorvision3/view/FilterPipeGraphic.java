/* FilterPipeGraphic.java */

package edu.colorado.phet.colorvision3.view;

import java.awt.Component;

/**
 * FilterPipeGraphic
 *
 * @author cmalley
 * @revision $Id$
 */
public class FilterPipeGraphic extends PipeGraphic
{
  /**
   * Sole constructor.
   */
  public FilterPipeGraphic( Component parent )
  {    
    super( parent );
    
    super.setThickness( 5 );
    super.addSegment( PipeGraphic.HORIZONTAL, 0, 110, 105 );
    super.addSegment( PipeGraphic.VERTICAL, 100,   0, 115 );
  }
}


/* end of file */