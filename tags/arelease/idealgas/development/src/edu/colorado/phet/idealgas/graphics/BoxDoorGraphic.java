/**
 * Class: BoxDoorGraphic
 * Package: edu.colorado.phet.idealgas.graphics
 * Author: Another Guy
 * Date: Apr 14, 2003
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.graphics.MovableImageGraphic;
import edu.colorado.phet.controller.PhetApplication;

import java.awt.*;
import java.awt.event.MouseEvent;

public class BoxDoorGraphic extends MovableImageGraphic {

    private float doorStartPointMaxX = -1;
    private float doorStartPointX;
    private Point draggingRefPos;

    public BoxDoorGraphic( Image image, float x, float y,
                           float minX, float minY,
                           float maxX, float maxY ) {
        super( image, x, y, minX, minY, maxX, maxY );
    }

    public void mouseDragged( MouseEvent event ) {
        super.mouseDragged( event );

        // HACK: Terrible! This should be done differently
        if( isSelected() ) {
            if( doorStartPointMaxX == -1 ) {
                doorStartPointMaxX = (float)getImageStartPt().getX() + ( this.getImage().getWidth( this ) );
            }
            float xDiff = (float)event.getPoint().getX() - (float)getDragStartPt().getX();

            float yCurr = (float)this.getLocationPoint2D().getY();
            float xNew = (float)getImageStartPt().getX() + xDiff;
            xNew = Math.max( xNew, this.getMinX() );
            this.setPosition( xNew, yCurr );

            BaseIdealGasApparatusPanel apparatusPanel =
                    (BaseIdealGasApparatusPanel)PhetApplication.instance().getPhetMainPanel().getApparatusPanel();
            apparatusPanel.setOpening( doorStartPointMaxX,
                                       (float)this.getLocationPoint2D().getX(),
                                       (float)this.getLocationPoint2D().getY() );
        }
    }

    public boolean isInHotSpot( Point p ) {
        return false;
    }
}
