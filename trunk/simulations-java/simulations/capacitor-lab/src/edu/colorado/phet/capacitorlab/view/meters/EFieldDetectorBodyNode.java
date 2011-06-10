// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.*;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.drag.WorldLocationDragHandler;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.meter.EFieldDetector;
import edu.colorado.phet.capacitorlab.view.meters.ZoomButtonNode.ZoomInButtonNode;
import edu.colorado.phet.capacitorlab.view.meters.ZoomButtonNode.ZoomOutButtonNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.Vector2DNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;


/**
 * Body of the E-Field Detector, origin at upper-left corner of bounding rectangle.
 * The body includes a set of controls and a vector display.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class EFieldDetectorBodyNode extends PhetPNode {

    private static final Font TITLE_FONT = new PhetFont( Font.BOLD, 18 );
    private static final Color TITLE_COLOR = Color.WHITE;

    private static final Color BODY_COLOR = new Color( 75, 75, 75 );
    private static final double BODY_CORNER_RADIUS = 15;
    private static final int BODY_X_MARGIN = 5;
    private static final int BODY_Y_MARGIN = 5;
    private static final int BODY_X_SPACING = 2;
    private static final int BODY_Y_SPACING = 4;

    private static final PDimension VIEWPORT_SIZE = new PDimension( 175, 200 );
    private static final PDimension SIMPLIFIED_VIEWPORT_SIZE = new PDimension( 85, VIEWPORT_SIZE.getHeight() );
    private static final Color VIEWPORT_BACKGROUND = Color.WHITE;

    private static final Dimension VECTOR_ARROW_HEAD_SIZE = new Dimension( 30, 20 );
    private static final int VECTOR_ARROW_TAIL_WIDTH = 10;

    private static final Font VALUE_FONT = new PhetFont( 14 );
    private static final NumberFormat VALUE_FORMAT = new DecimalFormat( "0" );
    private static final Color VALUE_COLOR = Color.BLACK;

    private static final Font CONTROL_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color CONTROL_COLOR = Color.WHITE;

    private final ViewportNode viewportNode; // display area for vectors and values
    private final Point2D connectionOffset; // offset for connection point of wire that attaches probe to body
    private final Property<Boolean> zoomInEnabledProperty, zoomOutEnabledProperty;

    /**
     * Constructor
     *
     * @param detector                 model of the detector
     * @param mvt                      model-view transform
     * @param vectorReferenceMagnitude used for calibrating the initial scale of the vector display
     * @param simplified               if true, disabled some feature of the detector
     */
    public EFieldDetectorBodyNode( final EFieldDetector detector, final CLModelViewTransform3D mvt, double vectorReferenceMagnitude, boolean simplified ) {

        this.zoomInEnabledProperty = new Property<Boolean>( false );
        this.zoomOutEnabledProperty = new Property<Boolean>( false );

        // title that appears at the top
        PText titleNode = new PText( CLStrings.ELECTRIC_FIELD ) {{
            setTextPaint( TITLE_COLOR );
            setFont( TITLE_FONT );
        }};

        // close button
        PImage closeButtonNode = new PImage( CLImages.CLOSE_BUTTON ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override
                public void mouseReleased( PInputEvent event ) {
                    detector.visibleProperty.set( false );
                }
            } );
        }};

        // viewport
        viewportNode = new ViewportNode( detector, vectorReferenceMagnitude, zoomInEnabledProperty, zoomOutEnabledProperty, simplified );
        viewportNode.setSimplified( simplified );

        // control panel
        ControlPanelNode controlPanelNode = new ControlPanelNode( detector, viewportNode, zoomInEnabledProperty, zoomOutEnabledProperty, simplified );

        // background
        double width = controlPanelNode.getFullBoundsReference().getWidth() + viewportNode.getFullBoundsReference().getWidth() + ( 2 * BODY_X_MARGIN ) + BODY_X_SPACING;
        double height = titleNode.getFullBoundsReference().getHeight() + BODY_Y_SPACING + Math.max( controlPanelNode.getFullBoundsReference().getHeight(), viewportNode.getFullBoundsReference().getHeight() ) + ( 2 * BODY_Y_MARGIN );
        PPath backgroundNode = new PPath( new RoundRectangle2D.Double( 0, 0, width, height, BODY_CORNER_RADIUS, BODY_CORNER_RADIUS ) );
        backgroundNode.setPaint( BODY_COLOR );
        backgroundNode.setStroke( null );

        // rendering order
        addChild( backgroundNode );
        addChild( titleNode );
        addChild( closeButtonNode );
        addChild( controlPanelNode );
        addChild( viewportNode );

        // layout
        {
            double x = 0;
            double y = 0;
            backgroundNode.setOffset( x, y );
            // title centered at top
            x = backgroundNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
            y = BODY_Y_MARGIN;
            titleNode.setOffset( x, y );
            // close button in upper right
            x = backgroundNode.getFullBoundsReference().getMaxX() - closeButtonNode.getFullBoundsReference().getWidth() - BODY_X_MARGIN;
            y = backgroundNode.getFullBoundsReference().getMinY() + BODY_Y_MARGIN;
            closeButtonNode.setOffset( x, y );
            // control panel on left
            x = BODY_X_MARGIN;
            y = titleNode.getFullBoundsReference().getMaxY() + BODY_Y_SPACING;
            controlPanelNode.setOffset( x, y );
            // viewport on right
            x = controlPanelNode.getFullBoundsReference().getMaxX() + BODY_X_SPACING;
            y = controlPanelNode.getYOffset();
            viewportNode.setOffset( x, y );
        }

        // wire connects to the left center of the detector body
        connectionOffset = new Point2D.Double( 0, getFullBoundsReference().getHeight() / 2 );

        // interactivity
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new WorldLocationDragHandler( detector.bodyLocationProperty, this, mvt ) );

        // location
        detector.bodyLocationProperty.addObserver( new SimpleObserver() {
            public void update() {
                setOffset( mvt.modelToView( detector.bodyLocationProperty.get() ) );
            }
        } );
    }

    // Gets the offset for connecting wire that attached to probe.
    public Point2D getConnectionOffset() {
        return new Point2D.Double( connectionOffset.getX(), connectionOffset.getY() );
    }

    // Control panel, contains 3 subpanels
    private static class ControlPanelNode extends PNode {

        public ControlPanelNode( EFieldDetector detector, final ViewportNode viewportNode,
                                 Property<Boolean> zoomInEnabledProperty, Property<Boolean> zoomOutEnabledProperty, boolean simplified ) {

            // Vector visibility controls
            PSwing showVectorsPSwing = new PSwing( new ShowVectorsPanel( detector ) );
            showVectorsPSwing.setVisible( !simplified );

            // Zoom controls
            ActionListener zoomActionListener = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    viewportNode.zoom();
                }
            };
            ZoomPanelNode zoomPanelNode = new ZoomPanelNode( zoomActionListener, zoomActionListener, zoomInEnabledProperty, zoomOutEnabledProperty );

            // Show Values check box
            PSwing showValuesPSwing = new PSwing( new DetectorCheckBox( CLStrings.SHOW_VALUES, detector.valuesVisibleProperty, CONTROL_COLOR ) );

            // rendering order
            addChild( showVectorsPSwing );
            addChild( zoomPanelNode );
            addChild( showValuesPSwing );

            // layout
            // vector controls at upper left
            double x = 0;
            double y = 0;
            showVectorsPSwing.setOffset( x, y );
            // zoom controls below vector controls
            x = BODY_X_MARGIN;
            y = showVectorsPSwing.getFullBoundsReference().getMaxY() + ( 2 * BODY_Y_SPACING ) - PNodeLayoutUtils.getOriginYOffset( zoomPanelNode );
            zoomPanelNode.setOffset( x, y );
            // "Show values" control below zoom controls
            x = BODY_X_MARGIN;
            y = zoomPanelNode.getFullBoundsReference().getMaxY() + BODY_Y_SPACING;
            showValuesPSwing.setOffset( x, y );
        }
    }

    /*
     * Panel with check boxes for vectors.
     */
    private static class ShowVectorsPanel extends GridPanel {

        public ShowVectorsPanel( final EFieldDetector detector ) {
            setBackground( BODY_COLOR );

            // components
            JLabel showVectorsLabel = new JLabel( CLStrings.SHOW_VECTORS ) {{
                setFont( CONTROL_FONT );
                setForeground( CONTROL_COLOR );
            }};
            JCheckBox plateCheckBox = new DetectorCheckBox( CLStrings.PLATE, detector.plateVisibleProperty, CLPaints.PLATE_EFIELD_VECTOR );
            JCheckBox dielectricCheckBox = new DetectorCheckBox( CLStrings.DIELECTRIC, detector.dielectricVisibleProperty, CLPaints.DIELECTRIC_EFIELD_VECTOR );
            JCheckBox sumCheckBox = new DetectorCheckBox( CLStrings.SUM, detector.sumVisibleProperty, CLPaints.SUM_EFIELD_VECTOR );

            // layout
            setAnchor( Anchor.WEST );
            int row = 0;
            int column = 0;
            add( showVectorsLabel, row++, column );
            add( plateCheckBox, row++, column );
            add( dielectricCheckBox, row++, column );
            add( sumCheckBox, row++, column );
        }
    }

    /*
     * Viewport where the vectors are displayed.
     * Vectors are clipped to this area.
     * Vector size is computed relative to a specified reference magnitude.
     */
    private static final class ViewportNode extends PClip {

        private final EFieldDetector detector;
        private final Property<Boolean> zoomInEnabledProperty, zoomOutEnabledProperty;

        private final PComposite sceneNode; // the portion of the scenegraph visible in the viewport
        private final FieldVectorNode plateVectorNode, dielectricVectorNode, sumVectorNode;
        private final FieldValueNode plateValueNode, dielectricValueNode, sumValueNode;
        private double vectorsScale;
        private boolean simplified;

        public ViewportNode( final EFieldDetector detector, double vectorReferenceMagnitude,
                             Property<Boolean> zoomInEnabledProperty, Property<Boolean> zoomOutEnabledProperty, boolean simplified ) {

            PDimension size = ( simplified ? SIMPLIFIED_VIEWPORT_SIZE : VIEWPORT_SIZE );
            setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
            setPaint( VIEWPORT_BACKGROUND );
            setStroke( null );

            this.detector = detector;
            this.zoomInEnabledProperty = zoomInEnabledProperty;
            this.zoomOutEnabledProperty = zoomOutEnabledProperty;
            vectorsScale = 1;
            simplified = false;

            // vectors
            plateVectorNode = new FieldVectorNode( CLPaints.PLATE_EFIELD_VECTOR, vectorReferenceMagnitude );
            dielectricVectorNode = new FieldVectorNode( CLPaints.DIELECTRIC_EFIELD_VECTOR, vectorReferenceMagnitude );
            sumVectorNode = new FieldVectorNode( CLPaints.SUM_EFIELD_VECTOR, vectorReferenceMagnitude );

            // values
            plateValueNode = new FieldValueNode( CLStrings.PLATE, CLPaints.PLATE_EFIELD_VECTOR );
            dielectricValueNode = new FieldValueNode( CLStrings.DIELECTRIC, CLPaints.DIELECTRIC_EFIELD_VECTOR );
            sumValueNode = new FieldValueNode( CLStrings.SUM, CLPaints.SUM_EFIELD_VECTOR );

            /*
             * Everything visible in the viewport is a child of this intermediate node.
             * This allows us to compute the zoom scaling factor by comparing the scene bounds to the size of the viewport.
             */
            sceneNode = new PComposite();
            addChild( sceneNode );
            sceneNode.addChild( plateVectorNode );
            sceneNode.addChild( dielectricVectorNode );
            sceneNode.addChild( sumVectorNode );
            sceneNode.addChild( plateValueNode );
            sceneNode.addChild( dielectricValueNode );
            sceneNode.addChild( sumValueNode );

            // observe vector changes
            SimpleObserver vectorsObserver = new SimpleObserver() {
                public void update() {
                    updateVectors();
                }
            };
            detector.addPlateVectorObserver( vectorsObserver );
            detector.addDielectricVectorObserver( vectorsObserver );
            detector.addSumVectorObserver( vectorsObserver );

            // observe visibility changes
            RichSimpleObserver visibilityObserver = new RichSimpleObserver() {
                public void update() {
                    updateVisibility();
                }
            };
            visibilityObserver.observe( detector.plateVisibleProperty, detector.dielectricVisibleProperty, detector.sumVisibleProperty, detector.valuesVisibleProperty );

            zoom();
        }

        /**
         * A simplified detector shows less stuff.
         *
         * @param simplified
         */
        public void setSimplified( boolean simplified ) {
            if ( simplified != this.simplified ) {
                this.simplified = simplified;
                updateVisibility();
            }
        }

        /*
         * Zooms to the optimum scale that makes everything visible in the viewport,
         * with a little bit of whitespace at the top and bottom of the viewport.
         * The zoom factor computation is complicated by the fact that the vectors scale,
         * while their labels and values do not.
         * Zooming is based solely on vertical dimensions (height); width of the scene is irrelevant.
         */
        private void zoom() {
            final double plateVectorHeight = plateVectorNode.getFullBoundsReference().getHeight();
            final double labelsAndValuesHeight = getSceneHeight() - plateVectorHeight;
            final double percentOfViewPortToFill = 0.9;
            double zoomFactor = ( percentOfViewPortToFill * ( getViewportHeight() - labelsAndValuesHeight ) ) / ( getSceneHeight() - labelsAndValuesHeight );
            vectorsScale = vectorsScale * zoomFactor;
            updateVectors();
        }

        // True when the scene is zoomable, and the scene is clipped.
        private boolean canZoomOut() {
            boolean canZoomOut = false;
            if ( isZoomable() ) {
                canZoomOut = ( getViewportHeight() < getSceneHeight() );
            }
            return canZoomOut;
        }

        // True when the scene is zoomable, and the scene fills less than some portion of the viewport.
        private boolean canZoomIn() {
            boolean canZoomIn = false;
            if ( isZoomable() ) {
                final double percentOfViewportThatIsFilled = 0.75;
                canZoomIn = ( percentOfViewportThatIsFilled * getViewportHeight() > getSceneHeight() );
            }
            return canZoomIn;
        }

        private double getViewportHeight() {
            return this.getFullBoundsReference().getHeight();
        }

        // Gets the height of the scene, what's being displayed in the viewport.
        private double getSceneHeight() {
            return sceneNode.getFullBoundsReference().getHeight();
        }

        // True when the scene contain a least one visible, non-zero vector.
        private boolean isZoomable() {
            return hasVisibleVector() && hasNonZeroVector();
        }

        // True if some vector is non-zero.
        private boolean hasNonZeroVector() {
            return detector.getPlateVector() != 0 || detector.getDielectricVector() != 0 || detector.getSumVector() != 0;
        }

        // True if some vector is visible.
        private boolean hasVisibleVector() {
            return plateVectorNode.isVisible() || dielectricVectorNode.isVisible() || sumVectorNode.isVisible();
        }

        // Updates vectors and numeric values.
        private void updateVectors() {

            plateVectorNode.setXY( 0, vectorsScale * detector.getPlateVector() );
            plateValueNode.setValue( detector.getPlateVector() );

            dielectricVectorNode.setXY( 0, vectorsScale * -detector.getDielectricVector() ); // change sign because dielectric vector points in opposite direction
            dielectricValueNode.setValue( detector.getDielectricVector() );

            sumVectorNode.setXY( 0, vectorsScale * detector.getSumVector() );
            sumValueNode.setValue( detector.getSumVector() );

            updateLayout();
        }

        /*
         * Updates visibility of vectors and numeric values.
         * When the vector display is simplified, only the Plate vector is shown.
         */
        private void updateVisibility() {

            // vectors
            final boolean plateVisible = detector.plateVisibleProperty.get();
            plateVectorNode.setVisible( plateVisible );
            plateValueNode.setVisible( plateVisible );

            final boolean dielectricVisible = detector.dielectricVisibleProperty.get();
            dielectricVectorNode.setVisible( dielectricVisible );
            dielectricValueNode.setVisible( dielectricVisible );

            boolean sumVisible = detector.sumVisibleProperty.get();
            sumVectorNode.setVisible( sumVisible );
            sumValueNode.setVisible( sumVisible );

            // values
            final boolean valuesVisible = detector.valuesVisibleProperty.get();
            plateValueNode.setValueVisible( valuesVisible );
            dielectricValueNode.setValueVisible( valuesVisible );
            sumValueNode.setValueVisible( valuesVisible );

            // When the vector display is simplified, only the Plate vector is shown.
            if ( simplified ) {
                dielectricVectorNode.setVisible( false );
                dielectricValueNode.setVisible( false );
                sumVectorNode.setVisible( false );
                sumValueNode.setVisible( false );
            }

            updateLayout();
        }

        // dynamic layout
        private void updateLayout() {

            double x, y;

            // vectors
            {
                /*
                 * Horizontal spacing between plate and dielectric vector centers.
                 * This is zero for the simplified case, since only the Plate vector
                 * is visible, and we want it to be horizontally centered in the display.
                 */
                final double xSpacing = simplified ? 0 : this.getBoundsReference().getWidth() / 4;

                // plate vector is vertically centered
                x = this.getBoundsReference().getCenterX() - xSpacing;
                y = this.getBoundsReference().getCenterY() - ( plateVectorNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( plateVectorNode );
                plateVectorNode.setOffset( x, y );

                // sum vector is aligned with tail of plate vector
                x = this.getBoundsReference().getCenterX() + xSpacing;
                y = plateVectorNode.getYOffset();
                sumVectorNode.setOffset( x, y );

                // dielectric vector fills in space above or below sum vector
                x = sumVectorNode.getXOffset();
                if ( sumVectorNode.getVector().getY() < 0 ) {
                    y = sumVectorNode.getFullBoundsReference().getMinY() - dielectricVectorNode.getFullBoundsReference().getHeight(); // above
                }
                else {
                    y = sumVectorNode.getFullBoundsReference().getMaxY() + dielectricVectorNode.getFullBoundsReference().getHeight(); // below
                }
                dielectricVectorNode.setOffset( x, y );
            }

            // labeled values, all placed at vector tails, horizontally centered
            {
                final double ySpacing = 2; // space between vector tail and label

                // plate label
                x = plateVectorNode.getFullBoundsReference().getCenterX();
                if ( plateVectorNode.getVector().getY() >= 0 ) {
                    y = plateVectorNode.getFullBoundsReference().getMinY() - plateValueNode.getFullBoundsReference().getHeight() - ySpacing;
                }
                else {
                    y = plateVectorNode.getFullBoundsReference().getMaxY() + ySpacing;
                }
                plateValueNode.setOffset( x, y );

                // sum label
                x = sumVectorNode.getFullBoundsReference().getCenterX();
                if ( sumVectorNode.getVector().getY() >= 0 ) {
                    y = sumVectorNode.getFullBoundsReference().getMinY() - sumValueNode.getFullBoundsReference().getHeight() - ySpacing;
                }
                else {
                    y = sumVectorNode.getFullBoundsReference().getMaxY() + ySpacing;
                }
                sumValueNode.setOffset( x, y );

                // dielectric label
                x = dielectricVectorNode.getFullBoundsReference().getCenterX();
                if ( dielectricVectorNode.getVector().getY() > 0 || ( dielectricVectorNode.getVector().getY() == 0 && sumVectorNode.getVector().getY() < 0 ) ) {
                    // above vector
                    y = dielectricVectorNode.getFullBoundsReference().getMinY() - dielectricValueNode.getFullBoundsReference().getHeight() - ySpacing;
                }
                else {
                    // below vector
                    y = dielectricVectorNode.getFullBoundsReference().getMaxY() + ySpacing;
                }
                dielectricValueNode.setOffset( x, y );
            }

            updateZoomEnabled();
        }

        private void updateZoomEnabled() {
            zoomInEnabledProperty.set( canZoomIn() );
            zoomOutEnabledProperty.set( canZoomOut() );
        }
    }

    /*
     * Encapsulates the "look" of check boxes in the detector.
     */
    private static class DetectorCheckBox extends PropertyCheckBox {
        public DetectorCheckBox( String text, Property<Boolean> property, Color foreground ) {
            super( text, property );
            setOpaque( false );
            setFont( CONTROL_FONT );
            setForeground( foreground );
        }
    }

    /*
     * Field vector.
     */
    private static class FieldVectorNode extends Vector2DNode {

        public FieldVectorNode( Color color, double vectorReferenceMagnitude ) {
            super( 0, 0, vectorReferenceMagnitude, 1 );
            setArrowFillPaint( color );
            setHeadSize( VECTOR_ARROW_HEAD_SIZE );
            setTailWidth( VECTOR_ARROW_TAIL_WIDTH );
        }
    }

    /*
     * Displays a labeled vector value.
     * Label is above value, and they are horizontally centered.
     * Origin is at the top center of the bounding rectangle.
     */
    private static class FieldValueNode extends PComposite {

        private final PText labelNode, valueNode;

        public FieldValueNode( String label, Color backgroundColor ) {

            labelNode = new PText( label );
            labelNode.setPaint( backgroundColor );
            labelNode.setTextPaint( VALUE_COLOR );
            labelNode.setFont( VALUE_FONT );
            addChild( labelNode );

            valueNode = new PText();
            valueNode.setPaint( backgroundColor );
            valueNode.setTextPaint( VALUE_COLOR );
            valueNode.setFont( VALUE_FONT );
            addChild( valueNode );

            setValue( 0 );
        }

        public void setValue( double value ) {
            String valueString = VALUE_FORMAT.format( Math.abs( value ) );
            valueNode.setText( MessageFormat.format( CLStrings.PATTERN_VALUE_UNITS, valueString, CLStrings.VOLTS_PER_METER ) );
            updateLayout();
        }

        /*
         * Changes visibility of the value by adding/removing it from the scenegraph,
         * so that it only contributes to bounds computations when visible.
         */
        public void setValueVisible( boolean valueVisible ) {
            if ( valueVisible ) {
                addChild( valueNode );
            }
            else {
                if ( indexOfChild( valueNode ) != -1 ) {
                    removeChild( valueNode );
                }
            }
        }

        private void updateLayout() {
            double x = -labelNode.getFullBoundsReference().getWidth() / 2;
            double y = 0;
            labelNode.setOffset( x, y );
            x = -valueNode.getFullBoundsReference().getWidth() / 2;
            y = labelNode.getFullBoundsReference().getMaxY() - 1;
            valueNode.setOffset( x, y );
        }
    }

    /*
     * Panel with zoom controls.
     */
    private static class ZoomPanelNode extends PNode {

        public ZoomPanelNode( ActionListener zoomInActionListener, ActionListener zoomOutActionListener,
                              final Property<Boolean> zoomInEnabledProperty, final Property<Boolean> zoomOutEnabledProperty ) {

            PText labelNode = new PText( CLStrings.ZOOM );
            addChild( labelNode );
            labelNode.setFont( CONTROL_FONT );
            labelNode.setTextPaint( CONTROL_COLOR );

            final ZoomInButtonNode zoomInButton = new ZoomInButtonNode();
            addChild( zoomInButton );
            zoomInButton.addActionListener( zoomInActionListener );

            final ZoomOutButtonNode zoomOutButton = new ZoomOutButtonNode();
            addChild( zoomOutButton );
            zoomOutButton.addActionListener( zoomOutActionListener );

            // layout
            double x = labelNode.getFullBoundsReference().getMaxX() + 4;
            double y = labelNode.getFullBoundsReference().getMaxY() - zoomInButton.getFullBoundsReference().getHeight();
            zoomInButton.setOffset( x, y );
            y = zoomInButton.getFullBoundsReference().getMaxY() + 3;
            zoomOutButton.setOffset( x, y );

            zoomInEnabledProperty.addObserver( new SimpleObserver() {
                public void update() {
                    zoomInButton.setEnabled( zoomInEnabledProperty.get() );
                }
            } );
            zoomOutEnabledProperty.addObserver( new SimpleObserver() {
                public void update() {
                    zoomOutButton.setEnabled( zoomOutEnabledProperty.get() );
                }
            } );
        }
    }
}
