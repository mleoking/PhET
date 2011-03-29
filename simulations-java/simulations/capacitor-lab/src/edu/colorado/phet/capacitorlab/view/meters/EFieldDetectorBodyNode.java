// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.drag.LocationDragHandler;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.EFieldDetector;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.Vector2DNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
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
public class EFieldDetectorBodyNode extends PhetPNode {

    private static final Font TITLE_FONT = new PhetFont( Font.BOLD, 18 );
    private static final Color TITLE_COLOR = Color.WHITE;

    private static final Color BODY_COLOR = new Color( 75, 75, 75 );
    private static final double BODY_CORNER_RADIUS = 15;
    private static final int BODY_X_MARGIN = 5;
    private static final int BODY_Y_MARGIN = 5;
    private static final int BODY_X_SPACING = 2;
    private static final int BODY_Y_SPACING = 4;

    private static final PDimension VECTOR_DISPLAY_SIZE = new PDimension( 200, 200 );
    private static final Color VECTOR_DISPLAY_BACKGROUND = Color.WHITE;
    private static final double VECTOR_REFERENCE_MAGNITUDE = BatteryCapacitorCircuit.getMaxPlatesDielectricEFieldWithBattery(); // reference for the max magnification
    private static final double VECTOR_REFERENCE_LENGTH = 3 * VECTOR_DISPLAY_SIZE.getHeight();
    private static final Dimension VECTOR_ARROW_HEAD_SIZE = new Dimension( 30, 20 );
    private static final int VECTOR_ARROW_TAIL_WIDTH = 10;

    private static final Font VALUE_FONT = new PhetFont( 14 );
    private static final NumberFormat VALUE_FORMAT = new DecimalFormat( "0" );
    private static final Color VALUE_COLOR = Color.BLACK;

    private static final Font CONTROL_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color CONTROL_COLOR = Color.WHITE;

    private static final int ZOOM_FACTOR = 5;
    private static final IntegerRange ZOOM_LEVEL_RANGE = new IntegerRange( 0, 4, 4 ); // start fully zoomed in

    private final PSwing showVectorsPSwing;
    private final VectorDisplayNode vectorDisplayNode;
    private final Point2D connectionOffset; // offset for connection point of wire that attaches probe to body
    private final ZoomPanel zoomPanel;

