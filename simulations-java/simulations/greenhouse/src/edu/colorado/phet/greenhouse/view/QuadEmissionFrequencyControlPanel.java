/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 *
 * @author John Blanco
 */
public class QuadEmissionFrequencyControlPanel extends PNode {

    private static Color BACKGROUND_COLOR = Color.LIGHT_GRAY;

    public QuadEmissionFrequencyControlPanel( final PhotonAbsorptionModel model ){

        // Create the main background shape.
        PNode backgroundNode = new PhetPPath( new RoundRectangle2D.Double(0, 0, 800, 150, 10, 10), BACKGROUND_COLOR );

        // Add the radio buttons that set the emission frequency.
        JPanel buttonPanel = new JPanel();
        // TODO: i18n
        buttonPanel.add( new WavelengthSelectButton( "Microwave", model, GreenhouseConfig.sunlightWavelength ) );
        // TODO: i18n
        buttonPanel.add( new WavelengthSelectButton( "Infrared", model, GreenhouseConfig.irWavelength ) );
        // TODO: i18n
        buttonPanel.add( new WavelengthSelectButton( "Visible", model, GreenhouseConfig.sunlightWavelength ) );
        // TODO: i18n
        buttonPanel.add( new WavelengthSelectButton( "Ultraviolet", model, GreenhouseConfig.irWavelength ) );

        PSwing buttonPanelNode = new PSwing( buttonPanel );

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
        }
    }
}
