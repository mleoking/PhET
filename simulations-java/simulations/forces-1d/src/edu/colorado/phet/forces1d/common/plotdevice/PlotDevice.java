/*PhET, 2004.*/
package edu.colorado.phet.forces1d.common.plotdevice;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.forces1d.Force1DResources;
import edu.colorado.phet.forces1d.charts.BufferedLinePlot;
import edu.colorado.phet.forces1d.charts.Chart;
import edu.colorado.phet.forces1d.charts.DataSet;
import edu.colorado.phet.forces1d.charts.Range2D;
import edu.colorado.phet.forces1d.charts.controllers.HorizontalCursor2;
import edu.colorado.phet.forces1d.charts.controllers.VerticalChartSlider2;
import edu.colorado.phet.forces1d.model.DataSeries;
import edu.colorado.phet.forces1d.model.PhetTimer;
import edu.colorado.phet.forces1d.phetcommon.view.ApparatusPanel;
import edu.colorado.phet.forces1d.phetcommon.view.ApparatusPanel2;
import edu.colorado.phet.forces1d.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.forces1d.phetcommon.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.*;
import edu.colorado.phet.forces1d.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.forces1d.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.forces1d.view.OffsetManager;
import edu.colorado.phet.forces1d.view.PlotDeviceFontManager;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:54:39 AM
 */
public class PlotDevice extends GraphicLayerSet {
    private boolean displayTextField = false;
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
    //    private ChartButton showButton;
    private PhetGraphic showButtonGraphic;
    private DecimalFormat format = new DecimalFormat( "0.00" );
    private String units;
    private Font verticalTitleFont = PlotDeviceFontManager.getFontSet().getVerticalTitleFont();
    private ArrayList listeners = new ArrayList();
    private PhetTextGraphic timeLabel;
    private double value = Double.NaN;
    private BufferedPhetGraphic bufferedPhetGraphic;
    private boolean controllable;
    private Point buttonLoc = new Point();
    private PhetGraphic textFieldGraphic;
    private ApparatusPanel apparatusPanel;
    private OffsetManager offsetManager;

    public PlotDevice( final ParameterSet parameters, BufferedPhetGraphic bufferedPhetGraphic, OffsetManager offsetManager )
            throws IOException {
        super( parameters.panel );
        this.offsetManager = offsetManager;

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
        final ApparatusPanel panel = parameters.panel;
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

        ChartButton showButton = new ChartButton( Force1DResources.get( "PlotDevice.graph" ) + " " + title );
        showButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( true );
            }
        } );
        showButtonGraphic = PhetJComponent.newInstance( panel, showButton );
        verticalChartSlider = new VerticalChartSlider2( panel, chartComponent.getChart() );
//        verticalChartSlider.getSlider().setBackground( PhetLookAndFeel.backgroundColor );
        //TODO do we need to change the backgound of the slider?
