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
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * SimpleGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleGraphic extends PhetShapeGraphic {
//public class SimpleGraphic extends PhetGraphic {
    private Rectangle bounds = new Rectangle( 0, 0, 50, 50 );

    public SimpleGraphic() {
        super( null );
                
    }

    public SimpleGraphic( Component component ) {
        super( component );
        setShape( bounds );
        this.setFill( Color.green );
    }


    public void paint( Graphics2D g ) {
        super.paint( g );
    }

    protected Rectangle determineBounds() {
        return super.determineBounds();
    }

    public Rectangle getBounds() {
        return super.getBounds();
    }

    public void setBounds( Rectangle bounds ) {
        System.out.println( "-->" + super.getBounds() );
        super.setBounds( bounds );
    }

    public boolean contains( int x, int y ) {
        if( super.contains( x, y )) {
        }
        return super.contains( x, y );
    }
}
