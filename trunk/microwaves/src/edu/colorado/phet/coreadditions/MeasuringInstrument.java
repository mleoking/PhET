/**
 * Class: MeasuringInstrument
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Sep 25, 2003
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.view.graphics.ModelViewTransform2D;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class MeasuringInstrument
        extends TxObservingGraphic
        implements MouseListener {

    public MeasuringInstrument( ModelViewTransform2D tx ) {
        super( tx );
    }

    public void paint( Graphics2D graphics2D ) {
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
    }

    public void mouseExited( MouseEvent e ) {
    }
}
