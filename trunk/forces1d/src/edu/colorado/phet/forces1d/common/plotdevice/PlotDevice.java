/*PhET, 2004.*/
package edu.colorado.phet.forces1d.common.plotdevice;

import edu.colorado.phet.chart.BufferedLinePlot;
import edu.colorado.phet.chart.Chart;
import edu.colorado.phet.chart.DataSet;
import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.chart.controllers.HorizontalCursor2;
import edu.colorado.phet.chart.controllers.VerticalChartSlider2;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.forces1d.model.DataSeries;
import edu.colorado.phet.forces1d.model.PhetTimer;
import edu.colorado.phet.forces1d.view.PlotDeviceFontManager;

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
public class PlotDevice extends
//                        CompositePhetGraphic
                        GraphicLayerSet {
    private boolean adorned = false;
    private Color chartBackgroundColor = new Color( 255, 247, 204 );
    private String title;
    private PlotDeviceModel plotDeviceModel;
    private PlotDeviceView plotDeviceView;

    private PhetTimer timer;
    private Color color;
    private Stroke stroke;

    private VerticalChartSlider2 verticalChartSlider;
    private HorizontalCursor2 horizontalCursor;
    private TextBox textBox;
    private ChartComponent chartComponent;
    private ChartButton showButton;
    private DecimalFormat format = new DecimalFormat( "0.00" );
    private String units;
    private Font verticalTitleFont = PlotDeviceFontManager.getFontSet().getVerticalTitleFont();
    private ArrayList listeners = new ArrayList();
    private PhetTextGraphic timeLabel;
    private double value = Double.NaN;
    private BufferedPhetGraphic bufferedPhetGraphic;
    private boolean controllable;
    private Point buttonLoc = new Point();

    public void paint( Graphics2D g2 ) {
        super.paint( g2 );
    }

    public PlotDevice( final ParameterSet parameters, BufferedPhetGraphic bufferedPhetGraphic )
            throws IOException {
        super( parameters.panel );
        this.controllable = parameters.controllable;
        this.bufferedPhetGraphic = bufferedPhetGraphic;
        this.plotDeviceView = parameters.plotDeviceView;
        this.units = parameters.units;
        this.title = parameters.title;
        this.plotDeviceModel = parameters.plotDeviceModel;
        this.timer = parameters.plotDeviceModel.getRecordingTimer();
        this.color = parameters.color;
        this.stroke = parameters.stroke;
        int singleClickZoom = parameters.singleClickZoom;
        int holdDownZoom = parameters.holdDownZoom;
        double maxZoomRange = parameters.maxZoomRange;
        chartComponent = new ChartComponent( parameters.panel, parameters.inputBox, parameters.series, parameters.xShift, holdDownZoom, singleClickZoom, maxZoomRange, parameters.verticalTitle );
        ApparatusPanel panel = parameters.panel;
        Rectangle2D.Double inputBox = parameters.inputBox;

        horizontalCursor = new HorizontalCursor2( panel, chartComponent.getChart(), new Color( 15, 0, 255, 50 ), new Color( 50, 0, 255, 150 ), 8 );
        horizontalCursor.addListener( new HorizontalCursor2.Listener() {
            public void modelValueChanged( double modelX ) {
                plotDeviceModel.setPlaybackMode();
                plotDeviceModel.setPaused( true );

                //TODO, need to get the index for this time.  This will enable cursor dragging.
                int index = plotDeviceModel.convertTimeToIndex( modelX );
                plotDeviceModel.cursorMovedToTime( modelX, index );
            }
        } );
        panel.addGraphic( horizontalCursor, 1000 );

        setInputRange( inputBox );
        timer.addListener( new PhetTimer.Listener() {
            public void timeChanged( PhetTimer timer ) {
                update();
            }
        } );

        showButton = new ChartButton( "Graph " + title );
        showButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( true );
            }
        } );
        verticalChartSlider = new VerticalChartSlider2( panel, chartComponent.getChart() );
