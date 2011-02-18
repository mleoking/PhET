// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculesandlight.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SpectrumImageFactory.ExponentialGrowthSpectrumImageFactory;
import edu.colorado.phet.common.photonabsorption.model.PhotonAbsorptionModel;
import edu.colorado.phet.common.photonabsorption.view.PhotonNode;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.moleculesandlight.MoleculesAndLightConfig;
import edu.colorado.phet.moleculesandlight.MoleculesAndLightResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This is a control panel that is intended for use in the play area and
 * that allows the setting of 4 different photon emission frequencies.
 *
 * @author John Blanco
 */
public class QuadEmissionFrequencyControlPanel extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private static final Color BACKGROUND_COLOR = new Color( 185, 178, 95 );
    private static final Dimension PANEL_SIZE = new Dimension( 850, 150 );

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

        // Add the radio buttons that set the emission wavelength.
        final WavelengthSelectButtonNode microwaveSelectorNode =
            new WavelengthSelectButtonNode( MoleculesAndLightResources.getString( "QuadWavelengthSelector.Microwave" ), model, MoleculesAndLightConfig.MICRO_WAVELENGTH );
        final WavelengthSelectButtonNode infraredSelectorNode =
            new WavelengthSelectButtonNode( MoleculesAndLightResources.getString( "QuadWavelengthSelector.Infrared" ), model, MoleculesAndLightConfig.IR_WAVELENGTH );
        final WavelengthSelectButtonNode visibleLightSelectorNode =
            new WavelengthSelectButtonNode( MoleculesAndLightResources.getString( "QuadWavelengthSelector.Visible" ), model, MoleculesAndLightConfig.VISIBLE_WAVELENGTH );
        final WavelengthSelectButtonNode ultravioletSelectorNode =
            new WavelengthSelectButtonNode( MoleculesAndLightResources.getString( "QuadWavelengthSelector.Ultraviolet" ), model, MoleculesAndLightConfig.UV_WAVELENGTH );

        // Put all the buttons into a button group.  Without this, for some
        // reason, the individual buttons will toggle to the off state if
        // pressed twice in a row.
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( microwaveSelectorNode.getButton() );
        buttonGroup.add( infraredSelectorNode.getButton() );
        buttonGroup.add( visibleLightSelectorNode.getButton() );
        buttonGroup.add( ultravioletSelectorNode.getButton() );

        // Create a "panel" sort of node that contains all the selector
        // buttons, then position it on the main node.
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
        wavelengthSelectorPanelNode.setOffset( 0, 10 );


        // Add the energy arrow.
        EnergyArrow energyArrow = new EnergyArrow( MoleculesAndLightResources.getString( "QuadWavelengthSelector.HigherEnergy" ), model ){{
            centerFullBoundsOnPoint( backgroundNode.getFullBoundsReference().getCenterX(),
                    PANEL_SIZE.getHeight() - getFullBoundsReference().height / 2 - 10 );
        }};
        backgroundNode.addChild( energyArrow );

        // Add everything in the needed order.
        addChild( backgroundNode );
        backgroundNode.addChild( wavelengthSelectorPanelNode );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    /**
     * Convenience class that puts a radio button with a caption into a PNode.
     */
    private static class WavelengthSelectButtonNode extends PNode {

        private static final Font LABEL_FONT  = new PhetFont( 16 );
        JRadioButton button;

        public WavelengthSelectButtonNode( final String text, final PhotonAbsorptionModel photonAbsorptionModel, final double wavelength ){

            // Add the radio button.
            button = new JRadioButton(){{
                setFont( LABEL_FONT );
                setText( text );
                setBackground( BACKGROUND_COLOR );
                setOpaque( false );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        photonAbsorptionModel.setEmittedPhotonWavelength( wavelength );
                    }
                });
                photonAbsorptionModel.addListener( new PhotonAbsorptionModel.Adapter() {
                    @Override
                    public void emittedPhotonWavelengthChanged() {
                        setSelected( photonAbsorptionModel.getEmittedPhotonWavelength() == wavelength );
                    }
                } );
                // Set initial state.
                setSelected( photonAbsorptionModel.getEmittedPhotonWavelength() == wavelength );
            }};

            // We received some feedback that the buttons were a little small,
            // so the following scaling operation makes them bigger relative
            // to the font.
            PSwing buttonNode = new PSwing( button ){{
                setScale( 1.5 );
            }};
            addChild( buttonNode );

            // Add an image of a photon.
            PhotonNode photonNode = new PhotonNode( wavelength );
            photonNode.addInputEventListener( new PBasicInputEventHandler(){
                @Override
                public void mouseClicked( PInputEvent event ) {
                    photonAbsorptionModel.setEmittedPhotonWavelength( wavelength );
                }
            });
            addChild( photonNode );

            // Do the layout.  The photon is above the radio button, centered.
            photonNode.setOffset(
                    buttonNode.getFullBoundsReference().width / 2,
                    photonNode.getFullBoundsReference().height / 2 );
            buttonNode.setOffset( 0, photonNode.getFullBoundsReference().height * 0.6 );

        }

        public JRadioButton getButton(){
            return button;
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
            put( MoleculesAndLightConfig.MICRO_WAVELENGTH, new DoubleRange(1E-3, 1));
            put( MoleculesAndLightConfig.IR_WAVELENGTH, new DoubleRange(780E-9, 1E-3));
            put( MoleculesAndLightConfig.VISIBLE_WAVELENGTH, new DoubleRange(380E-9, 780E-9));
            put( MoleculesAndLightConfig.UV_WAVELENGTH, new DoubleRange(1E-9, 380E-9));
        }};

        private final PhotonAbsorptionModel model;
        private final PPath markerNode = new PhetPPath( MARKER_STROKE, MARKER_COLOR );
        private final PImage spectrumImageNode;
        private final double height;
        HashMap<Double, Double> mapWavelengthToXPos;

        /**
         * Constructor.
         * @param mapWavelengthToXPos
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
            // opposite, so we flip it here.  Note that this makes the offset
            // go to the lower right corner, so positioning becomes a bit
            // tricky.
            spectrumImageNode.rotateAboutPoint( Math.PI, spectrumImageNode.getFullBoundsReference().getCenter2D() );
            spectrumImageNode.setOffset( spectrumImageNode.getOffset().getX(), height );

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
            double rangeIndicatorMinY = totalMarkerHeight * 2 / 3;
            double rangeIndicatorMaxY = totalMarkerHeight;
            double rangeIndicatorMinX = mapWavelengthToNormalizedXPos( wavelengthRange.getMin() ) * spectrumImageNode.getFullBoundsReference().width;
            double rangeIndicatorMaxX = mapWavelengthToNormalizedXPos( wavelengthRange.getMax() ) * spectrumImageNode.getFullBoundsReference().width;
            DoubleGeneralPath markerPath = new DoubleGeneralPath();
            markerPath.moveTo( rangeIndicatorMinX, rangeIndicatorMaxY );
            markerPath.lineTo( rangeIndicatorMinX, rangeIndicatorMinY );
            markerPath.lineTo( rangeIndicatorMaxX, rangeIndicatorMinY );
            markerPath.lineTo( rangeIndicatorMaxX, rangeIndicatorMaxY );

            // Now add the line that connects the range indicator to the selector.
            double rangeIndicatorMiddleX = rangeIndicatorMinX + ( ( rangeIndicatorMaxX - rangeIndicatorMinX  ) / 2 );
            assert mapWavelengthToXPos.containsKey( model.getEmittedPhotonWavelength() ); // If not there, we can't draw the connecting line.
            Point2D connectingLineEndPoint = parentToLocal( new Point2D.Double( mapWavelengthToXPos.get( model.getEmittedPhotonWavelength() ), 0 ) );
            markerPath.moveTo( rangeIndicatorMiddleX, rangeIndicatorMinY );
            markerPath.lineTo( rangeIndicatorMiddleX, rangeIndicatorMinY - totalMarkerHeight / 3 );
            markerPath.lineTo( connectingLineEndPoint.getX(), rangeIndicatorMinY - totalMarkerHeight / 3 );
            markerPath.lineTo( connectingLineEndPoint.getX(), 0 );

            // Set the node to the path that we just calculated.
            markerNode.setPathTo( markerPath.getGeneralPath() );
        }

        private double mapWavelengthToNormalizedXPos( double wavelength ){
            return 1 - Math.log( wavelength / MIN_WAVELENGTH ) / Math.log( MAX_WAVELENGTH / MIN_WAVELENGTH );
        }
    }

    /**
     * Class that defines the "energy arrow", which is an arrow that depicts
     * the direction of increasing energy.
     *
     * @author John Blanco
     */
    private static class EnergyArrow extends PNode {

        private static final double ARROW_LENGTH = 250;
        private static final double ARROW_HEAD_HEIGHT = 15;
        private static final double ARROW_HEAD_WIDTH = 15;
        private static final double ARROW_TAIL_WIDTH = 2;
        private static final Paint ARROW_COLOR = Color.BLACK;

        public EnergyArrow( String captionText, final PhotonAbsorptionModel model ){
            // Create and add the arrow.  The arrow points to the right.
            Point2D headPoint, tailPoint;
            headPoint = new Point2D.Double(ARROW_LENGTH, 0);
            tailPoint = new Point2D.Double(0, 0);
            final ArrowNode arrowNode = new ArrowNode( tailPoint, headPoint, ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH ){{
                setPaint( ARROW_COLOR );
                setStroke( new BasicStroke( 3 ) );
            }};
            addChild( arrowNode );

            // Create and add the caption.
            HTMLNode caption = new HTMLNode( captionText );
            caption.setFont( new PhetFont( 20, true ) );
            caption.setOffset(
                    arrowNode.getFullBoundsReference().getCenterX() - caption.getFullBoundsReference().width / 2,
                    arrowNode.getFullBoundsReference().getMaxY());
            addChild( caption );
        }
    }
}
