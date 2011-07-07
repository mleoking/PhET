// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.modules.mandel;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.IntensitySlider;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ConnectorNode;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.colorado.phet.quantumwaveinterference.controls.SRRWavelengthSlider;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;
import edu.colorado.phet.quantumwaveinterference.view.gun.*;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.BlueGunDetails;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.HorizontalWireConnector;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 2, 2006
 * Time: 9:33:23 PM
 */

public class MandelGun extends PhetPNode {
    private PImage pimage;
    private GunControlPanel gunControlPanel;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private IntensitySlider intensitySlider;
    private SRRWavelengthSlider wavelengthSliderGraphic;
    private boolean on = false;
    private OnOffCheckBox onOffCheckBox;
    private PSwing onGunGraphic;

    public MandelGun( String image, QWIPanel qwiPanel ) {
        pimage = PImageFactory.create( image );
        addChild( pimage );
        gunControlPanel = new GunControlPanel( qwiPanel );

        intensitySlider = new IntensitySlider( Color.blue, IntensitySlider.HORIZONTAL, new Dimension( 140, 30 ) );
        intensitySlider.setValue( 100 );
        VerticalLayoutPanel vlp = new VerticalLayoutPanel();
        vlp.addFullWidth( intensitySlider );
        wavelengthSliderGraphic = new SRRWavelengthSlider( qwiPanel );
        final SRRWavelengthSliderComponent srrWavelengthSliderComponent = new SRRWavelengthSliderComponent( wavelengthSliderGraphic );

        vlp.addFullWidth( srrWavelengthSliderComponent );
        gunControlPanel.setGunControls( vlp );
        addChild( gunControlPanel.getPSwing() );
        wavelengthSliderGraphic.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                srrWavelengthSliderComponent.repaint();
            }
        } );
        wavelengthSliderGraphic.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateSliderColor();
                fireColorChanged();
            }
        } );
        updateSliderColor();
        intensitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireIntensityChanged();
            }
        } );
        ConnectorNode connectorGraphic = new HorizontalWireConnector( pimage, gunControlPanel.getPSwing() );
        addChild( 0, connectorGraphic );

        onOffCheckBox = new OnOffCheckBox( new OnOffItem() {
            public boolean isOn() {
                return on;
            }

            public void setOn( boolean ison ) {
                on = ison;
                fireIntensityChanged();
            }
        } );
        onGunGraphic = new PSwing( onOffCheckBox );
        onGunGraphic.addInputEventListener( new CursorHandler() );
        addChild( onGunGraphic );
        onGunGraphic.setOffset( pimage.getFullBounds().getX() + pimage.getFullBounds().getWidth() / 2 - onGunGraphic.getFullBounds().getWidth() / 2 + BlueGunDetails.onGunControlDX, BlueGunDetails.gunControlAreaY + pimage.getFullBounds().getY() );
    }

    private void fireColorChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = listeners.get( i );
            listener.wavelengthChanged();
        }
    }

    private void fireIntensityChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = listeners.get( i );
            listener.intensityChanged();
        }
    }

    private void updateSliderColor() {
        intensitySlider.setColor( wavelengthSliderGraphic.getVisibleColor() );
    }

    public void setControlsOffset( double x, double y ) {
        gunControlPanel.getPSwing().setOffset( x, y );
    }

    public PNode getGunControlPanelPSwing() {
        return gunControlPanel.getPSwing();
    }

    public PNode getGunImageGraphic() {
        return pimage;
    }

    public void setControlBackgroundColor( Color gunBackgroundColor ) {
        onOffCheckBox.setBackground( gunBackgroundColor );
    }

    public void translateOnGunControls( int dx, int dy ) {
        onGunGraphic.translate( dx, dy );
    }

    public boolean isOn() {
        return on;
    }

    public double getWavelength() {
        return wavelengthSliderGraphic.getWavelength();
    }

    public Photon getPhoton() {
        return null;
    }

    public double getIntensity() {
        return on ? intensitySlider.getValue() / 100.0 : 0.0;
    }

    public double getMomentum() {
        return 1.0 / getWavelength();//todo fix this!
    }

    public void setWavelength( double wavelength ) {
        wavelengthSliderGraphic.setWavelength( wavelength );
    }

    public static interface Listener {
        void wavelengthChanged();

        void intensityChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }
}
