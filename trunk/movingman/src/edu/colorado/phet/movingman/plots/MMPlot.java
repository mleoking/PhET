/*PhET, 2004.*/
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.chart.controllers.HorizontalCursor;
import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.graphics.BufferedGraphicForComponent;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.movingman.MMTimer;
import edu.colorado.phet.movingman.MovingManModel;
import edu.colorado.phet.movingman.MovingManModule;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:54:39 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class MMPlot implements Graphic, Observer {
    private String title;
    private MovingManModule module;
    private DataSeries dataSeries;
    private MMTimer timer;
    private Color color;
    private Stroke stroke;
    private BufferedGraphicForComponent buffer;
    private double xShift;

    private boolean visible = true;
    private Chart chart;
    private DataSet dataSet;
    private float lastTime;
    private Font axisFont = new Font( "Lucida Sans", Font.BOLD, 14 );
    private Font titleFont = new Font( "Lucida Sans", Font.BOLD, 16 );
    private Font readoutFont = new Font( "Lucida Sans", Font.BOLD, 22 );
    private VerticalChartSlider verticalChartSlider;
    private HorizontalCursor horizontalCursor;
    private GeneralPath path = new GeneralPath();
    private CloseButton closeButton;
    private ChartButton showButton;
    private MagButton magPlus;
    private MagButton magMinus;
    private TextBox textBox;
    private boolean cursorVisible;
    private PhetTextGraphic readoutTitle;
    private PhetTextGraphic readoutUnits;

    private DecimalFormat format = new DecimalFormat( "0.00" );
    private double value;
    private FloatingControl floatingControl;

    static class FloatingControl extends VerticalLayoutPanel {
        static BufferedImage play;
        static BufferedImage pause;
        private MovingManModule module;

        static {
            try {
                play = ImageLoader.loadBufferedImage( "images/icons/java/media/Play16.gif" );
                pause = ImageLoader.loadBufferedImage( "images/icons/java/media/Pause16.gif" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }

        }

        public FloatingControl( final MovingManModule module ) {
            this.module = module;
            final JButton pauseButton = new JButton( new ImageIcon( pause ) );
            pauseButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setPaused( true );
                }
            } );
            final JButton playButton = new JButton( new ImageIcon( play ) );
            playButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setPaused( false );
                }
            } );
            module.addPauseListener( new MovingManModule.PauseListener() {
                public void modulePaused() {
                    playButton.setEnabled( true );
                    pauseButton.setEnabled( false );
                }

                public void moduleStarted() {
                    playButton.setEnabled( false );
                    pauseButton.setEnabled( true );
                }
            } );
            add( playButton );
            add( pauseButton );
            pauseButton.setEnabled( false );
        }
    }

    public MMPlot( String title, final MovingManModule module, final DataSeries series, MMTimer timer, Color color, Stroke stroke, Rectangle2D.Double inputBox, BufferedGraphicForComponent buffer, double xShift, String units ) throws IOException {
        this.title = title;
        this.module = module;
        this.dataSeries = series;
        this.timer = timer;
        this.color = color;
        this.stroke = stroke;
        this.buffer = buffer;
        this.xShift = xShift;
        chart = new Chart( module.getApparatusPanel(), new Range2D( inputBox ), new Rectangle( 0, 0, 100, 100 ) );
        horizontalCursor = new HorizontalCursor( chart, new Color( 15, 0, 255, 50 ), new Color( 50, 0, 255, 150 ), 8 );
        module.getApparatusPanel().addGraphic( horizontalCursor, 1000 );

        chart.setBackground( createBackground() );
        dataSet = new DataSet();
        setInputRange( inputBox );
        timer.addObserver( this );
        chart.getHorizontalTicks().setVisible( false );
        chart.getHorizonalGridlines().setMajorGridlinesColor( Color.darkGray );
        chart.getVerticalGridlines().setMajorGridlinesColor( Color.darkGray );
        chart.getXAxis().setMajorTickFont( axisFont );
        chart.getYAxis().setMajorTicksVisible( false );
        chart.getYAxis().setMajorTickFont( axisFont );
        chart.getVerticalGridlines().setMinorGridlinesVisible( false );
        chart.getXAxis().setMajorGridlines( new double[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20} ); //to ignore the 0.0
        chart.getXAxis().setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{6, 6}, 0 ) );

        verticalChartSlider = new VerticalChartSlider( chart );
        chart.getVerticalTicks().setMajorOffset( -verticalChartSlider.getSlider().getWidth() - 5, 0 );
        horizontalCursor.addListener( new HorizontalCursor.Listener() {
            public void modelValueChanged( double modelX ) {
                module.cursorMovedToTime( modelX );
            }
        } );
        closeButton = new CloseButton();
        module.getApparatusPanel().add( closeButton );
        chart.setVerticalTitle( title, color, titleFont );
        verticalChartSlider.addListener( new VerticalChartSlider.Listener() {
            public void valueChanged( double value ) {
                module.setWiggleMeVisible( false );
            }
        } );
        setCloseHandler( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( false );
                module.relayout();
            }
        } );
        showButton = new ChartButton( "Show " + title );
        showButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( true );
            }
        } );

        BufferedImage imgPlus = ImageLoader.loadBufferedImage( "images/icons/mag-plus-15.gif" );
        BufferedImage imgMinus = ImageLoader.loadBufferedImage( "images/icons/mag-minus-15.gif" );
        final double smooth = 1;
        ActionListener smoothPos = new Increment( smooth );
        ActionListener smoothNeg = new Decrement( smooth );
        ActionListener incPos = new Increment( 5 );
        ActionListener incNeg = new Decrement( 5 );
        magPlus = new MagButton( new ImageIcon( imgPlus ), smoothPos, incPos );
        magMinus = new MagButton( new ImageIcon( imgMinus ), smoothNeg, incNeg );
        module.getApparatusPanel().add( magPlus );
        module.getApparatusPanel().add( magMinus );

        readoutTitle = new PhetTextGraphic( module.getApparatusPanel(), readoutFont, title + " = ", color, 100, 100 );
        module.getApparatusPanel().addGraphic( readoutTitle, 10000 );
        readoutUnits = new PhetTextGraphic( module.getApparatusPanel(), readoutFont, units, color, 100, 100 );
        module.getApparatusPanel().addGraphic( readoutUnits, 10000 );
        textBox = new TextBox( 4 );
        textBox.setEditable( false );
        textBox.setHorizontalAlignment( JTextField.RIGHT );

        module.getApparatusPanel().add( textBox );
        textBox.setText( "0.00" );

        Observer o = new Observer() {
            public void update( Observable o, Object arg ) {
                int index = 0;
                if( module.isTakingData() ) {
                    index = series.size() - 1;
                }
                else {
                    double time = module.getPlaybackTimer().getTime() + getxShift();
                    index = (int)( time / MovingManModel.TIMER_SCALE );
                }
                if( series.indexInBounds( index ) ) {
                    value = series.pointAt( index );
                    String valueString = format.format( value );
                    if( valueString.equals( "-0.00" ) ) {
                        valueString = "0.00";
                    }
                    if( !textBox.getText().equals( valueString ) ) {
                        textBox.setText( valueString );
                    }
                }
            }
        };
        module.getRecordingTimer().addObserver( o );
        module.getPlaybackTimer().addObserver( o );
        floatingControl = new FloatingControl( module );
        module.getApparatusPanel().add( floatingControl );

        module.getApparatusPanel().addMouseMotionListener( new MouseMotionListener() {
            public void mouseDragged( MouseEvent e ) {
            }

            public void mouseMoved( MouseEvent e ) {
                if( isVisible() ) {
                    boolean vis = ( e.getY() > chart.getViewBounds().y && e.getY() < chart.getViewBounds().y + chart.getViewBounds().height );
                    floatingControl.setVisible( vis );
                    magPlus.setVisible( vis );
                    magMinus.setVisible( vis );
                    closeButton.setVisible( vis );
                }
            }
        } );
    }

    public TextBox getTextBox() {
        return textBox;
    }

    public void setModulePaused( boolean paused ) {
        textBox.setEditable( paused );
    }

    public void requestTypingFocus() {
        textBox.requestFocusInWindow();
    }

    public static class TextBox extends JTextField {
        public TextBox( String text ) {
            super( text );
            init();
        }

        public TextBox( int text ) {
            super( text );
            init();
        }

        private void init() {
            addMouseListener( new MouseAdapter() {
                public void mousePressed( MouseEvent e ) {
                    if( isEnabled() ) {
                        selectAll();
                    }
                }
            } );
        }

    }

    class Decrement implements ActionListener {
        double increment;

        public Decrement( double increment ) {
            this.increment = increment;
        }

        public void actionPerformed( ActionEvent e ) {
            Range2D origRange = chart.getRange();
            double diffY = origRange.getMaxY();
            double newDiffY = diffY + increment;
            int MAX = 100;
            if( newDiffY < MAX ) {
                setMagnitude( newDiffY );
                setPaintYLines( getYLines( newDiffY, 5 ) );
                module.repaintBackground();
            }
        }
    }

    class Increment implements ActionListener {
        double increment;

        public Increment( double increment ) {
            this.increment = increment;
        }

        public void actionPerformed( ActionEvent e ) {
            Range2D origRange = chart.getRange();
            double diffY = origRange.getMaxY();
            double newDiffY = diffY - increment;
            if( newDiffY > 0 ) {
                setMagnitude( newDiffY );
                setPaintYLines( getYLines( newDiffY, 5 ) );
                module.repaintBackground();
            }
        }

    }

    private double[] getYLines( double magnitude, double dy ) {
        ArrayList values = new ArrayList();
        for( double i = dy; i < magnitude; i += dy ) {
            values.add( new Double( i ) );
        }
        if( values.size() > 5 ) {
            return getYLines( magnitude, dy * 2 );
        }
        if( values.size() <= 1 ) {
            return getYLines( magnitude, dy / 2 );
        }
        double[] d = new double[values.size()];
        for( int i = 0; i < d.length; i++ ) {
            d[i] = ( (Double)values.get( i ) ).doubleValue();
        }
        return d;
    }

    static class RepeatClicker extends MouseAdapter {
        ActionListener target;
        private ActionListener discrete;
        int initDelay = 300;
        int delay = 30;
        Timer timer;
        private long pressTime;

        public RepeatClicker( ActionListener smooth, ActionListener discrete ) {
            this.target = smooth;
            this.discrete = discrete;
        }

        public void mouseClicked( MouseEvent e ) {
        }

        public void mousePressed( MouseEvent e ) {
            pressTime = System.currentTimeMillis();
            timer = new Timer( delay, target );
            timer.setInitialDelay( initDelay );
            timer.start();
        }

        public void mouseReleased( MouseEvent e ) {
            if( timer != null ) {
                timer.stop();
                long time = System.currentTimeMillis();
                if( time - pressTime < initDelay ) {
                    discrete.actionPerformed( null );
                }
            }
        }
    }

    class MagButton extends JButton {
        public MagButton( Icon icon, ActionListener smooth, ActionListener click ) {
            super( icon );
            addMouseListener( new RepeatClicker( smooth, click ) );
        }

    }

    public ChartButton getShowButton() {
        return showButton;
    }

    public static class ChartButton extends JButton {
        private static Font font = new Font( "Lucida Sans", Font.BOLD, 14 );

        public ChartButton( String label ) throws IOException {
            super( label, new ImageIcon( ImageLoader.loadBufferedImage( "images/arrow-right.gif" ) ) );
            setFont( font );
            setVerticalTextPosition( AbstractButton.CENTER );
            setHorizontalTextPosition( AbstractButton.LEFT );
        }
    }

    private Paint createBackground() {
        return Color.yellow;
    }

    public void setCloseHandler( ActionListener actionListener ) {
        closeButton.addActionListener( actionListener );
    }

    public MovingManModule getModule() {
        return module;
    }

    public void reset() {
        path.reset();
        dataSet.clear();
        horizontalCursor.setMaxX( Double.POSITIVE_INFINITY );//so it can't be dragged past, hopefully.
        textBox.setText( "0.00" );
        verticalChartSlider.setValue( 0 );
    }

    public void setViewBounds( int x, int y, int width, int height ) {
        setViewBounds( new Rectangle( x, y, width, height ) );
    }

    public boolean isVisible() {
        return visible;
    }

    private static class CloseButton extends JButton {
        private static Icon icon;

        public CloseButton() throws IOException {
            super( loadIcon() );
        }

        public static Icon loadIcon() throws IOException {
            if( icon == null ) {
                BufferedImage image = ImageLoader.loadBufferedImage( "images/x-25.gif" );
                icon = new ImageIcon( image );
            }
            return icon;
        }

        public void setPosition( int x, int y ) {
            reshape( x, y, getPreferredSize().width, getPreferredSize().height );
        }
    }

    public void paint( Graphics2D g ) {
        if( visible ) {
            GraphicsState state = new GraphicsState( g );
            chart.paint( g );
            Point pt = chart.getTransform().modelToView( 15, 0 );
            pt.y -= 3;
            PhetTextGraphic ptt = new PhetTextGraphic( module.getApparatusPanel(), new Font( "Lucida Sans", Font.BOLD, 16 ), "Time", Color.red, pt.x, pt.y );
            ptt.paint( g );
            Rectangle bounds = ptt.getBounds();
            Point2D tail = RectangleUtils.getRightCenter( bounds );
            tail = new Point2D.Double( tail.getX() + 5, tail.getY() );
            Point2D tip = new Point2D.Double( tail.getX() + 30, tail.getY() );
            Arrow arrow = new Arrow( tail, tip, 9, 9, 5 );
            PhetShapeGraphic psg = new PhetShapeGraphic( module.getApparatusPanel(), arrow.getShape(), Color.red, new BasicStroke( 1 ), Color.black );
            psg.paint( g );

            g.setClip( chart.getViewBounds() );
            g.setColor( color );
            g.setStroke( stroke );
            g.draw( path );

            state.restoreGraphics();
        }
    }

    public double getxShift() {
        return xShift;
    }

    public ModelViewTransform2D getTransform() {
        return chart.getTransform();
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
        setSliderVisible( visible );
        if( visible && cursorVisible ) {
            horizontalCursor.setVisible( true );
        }
        else {
            horizontalCursor.setVisible( false );
        }
        closeButton.setVisible( visible );
        module.getApparatusPanel().setLayout( null );
        module.getApparatusPanel().add( showButton );
        showButton.reshape( 100, 100, showButton.getPreferredSize().width, showButton.getPreferredSize().height );
        module.relayout();
        showButton.setVisible( !visible );
        magPlus.setVisible( visible );
        magMinus.setVisible( visible );

        readoutTitle.setVisible( visible );
        textBox.setVisible( visible );
        readoutUnits.setVisible( visible );

        floatingControl.setVisible( visible );
    }

    public void setShift( double xShift ) {
        this.xShift = xShift;
    }

    public void setInputRange( Rectangle2D.Double inputBox ) {
        Range2D range = new Range2D( inputBox );
        chart.setRange( range );
        refitCurve();
        module.repaintBackground( chart.getViewBounds() );
    }

    private void refitCurve() {
        path.reset();
        Point2D.Double[] copy = dataSet.toArray();
        dataSet.clear();
        for( int i = 0; i < copy.length; i++ ) {
            Point2D.Double aDouble = copy[i];
            dataSet.addPoint( aDouble );
            drawSegment();
        }
    }

    public void setPaintYLines( double[] lines ) {
        double[] full = new double[lines.length * 2 + 1];
        for( int i = 0; i < lines.length; i++ ) {
            full[i] = lines[i];
            full[full.length - 1 - i] = -lines[i];
        }
        full[lines.length] = 0;

        double[] half = new double[lines.length * 2];
        for( int i = 0; i < lines.length; i++ ) {
            half[i] = lines[i];
            half[half.length - 1 - i] = -lines[i];
        }
        chart.getHorizonalGridlines().setMajorGridlines( half );
        chart.getVerticalTicks().setMajorGridlines( full );
        chart.getYAxis().setMajorGridlines( full );
    }

    public void setViewBounds( Rectangle rectangle ) {
        chart.setViewBounds( rectangle );
        chart.setBackground( createBackground() );
        verticalChartSlider.setOffsetX( chart.getVerticalTicks().getMajorTickTextBounds().width + 5 );
        verticalChartSlider.update();
//        chart.getVerticalTicks().setMajorOffset( -verticalChartSlider.getSlider().getWidth() - 5, 0 );
        chart.getVerticalTicks().setMajorOffset( 0, 0 );
        Rectangle vb = chart.getViewBounds();
        int x = vb.x + vb.width - closeButton.getPreferredSize().width;
        int y = vb.y;
        closeButton.setPosition( x - 2, y + 2 );

        Dimension buttonSize = magPlus.getPreferredSize();
        JSlider js = verticalChartSlider.getSlider();
//        int buttonX = js.getX() - buttonSize.width;
        int buttonX = 5;
        int buttonSeparator = 2;
        int buttonY = js.getY() + js.getHeight() - 2 * buttonSize.height - buttonSeparator;

        magPlus.reshape( buttonX, buttonY, buttonSize.width, buttonSize.height );
        magMinus.reshape( buttonX, buttonY + buttonSeparator + buttonSize.height, buttonSize.width, buttonSize.height );


        readoutTitle.setPosition( chart.getViewBounds().x + 15, chart.getViewBounds().y + readoutTitle.getHeight() - 5 );
        Rectangle valueBounds = readoutTitle.getBounds();
        textBox.reshape( valueBounds.x + valueBounds.width,
                         valueBounds.y - 5,
                         textBox.getPreferredSize().width,
                         textBox.getPreferredSize().height );
        readoutUnits.setPosition( textBox.getX() + textBox.getWidth() + 5, readoutTitle.getY() );

//        int floaterX = js.getX() - floatingControl.getPreferredSize().width;
        int floaterX = 5;
        floatingControl.reshape( floaterX, chart.getViewBounds().y, floatingControl.getPreferredSize().width, floatingControl.getPreferredSize().height );

        refitCurve();
    }

    public void update( Observable o, Object arg ) {
        float time = (float)timer.getTime();
        if( time == lastTime ) {
            return;
        }
        lastTime = time;
        if( dataSeries.size() <= 1 ) {
            dataSet.clear();
        }
        else {
            float position = (float)dataSeries.getLastPoint();// * scale + yoffset;
            if( Float.isInfinite( position ) ) {
                return;
            }
            Point2D.Double pt = new Point2D.Double( time - xShift, position );
            dataSet.addPoint( pt );
            horizontalCursor.setMaxX( time );//so it can't be dragged past the end of recorded pressTime.
            drawSegment();
        }
    }

    private void drawSegment() {
        if( visible && buffer.getImage() != null && dataSet.size() >= 2 ) {
            int element = dataSet.size();
            Point2D a = chart.getTransform().modelToView( dataSet.pointAt( element - 2 ) );
            Point2D b = chart.getTransform().modelToView( dataSet.pointAt( element - 1 ) );
            Line2D.Double line = new Line2D.Double( a, b );
            if( dataSet.size() == 2 ) {
                path.reset();
                path.moveTo( (int)a.getX(), (float)a.getY() );
            }
            if( path.getCurrentPoint() != null ) {
                path.lineTo( (float)b.getX(), (float)b.getY() );
                Graphics2D g2 = module.getBackground().getImage().createGraphics();
                g2.setStroke( new BasicStroke( 2 ) );
                g2.setColor( color );
                g2.setClip( chart.getViewBounds() );
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
                g2.draw( line );
                Shape shape = stroke.createStrokedShape( line );
                module.getApparatusPanel().repaint( shape.getBounds() );
            }
        }
    }

    public void setMagnitude( double magnitude ) {
        Rectangle2D.Double positionInputBox = new Rectangle2D.Double( module.getMinTime(), -magnitude, module.getMaxTime() - module.getMinTime(), magnitude * 2 );
        setInputRange( positionInputBox );
        module.repaintBackground( chart.getViewBounds() );
    }

    public void setSliderVisible( boolean b ) {
        verticalChartSlider.setVisible( b );
    }

    public void addSliderListener( VerticalChartSlider.Listener listener ) {
        verticalChartSlider.addListener( listener );
    }

    public void updateSlider() {
        JSlider js = verticalChartSlider.getSlider();
        if( !js.hasFocus() && dataSet.size() > 0 ) {
            double lastY = dataSet.getLastPoint().getY();
            verticalChartSlider.setValue( lastY );
        }
    }

    public void cursorMovedToTime( double time ) {
        horizontalCursor.setX( time );
        verticalChartSlider.setValue( value );
    }

    public void setCursorVisible( boolean visible ) {
        if( isVisible() ) {
            horizontalCursor.setVisible( visible );
        }
        cursorVisible = visible;
    }
}