    public EFieldDetectorBodyNode( final EFieldDetector detector, final CLModelViewTransform3D mvt ) {

        // title that appears at the top
        PText titleNode = new PText( CLStrings.ELECTRIC_FIELD );
        titleNode.setTextPaint( TITLE_COLOR );
        titleNode.setFont( TITLE_FONT );

        // close button
        PImage closeButtonNode = new PImage( CLImages.CLOSE_BUTTON );
        closeButtonNode.addInputEventListener( new CursorHandler() );
        closeButtonNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseReleased( PInputEvent event ) {
                detector.visible.setValue( false );
            }
        } );

        // display area for vectors and values
        vectorDisplayNode = new VectorDisplayNode( detector );

        // Vector controls
        ShowVectorsPanel showVectorsPanel = new ShowVectorsPanel( detector );
        showVectorsPSwing = new PSwing( showVectorsPanel );

        // Zoom controls
        zoomPanel = new ZoomPanel( ZOOM_LEVEL_RANGE );
        zoomPanel.addZoomInListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                vectorDisplayNode.zoomIn();
            }
        });
        zoomPanel.addZoomOutListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                vectorDisplayNode.zoomOut();
            }
        });
        PSwing zoomPSwing = new PSwing( zoomPanel );

        // Show Values check box
        final JCheckBox showValuesCheckBox = new JCheckBox( CLStrings.SHOW_VALUES );
        showValuesCheckBox.setOpaque( false );
        showValuesCheckBox.setFont( CONTROL_FONT );
        showValuesCheckBox.setForeground( CONTROL_COLOR );
        showValuesCheckBox.setSelected( detector.isValuesVisible() );
        showValuesCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                detector.setValuesVisible( showValuesCheckBox.isSelected() );
            }
        } );
        PSwing showValuesPSwing = new PSwing( showValuesCheckBox );

        // background
        double maxControlWidth = Math.max( showVectorsPSwing.getFullBoundsReference().getWidth(), showValuesPSwing.getFullBoundsReference().getWidth() );
        double width = maxControlWidth + vectorDisplayNode.getFullBoundsReference().getWidth() + ( 2 * BODY_X_MARGIN ) + BODY_X_SPACING;
        final double controlsHeight = showVectorsPSwing.getFullBoundsReference().getHeight() + showValuesPSwing.getFullBoundsReference().getHeight();
        double height = titleNode.getFullBoundsReference().getHeight() + BODY_Y_SPACING + Math.max( controlsHeight, vectorDisplayNode.getFullBoundsReference().getHeight() ) + ( 2 * BODY_Y_MARGIN );
        PPath backgroundNode = new PPath( new RoundRectangle2D.Double( 0, 0, width, height, BODY_CORNER_RADIUS, BODY_CORNER_RADIUS ) );
        backgroundNode.setPaint( BODY_COLOR );
        backgroundNode.setStroke( null );

        // rendering order
        addChild( backgroundNode );
        addChild( titleNode );
        addChild( closeButtonNode );
        addChild( showVectorsPSwing );
        addChild( zoomPSwing );
        addChild( showValuesPSwing );
        addChild( vectorDisplayNode );

        // layout
        double x = 0;
        double y = 0;
        backgroundNode.setOffset( x, y );
        // title
        x = backgroundNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        y = BODY_Y_MARGIN;
        titleNode.setOffset( x, y );
        // close button
        x = backgroundNode.getFullBoundsReference().getMaxX() - closeButtonNode.getFullBoundsReference().getWidth() - BODY_X_MARGIN;
        y = backgroundNode.getFullBoundsReference().getMinY() + BODY_Y_MARGIN;
        closeButtonNode.setOffset( x, y );
        // vector controls in upper left
        x = BODY_X_MARGIN;
        y = titleNode.getFullBoundsReference().getMaxY() + BODY_Y_SPACING;
        showVectorsPSwing.setOffset( x, y );
        // zoom controls below vector controls
        x = showVectorsPSwing.getFullBoundsReference().getMinX();
        y = showVectorsPSwing.getFullBoundsReference().getMaxY() + ( 2 * BODY_Y_SPACING );
        zoomPSwing.setOffset( x, y );
        // "Show values" control at lower left
        x = BODY_X_MARGIN;
        y = backgroundNode.getFullBoundsReference().getMaxY() - showValuesPSwing.getFullBoundsReference().getHeight() - BODY_Y_MARGIN;
        showValuesPSwing.setOffset( x, y );
        // vectors
        x = BODY_X_MARGIN + maxControlWidth + BODY_X_SPACING;
        y = showVectorsPSwing.getYOffset();
        vectorDisplayNode.setOffset( x, y );

        // wire connects to the left center of the detector body
        connectionOffset = new Point2D.Double( 0, getFullBoundsReference().getHeight() / 2 );

        // interactivity
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new LocationDragHandler( this, mvt ) {

            protected Point3D getModelLocation() {
                return detector.bodyLocation.getValue();
            }

            protected void setModelLocation( Point3D location ) {
                detector.bodyLocation.setValue( location );
            }
        });

        // observers
        {
            // location
            detector.bodyLocation.addObserver( new SimpleObserver() {
                public void update() {
                    setOffset( mvt.modelToView( detector.bodyLocation.getValue() ) );
                }
            } );

            // show values check box
            detector.addValuesVisibleObserver( new SimpleObserver() {
                public void update() {
                    showValuesCheckBox.setSelected( detector.isValuesVisible() );
                }
            } );
        }
    }

    /**
     * Calling this with true provides a simplified E-Field detector,
     * with fewer controls and fewer visible vectors.
     * @param simplified
     */
    public void setSimplified( boolean simplified ) {
        showVectorsPSwing.setVisible( !simplified );
        vectorDisplayNode.setSimplified( simplified );
    }

    public Point2D getConnectionOffset() {
        return new Point2D.Double( connectionOffset.getX(), connectionOffset.getY() );
    }

    /*
     * Panel with check boxes for vectors.
     */
    private static class ShowVectorsPanel extends GridPanel {

        private final JCheckBox plateCheckBox, dielectricCheckBox, sumCheckBox;

        public ShowVectorsPanel( final EFieldDetector detector ) {
            setBackground( BODY_COLOR );

            JLabel showVectorsLabel = new JLabel( CLStrings.SHOW_VECTORS );
            showVectorsLabel.setFont( CONTROL_FONT );
            showVectorsLabel.setForeground( CONTROL_COLOR );

            plateCheckBox = new JCheckBox( CLStrings.PLATE );
            plateCheckBox.setOpaque( false );
            plateCheckBox.setFont( CONTROL_FONT );
            plateCheckBox.setForeground( CLPaints.PLATE_EFIELD_VECTOR );
            plateCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    detector.setPlateVisible( plateCheckBox.isSelected() );
                }
            } );

            dielectricCheckBox = new JCheckBox( CLStrings.DIELECTRIC );
            dielectricCheckBox.setOpaque( false );
            dielectricCheckBox.setFont( CONTROL_FONT );
            dielectricCheckBox.setForeground( CLPaints.DIELECTRIC_EFIELD_VECTOR );
            dielectricCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    detector.setDielectricVisible( dielectricCheckBox.isSelected() );
                }
            } );

            sumCheckBox = new JCheckBox( CLStrings.SUM );
            sumCheckBox.setOpaque( false );
            sumCheckBox.setFont( CONTROL_FONT );
            sumCheckBox.setForeground( CLPaints.SUM_EFIELD_VECTOR );
            sumCheckBox.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    detector.setSumVisible( sumCheckBox.isSelected() );
                }
            } );

            // layout
            setAnchor( Anchor.WEST );
            int row = 0;
            int column = 0;
            add( showVectorsLabel, row++, column );
            add( plateCheckBox, row++, column );
            add( dielectricCheckBox, row++, column );
            add( sumCheckBox, row++, column );

            // observers
            {
                detector.addPlateVisibleObserver( new SimpleObserver() {
                    public void update() {
                        plateCheckBox.setSelected( detector.isPlateVisible() );
                    }
                } );

                detector.addDielectricVisibleObserver( new SimpleObserver() {
                    public void update() {
                        dielectricCheckBox.setSelected( detector.isDielectricVisible() );
                    }
                } );

                detector.addSumVisibleObserver( new SimpleObserver() {
                    public void update() {
                        sumCheckBox.setSelected( detector.isSumVectorVisible() );
                    }
                } );
            }
        }
    }

    /*
     * Rectangular area where the vectors are displayed.
     * Vectors are clipped to this area.
     */
    private static final class VectorDisplayNode extends PClip {

        private final EFieldDetector detector;
        private final FieldVectorNode plateVectorNode, dielectricVectorNode, sumVectorNode;
        private final FieldValueNode plateValueNode, dielectricValueNode, sumValueNode;
        private double zoomMultiplier;
        private boolean simplified;

        public VectorDisplayNode( final EFieldDetector detector ) {

            setPathTo( new Rectangle2D.Double( 0, 0, VECTOR_DISPLAY_SIZE.getWidth(), VECTOR_DISPLAY_SIZE.getHeight() ) );
            setPaint( VECTOR_DISPLAY_BACKGROUND );
            setStroke( null );

            this.detector = detector;
            zoomMultiplier = 1;
            simplified = false;

            // vectors
            plateVectorNode = new FieldVectorNode( CLPaints.PLATE_EFIELD_VECTOR );
            dielectricVectorNode = new FieldVectorNode( CLPaints.DIELECTRIC_EFIELD_VECTOR );
            sumVectorNode = new FieldVectorNode( CLPaints.SUM_EFIELD_VECTOR );

            // values
            plateValueNode = new FieldValueNode( CLStrings.PLATE, CLPaints.PLATE_EFIELD_VECTOR );
            dielectricValueNode = new FieldValueNode( CLStrings.DIELECTRIC, CLPaints.DIELECTRIC_EFIELD_VECTOR );
            sumValueNode = new FieldValueNode( CLStrings.SUM, CLPaints.SUM_EFIELD_VECTOR );

            // rendering order
            addChild( plateVectorNode );
            addChild( dielectricVectorNode );
            addChild( sumVectorNode );
            addChild( plateValueNode );
            addChild( dielectricValueNode );
            addChild( sumValueNode );

            // listen to detector properties
            detector.addPlateVectorObserver( new SimpleObserver() {
                public void update() {
                    updateVectors();
                }
            } );
            detector.addDielectricVectorObserver( new SimpleObserver() {
                public void update() {
                    updateVectors();
                }
            } );
            detector.addSumVectorObserver( new SimpleObserver() {
                public void update() {
                    updateVectors();
                }
            } );
            detector.addPlateVisibleObserver( new SimpleObserver() {
                public void update() {
                    updateVisibility();
                }
            } );
            detector.addDielectricVisibleObserver( new SimpleObserver() {
                public void update() {
                    updateVisibility();
                }
            } );
            detector.addSumVisibleObserver( new SimpleObserver() {
                public void update() {
                    updateVisibility();
                }
            } );
            detector.addValuesVisibleObserver( new SimpleObserver() {
                public void update() {
                    updateVisibility();
                }
            } );

            updateLayout();
        }

        /**
         * When the vector display is simplified, only the Plate vector is shown.
         * @param simplified
         */
        public void setSimplified( boolean simplified ) {
            if ( simplified != this.simplified ) {
                this.simplified = simplified;
                updateVisibility();
            }
        }

        public void zoomIn() {
            zoomMultiplier *= ZOOM_FACTOR;
            updateVectors();
        }

        public void zoomOut() {
            zoomMultiplier /= ZOOM_FACTOR;
            updateVectors();
        }

        private void updateVectors() {

            plateVectorNode.setXY( 0, zoomMultiplier * detector.getPlateVector() );
            plateValueNode.setValue( detector.getPlateVector() );

            dielectricVectorNode.setXY( 0, zoomMultiplier * -detector.getDielectricVector() ); // change sign because dielectric vector points in opposite direction
            dielectricValueNode.setValue( detector.getDielectricVector() );

            sumVectorNode.setXY( 0, zoomMultiplier * detector.getSumVector() );
            sumValueNode.setValue( detector.getSumVector() );

            updateLayout();
        }

        private void updateVisibility() {

            plateVectorNode.setVisible( detector.isPlateVisible() );
            plateValueNode.setVisible( detector.isPlateVisible() );
            plateValueNode.setValueVisible( detector.isValuesVisible() );

            dielectricVectorNode.setVisible( detector.isDielectricVisible() && !simplified );
            dielectricValueNode.setVisible( detector.isDielectricVisible() && !simplified );
            dielectricValueNode.setValueVisible( detector.isValuesVisible() && !simplified );

            sumVectorNode.setVisible( detector.isSumVectorVisible() && !simplified );
            sumValueNode.setVisible( detector.isSumVectorVisible() && !simplified );
            sumValueNode.setValueVisible( detector.isValuesVisible() && !simplified );

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
        }
    }

    /*
     * Field vector.
     */
    private static class FieldVectorNode extends Vector2DNode {

        public FieldVectorNode( Color color ) {
            super( 0, 0, VECTOR_REFERENCE_MAGNITUDE, VECTOR_REFERENCE_LENGTH );
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
    private static class ZoomPanel extends GridPanel {

        private final IntegerRange zoomLevelRange;
        private final JButton zoomInButton, zoomOutButton;
        private int zoomLevel;

        public ZoomPanel( IntegerRange zoomLevelRange ) {
            setOpaque( false );

            this.zoomLevelRange = new IntegerRange( zoomLevelRange );

            JLabel label = new JLabel( CLStrings.ZOOM );
            label.setFont( CONTROL_FONT );
            label.setForeground( CONTROL_COLOR );

            zoomInButton = new JButton( ZoomButtonNode.getZoomInIcon( 1.5 /* scale */ ) );
            zoomInButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setZoomLevel( zoomLevel + 1 );
                }
            });

            zoomOutButton = new JButton( ZoomButtonNode.getZoomOutIcon( 1.5 /* scale */ ) );
            zoomOutButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setZoomLevel( zoomLevel - 1 );
                }
            });

            // layout
            setAnchor( Anchor.WEST );
            int row = 0;
            int column = 0;
            add( label, row++, column );
            add( zoomInButton, row, column++ );
            add( zoomOutButton, row, column++ );

            // default state
            setZoomLevel( zoomLevelRange.getDefault() );
        }

        private void setZoomLevel( int zoomLevel ) {
            this.zoomLevel = zoomLevel;
            setZoomInEnabled( zoomLevel != zoomLevelRange.getMax() );
            setZoomOutEnabled( zoomLevel != zoomLevelRange.getMin() );
        }

        private void setZoomInEnabled( boolean enabled ) {
            zoomInButton.setEnabled( enabled );
        }

        private void setZoomOutEnabled( boolean enabled ) {
            zoomOutButton.setEnabled( enabled );
        }

        public void addZoomInListener( ActionListener listener ) {
            zoomInButton.addActionListener( listener );
        }

        public void addZoomOutListener( ActionListener listener ) {
            zoomOutButton.addActionListener( listener );
        }
    }
}
