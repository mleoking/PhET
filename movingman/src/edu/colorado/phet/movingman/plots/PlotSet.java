/** Sam Reid*/
package edu.colorado.phet.movingman.plots;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.chart.controllers.VerticalChartSlider;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.movingman.MMFontManager;
import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.model.Man;
import edu.colorado.phet.movingman.model.MovingManModel;
import edu.colorado.phet.movingman.plotdevice.PlotDeviceListenerAdapter;
import edu.colorado.phet.movingman.plotdevice.PlotDeviceSeries;
import edu.colorado.phet.movingman.view.GoPauseClearPanel;
import edu.colorado.phet.movingman.view.MovingManApparatusPanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 19, 2004
 * Time: 6:54:48 PM
 * Copyright (c) Oct 19, 2004 by Sam Reid
 */
public class PlotSet {
    private MMPlotSuite positionSuite;
    private MMPlotSuite velSuite;
    private MMPlotSuite accSuite;

    private MovingManModule module;
    private MovingManModel movingManModel;
    private MovingManApparatusPanel movingManApparatusPanel;
    private Font readoutFont = MMFontManager.getFontSet().getReadoutFont();
    private ArrayList listeners = new ArrayList();
    private Color foregroundColor = new Color( 225, 255, 225 );

    public PlotSet( final MovingManModule module,
                    final MovingManApparatusPanel movingManApparatusPanel ) {
        this.movingManApparatusPanel = movingManApparatusPanel;
        this.movingManModel = module.getMovingManModel();
        this.module = module;
        double maxPositionView = 12;
        double maxVelocity = 12;
        double maxAccel = 12;

        BasicStroke plotStroke2 = new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

        final MMPlot positionPlot = new MMPlot( module, movingManApparatusPanel, "Position", "x", "images/blue-arrow.png", Color.blue );
        positionPlot.setChartRange( new Range2D( movingManModel.getMinTime(), -maxPositionView, movingManModel.getMaxTime(), maxPositionView ) );
        final PlotDeviceSeries positionSeries = new PlotDeviceSeries( positionPlot,
                                                                      module.getMovingManModel().getPositionDataSuite().getSmoothedDataSeries(), Color.blue, "Position", plotStroke2, readoutFont, SimStrings.get( "PlotSet.MetersAbbreviation" ), "-99.9" );
        positionPlot.addPlotDeviceData( positionSeries );

        ManValueChange positionValueChange = new ManValueChange.PositionChange( module, this );
        ManValueChange velValueChange = new ManValueChange.VelocityChange( module, this );
        ManValueChange accValueChange = new ManValueChange.AccelerationChange( module, this );

        final SliderHandler positionHandler = new SliderHandler( module, positionValueChange ) {
            public void valueChanged( double value ) {
                if( value < -10 ) {
                    value = -10;
                }
                if( value > 10 ) {
                    value = 10;
                }
                super.valueChanged( value );
            }
        };
        positionPlot.addListener( new PlotDeviceListenerAdapter() {
            public void sliderDragged( double dragValue ) {
                positionHandler.valueChanged( dragValue );
            }
        } );

        final MMPlot velocityPlot = new MMPlot( module, movingManApparatusPanel, "Velocity", "v", "images/red-arrow.png", Color.red );

        PlotDeviceSeries velSeries = new PlotDeviceSeries( velocityPlot, module.getMovingManModel().getVelocitySeries().getSmoothedDataSeries(),
                                                           Color.red, "Velocity", plotStroke2, readoutFont, SimStrings.get( "PlotSet.MetersPerSecondAbbreviation" ), "-99.9" );
        velocityPlot.addPlotDeviceData( velSeries );
        velocityPlot.setChartRange( new Range2D( movingManModel.getMinTime(), -maxVelocity, movingManModel.getMaxTime(), maxVelocity ) );

        final SliderHandler velHandler = new SliderHandler( module, velValueChange );
        velocityPlot.addListener( new PlotDeviceListenerAdapter() {
            public void sliderDragged( double dragValue ) {
                velHandler.valueChanged( dragValue );
            }
        } );

        final MMPlot accelerationPlot = new MMPlot( module, movingManApparatusPanel, "Acceleration", "a", "images/green-arrow.png", Color.green );

        Color green = new Color( 40, 165, 50 );
        accelerationPlot.addPlotDeviceData( new PlotDeviceSeries( accelerationPlot, module.getMovingManModel().getAccelerationDataSuite().getSmoothedDataSeries(),
                                                                  green, "Acceleration", plotStroke2, readoutFont, SimStrings.get( "PlotSet.MetersPerSecondSquaredAbbreviation" ), "-999.9" ) );
        accelerationPlot.setChartRange( new Range2D( movingManModel.getMinTime(), -maxAccel, movingManModel.getMaxTime(), maxAccel ) );

        final SliderHandler accelHandler = new SliderHandler( module, accValueChange );
        accelerationPlot.addListener( new PlotDeviceListenerAdapter() {
            public void sliderDragged( double dragValue ) {
                System.out.println( "dragValue = " + dragValue );
                accelHandler.valueChanged( dragValue );
            }
        } );

        module.getMan().addListener( new Man.Adapter() {
            public void positionChanged( double x ) {
                if( module.isPaused() ) {
                    positionPlot.valueChanged( x );
                }
            }

            public void velocityChanged( double velocity ) {
                if( module.isPaused() ) {
                    velocityPlot.valueChanged( velocity );
                }
            }

            public void accelerationChanged( double acceleration ) {
                if( module.isPaused() ) {
                    accelerationPlot.valueChanged( acceleration );
                }
            }
        } );

        positionSuite = new MMPlotSuite( module, movingManApparatusPanel, positionPlot );
        velSuite = new MMPlotSuite( module, movingManApparatusPanel, velocityPlot );
        accSuite = new MMPlotSuite( module, movingManApparatusPanel, accelerationPlot );

        positionSuite.getTextBox().addKeyListener( new TextHandler( positionSuite.getTextBox(), module, positionValueChange ) );
        accSuite.getTextBox().addKeyListener( new TextHandler( accSuite.getTextBox(), module, accValueChange ) );
        velSuite.getTextBox().addKeyListener( new TextHandler( velSuite.getTextBox(), module, velValueChange ) );

        MMPlotSuite.Listener listener = new MMPlotSuite.Listener() {
            public void plotVisibilityChanged() {
                movingManApparatusPanel.relayout();
            }

            public void valueChanged( double value ) {
            }
        };
        positionSuite.addListener( listener );
        velSuite.addListener( listener );
        accSuite.addListener( listener );

        PlotDeviceListenerAdapter plotDeviceListenerAdapter = new PlotDeviceListenerAdapter() {
            public void maxTimeChanged( double maxTime ) {
                positionPlot.setMaxTime( maxTime );
                velocityPlot.setMaxTime( maxTime );
                accelerationPlot.setMaxTime( maxTime );
            }
        };
        positionPlot.addListener( plotDeviceListenerAdapter );
        velocityPlot.addListener( plotDeviceListenerAdapter );
        accelerationPlot.addListener( plotDeviceListenerAdapter );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public MMPlotSuite getPlotSuiteFor( GoPauseClearPanel goPauseClearPanel ) {
        if( positionSuite.getGoPauseClearPanel() == goPauseClearPanel ) {
            return positionSuite;
        }
        else if( velSuite.getGoPauseClearPanel() == goPauseClearPanel ) {
            return velSuite;
        }
        else if( accSuite.getGoPauseClearPanel() == goPauseClearPanel ) {
            return accSuite;
        }
        return null;
    }

    public MMPlotSuite[] getOtherPlots( MMPlotSuite ignore ) {
        ArrayList list = new ArrayList();
        if( ignore != positionSuite ) {
            list.add( positionSuite );
        }
        if( ignore != velSuite ) {
            list.add( velSuite );
        }
        if( ignore != accSuite ) {
            list.add( accSuite );
        }
        return (MMPlotSuite[])list.toArray( new MMPlotSuite[0] );
    }

    public static interface Listener {

        void setAccelerationControlMode();

        void setVelocityControlMode();

        void setPositionControlMode();
    }

    void notifyAccelerationControlMode() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.setAccelerationControlMode();
        }
    }

    void notifyVelocityControlMode() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.setVelocityControlMode();
        }
    }

    void notifyPositionControlMode() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.setPositionControlMode();
        }
    }

    public MMPlot getAccelerationPlot() {
        return accSuite.getPlotDevice();
    }

    public MMPlot getPositionPlot() {
        return positionSuite.getPlotDevice();
    }

    public MMPlot getVelocityPlot() {
        return velSuite.getPlotDevice();
    }

    public void updateSliders() {
        getPositionPlot().updateSlider();
        getVelocityPlot().updateSlider();
        getAccelerationPlot().updateSlider();
    }

    public void setCursorsVisible( boolean visible ) {
        getPositionPlot().setCursorVisible( visible );
        getVelocityPlot().setCursorVisible( visible );
        getAccelerationPlot().setCursorVisible( visible );
    }

    public void reset() {
        positionSuite.reset();
        velSuite.reset();
        accSuite.reset();
    }

    public void enterTextBoxValues() {
        if( positionSuite.getTextBox().isChangedByUser() ) {
            try {
                String x = positionSuite.getTextBox().getText();
                double xVal = Double.parseDouble( x );
                module.getMan().setPosition( xVal );
            }
            catch( NumberFormatException nfe ) {
                getPositionPlot().setTextValue( module.getMan().getPosition() );
            }
            positionSuite.getTextBox().clearChangedByUser();
        }
        if( velSuite.getTextBox().isChangedByUser() ) {
            try {
                String v = velSuite.getTextBox().getText();
                double vVal = Double.parseDouble( v );
                module.getMan().setVelocity( vVal );
            }
            catch( NumberFormatException nfe ) {
                getPositionPlot().setTextValue( module.getMan().getPosition() );
            }
            velSuite.getTextBox().clearChangedByUser();
        }
        if( accSuite.getTextBox().isChangedByUser() ) {
            try {
                String a = accSuite.getTextBox().getText();
                double aVal = Double.parseDouble( a );
                module.getMan().setAcceleration( aVal );
            }
            catch( NumberFormatException nfe ) {
                getPositionPlot().setTextValue( module.getMan().getPosition() );
            }
            accSuite.getTextBox().clearChangedByUser();
        }
    }

    static class SliderHandler implements VerticalChartSlider.Listener {
        private MovingManModule module;
        ManValueChange manValueChange;

        public SliderHandler( MovingManModule module, ManValueChange manValueChange ) {
            this.module = module;
            this.manValueChange = manValueChange;
        }

        public void valueChanged( double value ) {
            module.setRecordMode();
            manValueChange.setValue( module.getMan(), value );
            module.setSmoothingSmooth();
        }
    }

    public static class TextHandler implements KeyListener {
        TextBox textBox;
        MovingManModule module;
        ManValueChange manValueChange;

        public TextHandler( TextBox textBox, MovingManModule module, ManValueChange manValueChange ) {
            this.textBox = textBox;
            this.module = module;
            this.manValueChange = manValueChange;
        }

        public void keyTyped( KeyEvent e ) {
        }

        public void keyPressed( KeyEvent e ) {
        }

        public void keyReleased( KeyEvent e ) {
            if( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                String str = textBox.getText();
                double value = Double.parseDouble( str );
                manValueChange.setValue( module.getMan(), value );
                module.setSmoothingSharp();
            }
        }
    }

    public MMPlotSuite getPositionPlotSuite() {
        return positionSuite;
    }

    public MMPlotSuite getVelocityPlotSuite() {
        return velSuite;
    }

    public MMPlotSuite getAccelerationPlotSuite() {
        return accSuite;
    }


}