package edu.colorado.phet.testlocation.view;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;


/**
 * InteractivityHandler provides unconstrained dragging and rotation of graphics.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class InteractivityHandler extends MouseInputAdapter implements TranslationListener {

    private static final int ROTATION_DELTA = 5; // degrees
    
    private PhetGraphic _graphic;
    private int _previousX, _previousY;

    public static void register( PhetGraphic graphic ) {
        new InteractivityHandler( graphic );
    }
    
    private InteractivityHandler( PhetGraphic graphic ) {
        _graphic = graphic;
        _graphic.setCursorHand();
        _graphic.addTranslationListener( this );
        _graphic.addMouseInputListener( this );
    }

    // Drag left-right to rotate.
    public void mouseDragged( MouseEvent e ) {
        if( SwingUtilities.isRightMouseButton( e ) ) {
            int angle = ROTATION_DELTA;
            if ( e.getX() < _previousX ) {
                angle = -angle;
            } 
            _graphic.rotate( Math.toRadians(angle) );
            _previousX = e.getX();
            _previousY = e.getY();
        }
    }

    public void mousePressed( MouseEvent e ) {
        _previousX = e.getX();
        _previousY = e.getY();
    }
    
    public void translationOccurred( TranslationEvent e ) {
        if( SwingUtilities.isLeftMouseButton( e.getMouseEvent() ) ) {
            int x = _graphic.getLocation().x + e.getDx();
            int y = _graphic.getLocation().y + e.getDy();
            _graphic.setLocation( x, y );
        }
    }
}