//        addGraphic( verticalChartSlider, Double.POSITIVE_INFINITY );
        verticalChartSlider.addListener( new VerticalChartSlider2.Listener() {
            public void valueChanged( double value ) {
                for( int i = 0; i < listeners.size(); i++ ) {
                    Listener listener = (Listener)listeners.get( i );
                    listener.valueChanged( value );
                }
            }
        } );
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

        plotDeviceModel.addListener( new PlotDeviceModel.ListenerAdapter() {
            public void rewind() {
                horizontalCursor.setX( 0 );
            }
        } );
        timeLabel = new PhetTextGraphic( panel, PlotDeviceFontManager.getFontSet().getTimeLabelFont(), "Time", Color.red, 0, 0 );
        addGraphic( chartComponent.getChart() );
        addGraphic( timeLabel );
        respectControllable();
        plotDeviceModel.addListener( new PlotDeviceModel.ListenerAdapter() {
            public void recordingStarted() {
                if( isVisible() ) {
                    horizontalCursor.setVisible( false );
                }
            }

            public void recordingPaused() {
                if( isVisible() ) {
                    horizontalCursor.setVisible( true );
                    horizontalCursor.setModelX( horizontalCursor.getMaxX() );
                }
            }

            public void recordingFinished() {
                if( isVisible() ) {
                    horizontalCursor.setVisible( true );
                    horizontalCursor.setModelX( horizontalCursor.getMaxX() );//TODO maybe this should be more to the middle of the screen.?
                }
            }

            public void playbackStarted() {
                if( isVisible() ) {
                    horizontalCursor.setVisible( true );
                }
            }

            public void playbackPaused() {
                if( isVisible() ) {
                    horizontalCursor.setVisible( true );
                }
            }

            public void playbackFinished() {
                if( isVisible() ) {
                    horizontalCursor.setVisible( true );
                }
            }

            public void reset() {
//                horizontalCursor.setVisible( );
            }

            public void rewind() {
//                super.rewind();
                if( isVisible() ) {
                    horizontalCursor.setVisible( true );
                }
            }
        } );
        horizontalCursor.setVisible( false );
    }

    public ChartComponent getChartComponent() {
        return chartComponent;
    }

    public void setAdorned( boolean adorned ) {
        this.adorned = adorned;
    }

    public void setDataSeriesVisible( int index, boolean visible ) {
        chartComponent.setDataSeriesVisible( index, visible );
    }

    private void respectControllable() {
        if( !controllable ) {
            setSliderVisible( false );
            textBox.setEditable( false );
        }
    }

    private void setInputRange( Rectangle2D.Double inputBox ) {
        Range2D range = new Range2D( inputBox );
        getChart().setRange( range );
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
        if( value != this.value ) {
            verticalChartSlider.setValue( value );
            setTextValue( value );
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.valueChanged( value );
            }
            this.value = value;
        }
    }

    public Chart getChart() {
        return chartComponent.getChart();
    }

    public void addDataSeries( DataSeries dataSeries, Color color, String title, Stroke stroke ) {
        chartComponent.addSeries( dataSeries, color, title, stroke );
    }

    public void setButtonLoc( int x, double y ) {
        this.buttonLoc.setLocation( x, y );
        showButton.reshape( buttonLoc.x, buttonLoc.y, showButton.getPreferredSize().width, showButton.getPreferredSize().height );
    }

    public int getButtonHeight() {
        return this.showButton.getHeight();
    }

    public void repaintBuffer() {
        if( isVisible() ) {
            chartComponent.repaintBuffer();
        }
    }

    public void setChartBackground( Color color ) {
        chartComponent.setBackground( color );
    }

    public void clearData() {
        chartComponent.clearData();
    }

    public void removeDefaultDataSeries() {
        chartComponent.removeDefaultDataSeries();
    }

    public static interface Listener {
        void readoutChanged( double value );

        void valueChanged( double value );
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
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.readoutChanged( value );
            }
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
        if( rectangle.width > 0 && rectangle.height > 0 ) {
            chartComponent.setViewBounds( rectangle );
            verticalChartSlider.setOffsetX( chartComponent.chart.getVerticalTicks().getMajorTickTextBounds().width + getChart().getTitle().getBounds().width );
            verticalChartSlider.update();
            int floaterX = 5;
//            titleLable.reshape( floaterX, getChart().getViewBounds().y, titleLable.getPreferredSize().width, titleLable.getPreferredSize().height );
            textBox.reshape( floaterX,
                             getChart().getViewBounds().y,
                             textBox.getPreferredSize().width,
                             textBox.getPreferredSize().height );
//            int dw = Math.abs( textBox.getWidth() - floatingControl.getPreferredSize().width );
//            int floatX = floaterX + dw / 2;
//            floatingControl.reshape( floatX, textBox.getY() + textBox.getHeight() + 5, floatingControl.getPreferredSize().width, floatingControl.getPreferredSize().height );
            chartComponent.setViewBounds( rectangle );
            Point ctr = RectangleUtils.getCenter( chartComponent.determineBounds() );
            timeLabel.setLocation( ctr );
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
        showButton.reshape( buttonLoc.x, buttonLoc.y, showButton.getPreferredSize().width, showButton.getPreferredSize().height );
        plotDeviceView.relayout();
        showButton.setVisible( !visible );
        textBox.setVisible( visible && adorned );

//        floatingControl.setVisible( visible );
//        titleLable.setVisible( visible && adorned );
        respectControllable();
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

    public VerticalChartSlider2 getVerticalChartSlider() {
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
        if( index < chartComponent.seriesAt( 0 ).dataSeries.size() ) {

            horizontalCursor.setX( time );
            DataSeries dataSeries = chartComponent.seriesAt( 0 ).dataSeries;
            verticalChartSlider.setValue( dataSeries.pointAt( index ) );
            setTextValue( dataSeries.pointAt( index ) );
            chartComponent.cursorMovedToTime( time, index );
        }//TODO we'll have to handle multiple series.
    }

    public void setCursorVisible( boolean visible ) {
        if( isVisible() ) {
            horizontalCursor.setVisible( visible );
        }
    }

//    public FloatingControl getFloatingControl() {
//        return floatingControl;
//    }

    public class ChartComponent {
        private CloseButton closeButton;
        private ArrayList series = new ArrayList();
        private MagButton magPlus;
        private MagButton magMinus;
        private Chart chart;
        private float lastTime;
        private double xShift;
        private Series defaultSeries;

        public MagButton getMagPlus() {
            return magPlus;
        }

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

        public void cursorMovedToTime( double time, int index ) {
            for( int i = 0; i < series.size(); i++ ) {
                Series series1 = (Series)series.get( i );
                series1.cursorMovedToTime( time, index );
            }
        }

        public void repaintBuffer() {
            for( int i = 0; i < series.size(); i++ ) {
                Series series1 = (Series)series.get( i );
                series1.repaintBuffer();
            }
        }

        public void setBackground( Color color ) {
//            chartBackgroundColor = color;
            chart.setBackground( color );
        }

        public void clearData() {
            for( int i = 0; i < series.size(); i++ ) {
                Series series1 = (Series)series.get( i );
                series1.clearData();
            }
        }

        public void removeDefaultDataSeries() {
            series.remove( defaultSeries );
            defaultSeries.remove();
        }

        public class Series {
            DataSet dataSet;
            DataSeries dataSeries;
            Color color;
            Stroke stroke;
            PhetTextGraphic readout;
            PhetTextGraphic readoutValue;
            private BufferedLinePlot bufferedLinePlot;
            DecimalFormat format = new DecimalFormat( "0.0" );
            private PhetGraphic readoutUnits;
            private boolean visible = true;
            private ApparatusPanel panel;

            public Series( String name, ApparatusPanel panel, DataSeries dataSeries, Stroke stroke, Color color ) {
                this.panel = panel;
                Font readoutFont = PlotDeviceFontManager.getFontSet().getReadoutFont();
                this.dataSeries = dataSeries;
                dataSet = new DataSet();
                bufferedLinePlot = new BufferedLinePlot( chart, dataSet, stroke, color, bufferedPhetGraphic );
                dataSeries.addListener( new DataSeries.Listener() {
                    public void changed() {
                        update( (float)timer.getTime() );
                    }
                } );
                readout = new PhetTextGraphic( panel, readoutFont, name + " = ", color, 100, 100 );
                panel.addGraphic( readout, 10000 );
                readoutValue = new PhetTextGraphic( panel, readoutFont, "0.0 ", color, 100, 100 );
                if( units.startsWith( "<html>" ) ) {
                    readoutUnits = new HTMLGraphic( panel, readoutFont, units, color );
                }
                else {
                    readoutUnits = new PhetTextGraphic( panel, readoutFont, units, color, 0, 0 );
                }
//                final PhetShapeGraphic readoutBorder=new PhetShapeGraphic( panel,null,new BasicStroke( 1),Color.black );
                readoutValue.addPhetGraphicListener( new PhetGraphicListener() {
                    public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                        int bottom = phetGraphic.getY() + phetGraphic.getHeight();
                        int myHeight = readoutUnits.getHeight();

                        readoutUnits.setLocation( phetGraphic.getX() + phetGraphic.getWidth(), bottom - myHeight );
//                        readoutBorder.setShape( readoutUnits.getBounds() );
                    }

                    public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
                        readoutUnits.setVisible( phetGraphic.isVisible() );
                    }
                } );

                panel.addGraphic( readoutValue, 10000 );
                panel.addGraphic( readoutUnits, 10000 );
//                panel.addGraphic( readoutBorder,10000);
            }

            public void remove() {
                panel.removeGraphic( readout );
                panel.removeGraphic( readoutValue );
                panel.removeGraphic( readoutUnits );
            }

            public void reset() {
                dataSet.clear();
                dataSeries.reset();
            }

            public void setVisible( boolean visible ) {
                this.visible = visible;
                boolean reallyVisible = visible && PlotDevice.this.isVisible();
                readout.setVisible( reallyVisible );
                readoutValue.setVisible( reallyVisible );
                bufferedLinePlot.setVisible( reallyVisible );
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
                    dataSet.addPoint( pt );//this causes a repaint.
                    String text = format.format( position ) + " ";// + units;
                    readoutValue.setText( text );

                    horizontalCursor.setMaxX( time );//so it can't be dragged past the end of recorded time.
                }
            }

            public void cursorMovedToTime( double time, int index ) {
                double value = dataSeries.pointAt( index );
                String text = format.format( value ) + " ";//+ units;
                readoutValue.setText( text );
            }

            public void repaintBuffer() {
                if( visible && PlotDevice.this.isVisible() ) {
                    bufferedLinePlot.clear();
                    dataSet.clear();
                    bufferedLinePlot.setAutoRepaint( false );
                    for( int i = 0; i < dataSeries.size(); i++ ) {
                        double pt = dataSeries.pointAt( i );
                        double time = plotDeviceModel.convertIndexToTime( i );
                        dataSet.addPoint( time, pt );
                    }
                    bufferedLinePlot.setAutoRepaint( true );
                    bufferedLinePlot.repaintAll();
                }
            }

            public void clearData() {
                readoutValue.setText( format.format( 0.0 ) + " " );
            }
        }

        public ChartComponent( ApparatusPanel panel, Rectangle2D inputBox, DataSeries dataSeries, double xShift,
                               double holdDownZoom, double singleClickZoom, double maxZoomRange, String verticalTitle ) throws IOException {
            Font axisFont = PlotDeviceFontManager.getFontSet().getAxisFont();
            chart = new Chart( panel, new Range2D( inputBox ), new Rectangle( 0, 0, 100, 100 ) );
            defaultSeries = new Series( title, panel, dataSeries, stroke, color );
            addSeries( defaultSeries );
            this.xShift = xShift;
            chart.setBackground( chartBackgroundColor );

            chart.getHorizontalTicks().setVisible( false );
            chart.getHorizonalGridlines().setMajorGridlinesColor( Color.darkGray );
            chart.getVerticalGridlines().setMajorGridlinesColor( Color.darkGray );
            chart.getXAxis().setMajorTickFont( axisFont );
            chart.getYAxis().setMajorTicksVisible( false );
            chart.getYAxis().setMajorTickFont( axisFont );
            chart.getVerticalGridlines().setMinorGridlinesVisible( false );
            chart.getXAxis().setMajorGridlines( new double[]{2, 4, 6, 8, 10, 12, 14, 16, 18, 20} ); //to ignore the 0.0
            chart.getXAxis().setStroke( new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{6, 6}, 0 ) );
            chart.getYAxis().setMinorTicksVisible( false );
            double spacing = inputBox.getHeight() / 10;
            chart.getYAxis().setMajorTickSpacing( spacing );
            chart.getVerticalTicks().setMajorTickSpacing( spacing );
            chart.getHorizonalGridlines().setMajorTickSpacing( spacing );
            chart.setVerticalTitle( verticalTitle, color, verticalTitleFont, 60 );
            chart.getVerticalTicks().setMajorOffset( new JSlider().getWidth() - 5, 0 );

            closeButton = new CloseButton();
            closeButton.setToolTipText( "Close Graph" );
            panel.add( closeButton );

            BufferedImage imgPlus = ImageLoader.loadBufferedImage( "images/icons/glass-20-plus.gif" );
            BufferedImage imgMinus = ImageLoader.loadBufferedImage( "images/icons/glass-20-minus.gif" );
            ActionListener smoothPos = new Increment( holdDownZoom );
            ActionListener smoothNeg = new Decrement( holdDownZoom, maxZoomRange );
            ActionListener incPos = new Increment( singleClickZoom );
            ActionListener incNeg = new Decrement( singleClickZoom, maxZoomRange );
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
        }

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
            horizontalCursor.setMaxX( time );
        }

        public void setMagnitude( double magnitude ) {
            Rectangle2D.Double positionInputBox = new Rectangle2D.Double( plotDeviceModel.getMinTime(), -magnitude, plotDeviceModel.getMaxTime() - plotDeviceModel.getMinTime(), magnitude * 2 );
            chartComponent.setInputRange( positionInputBox );
            plotDeviceView.repaintBackground( getChart().getViewBounds() );
        }

        public void setViewBounds( Rectangle rectangle ) {
            chart.setViewBounds( rectangle );
//            chart.setBackground( chartBackgroundColor );
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
            double maxRange;

            public Decrement( double increment, double maxRange ) {
                this.increment = increment;
                this.maxRange = maxRange;
            }

            public void actionPerformed( ActionEvent e ) {
                Range2D origRange = chart.getRange();
                double diffY = origRange.getMaxY();
                double newDiffY = diffY + increment;
                double MAX = maxRange;
                if( newDiffY < MAX ) {
                    setMagnitude( newDiffY );
                    setPaintYLines( getYLines( newDiffY, 5 ) );
                    plotDeviceView.repaintBackground();//TODO this could just repaint this part (not the whole thing.)
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
//        private PhetTimer timer;
        private Color color;
        private Stroke stroke;
        private Rectangle2D.Double inputBox;
        private double xShift;
        private String units;
        private String labelStr;
        private boolean controllable;
        public int singleClickZoom = 5;
        public int holdDownZoom = 1;
        public double maxZoomRange = 100;
        public String verticalTitle;

        public ParameterSet( ApparatusPanel panel, String title, final PlotDeviceModel plotDeviceModel,
                             final PlotDeviceView plotDeviceView,
                             final DataSeries series, Color color,
                             Stroke stroke, Rectangle2D.Double inputBox,
                             double xShift, String units, String labelStr, boolean controllable, String verticalTitle ) {
            this.panel = panel;
            this.title = title;
            this.plotDeviceModel = plotDeviceModel;
            this.plotDeviceView = plotDeviceView;
            this.series = series;
//            this.timer = recordTimer;
            this.color = color;
            this.stroke = stroke;
            this.inputBox = inputBox;
            this.xShift = xShift;
            this.units = units;
            this.labelStr = labelStr;
            this.controllable = controllable;
            this.verticalTitle = verticalTitle;
        }

        public void setZoomRates( int singleClickZoom, int holdDownZoom, double maxZoomRange ) {
            this.singleClickZoom = singleClickZoom;
            this.holdDownZoom = holdDownZoom;
            this.maxZoomRange = maxZoomRange;
        }

    }

    public static class TextBox extends JPanel {
        boolean changedByUser;
        JTextField textField;
        JLabel label;
        static Font font = PlotDeviceFontManager.getFontSet().getTextBoxFont();
        private PlotDevice plotDevice;

        public TextBox( PlotDeviceModel module, int text, String labelText, final PlotDevice plotDevice ) {
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
                }

                public void keyPressed( KeyEvent e ) {
                }

                public void keyReleased( KeyEvent e ) {
                    changedByUser = true;
                    if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                        System.out.println( "pressed enter." );
                        parseAndSetValue();
                    }
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
                    textField.setEditable( true && plotDevice.controllable );
                }

                public void recordingFinished() {
                    textField.setEditable( false );
                }

                public void playbackStarted() {
                    textField.setEditable( false );
                }

                public void playbackPaused() {
                    textField.setEditable( true && plotDevice.controllable );
                }

                public void playbackFinished() {
                    textField.setEditable( false );
                }

                public void reset() {
                    textField.setEditable( true && plotDevice.controllable );
                }

                public void rewind() {
                    textField.setEditable( true && plotDevice.controllable );
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
//                BufferedImage image = ImageLoader.loadBufferedImage( "images/x-25.gif" );
//                BufferedImage image = ImageLoader.loadBufferedImage( "images/x.png" );
                BufferedImage image = ImageLoader.loadBufferedImage( "images/x-30.png" );
//                image=BufferedImageUtils.rescaleYMaintainAspectRatio(null,image,30 );
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
        private static Font font = PlotDeviceFontManager.getFontSet().getChartButtonFont();//new Font( "Lucida Sans", Font.BOLD, 14 );

        public ChartButton( String label ) throws IOException {
//            super( label, new ImageIcon( ImageLoader.loadBufferedImage( "images/arrow-right.gif" ) ) );
            super( label );
            setFont( font );
            setVerticalTextPosition( AbstractButton.CENTER );
            setHorizontalTextPosition( AbstractButton.LEFT );
        }
    }
}
