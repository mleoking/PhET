// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.test;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.view.IPlateChargeGridSizeStrategy;
import edu.colorado.phet.capacitorlab.view.IPlateChargeGridSizeStrategy.GridSizeStrategyFactory;
import edu.colorado.phet.capacitorlab.view.PlusNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Test harness for plate charge layout in Capacitor Lab simulation.
 * The grid size strategy is determined by PlateChargeNode$GridSizeStrategyFactory.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPlateChargeLayout extends JFrame {

    private static final IPlateChargeGridSizeStrategy GRID_SIZE_STRATEGY = GridSizeStrategyFactory.createStrategy();

    private static final Dimension CANVAS_SIZE = new Dimension( 800, 550 );
    private static final IntegerRange NUMBER_OF_CHARGES_RANGE = new IntegerRange( 0, 625, 0 );
    private static final IntegerRange PLATE_WIDTH_RANGE = new IntegerRange( 5, 500, 200 );
    private static final IntegerRange PLATE_HEIGHT_RANGE = new IntegerRange( 5, 500, 200 );
    private static final double PLUS_MINUS_WIDTH = 7;
    private static final double PLUS_MINUS_HEIGHT = 1;

    //==============================================================================
    // Model
    //==============================================================================

    public static class TestModel {

        private final Property<Integer> numberOfChargesProperty;
        private final Property<Dimension> plateSizeProperty;

        public TestModel() {
            numberOfChargesProperty = new Property<Integer>( NUMBER_OF_CHARGES_RANGE.getDefault() );
            plateSizeProperty = new Property<Dimension>( new Dimension( PLATE_WIDTH_RANGE.getDefault(), PLATE_HEIGHT_RANGE.getDefault() ) );
        }

        public void addNumberOfChargesObserver( SimpleObserver o ) {
            numberOfChargesProperty.addObserver( o );
        }

        public void setNumberOfCharges( int numberOfCharges ) {
            numberOfChargesProperty.setValue( numberOfCharges );
        }

        public int getNumberOfCharges() {
            return numberOfChargesProperty.getValue();
        }

        public void addPlateSizeObserver( SimpleObserver o ) {
            plateSizeProperty.addObserver( o );
        }

        public void setPlateWidth( int plateWidth ) {
            plateSizeProperty.setValue( new Dimension( plateWidth, getPlateHeight() ) );
        }

        public void setPlateHeight( int plateHeight ) {
            plateSizeProperty.setValue( new Dimension( getPlateWidth(), plateHeight ) );
        }

        public int getPlateWidth() {
            return plateSizeProperty.getValue().width;
        }

        public int getPlateHeight() {
            return plateSizeProperty.getValue().height;
        }
    }

    //==============================================================================
    // View
    //==============================================================================

    public static class TestCanvas extends PCanvas {

        private final TestModel model;
        private final PPath plateNode;
        private final PComposite parentChargesNode;
        private final HTMLNode debugNode;

        public TestCanvas( final TestModel model ) {
            setPreferredSize( CANVAS_SIZE );

            removeInputEventListener( getZoomEventHandler() );
            removeInputEventListener( getPanEventHandler() );

            // plate
            plateNode = new PPath();
            plateNode.setPaint( Color.LIGHT_GRAY );
            plateNode.setStroke( new BasicStroke( 1f ) );
            plateNode.setStrokePaint( Color.BLACK );

            // parent node for charges on the plate
            parentChargesNode = new PComposite();

            // debug output
            debugNode = new HTMLNode();
            debugNode.setFont( new PhetFont() );

            // rendering order
            addChild( plateNode );
            addChild( parentChargesNode );
            addChild( debugNode );

            // layout
            plateNode.setOffset( ( PLATE_WIDTH_RANGE.getMax() / 2 ) + 20, ( PLATE_HEIGHT_RANGE.getMax() / 2 ) + 20 );
            parentChargesNode.setOffset( plateNode.getOffset() );
            debugNode.setOffset( plateNode.getXOffset() + ( PLATE_WIDTH_RANGE.getMax() / 2 ) + 10, plateNode.getYOffset() );

            // model change listener
            this.model = model;
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    updatePlate();
                    updateCharges();
                }
            };
            model.addNumberOfChargesObserver( o );
            model.addPlateSizeObserver( o );
        }

        // convenience method for adding nodes to the canvas
        public void addChild( PNode child ) {
            getLayer().addChild( child );
        }

        /*
         * Updates the plate geometry to match the model.
         * Origin is at the geometric center.
         */
        private void updatePlate() {
            double width = model.getPlateWidth();
            double height = model.getPlateHeight();
            plateNode.setPathTo( new Rectangle2D.Double( -width / 2, -height / 2, width, height ) );
        }

        /*
         * Updates the charges to match the model.
         */
        private void updateCharges() {

            // get model values
            final int numberOfCharges = model.getNumberOfCharges();
            final double plateWidth = model.getPlateWidth(); // use double in grid computations!
            final double plateHeight = model.getPlateHeight(); // use double in grid computations!

            // clear the grid of existing charges
            parentChargesNode.removeAllChildren();

            // clear the debug output node
            debugNode.setHTML( "" );

            if ( numberOfCharges != 0 ) {
                // compute the grid dimensions
                Dimension gridSize = GRID_SIZE_STRATEGY.getGridSize( numberOfCharges, plateWidth, plateHeight );
                final int rows = gridSize.height;
                final int columns = gridSize.width;

                // populate the grid with charges
                double dx = plateWidth / columns;
                double dy = plateHeight / rows;
                double xOffset = dx / 2;
                double yOffset = dy / 2;
                for ( int row = 0; row < rows; row++ ) {
                    for ( int column = 0; column < columns; column++ ) {
                        // add a charge
                        PNode chargeNode = new PlusNode( PLUS_MINUS_WIDTH, PLUS_MINUS_HEIGHT, Color.RED );
                        parentChargesNode.addChild( chargeNode );

                        // position the charge in cell in the grid
                        double x = -( plateWidth / 2 ) + xOffset + ( column * dx );
                        double y = -( plateHeight / 2 ) + yOffset + ( row * dy );
                        chargeNode.setOffset( x, y );
                    }
                }

                // debug output
                debugNode.setHTML( "grid=" + rows + "x" + columns +
                        "<br>computed charges=" + numberOfCharges +
                        "<br>displayed charges=" + ( rows * columns ) );
            }
        }
    }

    //==============================================================================
    // Controls
    //==============================================================================

    public static class TestControlPanel extends GridPanel {

        public TestControlPanel( final TestModel model ) {
            setBorder( new LineBorder( Color.BLACK ) );

            // grid size strategy name
            JLabel strategyLabel = new JLabel( "grid size strategy: " + GRID_SIZE_STRATEGY.getClass().getName() );

            // number of charges
            final IntegerValueControl numberOfChargesControl = new IntegerValueControl( "# charges:", NUMBER_OF_CHARGES_RANGE.getMin(), NUMBER_OF_CHARGES_RANGE.getMax(), model.getNumberOfCharges(), "" );
            numberOfChargesControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.setNumberOfCharges( numberOfChargesControl.getValue() );
                }
            });

            // plate width
            final IntegerValueControl plateWidthControl = new IntegerValueControl( "plate width:", PLATE_WIDTH_RANGE.getMin(), PLATE_WIDTH_RANGE.getMax(), model.getPlateWidth(), "mm" );
            plateWidthControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.setPlateWidth( plateWidthControl.getValue() );
                }
            });

            // plate height
            final IntegerValueControl plateHeightControl = new IntegerValueControl( "plate height:", PLATE_HEIGHT_RANGE.getMin(), PLATE_HEIGHT_RANGE.getMax(), model.getPlateHeight(), "mm" );
            plateHeightControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.setPlateHeight( plateHeightControl.getValue() );
                }
            });

            // layout
            int row = 0;
            int column = 0;
            setAnchor( Anchor.WEST );
            setInsets( new Insets( 5, 5, 5, 5 ) );
            add( strategyLabel, row++, column );
            add( numberOfChargesControl, row++, column );
            add( plateWidthControl, row++, column );
            add( plateHeightControl, row++, column );

            // model change listener
            model.addNumberOfChargesObserver( new SimpleObserver() {
                public void update() {
                    numberOfChargesControl.setValue( model.getNumberOfCharges() );
                }
            } );
            model.addPlateSizeObserver( new SimpleObserver() {
                public void update() {
                    plateWidthControl.setValue( model.getPlateWidth() );
                    plateHeightControl.setValue( model.getPlateHeight() );
                }
            } );
        }

        /*
         * A slider with integrated title, value display and units.
         * Layout => title: slider value units
         */
        public static class IntegerValueControl extends JPanel {

            private final JSlider slider;
            private final JLabel valueLabel;

            public IntegerValueControl( String title, int min, int max, int value, String units ) {
                // components
                JLabel titleLabel = new JLabel( title );
                slider = new JSlider( min, max, value );
                valueLabel = new JLabel( String.valueOf( slider.getValue() ) );
                JLabel unitsLabel = new JLabel( units );
                // layout
                add( titleLabel );
                add( slider );
                add( valueLabel );
                add( unitsLabel );
                // keep value display in sync with slider
                slider.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        valueLabel.setText( String.valueOf( slider.getValue() ) );
                    }
                } );
            }

            public int getValue() {
                return slider.getValue();
            }

            public void setValue( int value ) {
                slider.setValue( value );
            }

            public void addChangeListener( ChangeListener listener ) {
                // warning: ChangeEvent.getSource will return the JSlider, not the IntegerValueControl
                slider.addChangeListener( listener );
            }

            public void removeChangeListener( ChangeListener listener ) {
                slider.addChangeListener( listener );
            }
        }
    }

    //==============================================================================
    // Main frame
    //==============================================================================

    public TestPlateChargeLayout() {
        super( TestPlateChargeLayout.class.getName() );

        TestModel model = new TestModel();
        TestCanvas canvas = new TestCanvas( model );
        TestControlPanel controlPanel = new TestControlPanel( model );

        // layout with controls below canvas
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.SOUTH );
        setContentPane( panel );

        pack();
    }

    public static void main( String[] args ) {
        JFrame frame = new TestPlateChargeLayout();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
