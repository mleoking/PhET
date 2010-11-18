/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.lwjgl.util.Dimension;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SpectrumImageFactory.ExponentialGrowthSpectrumImageFactory;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This is a control panel that is intended for use in the control panel and
 * that allows the setting of 4 different emission frequencies.
 *
 * @author John Blanco
 */
public class QuadEmissionFrequencyControlPanel extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private static final Color BACKGROUND_COLOR = Color.LIGHT_GRAY;
    private static final Dimension PANEL_SIZE = new Dimension( 800, 200 );
    private static final double EDGE_TO_ARROW_DISTANCE = 20;
    private static final double TOP_TO_ARROW_DISTANCE = 30;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public QuadEmissionFrequencyControlPanel( final PhotonAbsorptionModel model ){

        // Create the main background shape.
        final PNode backgroundNode = new PhetPPath( new RoundRectangle2D.Double(0, 0, PANEL_SIZE.getWidth(),
                PANEL_SIZE.getHeight(), 20, 20), BACKGROUND_COLOR );

        // Add the title.
        backgroundNode.addChild( new PText("Photon Energy"){{
            setFont( new PhetFont( 33 ) );
            setOffset( PANEL_SIZE.getWidth() / 2 - getFullBoundsReference().width / 2, 10 );
        }});

        // Add the arrows on the right and left sides.
        // TODO: i18n
        EnergyArrow leftArrowNode = new EnergyArrow( "Lower", true ){{
           setOffset( EDGE_TO_ARROW_DISTANCE, TOP_TO_ARROW_DISTANCE );
        }};
        backgroundNode.addChild( leftArrowNode );
        // TODO: i18n
        EnergyArrow rightArrowNode = new EnergyArrow( "Higher", false ){{
           setOffset( backgroundNode.getFullBoundsReference().width - getFullBoundsReference().getMaxX() - EDGE_TO_ARROW_DISTANCE,
                   TOP_TO_ARROW_DISTANCE );
        }};
        backgroundNode.addChild( rightArrowNode );

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

        // Create the node that represents the spectrum.
        SpectrumNode spectrumNode = new SpectrumNode( (int) ( PANEL_SIZE.getWidth() * 0.9 ),
                (int) ( PANEL_SIZE.getHeight() * 0.3 ), model );
        spectrumNode.setOffset( PANEL_SIZE.getWidth() / 2 - spectrumNode.getFullBoundsReference().width / 2,
                PANEL_SIZE.getHeight() / 2 - spectrumNode.getFullBoundsReference().height / 2 );

        // Add everything in the needed order.
        addChild( backgroundNode );
        backgroundNode.addChild( buttonPanelNode );
        addChild( spectrumNode );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

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

    /**
     * Class that defines the spectrum that is shown on this control panel.
     *
     * @author John Blanco
     */
    public static class SpectrumNode extends PNode {

        private static final double MIN_WAVELENGTH = 1E-10; // In meters.
        private static final double MAX_WAVELENGTH = 10; // In meters
        private static final double SPECTRUM_HEIGHT_PROPORTION = 0.8;

        // Static data structure that maps the frequency values used in the
        // model to frequency ranges depicted on this spectrum.
        private static final HashMap<Double, DoubleRange> mapFreqToRange = new HashMap<Double, DoubleRange>(){{
            put( GreenhouseConfig.microWavelength, new DoubleRange(1E-3, 1));
            put( GreenhouseConfig.irWavelength, new DoubleRange(780E-9, 1E-3));
            put( GreenhouseConfig.sunlightWavelength, new DoubleRange(380E-9, 780E-9));
            put( GreenhouseConfig.uvWavelength, new DoubleRange(1E-9, 380E-9));
        }};

        private final PhotonAbsorptionModel model;
        private final PPath markerNode = new PhetPPath( new BasicStroke( 5 ), Color.WHITE );
        private final PImage spectrumImageNode;
        private final double height;

        /**
         * Constructor.
         */
        public SpectrumNode( int width, int height, PhotonAbsorptionModel model ){

            this.model = model;
            this.height = height;

            model.addListener( new PhotonAbsorptionModel.Adapter(){
                @Override
                public void emittedPhotonWavelengthChanged() {
                    updateMarker();
                }
            });

            spectrumImageNode = new PImage( new ExponentialGrowthSpectrumImageFactory().createHorizontalSpectrum( width,
                    (int)( height * SPECTRUM_HEIGHT_PROPORTION ), MIN_WAVELENGTH * 1E9, MAX_WAVELENGTH * 1E9) );

            // The spectrum image factory creates a spectrum by default that
            // is oriented from short to long wavelengths, and we need the
            // opposite, so we flip it here.
            spectrumImageNode.rotateAboutPoint( Math.PI, spectrumImageNode.getFullBoundsReference().getCenter2D() );

            addChild( spectrumImageNode );
            addChild( markerNode );

            updateMarker();
        }

        /**
         * Update the marker, which reflects the currently selected band of
         * the spectrum.
         */
        private void updateMarker(){
            double wavelengthSetting = model.getEmittedPhotonWavelength();
            DoubleRange wavelengthRange = mapFreqToRange.get( wavelengthSetting );
            double minY = spectrumImageNode.getFullBoundsReference().getMaxY();
            double maxY = height;
            double minX = mapWavelengthToNormalizedXPos( wavelengthRange.getMin() ) * spectrumImageNode.getFullBoundsReference().width;
            double maxX = mapWavelengthToNormalizedXPos( wavelengthRange.getMax() ) * spectrumImageNode.getFullBoundsReference().width;
            DoubleGeneralPath markerPath = new DoubleGeneralPath();
            markerPath.moveTo( minX, minY );
            markerPath.lineTo( minX, maxY );
            markerPath.lineTo( maxX, maxY );
            markerPath.lineTo( maxX, minY );
            markerNode.setPathTo( markerPath.getGeneralPath() );
        }

        private double mapWavelengthToNormalizedXPos( double wavelength ){
            return 1 - Math.log( wavelength / MIN_WAVELENGTH ) / Math.log( MAX_WAVELENGTH / MIN_WAVELENGTH );
        }
    }

    private static class EnergyArrow extends PNode {

        private static final double ARROW_LENGTH = 60;
        private static final double ARROW_HEAD_HEIGHT = 15;
        private static final double ARROW_HEAD_WIDTH = 30;
        private static final double ARROW_TAIL_WIDTH = 10;

        public EnergyArrow( String captionText, boolean pointsLeft ){

            PText caption = new PText( captionText );
            caption.setFont( new PhetFont( 18, true ) );
            addChild( caption );

            Point2D headPoint, tailPoint;
            if ( pointsLeft ){
                // Arrow points to the left.
                headPoint = new Point2D.Double(0, 0);
                tailPoint = new Point2D.Double(ARROW_LENGTH, 0);
                caption.setOffset( ARROW_HEAD_HEIGHT + 10, ARROW_TAIL_WIDTH );
            }
            else{
                // Must point to the right.
                headPoint = new Point2D.Double(ARROW_LENGTH, 0);
                tailPoint = new Point2D.Double(0, 0);
                caption.setOffset( headPoint.getX() - ARROW_HEAD_HEIGHT - caption.getFullBoundsReference().width - 10,
                        ARROW_TAIL_WIDTH );
            }
            ArrowNode arrowNode = new ArrowNode( tailPoint, headPoint, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH ){{
                setPaint( Color.WHITE );
                setStroke( new BasicStroke( 3 ) );
            }};
            addChild( arrowNode );
        }
    }
}
