/*PhET, 2004.*/
package edu.colorado.phet.forces1d.common.plotdevice;

import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.chart.LinePlot;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.chart.controllers.HorizontalCursor;
import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.forces1d.model.DataSeries;
import edu.colorado.phet.forces1d.model.PhetTimer;
import edu.colorado.phet.forces1d.view.MMFontManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:54:39 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class PlotDevice extends CompositePhetGraphic {

    private String title;
    private PlotDeviceModel plotDeviceModel;
    private PlotDeviceView plotDeviceView;

    private PhetTimer timer;
    private Color color;
    private Stroke stroke;

    private VerticalChartSlider verticalChartSlider;
    private HorizontalCursor horizontalCursor;
    private TextBox textBox;
    private ChartComponent chartComponent;
    private ChartButton showButton;
    private DecimalFormat format = new DecimalFormat( "0.00" );
    private FloatingControl floatingControl;
    private String units;
    private JLabel titleLable;
    private PhetTextGraphic superScriptGraphic;
    private Font verticalTitleFont = MMFontManager.getFontSet().getVerticalTitleFont();
    private ArrayList listeners = new ArrayList();
    private PhetTextGraphic textGraphic;

    public void setDataSeriesVisible( int index, boolean visible ) {
        chartComponent.setDataSeriesVisible( index, visible );
    }

    public PlotDevice( final ParameterSet parameters )
            throws IOException {
        super( parameters.panel );
        this.plotDeviceView = parameters.plotDeviceView;
        this.units = parameters.units;
        this.title = parameters.title;
        this.plotDeviceModel = parameters.plotDeviceModel;
        this.timer = parameters.timer;
        this.color = parameters.color;
        this.stroke = parameters.stroke;
        chartComponent = new ChartComponent( parameters.panel, parameters.inputBox, parameters.series, parameters.xShift );
        ApparatusPanel panel = parameters.panel;
        Rectangle2D.Double inputBox = parameters.inputBox;

        horizontalCursor = new HorizontalCursor( panel, chartComponent.getChart(), new Color( 15, 0, 255, 50 ), new Color( 50, 0, 255, 150 ), 8 );
        horizontalCursor.addListener( new HorizontalCursor.Listener() {
            public void modelValueChanged( double modelX ) {
                plotDeviceModel.cursorMovedToTime( modelX );
            }
        } );
        panel.addGraphic( horizontalCursor, 1000 );

        setInputRange( inputBox );
        timer.addListener( new PhetTimer.Listener() {
            public void timeChanged( PhetTimer timer ) {
                update();
            }
        } );

        showButton = new ChartButton( "Show " + title );
        showButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( true );
            }
        } );
        verticalChartSlider = new VerticalChartSlider( chartComponent.getChart() );

        setCloseHandler( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( false );
                plotDeviceView.relayout();
            }
        } );

        textBox = new TextBox( plotDeviceModel, 5, parameters.labelStr, this );
        textBox.setHorizontalAlignment( JTextField.RIGHT );

        panel.add( textBox );

        setTextValue( 0 );
        plotDeviceModel.getRecordingTimer().addListener( new PhetTimer.Listener() {
            public void timeChanged( PhetTimer timer ) {
                updateTextBox( plotDeviceModel, parameters.series );
            }
        } );
        plotDeviceModel.getPlaybackTimer().addListener( new PhetTimer.Listener() {
            public void timeChanged( PhetTimer timer ) {
                updateTextBox( plotDeviceModel, parameters.series );
            }
        } );

        titleLable = new JLabel( title );
        Font titleFont = MMFontManager.getFontSet().getTitleFont();
        titleLable.setFont( titleFont );
        titleLable.setBackground( plotDeviceView.getBackgroundColor() );
        titleLable.setOpaque( true );
        titleLable.setForeground( color );//TODO titleLabel

        panel.add( titleLable );
        floatingControl = new FloatingControl( plotDeviceModel, plotDeviceView.getApparatusPanel() );//, titleLable );
        panel.add( floatingControl );
        plotDeviceModel.addListener( new PlotDeviceModel.ListenerAdapter() {
            public void rewind() {
                horizontalCursor.setX( 0 );
            }
        } );
        textGraphic = new PhetTextGraphic( panel, MMFontManager.getFontSet().getTimeLabelFont(), "Time", Color.red, 0, 0 );
    }

    private void setInputRange( Rectangle2D.Double inputBox ) {
        Range2D range = new Range2D( inputBox );
        getChart().setRange( range );
//        chartComponent.refitCurve();
        plotDeviceView.repaintBackground( getChart().getViewBounds() );
    }

    private void updateTextBox( final PlotDeviceModel module, final DataSeries series ) {
        int index = 0;
        if( module.isTakingData() ) {
            index = series.size() - 1;
        }
        else {
            double time = module.getPlaybackTimer().getTime() + chartComponent.getxShift();
            index = (int)time;//(int)( time / MovingManModel.TIMER_SCALE );
        }
        if( series.indexInBounds( index ) ) {
            double value = series.pointAt( index );
            setTextValue( value );
        }
    }

    public void setValue( double value ) {
        verticalChartSlider.setValue( value );
        setTextValue( value );
    }

    public Chart getChart() {
        return chartComponent.getChart();
    }

    public void addDataSeries( DataSeries dataSeries, Color color, String title, Stroke stroke ) {
        chartComponent.addSeries( dataSeries, color, title, stroke );
    }

    public void addSuperScript( String s ) {
        Font superScriptFont = new Font( "Lucida Sans", Font.BOLD, 12 );
        superScriptGraphic = new PhetTextGraphic( plotDeviceView.getApparatusPanel(), superScriptFont, s, color, 330, 230 );
        plotDeviceView.getApparatusPanel().addGraphic( superScriptGraphic, 999 );
    }

    public static interface Listener {
        void nominalValueChanged( double value );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void setTextValue( double value ) {
        String valueString = format.format( value );
        if( valueString.equals( "-0.00" ) ) {
            valueString = "0.00";
        }
        if( !textBox.getText().equals( valueString ) ) {
            textBox.setText( valueString );
        }
//        chartComponent.setText(valueString+" "+units);//TODO this is broken for Series
//        chartComponent.readoutValue.setText( valueString + " " + units );
//        moveScript();
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.nominalValueChanged( value );
        }
    }

    public TextBox getTextBox() {
        return textBox;
    }

    public void requestTypingFocus() {
        textBox.requestFocusInWindow();
    }

    public void setLabelText( String labelText ) {
        textBox.setLabelText( labelText );
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


    public ChartButton getShowButton() {
        return showButton;
    }


    public void setCloseHandler( ActionListener actionListener ) {
        chartComponent.closeButton.addActionListener( actionListener );
    }

    public PlotDeviceModel getPlotDeviceModel() {
        return plotDeviceModel;
    }

    public void reset() {
        chartComponent.reset();
        horizontalCursor.setMaxX( Double.POSITIVE_INFINITY );//so it can't be dragged past, hopefully.
        setTextValue( 0 );
        verticalChartSlider.setValue( 0 );
    }

    public void setViewBounds( int x, int y, int width, int height ) {
        setViewBounds( new Rectangle( x, y, width, height ) );
    }


    public void setViewBounds( Rectangle rectangle ) {
        chartComponent.setViewBounds( rectangle );
        verticalChartSlider.setOffsetX( chartComponent.chart.getVerticalTicks().getMajorTickTextBounds().width + getChart().getTitle().getBounds().width );
        verticalChartSlider.update();
        int floaterX = 5;
        titleLable.reshape( floaterX, getChart().getViewBounds().y, titleLable.getPreferredSize().width, titleLable.getPreferredSize().height );
        textBox.reshape( floaterX,
                         titleLable.getY() + titleLable.getHeight() + 5,
                         textBox.getPreferredSize().width,
                         textBox.getPreferredSize().height );
        int dw = Math.abs( textBox.getWidth() - floatingControl.getPreferredSize().width );
        int floatX = floaterX + dw / 2;
        floatingControl.reshape( floatX, textBox.getY() + textBox.getHeight() + 5, floatingControl.getPreferredSize().width, floatingControl.getPreferredSize().height );

//        chartComponent.refitCurve();
        chartComponent.setViewBounds( rectangle );
    }


    public void paint( Graphics2D g ) {
        if( isVisible() ) {
            GraphicsState state = new GraphicsState( g );
            getChart().paint( g );
            Point pt = getChart().getModelViewTransform().modelToView( 15, 0 );
            pt.y -= 3;
            textGraphic.setLocation( pt.x, pt.y - textGraphic.getHeight() );
            textGraphic.paint( g );
            Rectangle bounds = textGraphic.getBounds();
            Point2D tail = RectangleUtils.getRightCenter( bounds );
            tail = new Point2D.Double( tail.getX() + 5, tail.getY() );
//            Point2D tip = new Point2D.Double( tail.getX() + 30, tail.getY() );


//            Arrow arrow = new Arrow( tail, tip, 9, 9, 5 );
//            PhetShapeGraphic psg = new PhetShapeGraphic( plotDeviceView.getApparatusPanel(), arrow.getShapeGraphic(), Color.red, new BasicStroke( 1 ), Color.black );
//            psg.paint( g );
//            g.setFont( new Font( "Lucida Sans", Font.BOLD, 16 ) );
//            g.drawString( "Time", (float)tail.getX(), (float)( tail.getY() - g.getFont().getStringBounds( "Time", g.getFontRenderContext() ).getHeight() ) );
//            g.setClip( getChart().getViewBounds() );
//            g.setColor( color );
//            g.setStroke( stroke );
//            g.draw( chartComponent.seriesAt( 0 ).path );

            state.restoreGraphics();
        }
    }


    public ModelViewTransform2D getModelViewTransform() {
        return getChart().getModelViewTransform();
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        setSliderVisible( visible );
        if( visible ) {
            horizontalCursor.setVisible( true );
        }
        else {
            horizontalCursor.setVisible( false );
        }
        chartComponent.setVisible( visible );

        plotDeviceView.getApparatusPanel().setLayout( null );
        plotDeviceView.getApparatusPanel().add( showButton );
        showButton.reshape( 100, 100, showButton.getPreferredSize().width, showButton.getPreferredSize().height );
        plotDeviceView.relayout();
        showButton.setVisible( !visible );
        textBox.setVisible( visible );


        floatingControl.setVisible( visible );
        titleLable.setVisible( visible );
        if( superScriptGraphic != null ) {
            superScriptGraphic.setVisible( visible );
        }
    }

    public void update() {
        chartComponent.update( (float)timer.getTime() );
    }

    public void setMagnitude( double magnitude ) {
        chartComponent.setMagnitude( magnitude );
    }

    public void setSliderVisible( boolean b ) {
        verticalChartSlider.setVisible( b );
    }

    public void addSliderListener( VerticalChartSlider.Listener listener ) {
        verticalChartSlider.addListener( listener );
    }

    public VerticalChartSlider getVerticalChartSlider() {
        return verticalChartSlider;
    }

    public void updateSlider() {
        JSlider js = verticalChartSlider.getSlider();
        DataSet dataSet = chartComponent.getDefaultDataSet();
        if( !js.getValueIsAdjusting() && dataSet.size() > 0 ) {
            double lastY = dataSet.getLastPoint().getY();
            verticalChartSlider.setValue( lastY );
        }
    }

    public void cursorMovedToTime( double time, int index ) {
        horizontalCursor.setX( time );
        DataSeries dataSeries = chartComponent.seriesAt( 0 ).dataSeries;
        verticalChartSlider.setValue( dataSeries.pointAt( index ) );
        setTextValue( dataSeries.pointAt( index ) );
    }

    public void setCursorVisible( boolean visible ) {
        if( isVisible() ) {
            horizontalCursor.setVisible( visible );
        }
    }

    public FloatingControl getFloatingControl() {
        return floatingControl;
    }

    public static class FloatingControl extends VerticalLayoutPanel {
        static BufferedImage play;
        static BufferedImage pause;
        private JButton pauseButton;
        private JButton recordButton;
        private JButton resetButton;

        static {
            try {
                play = ImageLoader.loadBufferedImage( "images/icons/java/media/Play16.gif" );
                pause = ImageLoader.loadBufferedImage( "images/icons/java/media/Pause16.gif" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }

        static class ControlButton extends JButton {
            static Font font = MMFontManager.getFontSet().getControlButtonFont();

            public ControlButton( String text ) {
                super( text );
                setFont( font );
            }
        }

        public FloatingControl( final PlotDeviceModel plotDeviceModel, final ApparatusPanel apparatusPanel ) {
            pauseButton = new ControlButton( "Pause" );
            pauseButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    plotDeviceModel.setPaused( true );
                }
            } );
            recordButton = new ControlButton( "Go" );
            recordButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    plotDeviceModel.setRecordMode();
                    plotDeviceModel.setPaused( false );
                }
            } );

            resetButton = new ControlButton( "Clear" );
            resetButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    boolean paused = plotDeviceModel.isPaused();
                    plotDeviceModel.setPaused( true );
                    int option = JOptionPane.showConfirmDialog( apparatusPanel, "Are you sure you want to clear the graphs?", "Confirm Reset", JOptionPane.YES_NO_CANCEL_OPTION );
                    if( option == JOptionPane.OK_OPTION || option == JOptionPane.YES_OPTION ) {
                        plotDeviceModel.reset();
                    }
                    else if( option == JOptionPane.CANCEL_OPTION || option == JOptionPane.NO_OPTION ) {
                        plotDeviceModel.setPaused( paused );
                    }
                }
            } );
            plotDeviceModel.addListener( new PlotDeviceModel.ListenerAdapter() {
                public void recordingStarted() {
                    setButtons( false, true, true );
                }

                public void recordingPaused() {
                    setButtons( true, false, true );
                }

                public void recordingFinished() {
                    setButtons( false, false, true );
                }

                public void reset() {
                    setButtons( true, false, false );
                }

                public void rewind() {
                    setButtons( true, false, true );
                }
            } );
            add( recordButton );
            add( pauseButton );
            add( resetButton );
            pauseButton.setEnabled( false );
        }

        private void setButtons( boolean record, boolean pause, boolean reset ) {
            recordButton.setEnabled( record );
            pauseButton.setEnabled( pause );
            resetButton.setEnabled( reset );
        }

        public void setVisible( boolean aFlag ) {
            super.setVisible( aFlag );
        }
    }

    public class ChartComponent {
        private CloseButton closeButton;
        private ArrayList series = new ArrayList();
        private MagButton magPlus;
        private MagButton magMinus;
        private Chart chart;
        private float lastTime;
        private double xShift;

        public DataSet getDefaultDataSet() {
            return seriesAt( 0 ).dataSet;
        }

        private Series seriesAt( int i ) {
            return (Series)series.get( i );
        }

        public void addSeries( DataSeries dataSeries, Color color, String title, Stroke stroke ) {
            Series series = new Series( title, (ApparatusPanel)getComponent(), dataSeries, stroke, color );
            addSeries( series );
        }

        public void setDataSeriesVisible( int index, boolean visible ) {
            seriesAt( index ).setVisible( visible );
        }

        public class Series {
            DataSet dataSet;
            DataSeries dataSeries;
            Color color;
            Stroke stroke;
            PhetTextGraphic readout;
            PhetTextGraphic readoutValue;
            private LinePlot dataSetGraphic;

            public Series( String name, ApparatusPanel panel, DataSeries dataSeries, Stroke stroke, Color color ) {
                Font readoutFont = MMFontManager.getFontSet().getReadoutFont();
                this.dataSeries = dataSeries;
                dataSet = new DataSet();

                dataSetGraphic = new LinePlot( dataSet, stroke, color );
                chart.addDataSetGraphic( dataSetGraphic );
                dataSeries.addListener( new DataSeries.Listener() {
                    public void changed() {
                        update( (float)timer.getTime() );
                    }
                } );
                readout = new PhetTextGraphic( panel, readoutFont, name + " = ", color, 100, 100 );
                panel.addGraphic( readout, 10000 );
                readoutValue = new PhetTextGraphic( panel, readoutFont, "0.0 " + units, color, 100, 100 );
                panel.addGraphic( readoutValue, 10000 );
            }

//            public void refitCurve() {
//                Point2D.Double[] copy = dataSet.toArray();
//                dataSet.clear();
//                for( int i = 0; i < copy.length; i++ ) {
//                    Point2D.Double aDouble = copy[i];
//                    dataSet.addPoint( aDouble );
//                }
//            }

            public void reset() {
                dataSet.clear();
                dataSeries.reset();
            }

            public void setVisible( boolean visible ) {
                readout.setVisible( visible );
                readoutValue.setVisible( visible );
                if( visible ) {
                    while( !chart.containsDataSetGraphic( dataSetGraphic ) ) {
                        chart.addDataSetGraphic( dataSetGraphic );
                    }
                }
                else {
                    while( chart.containsDataSetGraphic( dataSetGraphic ) ) {
                        chart.removeDataSetGraphic( dataSetGraphic );
                    }
                }
//                chart.ad
            }

            public void setViewBounds( Rectangle rectangle ) {
                int index = series.indexOf( this );
                int yOffset = 0;
                for( int i = 0; i < index; i++ ) {
                    yOffset += seriesAt( i ).readout.getBounds().height + 2;
                }
                readout.setLocation( chart.getViewBounds().x + 15, chart.getViewBounds().y + readout.getHeight() - 5 + yOffset );
                readoutValue.setLocation( readout.getLocation().x + readout.getWidth() + 5, readout.getLocation().y );
            }

            public void update( float time ) {
                lastTime = time;
                if( dataSeries.size() <= 1 ) {
                    dataSet.clear();
                }
                else {
                    double position = dataSeries.getLastPoint();// * scale + yoffset;
                    if( Double.isInfinite( position ) ) {
                        return;
                    }
                    Point2D.Double pt = new Point2D.Double( time - xShift, position );
                    dataSet.addPoint( pt );
                    horizontalCursor.setMaxX( time );//so it can't be dragged past the end of recorded pressTime.
                }
            }
        }

        public ChartComponent( ApparatusPanel panel, Rectangle2D inputBox, DataSeries dataSeries, double xShift ) throws IOException {
            Font axisFont = MMFontManager.getFontSet().getAxisFont();

            chart = new Chart( panel, new Range2D( inputBox ), new Rectangle( 0, 0, 100, 100 ) );
            Series defaultSeries = new Series( title, panel, dataSeries, stroke, color );
            addSeries( defaultSeries );
            this.xShift = xShift;
            chart.setBackground( Color.yellow );

            chart.getHorizontalTicks().setVisible( true );
            chart.getHorizonalGridlines().setMajorGridlinesColor( Color.darkGray );
            chart.getVerticalGridlines().setMajorGridlinesColor( Color.darkGray );
            chart.getXAxis().setMajorTickFont( axisFont );
            chart.getYAxis().setMajorTicksVisible( false );
            chart.getYAxis().setMajorTickFont( axisFont );
            chart.getVerticalGridlines().setMinorGridlinesVisible( false );
            chart.getXAxis().setMajorGridlines( new double[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20} ); //to ignore the 0.0
            chart.getXAxis().setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{6, 6}, 0 ) );
            chart.getYAxis().setMinorTicksVisible( false );
            double spacing = inputBox.getHeight() / 21;
            chart.getYAxis().setMajorTickSpacing( spacing );
