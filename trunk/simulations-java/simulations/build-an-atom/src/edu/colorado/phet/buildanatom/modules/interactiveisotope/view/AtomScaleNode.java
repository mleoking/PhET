/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.interactiveisotope.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import javax.swing.JPanel;

import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Piccolo node that represents a scale on which an atom can be weighed.  This
 * node is intended to have a faux 3D look to it, but is not truly 3D in any
 * way.
 *
 * @author John Blanco
 */
public class AtomScaleNode extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private enum DisplayMode { MASS_NUMBER, ATOMIC_MASS };

    private static final Color COLOR = new Color( 228, 194, 167 );
    private static final Dimension2D SIZE = new PDimension( 320, 125 );
    private static final double WIEIGH_PLATE_WIDTH = SIZE.getWidth() * 0.70;
    private static final Stroke STROKE = new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final Paint STROKE_PAINT = Color.BLACK;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final Property<DisplayMode> displayModeProperty = new Property<DisplayMode>( DisplayMode.MASS_NUMBER );
    private final PNode weighPlateTop;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public AtomScaleNode( IDynamicAtom atom ) {

        // Set up some helper variables.
        double centerX = SIZE.getWidth() / 2;

        // NOTE: The scale shapes are generated from the bottom up, since
        // adding them in this order creates the correct layering effect.

        // Add the front of the scale base.
        Rectangle2D frontOfBaseShape = new Rectangle2D.Double( 0, SIZE.getHeight() * 0.55, SIZE.getWidth(), SIZE.getHeight() * 0.5 );
        final PNode frontOfBaseNode = new PhetPPath( frontOfBaseShape, COLOR, STROKE, STROKE_PAINT );
        addChild( frontOfBaseNode );

        // Add the readout to the scale base.
        final PNode scaleReadoutNode = new ScaleReadoutNode( atom, displayModeProperty ){{
            setOffset( SIZE.getWidth() * 0.05, frontOfBaseNode.getFullBoundsReference().getCenterY() - getFullBoundsReference().height / 2 );
        }};
        addChild( scaleReadoutNode );

        // Add the display mode selector to the scale base.
        addChild( new DisplayModeSelectionNode( displayModeProperty ){{
            // Scale the selector if necessary.  This is here primarily in
            // support of translation.
            double maxAllowableWidth = frontOfBaseNode.getFullBoundsReference().getMaxX() - scaleReadoutNode.getFullBoundsReference().getMaxX() - 10;
            if ( getFullBoundsReference().getWidth() > maxAllowableWidth ){
                setScale( maxAllowableWidth / getFullBoundsReference().width );
            }
            // Position the selector next to the readout.
            setOffset( scaleReadoutNode.getFullBoundsReference().getMaxX() + 5,
                    frontOfBaseNode.getFullBoundsReference().getCenterY() - getFullBoundsReference().height / 2 );
        }} );

        // Add the top portion of the scale base.  This is meant to look like
        // a tilted rectangle.  Because, hey, it's all a matter of
        // perspective.
        DoubleGeneralPath scaleBaseTopShape = new DoubleGeneralPath();
        scaleBaseTopShape.moveTo( SIZE.getWidth() * 0.15, SIZE.getHeight() * 0.375 );
        scaleBaseTopShape.lineTo( SIZE.getWidth() * 0.85, SIZE.getHeight() * 0.375 );
        scaleBaseTopShape.lineTo( SIZE.getWidth(), SIZE.getHeight() * 0.55 );
        scaleBaseTopShape.lineTo( 0, SIZE.getHeight() * 0.55 );
        scaleBaseTopShape.closePath();
        Rectangle2D scaleBaseTopShapeBounds = scaleBaseTopShape.getGeneralPath().getBounds2D();
        GradientPaint scaleBaseTopPaint = new GradientPaint(
                (float) scaleBaseTopShapeBounds.getCenterX(),
                (float) scaleBaseTopShapeBounds.getMaxY(),
                ColorUtils.brighterColor( COLOR, 0.5 ),
                (float) scaleBaseTopShapeBounds.getCenterX(),
                (float) scaleBaseTopShapeBounds.getMinY(),
                ColorUtils.darkerColor( COLOR, 0.5 )
        );
        PNode scaleBaseTop = new PhetPPath( scaleBaseTopShape.getGeneralPath(), scaleBaseTopPaint, STROKE, STROKE_PAINT );
        addChild( scaleBaseTop );

        // Add the shaft that connects the base to the weigh plate.
        DoubleGeneralPath connectingShaftShape = new DoubleGeneralPath();
        double connectingShaftDistanceFromTop = SIZE.getHeight() * 0.15;
        double connectingShaftWidth = SIZE.getWidth() * 0.1;
        double connectingShaftHeight = SIZE.getHeight() * 0.30;
        connectingShaftShape.moveTo( centerX - connectingShaftWidth / 2, connectingShaftDistanceFromTop );
        connectingShaftShape.lineTo( centerX - connectingShaftWidth / 2, connectingShaftDistanceFromTop + connectingShaftHeight );
        connectingShaftShape.quadTo( centerX, connectingShaftDistanceFromTop + connectingShaftHeight * 1.2, SIZE.getWidth() / 2 + connectingShaftWidth / 2, connectingShaftDistanceFromTop + connectingShaftHeight );
        connectingShaftShape.lineTo( centerX + connectingShaftWidth / 2, connectingShaftDistanceFromTop );
        Rectangle2D connectingShaftShapeBounds = connectingShaftShape.getGeneralPath().getBounds2D();
        GradientPaint connectingShaftPaint = new GradientPaint(
                (float) connectingShaftShapeBounds.getMinX(),
                (float) connectingShaftShapeBounds.getCenterY(),
                ColorUtils.brighterColor( COLOR, 0.5 ),
                (float) connectingShaftShapeBounds.getMaxX(),
                (float) connectingShaftShapeBounds.getCenterY(),
                ColorUtils.darkerColor( COLOR, 0.5 ) );
        PNode connectingShaft = new PhetPPath( connectingShaftShape.getGeneralPath(), connectingShaftPaint, STROKE, STROKE_PAINT );
        addChild( connectingShaft );

        // Draw the top of the weigh plate.  This is meant to look like a
        // tilted rectangle.
        DoubleGeneralPath weighPlateTopShape = new DoubleGeneralPath();
        weighPlateTopShape.moveTo( centerX - WIEIGH_PLATE_WIDTH * 0.35, 0 );
        weighPlateTopShape.lineTo( centerX + WIEIGH_PLATE_WIDTH * 0.35, 0 );
        weighPlateTopShape.lineTo( centerX + WIEIGH_PLATE_WIDTH / 2, SIZE.getHeight() * 0.125 );
        weighPlateTopShape.lineTo( centerX - WIEIGH_PLATE_WIDTH / 2, SIZE.getHeight() * 0.125 );
        weighPlateTopShape.closePath();
        Rectangle2D weighPlateTopShapeBounds = weighPlateTopShape.getGeneralPath().getBounds2D();
        GradientPaint weighPlateTopPaint = new GradientPaint(
                (float) weighPlateTopShapeBounds.getCenterX(),
                (float) weighPlateTopShapeBounds.getMaxY(),
                ColorUtils.brighterColor( COLOR, 0.5 ),
                (float) weighPlateTopShapeBounds.getCenterX(),
                (float) weighPlateTopShapeBounds.getMinY(),
                ColorUtils.darkerColor( COLOR, 0.2 ) );
        weighPlateTop = new PhetPPath( weighPlateTopShape.getGeneralPath(), weighPlateTopPaint, STROKE, STROKE_PAINT );
        addChild( weighPlateTop );

        // Add the front of the weigh plate.
        Rectangle2D frontOfWeighPlateShape = new Rectangle2D.Double( centerX - WIEIGH_PLATE_WIDTH / 2,
                SIZE.getHeight() * 0.125, WIEIGH_PLATE_WIDTH, SIZE.getHeight() * 0.15 );
        addChild( new PhetPPath( frontOfWeighPlateShape, COLOR, STROKE, STROKE_PAINT ) );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    public void reset(){
        displayModeProperty.reset();
    }

    public double getWeighPlateTopProjectedHeight(){
        return weighPlateTop.getFullBoundsReference().getHeight();
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    /**
     * Class that defines the readout on the front of the scale.  This readout
     * can display an atom's mass as either the mass number, which is an
     * integer number representing the total number of nucleons, or as the
     * atomic mass, which is the relative actual mass of the atom.
     *
     * @author John Blanco
     */
    private static class ScaleReadoutNode extends PNode {

        private static final DecimalFormat atomicMassNumberFormatter = new DecimalFormat("#0.00000");
        PText readoutText = new PText(){{
            setFont( new PhetFont( 24 ) );
        }};
        Property<DisplayMode> displayModeProperty;
        IDynamicAtom atom;
        private final PPath readoutBackground;

        public ScaleReadoutNode( IDynamicAtom atom, Property<DisplayMode> displayModeProperty ){

            this.atom = atom;
            this.displayModeProperty = displayModeProperty;

            readoutBackground = new PhetPPath(
                    new RoundRectangle2D.Double( 0, 0, SIZE.getWidth() * 0.4, SIZE.getHeight() * 0.33, 5, 5 ),
                    Color.WHITE,
                    new BasicStroke( 2 ),
                    Color.BLACK );
            addChild( readoutBackground );

            // Add the text that will appear in the readout.
            addChild( readoutText );

            // Watch the property that represents the display mode and update
            // the readout when it changes.
            displayModeProperty.addObserver( new SimpleObserver() {
                public void update() {
                    updateReadout();
                }
            } );

            // Watch the atom and update the readout whenever it changes.
            atom.addAtomListener( new AtomListener.Adapter() {
                @Override
                public void configurationChanged() {
                    updateReadout();
                }
            } );
        }

        private void updateReadout(){
            if ( displayModeProperty.getValue() == DisplayMode.MASS_NUMBER ){
                readoutText.setText( Integer.toString( atom.getMassNumber() ) );
            }
            else{
                double atomicMass = atom.getAtomicMass();
                readoutText.setText( atomicMass > 0 ? atomicMassNumberFormatter.format( atomicMass ) : "--" );
            }
            // Make sure that the text fits in the display.
            readoutText.setScale( 1 );
            if ( readoutText.getFullBoundsReference().width > readoutBackground.getFullBoundsReference().width ||
                 readoutText.getFullBoundsReference().height > readoutBackground.getFullBoundsReference().height ){
                double scaleFactor = Math.min(
                        readoutBackground.getFullBoundsReference().width / readoutText.getFullBoundsReference().width,
                        readoutBackground.getFullBoundsReference().height / readoutText.getFullBoundsReference().height );
                readoutText.setScale( scaleFactor );
            }
            // Center the text in the display.
            readoutText.centerFullBoundsOnPoint( readoutBackground.getFullBoundsReference().getCenterX(),
                    readoutBackground.getFullBoundsReference().getCenterY() );
        }
    }

    /**
     * This class represents a Piccolo node that contains the radio buttons
     * that allows the user to select the display mode for the scale.
     *
     * @author John Blanco
     */
    public static class DisplayModeSelectionNode extends PNode {

        private static final Font LABEL_FONT = new PhetFont( 16 );

        public DisplayModeSelectionNode( Property<DisplayMode> displayModeProperty ) {
            JPanel buttonPanel = new JPanel( new GridLayout( 2, 1 ) ){{
                setBackground( COLOR );
            }};
            // TODO: i18n
            PropertyRadioButton<DisplayMode> massNumberButton = new PropertyRadioButton<DisplayMode>( "Mass Number", displayModeProperty, DisplayMode.MASS_NUMBER ){{
                setBackground( COLOR );
                setFont( LABEL_FONT );
            }};
            buttonPanel.add( massNumberButton );
            // TODO: i18n
            PropertyRadioButton<DisplayMode> atomicMassButton = new PropertyRadioButton<DisplayMode>( "Atomic Mass (amu)", displayModeProperty, DisplayMode.ATOMIC_MASS ){{
                setBackground( COLOR );
                setFont( LABEL_FONT );
            }};
            buttonPanel.add( atomicMassButton );
            addChild( new PSwing( buttonPanel ) );
        }
    }
}
