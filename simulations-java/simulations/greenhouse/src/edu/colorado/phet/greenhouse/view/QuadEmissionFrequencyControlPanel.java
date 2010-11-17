/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.lwjgl.util.Dimension;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This is a control panel that is intended for use in the control panel and
 * that allows the setting of 4 different emission frequencies.
 *
 * @author John Blanco
 */
public class QuadEmissionFrequencyControlPanel extends PNode {

    private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
    public static final Dimension PANEL_SIZE = new Dimension( 800, 150 );

    public QuadEmissionFrequencyControlPanel( final PhotonAbsorptionModel model ){

        // Create the main background shape.
        PNode backgroundNode = new PhetPPath( new RoundRectangle2D.Double(0, 0, PANEL_SIZE.getWidth(),
                PANEL_SIZE.getHeight(), 10, 10), BACKGROUND_COLOR );

        // Add the radio buttons that set the emission frequency.
        JPanel buttonPanel = new JPanel();
        // TODO: i18n
        buttonPanel.add( new WavelengthSelectButton( "Microwave", model, GreenhouseConfig.microWavelength ) );
        // TODO: i18n
        buttonPanel.add( new WavelengthSelectButton( "Infrared", model, GreenhouseConfig.irWavelength ) );
        // TODO: i18n
        buttonPanel.add( new WavelengthSelectButton( "Visible", model, GreenhouseConfig.sunlightWavelength ) );
        // TODO: i18n
        buttonPanel.add( new WavelengthSelectButton( "Ultraviolet", model, GreenhouseConfig.uvWavelength ) );

        PSwing buttonPanelNode = new PSwing( buttonPanel );
        buttonPanelNode.setOffset( PANEL_SIZE.getWidth() / 2 - buttonPanelNode.getFullBoundsReference().width / 2,
                PANEL_SIZE.getHeight() - buttonPanelNode.getFullBounds().height );

        // Add everything in the needed order.
        addChild( backgroundNode );
        backgroundNode.addChild( buttonPanelNode );
    }

    /**
     * Convenience class for the radio buttons that select the wavelength.
     */
    private static class WavelengthSelectButton extends JRadioButton {

        private static final Font LABEL_FONT  = new PhetFont( 20 );

        public WavelengthSelectButton( String text, final PhotonAbsorptionModel model, final double wavelength ){
            setFont( LABEL_FONT );
            setText( text );
            setBackground( BACKGROUND_COLOR );
            setOpaque( false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.setEmittedPhotonWavelength( wavelength );
                }
            });
            model.addListener( new PhotonAbsorptionModel.Adapter() {
                @Override
                public void emittedPhotonWavelengthChanged() {
                    setSelected( model.getEmittedPhotonWavelength() == wavelength );
                }
            } );
            // Set initial state.
            setSelected( model.getEmittedPhotonWavelength() == wavelength );
        }
    }
}