//            chart.getHorizontalTicks().setMajorTickSpacing( spacing );
            chart.getVerticalTicks().setMajorTickSpacing( spacing );
            chart.getHorizonalGridlines().setMajorTickSpacing( spacing );
            chart.setVerticalTitle( title, color, verticalTitleFont );
            chart.getVerticalTicks().setMajorOffset( new JSlider().getWidth() - 5, 0 );

            closeButton = new CloseButton();
            closeButton.setToolTipText( "Close Graph" );
            panel.add( closeButton );

            BufferedImage imgPlus = ImageLoader.loadBufferedImage( "images/icons/glass-20-plus.gif" );
            BufferedImage imgMinus = ImageLoader.loadBufferedImage( "images/icons/glass-20-minus.gif" );
            final double smooth = 1;
            ActionListener smoothPos = new Increment( smooth );
            ActionListener smoothNeg = new Decrement( smooth );
            ActionListener incPos = new Increment( 5 );
            ActionListener incNeg = new Decrement( 5 );
            magPlus = new MagButton( new ImageIcon( imgPlus ), smoothPos, incPos, "Zoom In" );
            magMinus = new MagButton( new ImageIcon( imgMinus ), smoothNeg, incNeg, "Zoom Out" );
            panel.add( magPlus );
            panel.add( magMinus );
        }

        private void addSeries( Series defaultSeries ) {
            series.add( defaultSeries );
        }

        protected Rectangle determineBounds() {
            return chart.getVisibleBounds();
        }

        public void setShift( double xShift ) {
            this.xShift = xShift;
        }

        public void setInputRange( Rectangle2D.Double inputBox ) {
            Range2D range = new Range2D( inputBox );
            chart.setRange( range );
//            refitCurve();
        }

