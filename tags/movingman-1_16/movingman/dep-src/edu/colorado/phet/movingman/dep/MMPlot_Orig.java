///*PhET, 2004.*/
//package edu.colorado.phet.movingman.dep;
//
//import edu.colorado.phet.chart.Chart;
//import edu.colorado.phet.chart.DataSet;
//import edu.colorado.phet.chart.Range2D;
//import edu.colorado.phet.chart.controllers.HorizontalCursor;
//import edu.colorado.phet.chart.controllers.VerticalChartSlider;
//import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
//import edu.colorado.phet.common.view.graphics.shapes.Arrow;
//import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
//import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
//import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
//import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
//import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
//import edu.colorado.phet.common.view.util.GraphicsState;
//import edu.colorado.phet.common.view.util.ImageLoader;
//import edu.colorado.phet.common.view.util.RectangleUtils;
//import edu.colorado.phet.common.view.util.SimStrings;
//import edu.colorado.phet.movingman.MMFontManager;
//import edu.colorado.phet.movingman.MovingManModule;
//import edu.colorado.phet.timeseriesmodule.TimeSeries;
//import edu.colorado.phet.movingman.common.ScreenSizeHandlerFactory;
//import edu.colorado.phet.timeseriesmodule.PhetTimer;
//import edu.colorado.phet.timeseriesmodule.TimeSeries;
//import edu.colorado.phet.timeseriesmodule.PhetTimer;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.geom.GeneralPath;
//import java.awt.geom.Line2D;
//import java.awt.geom.Point2D;
//import java.awt.geom.Rectangle2D;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//
///**
// * User: Sam Reid
// * Date: Jun 30, 2003
// * Time: 12:54:39 AM
// * Copyright (c) Jun 30, 2003 by Sam Reid
// */
//public class MMPlot_Orig extends PhetGraphic {
//    private String title;
//    private MovingManModule module;
//    private TimeSeries timeSeries;
//    private PhetTimer timer;
//    private Color color;
//    private Stroke stroke;
////    private BufferedGraphicForComponent buffer;
//    private double xShift;
//
//    private boolean visible = true;
//    private Chart chart;
//    private DataSet dataSet;
//    private float lastTime;
//    private Font axisFont = MMFontManager.getFontSet().getAxisFont();
//    private Font titleFont = MMFontManager.getFontSet().getTitleFont();
//    private Font readoutFont = MMFontManager.getFontSet().getReadoutFont();
//
//    private VerticalChartSlider verticalChartSlider;
//    private HorizontalCursor horizontalCursor;
//    private GeneralPath path = new GeneralPath();
//    private CloseButton closeButton;
//    private ChartButton showButton;
//    private MagButton magPlus;
//    private MagButton magMinus;
//    private TextBox textBox;
//    private boolean cursorVisible;
//    private PhetTextGraphic readout;
//    private PhetTextGraphic readoutValue;
//
//    private DecimalFormat format = new DecimalFormat( "0.00" );
//    private double value;
//    private FloatingControl floatingControl;
//    private String units;
//    private JLabel titleLable;
//    private PhetTextGraphic superScriptGraphic;
//    private Font verticalTitleFont = MMFontManager.getFontSet().getVerticalTitleFont();
//    private ArrayList listeners = new ArrayList();
//    private HTMLGraphic readoutUnits;
//
//    public void valueChanged( double value ) {
//        verticalChartSlider.setValue( value );
//        setTextValue( value );
//    }
//
//    public void addSuperScript( String s ) {
//        Font superScriptFont = new Font( "Lucida Sans", Font.BOLD, 12 );
////        superScriptGraphic = new PhetTextGraphic( module.getApparatusPanel(), superScriptFont, s, color, 330, 230 );
//        superScriptGraphic = new PhetTextGraphic( module.getApparatusPanel(), superScriptFont, s, color, 330, 200 );
//        module.getApparatusPanel().addGraphic( superScriptGraphic, 999 );
//    }
//
//    public JButton getCloseButton() {
//        return closeButton;
//    }
//
//    public boolean isDragging() {
//        return this.getVerticalChartSlider().getSlider().isFocusOwner();
//    }
//
//    public static interface Listener {
//        void nominalValueChanged( double value );
//    }
//
//    public void addListener( Listener listener ) {
//        listeners.add( listener );
//    }
//
//    static class FloatingControl extends VerticalLayoutPanel {
//        static BufferedImage play;
//        static BufferedImage pause;
//        private MovingManModule module;
////        private JLabel titleLabel;
//        private JButton pauseButton;
//        private JButton recordButton;
//        private JButton resetButton;
//
//        static {
//            try {
//                play = ImageLoader.loadBufferedImage( "images/icons/java/media/Play16.gif" );
//                pause = ImageLoader.loadBufferedImage( "images/icons/java/media/Pause16.gif" );
//            }
//            catch( IOException e ) {
//                e.printStackTrace();
//            }
//        }
//
//        static class ControlButton extends JButton {
//            static Font font = MMFontManager.getFontSet().getControlButtonFont();
//
//            public ControlButton( String text ) {
//                super( text );
//                setFont( font );
//            }
//        }
//
//        public FloatingControl( final MovingManModule module ) {
//            this.module = module;
////            this.titleLabel = titleLabel;
//            pauseButton = new ControlButton( SimStrings.get( "MMPlot.PauseButton" ) );
//            pauseButton.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    module.setPaused( true );
//                }
//            } );
////            final JButton recordButton = new JButton( new ImageIcon( play ) );
//            recordButton = new ControlButton( SimStrings.get( "MMPlot.RecordButton" ) );
//            recordButton.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    module.setRecordMode();
//                    module.setPaused( false );
//                }
//            } );
//
//            resetButton = new ControlButton( SimStrings.get( "MMPlot.ResetButton" ) );
//            resetButton.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    boolean paused = module.isPaused();
//                    module.setPaused( true );
//                    int option = JOptionPane.showConfirmDialog( module.getApparatusPanel(),
//                                                                SimStrings.get( "MMPlot.ClearConfirmText" ),
//                                                                SimStrings.get( "MMPlot.ClearConfirmButton" ),
//                                                                JOptionPane.YES_NO_CANCEL_OPTION );
//                    if( option == JOptionPane.OK_OPTION || option == JOptionPane.YES_OPTION ) {
//                        module.reset();
//                    }
//                    else if( option == JOptionPane.CANCEL_OPTION || option == JOptionPane.NO_OPTION ) {
//                        module.setPaused( paused );
//                    }
//                }
//            } );
//            module.addListener( new TimeListenerAdapter() {
//                public void recordingStarted() {
//                    setButtons( false, true, true );
//                }
//
//                public void recordingPaused() {
//                    setButtons( true, false, true );
//                }
//
//                public void recordingFinished() {
//                    setButtons( false, false, true );
//                }
//
//                public void reset() {
//                    setButtons( true, false, false );
//                }
//
//                public void rewind() {
//                    setButtons( true, false, true );
//                }
//            } );
////            add( titleLabel );
//            add( recordButton );
//            add( pauseButton );
//            add( resetButton );
//            pauseButton.setEnabled( false );
//        }
//
//        private void setButtons( boolean record, boolean pause, boolean reset ) {
//            recordButton.setEnabled( record );
//            pauseButton.setEnabled( pause );
//            resetButton.setEnabled( reset );
//        }
//
//        public void setVisible( boolean aFlag ) {
//            super.setVisible( aFlag );
//        }
//    }
//
//    public MMPlot_Orig( String title, final MovingManModule module, final TimeSeries series, PhetTimer timer, Color color, Stroke stroke, Rectangle2D.Double inputBox,
//                        Object buffer,
//                        double xShift,
//                        String units, String labelStr )
//            throws IOException {
//        super( module.getApparatusPanel() );
//        this.units = units;
//        this.title = title;
//        this.module = module;
//        this.timeSeries = series;
//        this.timer = timer;
//        this.color = color;
//        this.stroke = stroke;
////        this.buffer = buffer;
//        this.xShift = xShift;
//        chart = new Chart( module.getApparatusPanel(), new Range2D( inputBox ), new Rectangle( 0, 0, 100, 100 ) );
//        horizontalCursor = new HorizontalCursor( getComponent(), chart, new Color( 15, 0, 255, 50 ), new Color( 50, 0, 255, 150 ), 8 );
//        module.getApparatusPanel().addGraphic( horizontalCursor, 1000 );
//
//        chart.setBackground( createBackground() );
//        dataSet = new DataSet();
//        setInputRange( inputBox );
////        timer.addObserver( this );
//        timer.addListener( new PhetTimer.Listener() {
//            public void timeChanged() {
//                update();
//            }
//        } );
//        chart.getHorizontalTicks().setVisible( false );
//        chart.getHorizonalGridlines().setMajorGridlinesColor( Color.darkGray );
//        chart.getVerticalGridlines().setMajorGridlinesColor( Color.darkGray );
//        chart.getXAxis().setMajorTickFont( axisFont );
//        chart.getYAxis().setMajorTicksVisible( false );
//        chart.getYAxis().setMajorTickFont( axisFont );
//        chart.getVerticalGridlines().setMinorGridlinesVisible( false );
//        chart.getXAxis().setMajorGridlines( new double[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20} ); //to ignore the 0.0
//        chart.getXAxis().setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{6, 6}, 0 ) );
//
//        chart.setVerticalTitle( title, color, verticalTitleFont );
//
//        verticalChartSlider = new VerticalChartSlider( chart );
//        chart.getVerticalTicks().setMajorOffset( -verticalChartSlider.getSlider().getWidth() - 5, 0 );
//        horizontalCursor.addListener( new HorizontalCursor.Listener() {
//            public void modelValueChanged( double modelX ) {
//                module.cursorMovedToTime( modelX );
//            }
//        } );
//        closeButton = new CloseButton();
//        closeButton.setToolTipText( SimStrings.get( "MMPlot.CloseButtonToolTipText" ) );
//        module.getApparatusPanel().add( closeButton );
//
//        setCloseHandler( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                setVisible( false );
//                module.relayout();
//            }
//        } );
//        showButton = new ChartButton( SimStrings.get( "MMPlot.ShowButton" ) + " " + title );
//        showButton.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                setVisible( true );
//            }
//        } );
//
//
////        BufferedImage imgPlus = ImageLoader.loadBufferedImage( "images/icons/mag-plus-10.gif" );
////        BufferedImage imgMinus = ImageLoader.loadBufferedImage( "images/icons/mag-minus-10.gif" );
////        BufferedImage imgPlus = ImageLoader.loadBufferedImage( "images/icons/glass-20-plus.gif" );
//        //        BufferedImage imgMinus = ImageLoader.loadBufferedImage( "images/icons/glass-20-minus.gif" );
//
//        BufferedImage imgPlus = ScreenSizeHandlerFactory.getScreenSizeHandler().getZoomInButtonImage();//ImageLoader.loadBufferedImage( "images/icons/glass-20-plus.gif" );
//        BufferedImage imgMinus = ScreenSizeHandlerFactory.getScreenSizeHandler().getZoomOutButtonImage();//ImageLoader.loadBufferedImage( "images/icons/glass-20-minus.gif" );
//
//        final double smooth = 1;
//        ActionListener smoothPos = new Increment( smooth );
//        ActionListener smoothNeg = new Decrement( smooth );
//        ActionListener incPos = new Increment( 5 );
//        ActionListener incNeg = new Decrement( 5 );
//        magPlus = new MagButton( new ImageIcon( imgPlus ), smoothPos, incPos, SimStrings.get( "MMPlot.ZoomInButton" ) );
//        magMinus = new MagButton( new ImageIcon( imgMinus ), smoothNeg, incNeg, SimStrings.get( "MMPlot.ZoomOutButton" ) );
//        module.getApparatusPanel().add( magPlus );
//        module.getApparatusPanel().add( magMinus );
//
//        readout = new PhetTextGraphic( module.getApparatusPanel(), readoutFont, title + " = ", color, 100, 100 );
//        module.getApparatusPanel().addGraphic( readout, 10000 );
////        readoutValue = new HTMLGraphic( module.getApparatusPanel(), readoutFont, units, color );
////        readoutValue.setLocation( 100, 100 );
//        readoutValue = new PhetTextGraphic( module.getApparatusPanel(), readoutFont, units, color, 100, 100 );
//        readoutUnits = new HTMLGraphic( module.getApparatusPanel(), readoutFont, units, color );
//        module.getApparatusPanel().addGraphic( readoutValue, 10000 );
//        module.getApparatusPanel().addGraphic( readoutUnits, 10000 );
//        textBox = new TextBox( module, 5, labelStr );
//        textBox.setHorizontalAlignment( JTextField.RIGHT );
//
//        module.getApparatusPanel().add( textBox );
//
//        setTextValue( 0 );
//        module.getRecordingTimer().addListener( new PhetTimer.Listener() {
//            public void timeChanged() {
//                updateTextBox( module, series );
//            }
//        } );
//        module.getPlaybackTimer().addListener( new PhetTimer.Listener() {
//            public void timeChanged() {
//                updateTextBox( module, series );
//            }
//        } );
//
//        titleLable = new JLabel( title );
//        titleLable.setFont( titleFont );
//        titleLable.setBackground( module.getBackgroundColor() );
//        titleLable.setOpaque( true );
//        titleLable.setForeground( color );//TODO titleLabel
//
//        module.getApparatusPanel().add( titleLable );
//        floatingControl = new FloatingControl( module );//, titleLable );
//        module.getApparatusPanel().add( floatingControl );
//        module.addListener( new TimeListenerAdapter() {
//            public void rewind() {
//                horizontalCursor.setX( 0 );
//            }
//        } );
//    }
//
//    private void updateTextBox( final MovingManModule module, final TimeSeries series ) {
//        int index = 0;
//        if( module.isTakingData() ) {
//            index = series.size() - 1;
//        }
//        else {
//            double time = module.getPlaybackTimer().getTime() + getxShift();
//            double maxTime = module.getRecordingTimer().getTime();
//            time = Math.min( time, maxTime );
//            index = (int)( time / MovingManModule.getTimeScale() );
////            System.out.println( "index = " + index );
//        }
//        if( series.indexInBounds( index ) ) {
//            value = series.pointAt( index ).getValue();
//            setTextValue( value );
//        }
//    }
//
//    public void setTextValue( double value ) {
//        String valueString = format.format( value );
//        if( valueString.equals( "-0.00" ) ) {
//            valueString = "0.00";
//        }
//        if( !textBox.getText().equals( valueString ) ) {
//            textBox.setText( valueString );
//        }
//        readoutValue.setText( valueString + " " );// + units );
//        setUnitsLocation();
////        moveScript();
//        for( int i = 0; i < listeners.size(); i++ ) {
//            Listener listener = (Listener)listeners.get( i );
//            listener.nominalValueChanged( value );
//        }
//    }
//
//    public TextBox getTextBox() {
//        return textBox;
//    }
//
//    public void requestTypingFocus() {
//        textBox.requestFocusInWindow();
//    }
//
//    public static class TextBox extends JPanel {
//        boolean changedByUser;
//        JTextField textField;
//        JLabel label;
//        static Font font = MMFontManager.getFontSet().getTextBoxFont();
//        private MovingManModule module;
//
//        public TextBox( MovingManModule module, int text, String labelText ) {
//            this.module = module;
//            textField = new JTextField( text );
//            label = new JLabel( labelText );
//            setLayout( new FlowLayout( FlowLayout.CENTER ) );
//            textField.addMouseListener( new MouseAdapter() {
//                public void mousePressed( MouseEvent e ) {
//                    if( isEnabled() ) {
//                        textField.selectAll();
//                    }
//                }
//            } );
//            textField.addKeyListener( new KeyListener() {
//                public void keyTyped( KeyEvent e ) {
//                    changedByUser = true;
//                }
//
//                public void keyPressed( KeyEvent e ) {
//                }
//
//                public void keyReleased( KeyEvent e ) {
//                }
//            } );
//            label.setFont( font );
//            textField.setFont( font );
//            add( label );
//            add( textField );
//            setBorder( BorderFactory.createLineBorder( Color.black ) );
//            module.addListener( new TimeListener() {
//                public void recordingStarted() {
//                    textField.setEditable( false );
//                }
//
//                public void recordingPaused() {
//                    textField.setEditable( true );
//                }
//
//                public void recordingFinished() {
//                    textField.setEditable( false );
//                }
//
//                public void playbackStarted() {
//                    textField.setEditable( false );
//                }
//
//                public void playbackPaused() {
//                    textField.setEditable( true );
//                }
//
//                public void playbackFinished() {
//                    textField.setEditable( false );
//                }
//
//                public void reset() {
//                    textField.setEditable( true );
//                }
//
//                public void rewind() {
//                    textField.setEditable( true );
//                }
//            } );
//        }
//
//        public void clearChangedByUser() {
//            changedByUser = false;
//        }
//
//        public boolean isChangedByUser() {
//            return changedByUser;
//        }
//
//        public synchronized void addKeyListener( KeyListener l ) {
//            textField.addKeyListener( l );
//        }
//
//        public void setEditable( boolean b ) {
//            textField.setEditable( b );
//        }
//
//        public void setHorizontalAlignment( int right ) {
//            textField.setHorizontalAlignment( right );
//        }
//
//        public String getText() {
//            return textField.getText();
//        }
//
//        public void setText( String valueString ) {
//            if( valueString.length() > textField.getColumns() ) {
//                valueString = valueString.subSequence( 0, textField.getColumns() ) + "";
//            }
//            textField.setText( valueString );
//        }
//    }
//
//    class Decrement implements ActionListener {
//        double increment;
//
//        public Decrement( double increment ) {
//            this.increment = increment;
//        }
//
//        public void actionPerformed( ActionEvent e ) {
//            Range2D origRange = chart.getRange();
//            double diffY = origRange.getMaxY();
//            double newDiffY = diffY + increment;
//            int MAX = 100;
//            if( newDiffY < MAX ) {
//                setMagnitude( newDiffY );
//                setPaintYLines( getYLines( newDiffY, 5 ) );
//                module.repaintBackground();
//            }
//        }
//    }
//
//    class Increment implements ActionListener {
//        double increment;
//
//        public Increment( double increment ) {
//            this.increment = increment;
//        }
//
//        public void actionPerformed( ActionEvent e ) {
//            Range2D origRange = chart.getRange();
//            double diffY = origRange.getMaxY();
//            double newDiffY = diffY - increment;
//            if( newDiffY > 0 ) {
//                setMagnitude( newDiffY );
//                setPaintYLines( getYLines( newDiffY, 5 ) );
//                module.repaintBackground();
//            }
//        }
//
//    }
//
//    private double[] getYLines( double magnitude, double dy ) {
//        ArrayList values = new ArrayList();
//        for( double i = dy; i < magnitude; i += dy ) {
//            values.add( new Double( i ) );
//        }
//        if( values.size() > 5 ) {
//            return getYLines( magnitude, dy * 2 );
//        }
//        if( values.size() <= 1 ) {
//            return getYLines( magnitude, dy / 2 );
//        }
//        double[] d = new double[values.size()];
//        for( int i = 0; i < d.length; i++ ) {
//            d[i] = ( (Double)values.get( i ) ).doubleValue();
//        }
//        return d;
//    }
//
//    static class RepeatClicker extends MouseAdapter {
//        ActionListener target;
//        private ActionListener discrete;
//        int initDelay = 300;
//        int delay = 30;
//        Timer timer;
//        private long pressTime;
//
//        public RepeatClicker( ActionListener smooth, ActionListener discrete ) {
//            this.target = smooth;
//            this.discrete = discrete;
//        }
//
//        public void mouseClicked( MouseEvent e ) {
//        }
//
//        public void mousePressed( MouseEvent e ) {
//            pressTime = System.currentTimeMillis();
//            timer = new Timer( delay, target );
//            timer.setInitialDelay( initDelay );
//            timer.start();
//        }
//
//        public void mouseReleased( MouseEvent e ) {
//            if( timer != null ) {
//                timer.stop();
//                long time = System.currentTimeMillis();
//                if( time - pressTime < initDelay ) {
//                    discrete.actionPerformed( null );
//                }
//            }
//        }
//    }
//
//    class MagButton extends JButton {
//        public MagButton( Icon icon, ActionListener smooth, ActionListener click, String tooltip ) {
//            super( icon );
//            addMouseListener( new RepeatClicker( smooth, click ) );
//            setToolTipText( tooltip );
//        }
//
//    }
//
//    public ChartButton getShowButton() {
//        return showButton;
//    }
//
//    public static class ChartButton extends JButton {
//        private static Font font = MMFontManager.getFontSet().getChartButtonFont();//new Font( "Lucida Sans", Font.BOLD, 14 );
//
//        public ChartButton( String label ) {
////            super( label, new ImageIcon( ImageLoader.loadBufferedImage( "images/arrow-right.gif" ) ) );
//            super( label );
//            setFont( font );
//            setVerticalTextPosition( AbstractButton.CENTER );
//            setHorizontalTextPosition( AbstractButton.LEFT );
//        }
//    }
//
//    private Paint createBackground() {
//        return Color.yellow;
//    }
//
//    public void setCloseHandler( ActionListener actionListener ) {
//        closeButton.addActionListener( actionListener );
//    }
//
//    public MovingManModule getModule() {
//        return module;
//    }
//
//    public void reset() {
//        path.reset();
//        dataSet.clear();
//        horizontalCursor.setMaxX( Double.POSITIVE_INFINITY );//so it can't be dragged past, hopefully.
//        setTextValue( 0 );
//        verticalChartSlider.setValue( 0 );
//    }
//
//    public void setViewBounds( int x, int y, int width, int height ) {
//        setViewBounds( new Rectangle( x, y, width, height ) );
//    }
//
//    public boolean isVisible() {
//        return visible;
//    }
//
//    private static class CloseButton extends JButton {
//        private static Icon icon;
//
//        public CloseButton() throws IOException {
//            super( loadIcon() );
//        }
//
//        public static Icon loadIcon() {
//            if( icon == null ) {
//                BufferedImage image = ScreenSizeHandlerFactory.getScreenSizeHandler().getCloseImage();
////                BufferedImage image = ImageLoader.loadBufferedImage( "images/x-25.gif" );
//                icon = new ImageIcon( image );
//            }
//            return icon;
//        }
//
//        public void setPosition( int x, int y ) {
//            reshape( x, y, getPreferredSize().width, getPreferredSize().height );
//        }
//    }
//
//    public void paint( Graphics2D g ) {
//        if( visible ) {
//            GraphicsState state = new GraphicsState( g );
//            chart.paint( g );
//            Point pt = chart.getModelViewTransform().modelToView( 15, 0 );
//            pt.y -= 3;
//            PhetTextGraphic ptt = new PhetTextGraphic( module.getApparatusPanel(),
//                                                       MMFontManager.getFontSet().getTimeLabelFont(),
//                                                       SimStrings.get( "MMPlot.TimeLabel" ), Color.red, pt.x, pt.y );
//            ptt.paint( g );
//            Rectangle bounds = ptt.getBounds();
//            Point2D tail = RectangleUtils.getRightCenter( bounds );
//            tail = new Point2D.Double( tail.getX() + 5, tail.getY() );
//            Point2D tip = new Point2D.Double( tail.getX() + 30, tail.getY() );
//            Arrow arrow = new Arrow( tail, tip, 9, 9, 5 );
//            PhetShapeGraphic psg = new PhetShapeGraphic( module.getApparatusPanel(), arrow.getShape(), Color.red, new BasicStroke( 1 ), Color.black );
//            psg.paint( g );
//
//            g.setClip( chart.getChartBounds() );
//            g.setColor( color );
//            g.setStroke( stroke );
//            g.draw( path );
//
//            state.restoreGraphics();
//        }
//    }
//
//    public double getxShift() {
//        return xShift;
//    }
//
//    public ModelViewTransform2D getModelViewTransform() {
//        return chart.getModelViewTransform();
//    }
//
//    public void setVisible( boolean visible ) {
//        this.visible = visible;
//        setSliderVisible( visible );
//        if( visible && cursorVisible ) {
//            horizontalCursor.setVisible( true );
//        }
//        else {
//            horizontalCursor.setVisible( false );
//        }
//        closeButton.setVisible( visible );
//        module.getApparatusPanel().setLayout( null );
//        module.getApparatusPanel().add( showButton );
//        showButton.reshape( 100, 100, showButton.getPreferredSize().width, showButton.getPreferredSize().height );
//        module.relayout();
//        showButton.setVisible( !visible );
//        magPlus.setVisible( visible );
//        magMinus.setVisible( visible );
//
//        readout.setVisible( visible );
//        textBox.setVisible( visible );
//        readoutValue.setVisible( visible );
//
//        floatingControl.setVisible( visible );
//        titleLable.setVisible( visible );
//        if( superScriptGraphic != null ) {
//            superScriptGraphic.setVisible( visible );
//        }
//    }
//
//    protected Rectangle determineBounds() {
//        return chart.getBounds();
//    }
//
//    public void setShift( double xShift ) {
//        this.xShift = xShift;
//    }
//
//    public void setInputRange( Rectangle2D.Double inputBox ) {
//        Range2D range = new Range2D( inputBox );
//        chart.setRange( range );
//        refitCurve();
//        module.repaintBackground( chart.getChartBounds() );
//    }
//
//    private void refitCurve() {
//        path.reset();
//        Point2D.Double[] copy = dataSet.toArray();
//        dataSet.clear();
//        for( int i = 0; i < copy.length; i++ ) {
//            Point2D.Double aDouble = copy[i];
//            dataSet.addPoint( aDouble );
//            drawSegment();
//        }
//    }
//
//    public void setPaintYLines( double[] lines ) {
//        double[] full = new double[lines.length * 2 + 1];
//        for( int i = 0; i < lines.length; i++ ) {
//            full[i] = lines[i];
//            full[full.length - 1 - i] = -lines[i];
//        }
//        full[lines.length] = 0;
//
//        double[] half = new double[lines.length * 2];
//        for( int i = 0; i < lines.length; i++ ) {
//            half[i] = lines[i];
//            half[half.length - 1 - i] = -lines[i];
//        }
//        chart.getHorizonalGridlines().setMajorGridlines( half );
//        chart.getVerticalTicks().setMajorGridlines( full );
//        chart.getYAxis().setMajorGridlines( full );
//    }
//
//    public void setViewBounds( Rectangle rectangle ) {
//        chart.setViewBounds( rectangle );
//        chart.setBackground( createBackground() );
////        verticalChartSlider.setOffsetX( chart.getVerticalTicks().getMajorTickTextBounds().width + chart.getTitle().getBounds().width );
//        verticalChartSlider.setOffsetX( chart.getVerticalTicks().getWidth() + chart.getTitle().getBounds().width );//TODO offset!
//        verticalChartSlider.update();
//        chart.getVerticalTicks().setMajorOffset( 0, 0 );
//        Rectangle vb = chart.getChartBounds();
//        int x = vb.x + vb.width - closeButton.getPreferredSize().width;
//        int y = vb.y;
//        closeButton.setPosition( x - 2, y + 2 );
//
//        Dimension buttonSize = magPlus.getPreferredSize();
//        JSlider js = verticalChartSlider.getSlider();
//        int magSep = 1;
//        int magOffsetY = 7;
//        int magY = js.getY() + js.getHeight() - 2 * buttonSize.height - magSep - magOffsetY;
//
//        int magX = chart.getChartBounds().x + 3;
//        magPlus.reshape( magX, magY, buttonSize.width, buttonSize.height );
//        magMinus.reshape( magX, magY + magSep + buttonSize.height, buttonSize.width, buttonSize.height );
//
//        readout.setLocation( chart.getChartBounds().x + 15, chart.getChartBounds().y + readout.getHeight() - 5 );
//        readoutValue.setLocation( readout.getX() + readout.getWidth() + 5, readout.getY() );
//        setUnitsLocation();
////        moveScript();
//
//        int floaterX = 5;
//
//        titleLable.reshape( floaterX, chart.getChartBounds().y, titleLable.getPreferredSize().width, titleLable.getPreferredSize().height );
//        textBox.reshape( floaterX,
//                         titleLable.getY() + titleLable.getHeight() + 5,
//                         textBox.getPreferredSize().width,
//                         textBox.getPreferredSize().height );
//        int dw = Math.abs( textBox.getWidth() - floatingControl.getPreferredSize().width );
//        int floatX = floaterX + dw / 2;
//        floatingControl.reshape( floatX, textBox.getY() + textBox.getHeight() + 5, floatingControl.getPreferredSize().width, floatingControl.getPreferredSize().height );
//
//        refitCurve();
//    }
//
//    private void setUnitsLocation() {
//        int yBottom = readoutValue.getY() + readoutValue.getHeight();
//        int y = yBottom - readoutUnits.getHeight();
//        readoutUnits.setLocation( readoutValue.getWidth() + readoutValue.getX(), y );
//    }
//
//    public void update() {
//        float time = (float)timer.getTime();
//        if( time == lastTime ) {
//            return;
//        }
//        lastTime = time;
//        if( timeSeries.size() <= 1 ) {
//            dataSet.clear();
//        }
//        else {
//            float position = (float)timeSeries.getLastPoint().getValue();// * scale + yoffset;
//            if( Float.isInfinite( position ) ) {
//                return;
//            }
//            Point2D.Double pt = new Point2D.Double( time - xShift, position );
//            dataSet.addPoint( pt );
//            horizontalCursor.setMaxX( time );//so it can't be dragged past the end of recorded pressTime.
//            drawSegment();
//        }
//    }
//
//    private void drawSegment() {
//        if( visible &&
////            buffer.getImage() != null
//             dataSet.size() >= 2 ) {
//            int element = dataSet.size();
//            Point2D a = chart.getModelViewTransform().modelToView( dataSet.pointAt( element - 2 ) );
//            Point2D b = chart.getModelViewTransform().modelToView( dataSet.pointAt( element - 1 ) );
//            Line2D.Double line = new Line2D.Double( a, b );
//            if( dataSet.size() == 2 ) {
//                path.reset();
//                path.moveTo( (int)a.getX(), (float)a.getY() );
//            }
//            if( path.getCurrentPoint() != null ) {
//                path.lineTo( (float)b.getX(), (float)b.getY() );
//                Graphics2D g2=null;
//                // = module.getBuffer().getImage().createGraphics();//todo commented out.
//                g2.setStroke( new BasicStroke( 2 ) );
//                g2.setColor( color );
//                g2.setClip( chart.getChartBounds() );
//                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
//                g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
//                g2.draw( line );
//                Shape shape = stroke.createStrokedShape( line );
//                module.getApparatusPanel().repaint( shape.getBounds() );
//            }
//        }
//    }
//
//    public void setMagnitude( double magnitude ) {
//        Rectangle2D.Double positionInputBox = new Rectangle2D.Double( module.getMinTime(), -magnitude, module.getMaxTime() - module.getMinTime(), magnitude * 2 );
//        setInputRange( positionInputBox );
//        module.repaintBackground( chart.getChartBounds() );
//    }
//
//    public void setSliderVisible( boolean b ) {
//        verticalChartSlider.setVisible( b );
//    }
//
//    public void addSliderListener( VerticalChartSlider.Listener listener ) {
//        verticalChartSlider.addListener( listener );
//    }
//
//    public VerticalChartSlider getVerticalChartSlider() {
//        return verticalChartSlider;
//    }
//
//    public void updateSlider() {
//        JSlider js = verticalChartSlider.getSlider();
//        if( !js.getValueIsAdjusting() && dataSet.size() > 0 ) {
//            double lastY = dataSet.getLastPoint().getY();
//            verticalChartSlider.setValue( lastY );
//        }
//    }
//
//    public void cursorMovedToTime( double time, int index ) {
//        horizontalCursor.setX( time );
//        verticalChartSlider.setValue( timeSeries.pointAt( index ).getValue() );
//        setTextValue( timeSeries.pointAt( index ).getValue() );
//    }
//
//    public void setCursorVisible( boolean visible ) {
//        if( isVisible() ) {
//            horizontalCursor.setVisible( visible );
//        }
//        cursorVisible = visible;
//    }
//}
