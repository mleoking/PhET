/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.piccolophet.test.help;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;


/**
 * TestHelpBalloon tests the features of HelpBalloon.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestHelpBalloon extends PhetApplication {

    private static final String VERSION = "0.00.01";
    private static final String TITLE = "TestHelpBalloon";
    private static final String DESCRIPTION = "test harness for HelpBalloon";

    // Clock parameters
    private static final int CLOCK_RATE = 25; // wall time: frames per second
    private static final double MODEL_RATE = 1; // model time: dt per clock tick

    // Arrow property values
    private static final HelpBalloon.Attachment DEFAULT_ARROW_TAIL_POSITION = HelpBalloon.TOP_LEFT;
    private static final int DEFAULT_ARROW_LENGTH = 40;
    private static final int DEFAULT_ARROW_ROTATION = 0;
    private static final int MIN_ARROW_ROTATION = (int) HelpBalloon.MIN_ARROW_ROTATION;
    private static final int MAX_ARROW_ROTATION = (int) HelpBalloon.MAX_ARROW_ROTATION;

    /* Test harness */
    public static void main( final String[] args ) {
        ApplicationConstructor applicationConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                try {
                    TestHelpBalloon app = new TestHelpBalloon( config );
                    return app;
                }
                catch( Exception e ) {
                    return null;
                }
            }
        };
        PhetApplicationConfig phetApplicationConfig = new PhetApplicationConfig( args, applicationConstructor, "piccolo-phet" );
        new PhetApplicationLauncher().launchSim( phetApplicationConfig, applicationConstructor );
    }

    /* Application */
    public TestHelpBalloon( PhetApplicationConfig c ) throws InterruptedException {
        super( c );

        Module module1 = new TestModule( "Module 1" );
        addModule( module1 );
    }

    /* Clock */
    private static class TestClock extends SwingClock {
        public TestClock() {
            super( 1000 / CLOCK_RATE, new TimingStrategy.Constant( MODEL_RATE ) );
        }
    }

    /* Module */
    private static class TestModule extends PiccoloModule {

        private HelpBalloon _helpBalloon;

        /**
         * Constructor.
         *
         * @param title
         */
        public TestModule( String title ) {
            super( title, new TestClock(), true /* startsPaused */ );

            setLogoPanel( null );
            setClockControlPanel( null );

            // Play area --------------------------------------------

            // Canvas
            final PhetPCanvas canvas = new PhetPCanvas( new Dimension( 1000, 1000 ) );
            setSimulationPanel( canvas );

            PPath pathNode = new PPath();
            pathNode.setPathToRectangle( 0, 0, 75, 75 );
            pathNode.setPaint( Color.RED );
            pathNode.setOffset( 0, 0 );

            PText textNode = new PText( "Drag me" );
            textNode.setFont( new PhetFont( Font.BOLD, 16 ) );
            textNode.setTextPaint( Color.BLACK );
            textNode.setOffset( pathNode.getWidth() / 2 - textNode.getWidth() / 2, pathNode.getHeight() / 2 - textNode.getHeight() / 2 );

            PComposite compositeNode = new PComposite();
            compositeNode.setOffset( 150, 150 );
            compositeNode.addChild( pathNode );
            compositeNode.addChild( textNode );
            compositeNode.addInputEventListener( new PDragEventHandler() );
            compositeNode.addInputEventListener( new CursorHandler() );

            canvas.getLayer().addChild( compositeNode );

            // Control panel --------------------------------------------

            JPanel canvasPanel = new JPanel();
            {
                final JButton canvasColorButton = new JButton( "canvas color..." );
                canvasColorButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent event ) {
                        Color color = JColorChooser.showDialog( getFrame(), "Canvas color", Color.WHITE );
                        canvas.setBackground( color );
                    }
                } );

                canvasPanel.setBorder( new TitledBorder( "Canvas properties" ) );
                EasyGridBagLayout layout = new EasyGridBagLayout( canvasPanel );
                canvasPanel.setLayout( layout );
                int row = 0;
                layout.addComponent( canvasColorButton, row++, 0 );
            }

            JPanel textPanel = new JPanel();
            {
                final JButton textColorButton = new JButton( "text color..." );
                textColorButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent event ) {
                        Color color = JColorChooser.showDialog( getFrame(), "Text color", Color.BLACK );
                        _helpBalloon.setTextColor( color );
                    }
                } );

                final LabeledSlider fontSizeSlider = new LabeledSlider( "font size = {0} points", 8, 24, 12, 24 - 8, 1 );
                fontSizeSlider.addChangeListener( new ChangeListener() {

                    public void stateChanged( ChangeEvent e ) {
                        Font font = new PhetFont( Font.PLAIN, fontSizeSlider.getValue() );
                        _helpBalloon.setFont( font );
                    }
                } );

                final LabeledSlider marginSlider = new LabeledSlider( "text margin = {0} pixels", 0, 20, 4, 20 - 0, 1 );
                marginSlider.addChangeListener( new ChangeListener() {

                    public void stateChanged( ChangeEvent e ) {
                        _helpBalloon.setTextMargin( marginSlider.getValue() );
                    }
                } );

                textPanel.setBorder( new TitledBorder( "Text properties" ) );
                EasyGridBagLayout layout = new EasyGridBagLayout( textPanel );
                textPanel.setLayout( layout );
                int row = 0;
                layout.addComponent( textColorButton, row++, 0 );
                layout.addComponent( fontSizeSlider, row++, 0 );
                layout.addComponent( marginSlider, row++, 0 );
            }

            JPanel shadowPanel = new JPanel();
            {
                final JCheckBox shadowCheckBox = new JCheckBox( "shadow enabled" );
                shadowCheckBox.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        _helpBalloon.setShadowTextEnabled( shadowCheckBox.isSelected() );
                    }
                } );

                final LabeledSlider shadowOffsetSlider = new LabeledSlider( "shadow offset = {0} pixels", -5, 5, 1, 5, 1 );
                shadowOffsetSlider.addChangeListener( new ChangeListener() {

                    public void stateChanged( ChangeEvent e ) {
                        _helpBalloon.setShadowTextOffset( shadowOffsetSlider.getValue() );
                    }
                } );

                final JButton shadowColorButton = new JButton( "shadow color..." );
                shadowColorButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent event ) {
                        Color color = JColorChooser.showDialog( getFrame(), "shadow color", Color.RED );
                        _helpBalloon.setShadowTextColor( color );
                    }
                } );

                shadowPanel.setBorder( new TitledBorder( "Shadow properties" ) );
                EasyGridBagLayout layout = new EasyGridBagLayout( shadowPanel );
                shadowPanel.setLayout( layout );
                int row = 0;
                layout.addComponent( shadowCheckBox, row++, 0 );
                layout.addComponent( shadowOffsetSlider, row++, 0 );
                layout.addComponent( shadowColorButton, row++, 0 );
            }

            JPanel balloonPanel = new JPanel();
            {
                final JCheckBox visibleCheckBox = new JCheckBox( "visible" );
                visibleCheckBox.setSelected( true );
                visibleCheckBox.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent event ) {
                        _helpBalloon.setBalloonVisible( visibleCheckBox.isSelected() );
                    }
                } );

                final JButton fillColorButton = new JButton( "balloon fill color..." );
                fillColorButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent event ) {
                        Color color = JColorChooser.showDialog( getFrame(), "Balloon fill color", Color.YELLOW );
                        _helpBalloon.setBalloonFillPaint( color );
                    }
                } );

                final JButton strokeColorButton = new JButton( "balloon stroke color..." );
                strokeColorButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent event ) {
                        Color color = JColorChooser.showDialog( getFrame(), "Balloon stroke color", Color.BLACK );
                        _helpBalloon.setBalloonStrokePaint( color );
                    }
                } );

                final LabeledSlider strokeWidthSlider = new LabeledSlider( "balloon stroke width = {0} pixels", 1, 10, 1, 10 - 1, 1 );
                strokeWidthSlider.addChangeListener( new ChangeListener() {

                    public void stateChanged( ChangeEvent e ) {
                        _helpBalloon.setBalloonStroke( new BasicStroke( strokeWidthSlider.getValue() ) );
                    }
                } );

                final LabeledSlider cornerRadiusSlider = new LabeledSlider( "balloon corner radius = {0} pixels", 0, 50, 15, 50 - 0, 5 );
                cornerRadiusSlider.addChangeListener( new ChangeListener() {

                    public void stateChanged( ChangeEvent e ) {
                        _helpBalloon.setBalloonCornerRadius( cornerRadiusSlider.getValue() );
                    }
                } );

                final LabeledSlider spacingSlider = new LabeledSlider( "arrow/balloon spacing = {0} pixels", 0, 10, 0, 10 - 0, 1 );
                spacingSlider.addChangeListener( new ChangeListener() {

                    public void stateChanged( ChangeEvent e ) {
                        _helpBalloon.setArrowBalloonSpacing( spacingSlider.getValue() );
                    }
                } );

                balloonPanel.setBorder( new TitledBorder( "Balloon properties" ) );
                EasyGridBagLayout layout = new EasyGridBagLayout( balloonPanel );
                balloonPanel.setLayout( layout );
                int row = 0;
                layout.addComponent( visibleCheckBox, row++, 0 );
                layout.addComponent( fillColorButton, row++, 0 );
                layout.addComponent( strokeColorButton, row++, 0 );
                layout.addComponent( strokeWidthSlider, row++, 0 );
                layout.addComponent( cornerRadiusSlider, row++, 0 );
                layout.addComponent( spacingSlider, row++, 0 );
            }

            JPanel arrowPanel = new JPanel();
            {
                HelpBalloon.Attachment[] tailPositions = {
                        HelpBalloon.TOP_LEFT, HelpBalloon.TOP_CENTER, HelpBalloon.TOP_RIGHT,
                        HelpBalloon.BOTTOM_LEFT, HelpBalloon.BOTTOM_CENTER, HelpBalloon.BOTTOM_RIGHT,
                        HelpBalloon.LEFT_TOP, HelpBalloon.LEFT_CENTER, HelpBalloon.LEFT_BOTTOM,
                        HelpBalloon.RIGHT_TOP, HelpBalloon.RIGHT_CENTER, HelpBalloon.RIGHT_BOTTOM};
                final JComboBox tailPositionComboBox = new JComboBox( tailPositions );
                tailPositionComboBox.setCursor( new Cursor( Cursor.HAND_CURSOR ) );
                tailPositionComboBox.setSelectedItem( DEFAULT_ARROW_TAIL_POSITION );
                tailPositionComboBox.setMaximumRowCount( tailPositions.length );
                tailPositionComboBox.addItemListener( new ItemListener() {

                    public void itemStateChanged( ItemEvent e ) {
                        if ( e.getStateChange() == ItemEvent.SELECTED ) {
                            _helpBalloon.setArrowTailPosition( (HelpBalloon.Attachment) tailPositionComboBox.getSelectedItem() );
                        }
                    }
                } );
                JPanel tailPositionPanel = new JPanel();
                EasyGridBagLayout positionLayout = new EasyGridBagLayout( tailPositionPanel );
                tailPositionPanel.setLayout( positionLayout );
                positionLayout.addComponent( new JLabel( "arrow tail position:" ), 0, 0 );
                positionLayout.addComponent( tailPositionComboBox, 0, 1 );

                final LabeledSlider lengthSlider = new LabeledSlider( "arrow length = {0} pixels", 0, 200, 40, 200 - 0, 10 );
                lengthSlider.addChangeListener( new ChangeListener() {

                    public void stateChanged( ChangeEvent e ) {
                        _helpBalloon.setArrowLength( lengthSlider.getValue() );
                    }
                } );

                final LabeledSlider rotationSlider = new LabeledSlider( "arrow rotation = {0} degrees",
                                                                        MIN_ARROW_ROTATION, MAX_ARROW_ROTATION, DEFAULT_ARROW_ROTATION, MAX_ARROW_ROTATION, 10 );
                rotationSlider.addChangeListener( new ChangeListener() {

                    public void stateChanged( ChangeEvent e ) {
                        _helpBalloon.setArrowRotation( rotationSlider.getValue() );
                    }
                } );

                final LabeledSlider headWidthSlider = new LabeledSlider( "arrow head width = {0} pixels", 5, 30, 10, 30 - 5, 1 );
                final LabeledSlider headHeightSlider = new LabeledSlider( "arrow head height = {0} pixels", 5, 30, 10, 30 - 5, 1 );
                ChangeListener headListener = new ChangeListener() {

                    public void stateChanged( ChangeEvent e ) {
                        int headWidth = headWidthSlider.getValue();
                        int headHeight = headHeightSlider.getValue();
                        _helpBalloon.setArrowHeadSize( headWidth, headHeight );
                    }
                };
                headWidthSlider.addChangeListener( headListener );
                headHeightSlider.addChangeListener( headListener );

                final LabeledSlider tailWidthSlider = new LabeledSlider( "arrow tail width = {0} pixels", 1, 30, 5, 30 - 1, 1 );
                tailWidthSlider.addChangeListener( new ChangeListener() {

                    public void stateChanged( ChangeEvent e ) {
                        _helpBalloon.setArrowTailWidth( tailWidthSlider.getValue() );
                    }
                } );

                final LabeledSlider strokeWidthSlider = new LabeledSlider( "arrow stroke width = {0} pixels", 1, 10, 1, 10 - 1, 1 );
                strokeWidthSlider.addChangeListener( new ChangeListener() {

                    public void stateChanged( ChangeEvent e ) {
                        _helpBalloon.setArrowStroke( new BasicStroke( strokeWidthSlider.getValue() ) );
                    }
                } );

                final JButton fillColorButton = new JButton( "arrow fill color..." );
                fillColorButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent event ) {
                        Color color = JColorChooser.showDialog( getFrame(), "Arrow fill color", Color.YELLOW );
                        _helpBalloon.setArrowFillPaint( color );
                    }
                } );

                final JButton strokeColorButton = new JButton( "arrow stroke color..." );
                strokeColorButton.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent event ) {
                        Color color = JColorChooser.showDialog( getFrame(), "Arrow stroke color", Color.BLACK );
                        _helpBalloon.setArrowStrokePaint( color );
                    }
                } );

                arrowPanel.setBorder( new TitledBorder( "Arrow properties" ) );
                EasyGridBagLayout layout = new EasyGridBagLayout( arrowPanel );
                arrowPanel.setLayout( layout );
                int row = 0;
                layout.addComponent( tailPositionPanel, row++, 0 );
                layout.addComponent( lengthSlider, row++, 0 );
                layout.addComponent( rotationSlider, row++, 0 );
                layout.addComponent( headWidthSlider, row++, 0 );
                layout.addComponent( headHeightSlider, row++, 0 );
                layout.addComponent( tailWidthSlider, row++, 0 );
                layout.addComponent( fillColorButton, row++, 0 );
                layout.addComponent( strokeColorButton, row++, 0 );
                layout.addComponent( strokeWidthSlider, row++, 0 );
            }

            // Control panel
            ControlPanel controlPanel = new ControlPanel();
            setControlPanel( controlPanel );
            controlPanel.addControlFullWidth( canvasPanel );
            controlPanel.addVerticalSpace( 20 );
            controlPanel.addControlFullWidth( textPanel );
            controlPanel.addVerticalSpace( 20 );
            controlPanel.addControlFullWidth( shadowPanel );
            controlPanel.addVerticalSpace( 20 );
            controlPanel.addControlFullWidth( balloonPanel );
            controlPanel.addVerticalSpace( 20 );
            controlPanel.addControlFullWidth( arrowPanel );

            // Help --------------------------------------------

            setHelpEnabled( true );

            HelpPane helpPane = getDefaultHelpPane();

            // Help that points at a static location
            _helpBalloon = new HelpBalloon( helpPane,
                                            "<html>This is a HelpBalloon.<br>Adjust its properties<br>in the control panel</html>",
                                            DEFAULT_ARROW_TAIL_POSITION, DEFAULT_ARROW_LENGTH, DEFAULT_ARROW_ROTATION );
            _helpBalloon.pointAt( pathNode, canvas );
            helpPane.add( _helpBalloon );
        }

        /* Enables the help button and help menu item */
        public boolean hasHelp() {
            return true;
        }

        public JFrame getFrame() {
            return PhetApplication.instance().getPhetFrame();
        }
    }

    private static class LabeledSlider extends JPanel {

        private JLabel _label;
        private String _format;
        private JSlider _slider;

        public LabeledSlider( String format, int min, int max, int value, int majorTickSpacing, int minorTickSpacing ) {
            _format = format;
            _label = new JLabel();
            _slider = new JSlider();
            _slider.setMinimum( min );
            _slider.setMaximum( max );
            _slider.setValue( value );
            _slider.setMajorTickSpacing( majorTickSpacing );
            _slider.setMinorTickSpacing( minorTickSpacing );
            _slider.setPaintTicks( true );
            _slider.setPaintLabels( true );
            _slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    updateLabel();
                }
            } );
            updateLabel();

            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            setLayout( layout );
            layout.addComponent( _label, 0, 0 );
            layout.addComponent( _slider, 1, 0 );
        }

        private void updateLabel() {
            int value = _slider.getValue();
            Object[] args = {new Integer( value )};
            String string = MessageFormat.format( _format, args );
            _label.setText( string );
        }

        public int getValue() {
            return _slider.getValue();
        }

        public void addChangeListener( ChangeListener listener ) {
            _slider.addChangeListener( listener );
        }
    }
}