//        private void refitCurve() {
//            for( int i = 0; i < series.size(); i++ ) {
//                Series series1 = (Series)series.get( i );
//                series1.refitCurve();
//            }
//        }

        public double getxShift() {
            return xShift;
        }

        public Chart getChart() {
            return chart;
        }

        public void reset() {
            for( int i = 0; i < series.size(); i++ ) {
                Series series1 = (Series)series.get( i );
                series1.reset();
            }
        }

        public void setVisible( boolean visible ) {
            closeButton.setVisible( visible );
            magPlus.setVisible( visible );
            magMinus.setVisible( visible );

            for( int i = 0; i < series.size(); i++ ) {
                Series series1 = (Series)series.get( i );
                series1.setVisible( visible );
            }
        }

        public void update( float time ) {
            if( time == lastTime ) {
                return;
            }
            for( int i = 0; i < series.size(); i++ ) {
                seriesAt( i ).update( time );
            }
        }

        public void setMagnitude( double magnitude ) {

            Rectangle2D.Double positionInputBox = new Rectangle2D.Double( plotDeviceModel.getMinTime(), -magnitude, plotDeviceModel.getMaxTime() - plotDeviceModel.getMinTime(), magnitude * 2 );
            chartComponent.setInputRange( positionInputBox );
            plotDeviceView.repaintBackground( getChart().getViewBounds() );
        }

        public void setViewBounds( Rectangle rectangle ) {
            chart.setViewBounds( rectangle );
            chart.setBackground( Color.yellow );
            chart.getVerticalTicks().setMajorOffset( 0, 0 );
            Rectangle vb = chart.getViewBounds();
            int x = vb.x + vb.width - closeButton.getPreferredSize().width;
            int y = vb.y;
            closeButton.setPosition( x - 2, y + 2 );

            Dimension buttonSize = magPlus.getPreferredSize();
            JSlider js = verticalChartSlider.getSlider();
            int magSep = 1;
            int magOffsetY = 7;
            int magY = js.getY() + js.getHeight() - 2 * buttonSize.height - magSep - magOffsetY;

            int magX = chart.getViewBounds().x + 3;
            magPlus.reshape( magX, magY, buttonSize.width, buttonSize.height );
            magMinus.reshape( magX, magY + magSep + buttonSize.height, buttonSize.width, buttonSize.height );

            for( int i = 0; i < series.size(); i++ ) {
                Series series1 = (Series)series.get( i );
                series1.setViewBounds( rectangle );
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
                    plotDeviceView.repaintBackground();
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
                    plotDeviceView.repaintBackground();
                }
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

    }

    public static class ParameterSet {
        private ApparatusPanel panel;
        private String title;
        private PlotDeviceModel plotDeviceModel;
        private PlotDeviceView plotDeviceView;
        private DataSeries series;
        private PhetTimer timer;
        private Color color;
        private Stroke stroke;
        private Rectangle2D.Double inputBox;
        private double xShift;
        private String units;
        private String labelStr;

        public ParameterSet( ApparatusPanel panel, String title, final PlotDeviceModel plotDeviceModel,
                             final PlotDeviceView plotDeviceView,
                             final DataSeries series, PhetTimer timer, Color color,
                             Stroke stroke, Rectangle2D.Double inputBox,
                             double xShift, String units, String labelStr ) {
            this.panel = panel;
            this.title = title;
            this.plotDeviceModel = plotDeviceModel;
            this.plotDeviceView = plotDeviceView;
            this.series = series;
            this.timer = timer;
            this.color = color;
            this.stroke = stroke;
            this.inputBox = inputBox;
            this.xShift = xShift;
            this.units = units;
            this.labelStr = labelStr;
        }
    }

    public static class TextBox extends JPanel {
        boolean changedByUser;
        JTextField textField;
        JLabel label;
        static Font font = MMFontManager.getFontSet().getTextBoxFont();
        private PlotDevice plotDevice;

        public TextBox( PlotDeviceModel module, int text, String labelText, PlotDevice plotDevice ) {
            this.plotDevice = plotDevice;
            textField = new JTextField( text );
            label = new JLabel( labelText );
            setLayout( new FlowLayout( FlowLayout.CENTER ) );
            textField.addMouseListener( new MouseAdapter() {
                public void mousePressed( MouseEvent e ) {
                    if( isEnabled() ) {
                        textField.selectAll();
                    }
                }
            } );
            textField.addKeyListener( new KeyListener() {
                public void keyTyped( KeyEvent e ) {
                    changedByUser = true;
                    //TODO a bug detecting VK_ENTER on my machine.
//                    if( e.getKeyCode() == KeyEvent.VK_RE ) {
                    parseAndSetValue();
//                    }
                }

                public void keyPressed( KeyEvent e ) {
                    System.out.println( "pressed" );
                }

                public void keyReleased( KeyEvent e ) {
                }
            } );
            label.setFont( font );
            textField.setFont( font );
            add( label );
            add( textField );
            setBorder( BorderFactory.createLineBorder( Color.black ) );
            module.addListener( new PlotDeviceModel.Listener() {
                public void recordingStarted() {
                    textField.setEditable( false );
                }

                public void recordingPaused() {
                    textField.setEditable( true );
                }

                public void recordingFinished() {
                    textField.setEditable( false );
                }

                public void playbackStarted() {
                    textField.setEditable( false );
                }

                public void playbackPaused() {
                    textField.setEditable( true );
                }

                public void playbackFinished() {
                    textField.setEditable( false );
                }

                public void reset() {
                    textField.setEditable( true );
                }

                public void rewind() {
                    textField.setEditable( true );
                }
            } );
        }

        private void parseAndSetValue() {
            String text = getText();
            double value = Double.parseDouble( text );

            plotDevice.setValue( value );//needs error handling.
        }

        public void clearChangedByUser() {
            changedByUser = false;
        }

        public boolean isChangedByUser() {
            return changedByUser;
        }

        public synchronized void addKeyListener( KeyListener l ) {
            textField.addKeyListener( l );
        }

        public void setEditable( boolean b ) {
            textField.setEditable( b );
        }

        public void setHorizontalAlignment( int right ) {
            textField.setHorizontalAlignment( right );
        }

        public String getText() {
            return textField.getText();
        }

        public void setText( String valueString ) {
            if( valueString.length() > textField.getColumns() ) {
                valueString = valueString.subSequence( 0, textField.getColumns() ) + "";
            }
            textField.setText( valueString );
        }

        public void setLabelText( String labelText ) {
            label.setText( labelText );
        }
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

    static class MagButton extends JButton {
        public MagButton( Icon icon, ActionListener smooth, ActionListener click, String tooltip ) {
            super( icon );
            addMouseListener( new RepeatClicker( smooth, click ) );
            setToolTipText( tooltip );
        }
    }

    public static class ChartButton extends JButton {
        private static Font font = MMFontManager.getFontSet().getChartButtonFont();//new Font( "Lucida Sans", Font.BOLD, 14 );

        public ChartButton( String label ) throws IOException {
            super( label, new ImageIcon( ImageLoader.loadBufferedImage( "images/arrow-right.gif" ) ) );
            setFont( font );
            setVerticalTextPosition( AbstractButton.CENTER );
            setHorizontalTextPosition( AbstractButton.LEFT );
        }
    }
}
