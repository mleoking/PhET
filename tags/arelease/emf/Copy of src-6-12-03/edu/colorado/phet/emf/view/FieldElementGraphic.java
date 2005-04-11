/**
 * Class: FieldElementGraphic
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: May 29, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.command.DeleteFieldElementCmd;
import edu.colorado.phet.common.userinterface.graphics.ObservingGraphic;
import edu.colorado.phet.emf.model.FieldElement;

import java.awt.*;
import java.util.Observable;

public class FieldElementGraphic implements ObservingGraphic {

    private Point location = new Point();
    private Rectangle viewBounds;

    public void paint( Graphics2D g2 ) {
        g2.drawArc( location.x, location.y, 3, 3, 0, 360 );
        g2.fillArc( location.x, location.y, 3, 3, 0, 360 );
    }

    public void setViewBounds( Rectangle viewBounds ) {
        this.viewBounds = viewBounds;
    }

    public void update( Observable o, Object arg ) {
        FieldElement fieldElement = (FieldElement)o;
        location.setLocation( fieldElement.getLocation() );
        if( !viewBounds.contains( location )) {
            new DeleteFieldElementCmd( fieldElement ).doItLater();
        }
    }
}
