/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;

import javax.swing.JRadioButton;

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

    private static final Color BACKGROUND_COLOR = new Color( 205, 198, 115 );
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
        // TODO: i18n
        PText title = new PText("Photon Energy"){{
            setFont( new PhetFont( 33 ) );
            setOffset( PANEL_SIZE.getWidth() / 2 - getFullBoundsReference().width / 2, 10 );
        }};
        backgroundNode.addChild( title );

        // Add the arrows on the right and left sides.
        // TODO: i18n
        EnergyArrow leftArrowNode = new EnergyArrow( "Lower", EnergyArrow.Direction.POINTS_LEFT ){{
           setOffset( EDGE_TO_ARROW_DISTANCE, TOP_TO_ARROW_DISTANCE );
        }};
        backgroundNode.addChild( leftArrowNode );
        // TODO: i18n
        EnergyArrow rightArrowNode = new EnergyArrow( "Higher", EnergyArrow.Direction.POINTS_RIGHT ){{
           setOffset( backgroundNode.getFullBoundsReference().width - getFullBoundsReference().getMaxX() - EDGE_TO_ARROW_DISTANCE,
                   TOP_TO_ARROW_DISTANCE );
        }};
        backgroundNode.addChild( rightArrowNode );

        // Add the radio buttons that set the emission frequency.
        // TODO: i18n
        final PNode microwaveSelectorNode = new WavelengthSelectButtonNode( "Microwave", model, GreenhouseConfig.microWavelength );
        // TODO: i18n
        final PNode infraredSelectorNode = new WavelengthSelectButtonNode( "Infrared", model, GreenhouseConfig.irWavelength );
        // TODO: i18n
        final PNode visibleLightSelectorNode = new WavelengthSelectButtonNode( "Visible", model, GreenhouseConfig.sunlightWavelength );
        // TODO: i18n
        final PNode ultravioletSelectorNode = new WavelengthSelectButtonNode( "Ultraviolet", model, GreenhouseConfig.uvWavelength );

        // Create a "panel" sort of node that contains all the selector
        // buttons, then position it at the center bottom of the main node.
        final PNode wavelengthSelectorPanelNode = new PNode();
        double interSelectorSpacing = ( PANEL_SIZE.getWidth() - microwaveSelectorNode.getFullBoundsReference().width -
                infraredSelectorNode.getFullBoundsReference().width -
                visibleLightSelectorNode.getFullBoundsReference().width -
                ultravioletSelectorNode.getFullBoundsReference().width ) / 5;
        interSelectorSpacing = Math.max( interSelectorSpacing, 0 ); // Don't allow less than 0.
        microwaveSelectorNode.setOffset( interSelectorSpacing, 0 );
        wavelengthSelectorPanelNode.addChild( microwaveSelectorNode );
        infraredSelectorNode.setOffset( microwaveSelectorNode.getFullBoundsReference().getMaxX() + interSelectorSpacing, 0 );
        wavelengthSelectorPanelNode.addChild( infraredSelectorNode );
        visibleLightSelectorNode.setOffset(  infraredSelectorNode.getFullBoundsReference().getMaxX() + interSelectorSpacing, 0 );
        wavelengthSelectorPanelNode.addChild( visibleLightSelectorNode );
        ultravioletSelectorNode.setOffset( visibleLightSelectorNode.getFullBoundsReference().getMaxX() + interSelectorSpacing, 0 );
        wavelengthSelectorPanelNode.addChild( ultravioletSelectorNode );
        wavelengthSelectorPanelNode.setOffset( 0, backgroundNode.getFullBoundsReference().height - wavelengthSelectorPanelNode.getFullBoundsReference().height );

        // Create a structure that maps the wavelengths to the x positions of
        // their selectors.  This is needed by the spectrum node in order to
        // create a visual connection between the selection button and the
        // selected range within the spectrum.
        HashMap<Double, Double> mapWavelengthToXPos = new HashMap<Double, Double>(){{
                put( GreenhouseConfig.microWavelength, wavelengthSelectorPanelNode.getXOffset() + microwaveSelectorNode.getFullBoundsReference().getCenterX() );
                put( GreenhouseConfig.irWavelength, wavelengthSelectorPanelNode.getXOffset() + infraredSelectorNode.getFullBoundsReference().getCenterX() );
                put( GreenhouseConfig.sunlightWavelength, wavelengthSelectorPanelNode.getXOffset() + visibleLightSelectorNode.getFullBoundsReference().getCenterX() );
                put( GreenhouseConfig.uvWavelength, wavelengthSelectorPanelNode.getXOffset() + ultravioletSelectorNode.getFullBoundsReference().getCenterX() );
        }};

        // Create the node that represents the spectrum.
        SpectrumNode spectrumNode = new SpectrumNode( (int) ( PANEL_SIZE.getWidth() * 0.9 ),
                (int) ( PANEL_SIZE.getHeight() * 0.5 ), model, mapWavelengthToXPos );
        spectrumNode.setOffset( PANEL_SIZE.getWidth() / 2 - spectrumNode.getFullBoundsReference().width / 2,
                leftArrowNode.getFullBoundsReference().getMaxY() + 5 );

        // Add everything in the needed order.
        addChild( backgroundNode );
        backgroundNode.addChild( wavelengthSelectorPanelNode );
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

        private static final Font LABEL_FONT  = new PhetFont( 26 );

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
     * Convenience class for the radio buttons that select the wavelength.
     */
    private static class WavelengthSelectButtonNode extends PNode {

        public WavelengthSelectButtonNode( String text, final PhotonAbsorptionModel model, final double wavelength ){
            WavelengthSelectButton button = new WavelengthSelectButton( text, model, wavelength );
            addChild( new PSwing( button ));
        }
    }

    /**
     * Class that defines the spectrum that is shown on this control panel.
     * This consists of the spectrum and of a marker that exists below the
     * actual spectrum that shows the currently selected range.
     *
     * @author John Blanco
     */
    public static class SpectrumNode extends PNode {

        private static final double MIN_WAVELENGTH = 1E-10; // In meters.
        private static final double MAX_WAVELENGTH = 10; // In meters
        private static final double SPECTRUM_HEIGHT_PROPORTION = 0.5;
        private static final Stroke MARKER_STROKE = new BasicStroke( 5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        private static final Color MARKER_COLOR = new Color( 139, 129, 76 );

        // Static data structure that maps the frequency values used in the
        // model to frequency ranges depicted on this spectrum.
        private static final HashMap<Double, DoubleRange> mapFreqToRange = new HashMap<Double, DoubleRange>(){{
            put( GreenhouseConfig.microWavelength, new DoubleRange(1E-3, 1));
            put( GreenhouseConfig.irWavelength, new DoubleRange(780E-9, 1E-3));
            put( GreenhouseConfig.sunlightWavelength, new DoubleRange(380E-9, 780E-9));
            put( GreenhouseConfig.uvWavelength, new DoubleRange(1E-9, 380E-9));
        }};

        private final PhotonAbsorptionModel model;
        private final PPath markerNode = new PhetPPath( MARKER_STROKE, MARKER_COLOR );
        private final PImage spectrumImageNode;
        private final double height;
        HashMap<Double, Double> mapWavelengthToXPos;

        /**
         * Constructor.
         * @param mapWavelengthToXPos TODO
         */
        public SpectrumNode( int width, int height, PhotonAbsorptionModel model, HashMap<Double, Double> mapWavelengthToXPos ){

            this.model = model;
            this.height = height;
            this.mapWavelengthToXPos = mapWavelengthToXPos;

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
         * the spectrum.  The marker consists of a spectrum range indicator
         * and a line connecting it to the selector.
         */
        private void updateMarker(){
            double wavelengthSetting = model.getEmittedPhotonWavelength();
            DoubleRange wavelengthRange = mapFreqToRange.get( wavelengthSetting );
            double totalMarkerHeight = height - spectrumImageNode.getFullBoundsReference().height;

            // Add the range indicator to the path.
            double rangeIndicatorMinY = spectrumImageNode.getFullBoundsReference().getMaxY();
            double rangeIndicatorMaxY = spectrumImageNode.getFullBoundsReference().getMaxY() + totalMarkerHeight / 3;
            double rangeIndicatorMinX = mapWavelengthToNormalizedXPos( wavelengthRange.getMin() ) * spectrumImageNode.getFullBoundsReference().width;
            double rangeIndicatorMaxX = mapWavelengthToNormalizedXPos( wavelengthRange.getMax() ) * spectrumImageNode.getFullBoundsReference().width;
            DoubleGeneralPath markerPath = new DoubleGeneralPath();
            markerPath.moveTo( rangeIndicatorMinX, rangeIndicatorMinY );
            markerPath.lineTo( rangeIndicatorMinX, rangeIndicatorMaxY );
            markerPath.lineTo( rangeIndicatorMaxX, rangeIndicatorMaxY );
            markerPath.lineTo( rangeIndicatorMaxX, rangeIndicatorMinY );

            // Now add the line that connects the range indicator to the selector.
            double rangeIndicatorMiddleX = rangeIndicatorMinX + ( ( rangeIndicatorMaxX - rangeIndicatorMinX  ) / 2 );
            assert mapWavelengthToXPos.containsKey( model.getEmittedPhotonWavelength() ); // If not there, we can't draw the connecting line.
            Point2D connectingLineEndPoint = parentToLocal( new Point2D.Double( mapWavelengthToXPos.get( model.getEmittedPhotonWavelength() ), 0 ) );
            markerPath.moveTo( rangeIndicatorMiddleX, rangeIndicatorMaxY );
            markerPath.lineTo( rangeIndicatorMiddleX, rangeIndicatorMaxY + totalMarkerHeight / 3 );
            markerPath.lineTo( connectingLineEndPoint.getX(), rangeIndicatorMaxY + totalMarkerHeight / 3 );
            markerPath.lineTo( connectingLineEndPoint.getX(), height );

            // Set the node to the path that we just calculated.
            markerNode.setPathTo( markerPath.getGeneralPath() );
        }

        private double mapWavelengthToNormalizedXPos( double wavelength ){
            return 1 - Math.log( wavelength / MIN_WAVELENGTH ) / Math.log( MAX_WAVELENGTH / MIN_WAVELENGTH );
        }
    }

    /**
     * Class that defines the "energy arrow", which is an arrow on each side
     * of the chart that indicates increasing or decreasin energy amounts.
     *
     * @author John Blanco
     */
    private static class EnergyArrow extends PNode {

        private static final double ARROW_LENGTH = 60;
        private static final double ARROW_HEAD_HEIGHT = 15;
        private static final double ARROW_HEAD_WIDTH = 30;
        private static final double ARROW_TAIL_WIDTH = 10;

        public enum Direction { POINTS_LEFT, POINTS_RIGHT };

        public EnergyArrow( String captionText, Direction direction ){

            PText caption = new PText( captionText );
            caption.setFont( new PhetFont( 18, true ) );
            addChild( caption );

            Point2D headPoint, tailPoint;
            if ( direction == Direction.POINTS_LEFT ){
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
