/*  */
package edu.colorado.phet.quantumwaveinterference.view.gun;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import edu.colorado.phet.quantumwaveinterference.controls.SRRWavelengthSlider;
import edu.umd.cs.piccolo.PCanvas;

/**
 * User: Sam Reid
 * Date: Jan 22, 2006
 * Time: 11:00:29 PM
 */

public class SRRWavelengthSliderComponent extends PCanvas {
    private SRRWavelengthSlider wavelengthSliderGraphic;

    public SRRWavelengthSliderComponent( SRRWavelengthSlider wavelengthSliderGraphic ) {
        this.wavelengthSliderGraphic = wavelengthSliderGraphic;
        getLayer().addChild( wavelengthSliderGraphic );
        setBounds( 0, 0, 400, 400 );
        setPreferredSize( new Dimension( (int) wavelengthSliderGraphic.getFullBounds().getWidth(), (int) wavelengthSliderGraphic.getFullBounds().getHeight() ) );
        setPanEventHandler( null );
        setZoomEventHandler( null );
        setOpaque( false );
        setBackground( new Color( 0, 0, 0, 0 ) );
        addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
                System.out.println( "SRRWavelengthSliderComponent.mousePressed" );
            }

            public void mouseReleased( MouseEvent e ) {
            }
        } );
    }

    public void setOpaque( boolean isOpaque ) {
        super.setOpaque( isOpaque );
        if ( wavelengthSliderGraphic != null ) {
            wavelengthSliderGraphic.setOpaque( isOpaque );
        }
    }

}
