/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.persistence.test.view;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * SimpleGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleGraphic extends PhetGraphic {
    private Rectangle bounds = new Rectangle( 100, 100, 50, 50 );

    public SimpleGraphic() {
        super( null );
                
    }

    public SimpleGraphic( Component component ) {
        super( component );
    }

    protected Rectangle determineBounds() {
        return bounds;
    }

    public void paint( Graphics2D g2 ) {
        g2.setColor( Color.green );
        g2.fill( bounds );
    }
}