//        addGraphic( verticalChartSlider, Double.POSITIVE_INFINITY );
        verticalChartSlider.addListener( new VerticalChartSlider2.Listener() {
            public void valueChanged( double value ) {
                for ( int i = 0; i < listeners.size(); i++ ) {
                    Listener listener = (Listener) listeners.get( i );
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

        textBox = new TextBox( plotDeviceModel, 9, parameters.labelStr, this );
        textBox.setHorizontalAlignment( JTextField.RIGHT );

        textFieldGraphic = PhetJComponent.newInstance( panel, textBox.textField );
        addListener( new Listener() {
            public void readoutChanged( double value ) {
            }

            public void valueChanged( double value ) {
                PhetJComponent.doScheduleRepaint(textFieldGraphic);
            }
        } );

        this.apparatusPanel = parameters.panel;
        plotDeviceModel.addListener( new PlotDeviceModel.ListenerAdapter() {
            public void recordingPaused() {
                Timer task = new Timer( 30, new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        try {
                            Thread.sleep( 100 );
                        }
                        catch( InterruptedException e1 ) {
                            e1.printStackTrace();
                        }
                        if ( displayTextField ) {
                            if ( panel instanceof ApparatusPanel2 ) {
                                ApparatusPanel2 p2 = (ApparatusPanel2) panel;
                                p2.getGraphic().setKeyFocus( textFieldGraphic );
                            }
                            textFieldGraphic.gainedKeyFocus();
                        }
                    }
                } );
                task.setRepeats( false );
                task.start();
            }

            public void recordingStarted() {
                textFieldGraphic.lostKeyFocus();
            }

            public void playbackStarted() {
                textFieldGraphic.lostKeyFocus();
            }

            public void playbackPaused() {
                textFieldGraphic.gainedKeyFocus();
            }
        } );

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
        timeLabel = new PhetTextGraphic( panel, PlotDeviceFontManager.getFontSet().getTimeLabelFont(), Force1DResources.get( "PlotDevice.time" ), Color.red, 0, 0 );
        addGraphic( chartComponent.getChart() );
        addGraphic( timeLabel );
        updateControllable();
        plotDeviceModel.addListener( new PlotDeviceModel.ListenerAdapter() {
            public void recordingStarted() {
                if ( isVisible() ) {
                    horizontalCursor.setVisible( false );
                }
            }

            public void recordingPaused() {
                if ( isVisible() ) {
                    horizontalCursor.setVisible( true );
                    horizontalCursor.setModelX( horizontalCursor.getMaxX() );
                }
            }

            public void recordingFinished() {
                if ( isVisible() ) {
                    horizontalCursor.setVisible( true );
                    horizontalCursor.setModelX( horizontalCursor.getMaxX() );//TODO maybe this should be more to the middle of the screen.?
                }
            }

            public void playbackStarted() {
                if ( isVisible() ) {
                    horizontalCursor.setVisible( true );
                }
            }

            public void playbackPaused() {
                if ( isVisible() ) {
                    horizontalCursor.setVisible( true );
                }
            }

            public void playbackFinished() {
                if ( isVisible() ) {
                    horizontalCursor.setVisible( true );
                }
            }

            public void reset() {
//                horizontalCursor.setVisible( );
            }

            public void rewind() {
//                super.rewind();
                if ( isVisible() ) {
                    horizontalCursor.setVisible( true );
                }
            }
        } );
        horizontalCursor.setVisible( false );
    }

    public PhetGraphic getTextFieldGraphic() {
        return textFieldGraphic;
    }

    public ChartComponent getChartComponent() {
        return chartComponent;
    }

    public void setDisplayTextField( boolean displayTextField ) {
        this.displayTextField = displayTextField;
        if ( displayTextField ) {
            apparatusPanel.addGraphic( textFieldGraphic );//TODO needs an "if use textField"
        }
        else {
            apparatusPanel.removeGraphic( textFieldGraphic );
        }
    }

    public void setDataSeriesVisible( int index, boolean visible ) {
        chartComponent.setDataSeriesVisible( index, visible );
    }

    private void updateControllable() {
        if ( !controllable ) {
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
        if ( module.isTakingData() ) {
            index = series.size() - 1;
        }
        else {
            double time = module.getPlaybackTimer().getTime() + chartComponent.getxShift();
            index = (int) time;//(int)( time / MovingManModel.TIMER_SCALE );
        }
        if ( series.indexInBounds( index ) ) {
            double value = series.pointAt( index );
            setTextValue( value );
        }
    }


    public void setValue( double value ) {
        if ( value != this.value ) {
            verticalChartSlider.setValue( value );
            setTextValue( value );
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
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
        showButtonGraphic.setLocation( buttonLoc.x, buttonLoc.y );
        textFieldGraphic.setLocation( 20, (int) y );
    }

    public int getButtonHeight() {
        return showButtonGraphic.getHeight();
    }

    public void repaintBuffer() {
        if ( isVisible() ) {
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

    double lastTextValue = Double.NaN;

    public void setTextValue( double value ) {
        if ( lastTextValue != value ) {
            lastTextValue = value;
            String valueString = format.format( value );
            if ( valueString.equals( "-0.00" ) ) {
                valueString = "0.00";
            }
            if ( !textBox.getText().equals( valueString ) ) {
                textBox.setText( valueString );
                for ( int i = 0; i < listeners.size(); i++ ) {
                    Listener listener = (Listener) listeners.get( i );
                    listener.readoutChanged( value );
                }
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
        for ( double i = dy; i < magnitude; i += dy ) {
            values.add( new Double( i ) );
        }
        if ( values.size() > 5 ) {
            return getYLines( magnitude, dy * 2 );
        }
        if ( values.size() <= 1 ) {
            return getYLines( magnitude, dy / 2 );
        }
        double[] d = new double[values.size()];
        for ( int i = 0; i < d.length; i++ ) {
            d[i] = ( (Double) values.get( i ) ).doubleValue();
        }
        return d;
    }

//    public ChartButton getShowButton() {
//        return showButton;
//    }

    public void setCloseHandler( ActionListener actionListener ) {
        chartComponent.closeButton.addActionListener( actionListener );
    }

    public PlotDeviceModel getPlotDeviceModel() {
        return plotDeviceModel;
    }

    public void reset() {
        chartComponent.reset();
        horizontalCursor.setMaxX( Double.POSITIVE_INFINITY );//so it can't be dragged past, hopefully.
        horizontalCursor.setVisible( false );
        setTextValue( 0 );
        verticalChartSlider.setValue( 0 );
    }

    public void setViewBounds( Rectangle rectangle ) {
        if ( rectangle.width > 0 && rectangle.height > 0 ) {
            chartComponent.setViewBounds( rectangle );
            verticalChartSlider.setOffsetX( chartComponent.chart.getVerticalTicks().getMajorTickTextBounds().width + getChart().getTitle().getBounds().width );
            verticalChartSlider.update();
            int floaterX = 5;
            textBox.setBounds( floaterX,
                               getChart().getViewBounds().y,
                               textBox.getPreferredSize().width,
                               textBox.getPreferredSize().height );
            textFieldGraphic.setLocation( floaterX, (int) ( getChart().getViewBounds().y + offsetManager.getOffset() ) );
            System.out.println( "offset="+offsetManager.getOffset() );
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
        if ( visible ) {
            horizontalCursor.setVisible( true );
        }
        else {
            horizontalCursor.setVisible( false );
        }
        chartComponent.setVisible( visible );

        plotDeviceView.getApparatusPanel().setLayout( null );
//        plotDeviceView.getApparatusPanel().add( showButton );
        plotDeviceView.getApparatusPanel().addGraphic( showButtonGraphic, Double.POSITIVE_INFINITY );
        showButtonGraphic.setLocation( buttonLoc.x, buttonLoc.y );
//        showButton.reshape( buttonLoc.x, buttonLoc.y, showButton.getPreferredSize().width, showButton.getPreferredSize().height );
        plotDeviceView.relayout();
        showButtonGraphic.setVisible( !visible );
//        showButton.setVisible( !visible );
        textBox.setVisible( visible && displayTextField );

//        floatingControl.setVisible( visible );
//        titleLable.setVisible( visible && adorned );
        updateControllable();
    }

    public void update() {
        chartComponent.update( (float) timer.getTime() );
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
        if ( !js.getValueIsAdjusting() && dataSet.size() > 0 ) {
            double lastY = dataSet.getLastPoint().getY();
            verticalChartSlider.setValue( lastY );
        }
    }

    public void cursorMovedToTime( double time, int index ) {
        if ( index < chartComponent.seriesAt( 0 ).dataSeries.size() ) {

            horizontalCursor.setX( time );
            DataSeries dataSeries = chartComponent.seriesAt( 0 ).dataSeries;
            verticalChartSlider.setValue( dataSeries.pointAt( index ) );
            setTextValue( dataSeries.pointAt( index ) );
            chartComponent.cursorMovedToTime( time, index );
        }//TODO we'll have to handle multiple series.
    }

    public void setCursorVisible( boolean visible ) {
        if ( isVisible() ) {
            horizontalCursor.setVisible( visible );
        }
    }

//    public FloatingControl getFloatingControl() {
//        return floatingControl;
//    }

    public class ChartComponent {
        //        private CloseButton closeButton;
        private ArrayList series = new ArrayList();
        //        private MagButton magPlus;
        //        private MagButton magMinus;
        private Chart chart;
        private float lastTime;
        private double xShift;
        private Series defaultSeries;
        private PhetGraphic magPlusGraphic;
        private PhetGraphic magMinusGraphic;
        private PhetGraphic closeButtonGraphic;
        private CloseButton closeButton = new CloseButton();

//        public MagButton getMagPlus() {
//            return magPlus;
//        }

        public DataSet getDefaultDataSet() {
            return seriesAt( 0 ).dataSet;
        }

        private Series seriesAt( int i ) {
            return (Series) series.get( i );
        }

        public void addSeries( DataSeries dataSeries, Color color, String title, Stroke stroke ) {
            Series series = new Series( title, (ApparatusPanel) getComponent(), dataSeries, stroke, color );
            addSeries( series );
        }

        public void setDataSeriesVisible( int index, boolean visible ) {
            seriesAt( index ).setVisible( visible );
        }

        public void cursorMovedToTime( double time, int index ) {
            for ( int i = 0; i < series.size(); i++ ) {
                Series series1 = (Series) series.get( i );
                series1.cursorMovedToTime( time, index );
            }
        }

        public void repaintBuffer() {
            for ( int i = 0; i < series.size(); i++ ) {
                Series series1 = (Series) series.get( i );
                series1.repaintBuffer();
            }
        }

        public void setBackground( Color color ) {
//            chartBackgroundColor = color;
            chart.setBackground( color );
        }

        public void clearData() {
            for ( int i = 0; i < series.size(); i++ ) {
                Series series1 = (Series) series.get( i );
                series1.clearData();
            }
        }

        public void removeDefaultDataSeries() {
            series.remove( defaultSeries );
            defaultSeries.remove();
        }

        public PhetGraphic getMagPlusGraphic() {
            return magPlusGraphic;
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
                        update( (float) timer.getTime() );
                    }
                } );
                readout = new PhetTextGraphic( panel, readoutFont, name + " = ", color, 100, 100 );
                panel.addGraphic( readout, 10000 );
                readoutValue = new PhetTextGraphic( panel, readoutFont, "0.0 ", color, 100, 100 );
                if ( units.startsWith( "<html>" ) ) {
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
                for ( int i = 0; i < index; i++ ) {
                    yOffset += seriesAt( i ).readout.getBounds().height + 2;
                }
                readout.setLocation( chart.getViewBounds().x + 15, chart.getViewBounds().y + readout.getHeight() - 5 + yOffset );
                readoutValue.setLocation( readout.getLocation().x + readout.getWidth() + 5, readout.getLocation().y );
            }

            public void update( float time ) {
                lastTime = time;
                if ( dataSeries.size() <= 1 ) {
                    dataSet.clear();
                }
                else {
                    double position = dataSeries.getLastPoint();// * scale + yoffset;
                    if ( Double.isInfinite( position ) ) {
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
                if ( visible && PlotDevice.this.isVisible() ) {
                    bufferedLinePlot.clear();
                    dataSet.clear();
                    bufferedLinePlot.setAutoRepaint( false );
                    for ( int i = 0; i < dataSeries.size(); i++ ) {
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

            closeButton.setToolTipText( Force1DResources.get( "PlotDevice.closeGraph" ) );
            closeButtonGraphic = PhetJComponent.newInstance( panel, closeButton );
//            panel.add( closeButton );
            panel.addGraphic( closeButtonGraphic, Double.POSITIVE_INFINITY );

            BufferedImage imgPlus = ImageLoader.loadBufferedImage( "forces-1d/images/icons/glass-20-plus.gif" );
            BufferedImage imgMinus = ImageLoader.loadBufferedImage( "forces-1d/images/icons/glass-20-minus.gif" );
            ActionListener smoothPos = new Increment( holdDownZoom );
            ActionListener smoothNeg = new Decrement( holdDownZoom, maxZoomRange );
            ActionListener incPos = new Increment( singleClickZoom );
            ActionListener incNeg = new Decrement( singleClickZoom, maxZoomRange );
            MagButton magPlus = new MagButton( new ImageIcon( imgPlus ), smoothPos, incPos, Force1DResources.get( "PlotDevice.zoomIn" ) );
            MagButton magMinus = new MagButton( new ImageIcon( imgMinus ), smoothNeg, incNeg, Force1DResources.get( "PlotDevice.zoomOut" ) );
            magPlusGraphic = PhetJComponent.newInstance( panel, magPlus );
            magMinusGraphic = PhetJComponent.newInstance( panel, magMinus );

            panel.addGraphic( magPlusGraphic, Double.POSITIVE_INFINITY );
            panel.addGraphic( magMinusGraphic, Double.POSITIVE_INFINITY );
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
            for ( int i = 0; i < series.size(); i++ ) {
                Series series1 = (Series) series.get( i );
                series1.reset();
            }
        }

        public void setVisible( boolean visible ) {
            closeButtonGraphic.setVisible( visible );
//            magPlus.setVisible( visible );
//            magMinus.setVisible( visible );
            magPlusGraphic.setVisible( visible );
            magMinusGraphic.setVisible( visible );

            for ( int i = 0; i < series.size(); i++ ) {
                Series series1 = (Series) series.get( i );
                series1.setVisible( visible );
            }
        }

        public void update( float time ) {
            if ( time == lastTime ) {
                return;
            }
            for ( int i = 0; i < series.size(); i++ ) {
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
            int x = vb.x + vb.width - closeButtonGraphic.getWidth();
            int y = vb.y;
            closeButtonGraphic.setLocation( x - 2, y + 2 );
            Dimension buttonSize = new Dimension( magPlusGraphic.getWidth(), magPlusGraphic.getHeight() );

            int magSep = 1;
            int magOffsetY = 7;
            int magY = chart.getViewBounds().y + chart.getViewBounds().height - 2 * buttonSize.height - magSep - magOffsetY;
            int magX = chart.getViewBounds().x + 3;

            magPlusGraphic.setLocation( magX, magY );
            magMinusGraphic.setLocation( magX, magY + magSep + buttonSize.height );
            for ( int i = 0; i < series.size(); i++ ) {
                Series series1 = (Series) series.get( i );
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
                if ( newDiffY < MAX ) {
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
                if ( newDiffY > 0 ) {
                    setMagnitude( newDiffY );
                    setPaintYLines( getYLines( newDiffY, 5 ) );
                    plotDeviceView.repaintBackground();
                }
            }

        }

        public void setPaintYLines( double[] lines ) {
            double[] full = new double[lines.length * 2 + 1];
            for ( int i = 0; i < lines.length; i++ ) {
                full[i] = lines[i];
                full[full.length - 1 - i] = -lines[i];
            }
            full[lines.length] = 0;

            double[] half = new double[lines.length * 2];
            for ( int i = 0; i < lines.length; i++ ) {
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

    public static class TypingTextField extends JTextField {
        private PlotDevice plotDevice;
        private boolean changedByUser = false;

        public TypingTextField( int columns, final PlotDevice plotDevice, PlotDeviceModel module ) {
            super( columns );
            Font borderFont = new Font( PhetFont.getDefaultFontName(), 0, 12 );
            setBorder( BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), Force1DResources.get( "PlotDevice.appliedForce" ), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, borderFont, plotDevice.color ) );
            this.plotDevice = plotDevice;
            this.addMouseListener( new MouseAdapter() {
                public void mousePressed( MouseEvent e ) {
                    if ( isEnabled() ) {
                        selectAll();
                    }
                }
            } );
            this.addKeyListener( new KeyListener() {
                public void keyTyped( KeyEvent e ) {
                    System.out.println( "e = " + e );
                }

                public void keyPressed( KeyEvent e ) {
                    System.out.println( "e = " + e );
                }

                public void keyReleased( KeyEvent e ) {
                    changedByUser = true;
                    if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                        System.out.println( "pressed enter." );
                        parseAndSetValue();
                    }
                }
            } );

//            module.addListener( new PlotDeviceModel.Listener() {
//                public void recordingStarted() {
//                    setEditable( false );
//                }
//
//                public void recordingPaused() {
//                    setEditable( true && plotDevice.controllable );
//                    selectAll();
//                }
//
//                public void recordingFinished() {
//                    setEditable( false );
//                }
//
//                public void playbackStarted() {
//                    setEditable( false );
//                }
//
//                public void playbackPaused() {
//                    setEditable( true && plotDevice.controllable );
//                    selectAll();
//                }
//
//                public void playbackFinished() {
//                    setEditable( false );
//                }
//
//                public void reset() {
//                    setEditable( true && plotDevice.controllable );
//                }
//
//                public void rewind() {
//                    setEditable( true && plotDevice.controllable );
//                }
//            } );
        }

        public void setEnabled( boolean enabled ) {
            super.setEnabled( enabled );
            if ( enabled ) {
                selectAll();
            }
        }

        private void parseAndSetValue() {
            String text = getText();
            text = text.replace( ',', '.' );//to handle multi-lingual
            double value = Double.parseDouble( text );
            plotDevice.setValue( value );//needs error handling.

        }
    }

    public static class TextBox extends JPanel {
        //        boolean changedByUser;
        private TypingTextField textField;
        JLabel label;
        static Font font = PlotDeviceFontManager.getFontSet().getTextBoxFont();
        private PlotDevice plotDevice;

        public TextBox( PlotDeviceModel module, int text, String labelText, final PlotDevice plotDevice ) {
            this.plotDevice = plotDevice;
            textField = new TypingTextField( text, plotDevice, module );
            label = new JLabel( labelText );
            setLayout( new FlowLayout( FlowLayout.CENTER ) );

            label.setFont( font );
            textField.setFont( font );
            add( label );
            add( textField );
            setBorder( BorderFactory.createLineBorder( Color.black ) );
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
            if ( valueString.length() > textField.getColumns() ) {
                valueString = valueString.subSequence( 0, textField.getColumns() ) + "";
            }
            if ( !textField.getText().equals( valueString ) ) {
                textField.setText( valueString );
            }
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
            if ( icon == null ) {
//                BufferedImage image = ImageLoader.loadBufferedImage( "forces-1d/images/x-25.gif" );
//                BufferedImage image = ImageLoader.loadBufferedImage( "forces-1d/images/x.png" );
                BufferedImage image = ImageLoader.loadBufferedImage( "forces-1d/images/x-30.png" );
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
            if ( timer != null ) {
                timer.stop();
                long time = System.currentTimeMillis();
                if ( time - pressTime < initDelay ) {
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
        private static Font font = PlotDeviceFontManager.getFontSet().getChartButtonFont();//new Font( PhetDefaultFont.LUCIDA_SANS, Font.BOLD, 14 );

        public ChartButton( String label ) throws IOException {
//            super( label, new ImageIcon( ImageLoader.loadBufferedImage( "forces-1d/images/arrow-right.gif" ) ) );
            super( label );
            setFont( font );
            setVerticalTextPosition( AbstractButton.CENTER );
            setHorizontalTextPosition( AbstractButton.LEFT );
        }
    }
}
