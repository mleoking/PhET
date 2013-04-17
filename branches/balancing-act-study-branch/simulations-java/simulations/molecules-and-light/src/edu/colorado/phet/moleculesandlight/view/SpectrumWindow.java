// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculesandlight.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.view.util.SpectrumImageFactory.ExponentialGrowthSpectrumImageFactory;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.moleculesandlight.MoleculesAndLightResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class defines a separate window that shows a representation of the
 * electromagnetic spectrum.
 *
 */
public class SpectrumWindow extends JFrame {

    /**
     * Constructor.
     */
    public SpectrumWindow() {
        super( MoleculesAndLightResources.getString( "SpectrumWindow.title" ) );

        // Size and center this window.
        setToDefaultSizeAndPosition();

        // Make sure the window is hidden when closed, not destroyed.  This
        // is actually the default value, but it is good to make sure.
        setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );

        // Create the canvas and set up its transform.
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setBackground( new Color( 233, 236, 174 ) );
        canvas.setBorder( BorderFactory.createLineBorder( Color.BLACK, 2 ) );
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas,
                new PDimension( SpectrumDiagram.OVERALL_DIMENSIONS.getWidth(), SpectrumDiagram.OVERALL_DIMENSIONS.getHeight() + 40 ) ) );

        // Add the spectrum diagram to the canvas.
        SpectrumDiagram spectrumDiagram = new SpectrumDiagram();
        canvas.addWorldChild( spectrumDiagram );

        // Add the close button.
        HTMLImageButtonNode closeButton = new HTMLImageButtonNode( MoleculesAndLightResources.getCommonString( "Common.choice.close" ), Color.ORANGE );
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                // Post an event in the system queue that indicates a close.
                WindowEvent wev = new WindowEvent(SpectrumWindow.this, WindowEvent.WINDOW_CLOSING);
                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
            }
        } );
        closeButton.centerFullBoundsOnPoint( spectrumDiagram.getFullBoundsReference().getCenterX(),
                spectrumDiagram.getFullBoundsReference().getMaxY() + 30 );
        canvas.addWorldChild( closeButton );

        // Set the canvas as the content pane.
        setContentPane( canvas );
    }

    public void setToDefaultSizeAndPosition() {
        setSize( 800, 600 );
        SwingUtils.centerWindowOnScreen( this );
    }

    /**
     * Class that contains the diagram of the EM spectrum.  This is done as a
     * PNode in order to be translatable.  This class includes the arrows,
     * the spectrum strip, the wavelength indicator, etc.  In other words, it
     * is the top level node within which the constituent parts that make up
     * the entire diagram are contained.
     */
    private static class SpectrumDiagram extends PNode {

        private static final Dimension OVERALL_DIMENSIONS = new Dimension( 690, 440 );
        private static final double HORIZONTAL_INSET = 30;

        public SpectrumDiagram() {

            // Add the title.
            PText title = new PText( MoleculesAndLightResources.getString( "SpectrumWindow.title" ) );
            title.setFont( new PhetFont( 30 ) );
            title.setOffset( OVERALL_DIMENSIONS.getWidth() / 2 - title.getFullBoundsReference().width / 2, 10 );
            addChild( title );

            // Add the frequency arrow.
            LabeledArrow frequencyArrow = new LabeledArrow(
                    OVERALL_DIMENSIONS.getWidth() - HORIZONTAL_INSET * 2,
                    LabeledArrow.Orientation.POINTING_RIGHT,
                    MoleculesAndLightResources.getString( "SpectrumWindow.frequencyArrowLabel" ),
                    new Color( 225, 142, 255 ),
                    Color.WHITE );
            frequencyArrow.setOffset( HORIZONTAL_INSET, title.getFullBoundsReference().getMaxY() + 25 );
            addChild( frequencyArrow );

            // Add the spectrum portion.
            LabeledSpectrumNode spectrum = new LabeledSpectrumNode( OVERALL_DIMENSIONS.width - 2 * HORIZONTAL_INSET );
            spectrum.setOffset( HORIZONTAL_INSET, frequencyArrow.getFullBoundsReference().getMaxY() + 10 );
            addChild( spectrum );

            // Add the wavelength arrow.
            LabeledArrow wavelengthArrow = new LabeledArrow(
                    OVERALL_DIMENSIONS.getWidth() - HORIZONTAL_INSET * 2,
                    LabeledArrow.Orientation.POINTING_LEFT,
                    MoleculesAndLightResources.getString( "SpectrumWindow.wavelengthArrowLabel" ),
                    Color.WHITE,
                    new Color( 235, 129, 98 ) );
            wavelengthArrow.setOffset( HORIZONTAL_INSET, spectrum.getFullBoundsReference().getMaxY() + 15 );
            addChild( wavelengthArrow );

            // Add the diagram that depicts the wave that gets shorter.
            ChirpNode decreasingWavelengthNode = new ChirpNode( OVERALL_DIMENSIONS.width - 2 * HORIZONTAL_INSET );
            decreasingWavelengthNode.setOffset( HORIZONTAL_INSET, wavelengthArrow.getFullBoundsReference().getMaxY() + 20 );
            addChild( decreasingWavelengthNode );
        }
    }

    /**
     * Class that defines a labeled arrow node.
     */
    private static class LabeledArrow extends PNode {
        public static double ARROW_HEAD_HEIGHT = 40;
        private static double ARROW_HEAD_WIDTH = 40;
        private static double ARROW_TAIL_WIDTH = 25;
        private static Font LABEL_FONT = new PhetFont( 16 );
        private static Stroke STROKE = new BasicStroke( 2 );

        public enum Orientation {
            POINTING_LEFT, POINTING_RIGHT
        };

        public LabeledArrow( double length, Orientation orientation, String captionText, Color topColor, Color bottomColor ) {

            // Create the paint that will be used to depict the arrow.  It is
            // assumed that the arrow has a gradient that changes in the
            // vertical direction.
            Paint gradientPaint = new GradientPaint( 0, (float) -ARROW_HEAD_HEIGHT / 2, topColor, 0,
                    (float) ARROW_HEAD_HEIGHT / 2, bottomColor );

            // Create and add the arrow node.
            ArrowNode arrowNode;
            if ( orientation == Orientation.POINTING_RIGHT ) {
                arrowNode = new ArrowNode( new Point2D.Double( 0, 0 ), new Point2D.Double( length, 0 ),
                        ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
            }
            else {
                assert orientation == Orientation.POINTING_LEFT;
                arrowNode = new ArrowNode( new Point2D.Double( length, 0 ), new Point2D.Double( 0, 0 ),
                        ARROW_HEAD_HEIGHT, ARROW_HEAD_WIDTH, ARROW_TAIL_WIDTH );
            }
            arrowNode.setPaint( gradientPaint );
            arrowNode.setStroke( STROKE );
            arrowNode.setOffset( 0, ARROW_HEAD_HEIGHT / 2 );
            addChild( arrowNode );

            // Create and add the textual label.
            PText label = new PText( captionText );
            label.setFont( LABEL_FONT );
            label.centerFullBoundsOnPoint( arrowNode.getFullBoundsReference().getCenterX(),
                    arrowNode.getFullBoundsReference().getCenterY() );
            addChild( label );
        }
    }

    /**
     * Class that depicts the frequencies and wavelengths of the EM spectrum
     * and labels the subsections (e.g. "Infrared").
     *
     * @author John Blanco
     */
    private static class LabeledSpectrumNode extends PNode {

        private static final double STRIP_HEIGHT = 65;
        private static final double MIN_FREQUENCY = 1E3;
        private static final double MAX_FREQUENCY = 1E21;
        private static final Stroke TICK_MARK_STROKE = new BasicStroke( 2f );
        private static final double TICK_MARK_HEIGHT = 8;
        private static final Font TICK_MARK_FONT = new PhetFont( 12 );
        private static final Stroke BAND_DIVIDER_STROKE = new BasicStroke( 2, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0, new float[] { 4, 4 }, 0 );
        private static final Font LABEL_FONT = new PhetFont( 16 );

        private final double stripWidth;
        private final PNode spectrumRootNode;

        public LabeledSpectrumNode( double width ) {
            stripWidth = width;

            spectrumRootNode = new PNode();
            addChild( spectrumRootNode );

            // Create the "strip", which is the solid background portions that
            // contains the different bands and that has tick marks on the top
            // and bottom.
            PNode strip = new PhetPPath( new Rectangle2D.Double( 0, 0, width, STRIP_HEIGHT ), new Color( 237, 243, 246 ),
                    new BasicStroke( 2f ), Color.BLACK );
            spectrumRootNode.addChild( strip );

            // Add the frequency tick marks to the top of the spectrum strip.
            for ( int i = 4; i <= 20; i++ ) {
                boolean includeLabel = i % 2 == 0;
                addFrequencyTickMark( Math.pow( 10, i ), includeLabel );
            }

            // Add the wavelength tick marks.
            for ( int i = -12; i <= 4; i++ ) {
                boolean includeLabel = i % 2 == 0;
                addWavelengthTickMark( Math.pow( 10, i ), includeLabel );
            }

            // Add the various bands.
            addBandLabel( 1E3, 1E9, MoleculesAndLightResources.getString( "SpectrumWindow.radioBandLabel" ) );
            addBandDivider( 1E9 );
            addBandLabel( 1E9, 3E11, MoleculesAndLightResources.getString( "SpectrumWindow.microwaveBandLabel" ) );
            addBandDivider( 3E11 );
            addBandLabel( 3E11, 6E14, MoleculesAndLightResources.getString( "SpectrumWindow.infraredBandLabel" ) );
            addBandLabel( 1E15, 8E15, MoleculesAndLightResources.getString( "SpectrumWindow.ultravioletBandLabel" ) );
            addBandDivider( 1E16 );
            addBandLabel( 1E16, 1E19, MoleculesAndLightResources.getString( "SpectrumWindow.xrayBandLabel" ) );
            addBandDivider( 1E19 );
            addBandLabel( 1E19, 1E21, MoleculesAndLightResources.getString( "SpectrumWindow.gammaRayBandLabel" ) );

            // Add the visible spectrum.
            int visSpectrumWidth = (int) Math.round( getOffsetFromFrequency( 790E12 ) - getOffsetFromFrequency( 400E12 ) );
            final Image horizontalSpectrum = new ExponentialGrowthSpectrumImageFactory().createHorizontalSpectrum( visSpectrumWidth, (int) STRIP_HEIGHT );
            BufferedImage flipped = BufferedImageUtils.flipX( BufferedImageUtils.toBufferedImage( horizontalSpectrum ) );
            PNode visibleSpectrum = new PImage( flipped );
            visibleSpectrum.setOffset( getOffsetFromFrequency( 400E12 ), 0 );
            spectrumRootNode.addChild( visibleSpectrum );

            // Add the label for the visible band.
            PText visibleBandLabel = new PText( MoleculesAndLightResources.getString( "SpectrumWindow.visibleBandLabel" ) );
            visibleBandLabel.setFont( LABEL_FONT );
            double visibleBandCenterX = visibleSpectrum.getFullBounds().getCenterX();
            visibleBandLabel.setOffset( visibleBandCenterX - visibleBandLabel.getFullBoundsReference().width / 2, -50 );
            spectrumRootNode.addChild( visibleBandLabel );

            // Add the arrow that connects the visible band label to the
            // visible band itself.
            ArrowNode visibleBandArrow = new ArrowNode(
                    new Point2D.Double( visibleBandCenterX, visibleBandLabel.getFullBoundsReference().getMaxY() ),
                    new Point2D.Double( visibleBandCenterX, 0 ),
                    7,
                    7,
                    2 );
            visibleBandArrow.setPaint( Color.BLACK );
            spectrumRootNode.addChild( visibleBandArrow );

            // Add the units.
            PText frequencyUnits = new PText( MoleculesAndLightResources.getString( "SpectrumWindow.cyclesPerSecondUnits" ) );
            frequencyUnits.setFont( LABEL_FONT );
            frequencyUnits.setOffset( stripWidth, -TICK_MARK_HEIGHT - frequencyUnits.getFullBoundsReference().getHeight() );
            spectrumRootNode.addChild( frequencyUnits );
            PText wavelengthUnits = new PText( MoleculesAndLightResources.getString( "SpectrumWindow.metersUnits" ) );
            wavelengthUnits.setFont( LABEL_FONT );
            wavelengthUnits.setOffset( stripWidth, STRIP_HEIGHT + TICK_MARK_HEIGHT + 5 );
            spectrumRootNode.addChild( wavelengthUnits );

            // Set the offset of the root node to account for child nodes that
            // ended up with negative offsets when the layout was complete.
            spectrumRootNode.setOffset(
                    Math.max( -spectrumRootNode.getFullBoundsReference().getMinX(), 0 ),
                    Math.max( -spectrumRootNode.getFullBoundsReference().getMinY(), 0 ) );
        }

        /**
         * Convert the given frequency to an offset from the left edge of the
         * spectrum strip.
         * @param frequency - Frequency in Hz.
         */
        private double getOffsetFromFrequency( double frequency ) {
            assert frequency >= MIN_FREQUENCY && frequency <= MAX_FREQUENCY;
            double logarithmicRange = Math.log10( MAX_FREQUENCY ) - Math.log10( MIN_FREQUENCY );
            double logarithmicFrequency = Math.log10( frequency );
            return ( logarithmicFrequency - Math.log10( MIN_FREQUENCY ) ) / logarithmicRange * stripWidth;
        }

        /**
         * Convert the given wavelength to an offset from the left edge of the
         * spectrum strip.
         * @param wavelength - wavelength in meters
         */
        private double getOffsetFromWavelength( double wavelength ) {
            return getOffsetFromFrequency( 299792458 / wavelength );
        }

        /**
         * Add a tick mark for the specified frequency.  Frequency tick marks
         * go on top of the strip.
         */
        private void addFrequencyTickMark( double frequency, boolean addLabel ) {
            // Create and add the tick mark line.
            DoubleGeneralPath path = new DoubleGeneralPath();
            path.moveTo( 0, 0 );
            path.lineTo( 0, -TICK_MARK_HEIGHT );
            PNode tickMarkNode = new PhetPPath( path.getGeneralPath(), TICK_MARK_STROKE, Color.BLACK );
            tickMarkNode.setOffset( getOffsetFromFrequency( frequency ), 0 );
            spectrumRootNode.addChild( tickMarkNode );

            if ( addLabel ) {
                // Create and add the label.
                PNode label = createExponentialLabel( frequency );
                label.setOffset(
                        tickMarkNode.getFullBoundsReference().getCenterX() - label.getFullBoundsReference().width / 2,
                        tickMarkNode.getFullBoundsReference().getMinY() - label.getFullBoundsReference().height );
                spectrumRootNode.addChild( label );
            }
        }

        /**
         * Add a tick mark for the specified frequency.  Frequency tick marks
         * go on top of the strip.
         */
        private void addWavelengthTickMark( double wavelength, boolean addLabel ) {
            // Create and add the tick mark line.
            DoubleGeneralPath path = new DoubleGeneralPath();
            path.moveTo( 0, 0 );
            path.lineTo( 0, TICK_MARK_HEIGHT );
            PNode tickMarkNode = new PhetPPath( path.getGeneralPath(), TICK_MARK_STROKE, Color.BLACK );
            tickMarkNode.setOffset( getOffsetFromWavelength( wavelength ), STRIP_HEIGHT );
            spectrumRootNode.addChild( tickMarkNode );

            if ( addLabel ) {
                // Create and add the label.
                PNode label = createExponentialLabel( wavelength );
                label.setOffset(
                        tickMarkNode.getFullBoundsReference().getCenterX() - label.getFullBoundsReference().width / 2,
                        tickMarkNode.getFullBoundsReference().getMaxY() );
                spectrumRootNode.addChild( label );
            }
        }

        private PNode createExponentialLabel( double value ) {
            int superscript = (int) Math.round( Math.log10( value ) );
            HTMLNode label = new HTMLNode( "<html>10<sup>" + superscript + "</sup></html>" );
            label.setFont( TICK_MARK_FONT );
            return label;
        }

        /**
         * Add a "band divider" at the given frequency.  A band divider is
         * a dotted line that spans the spectrum strip in the vertical
         * direction.
         */
        private void addBandDivider( double frequency ) {
            DoubleGeneralPath path = new DoubleGeneralPath();
            path.moveTo( 0, 0 );
            path.lineTo( 0, STRIP_HEIGHT );
            PNode bandDividerNode = new PhetPPath( path.getGeneralPath(), BAND_DIVIDER_STROKE, Color.BLACK );
            bandDividerNode.setOffset( getOffsetFromFrequency( frequency ), 0 );
            spectrumRootNode.addChild( bandDividerNode );
        }

        private void addBandLabel( double lowEndFrequency, double highEndFrequency, String htmlLabelText ) {
            // Argument validation.
            assert highEndFrequency >= lowEndFrequency;

            // Set up values needed for calculations.
            double leftBoundaryX = getOffsetFromFrequency( lowEndFrequency );
            double rightBoundaryX = getOffsetFromFrequency( highEndFrequency );
            double width = rightBoundaryX - leftBoundaryX;
            double centerX = leftBoundaryX + width / 2;

            // Create and add the label.
            HTMLNode labelNode = new HTMLNode( htmlLabelText );
            labelNode.setFont( LABEL_FONT );
            if ( labelNode.getFullBoundsReference().width > width ) {
                // Scale the label to fit.
                labelNode.setScale( width / labelNode.getFullBoundsReference().width );
            }
            labelNode.setOffset(
                    centerX - labelNode.getFullBoundsReference().width / 2,
                    STRIP_HEIGHT / 2 - labelNode.getFullBoundsReference().height / 2 );
            spectrumRootNode.addChild( labelNode );
        }
    }

    /**
     *  Class that depicts a wave that gets progressively shorter in wavelength
     * from left to right, which is called a chirp.
     */
    private static class ChirpNode extends PNode {
        public ChirpNode( double width ) {
            // Create and add the boundary and background.
            double boundingBoxHeight = width * 0.1; // Arbitrary, adjust as needed.
            PNode boundingBox = new PhetPPath( new Rectangle2D.Double( 0, 0, width, width * 0.1 ),
                    new Color( 237, 243, 246 ), new BasicStroke( 2f ), Color.black );
            addChild( boundingBox );

            // Create the line that represents the decreasing wavelength.
            DoubleGeneralPath squigglyLinePath = new DoubleGeneralPath(0, 0);
            int numPointsOnLine = 2000;
            for ( int i = 0; i < numPointsOnLine; i++ ) {
                double x = i * ( width / ( numPointsOnLine - 1 ) );
                double t = x / width;

                double f0 = 1;
                double k = 2;
                final double tScale = 4.5;
                double exponentialSinTerm = Math.sin( 2 * Math.PI * f0 * ( Math.pow( k, t * tScale ) - 1 ) / Math.log( k ) );

                double sinTerm = exponentialSinTerm;

                double y = ( sinTerm * boundingBoxHeight * 0.40 + boundingBoxHeight / 2 );
                squigglyLinePath.lineTo( x, y );
            }
            PNode squigglyLineNode = new PhetPPath( squigglyLinePath.getGeneralPath(),
                    new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND ), Color.BLACK );
            addChild( squigglyLineNode );
        }
    }

    /**
     * Test harness.
     */
    public static void main( String[] args ) {
        JFrame spectrumWindow = new SpectrumWindow();
        spectrumWindow.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        spectrumWindow.setLocation( 200, 100 );
        spectrumWindow.setVisible( true );
    }
}
