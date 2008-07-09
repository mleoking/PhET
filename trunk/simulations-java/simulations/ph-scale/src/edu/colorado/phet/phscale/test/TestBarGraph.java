/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * TestBarGraph tests the bar graph feature for ph-scale.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestBarGraph extends JFrame {
    
    private final JSlider _pHSlider;
    private final JLabel _pHValue;
    private final JRadioButton _concentrationRadioButton, _molesRadioButton;
    private final JRadioButton _logRadioButton, _linearRadioButton;
    
    public TestBarGraph() {
        super( "TestBarGraph" );
        
        IntervalXYDataset dataset = createDataset();
        JFreeChart chart = createChart( dataset );
        ChartPanel chartPanel = new ChartPanel( chart );
        
        // pH control
        JPanel pHControlPanel = new JPanel();
        {
            pHControlPanel.setBorder( new TitledBorder( "pH" ) );
            
            _pHSlider = new JSlider( -1, 15 );
            _pHSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    handlePHSliderChanged();
                }
            } );
            
            _pHValue = new JLabel( String.valueOf( _pHSlider.getValue() ) );

            // layout
            EasyGridBagLayout pHControlPanelLayout = new EasyGridBagLayout( pHControlPanel );
            pHControlPanel.setLayout( pHControlPanelLayout );
            int row = 0;
            int column = 0;
            pHControlPanelLayout.addComponent( _pHSlider, row, column++ );
            pHControlPanelLayout.addComponent( _pHValue, row, column++ );
            
            // default state
            _pHSlider.setValue( 7 );
        }
        
        // units control
        JPanel unitsControlPanel = new JPanel();
        {
            unitsControlPanel.setBorder( new TitledBorder( "Y-Axis Units" ) );
            
            _concentrationRadioButton = new JRadioButton( "moles/L" );
            _concentrationRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleUnitsChanged();
                }
            });
            
            _molesRadioButton = new JRadioButton( "moles" );
            _molesRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleUnitsChanged();
                }
            });
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _concentrationRadioButton );
            buttonGroup.add( _molesRadioButton );
            
            // layout
            EasyGridBagLayout unitsControlPanelLayout = new EasyGridBagLayout( unitsControlPanel );
            unitsControlPanel.setLayout( unitsControlPanelLayout );
            int row = 0;
            int column = 0;
            unitsControlPanelLayout.addComponent( _concentrationRadioButton, row++, column );
            unitsControlPanelLayout.addComponent( _molesRadioButton, row++, column );
            
            // default state
            _concentrationRadioButton.setSelected( true );
        }
        
        // scale control
        JPanel scaleControlPanel = new JPanel();
        {
            scaleControlPanel.setBorder( new TitledBorder( "Y-Axis Scale" ) );
            
            _logRadioButton = new JRadioButton( "logarithmic" );
            _logRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleScaleChanged();
                }
            });
            
            _linearRadioButton = new JRadioButton( "linear" );
            _linearRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    handleScaleChanged();
                }
            });
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _logRadioButton );
            buttonGroup.add( _linearRadioButton );
            
            // layout
            EasyGridBagLayout scaleControlPanelLayout = new EasyGridBagLayout( scaleControlPanel );
            scaleControlPanel.setLayout( scaleControlPanelLayout );
            int row = 0;
            int column = 0;
            scaleControlPanelLayout.addComponent( _logRadioButton, row++, column );
            scaleControlPanelLayout.addComponent( _linearRadioButton, row++, column );
            
            // default state
            _logRadioButton.setSelected( true );
        }
        
        // control panel, contains all of the above controls
        JPanel controlPanel = new JPanel();
        {
            EasyGridBagLayout controlPanelLayout = new EasyGridBagLayout( controlPanel );
            controlPanel.setLayout( controlPanelLayout );
            int row = 0;
            int column = 0;
            controlPanelLayout.addFilledComponent( pHControlPanel, row++, column, GridBagConstraints.HORIZONTAL );
            controlPanelLayout.addFilledComponent( unitsControlPanel, row++, column, GridBagConstraints.HORIZONTAL );
            controlPanelLayout.addFilledComponent( scaleControlPanel, row++, column, GridBagConstraints.HORIZONTAL );
        }
        
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( chartPanel, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.EAST );
        
        getContentPane().add( panel );
    }
    
    private static IntervalXYDataset createDataset() {
        
        TimeSeries series1 = new TimeSeries("Series 1", Day.class);
        series1.add(new Day(1, 1, 2003), 54.3);
        series1.add(new Day(2, 1, 2003), 20.3);
        series1.add(new Day(3, 1, 2003), 43.4);
        series1.add(new Day(4, 1, 2003), -12.0);

        TimeSeries series2 = new TimeSeries("Series 2", Day.class);
        series2.add(new Day(1, 1, 2003), 8.0);
        series2.add(new Day(2, 1, 2003), 16.0);
        series2.add(new Day(3, 1, 2003), 21.0);
        series2.add(new Day(4, 1, 2003), 5.0);

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        return dataset;
    }
    
    /** 
     * Creates a chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The chart.
     */
    private static JFreeChart createChart( IntervalXYDataset dataset ) {
        
        JFreeChart chart = ChartFactory.createXYBarChart(
            "",      // chart title
            "default X-axis label",
            false, // dataAxis
            "default Y-axis label",
            dataset,
            PlotOrientation.VERTICAL,
            false,  // legend
            false, // tooltips
            false // urls
        );

        XYPlot plot = (XYPlot) chart.getPlot(); 
        ClusteredXYBarRenderer r = new ClusteredXYBarRenderer();
        plot.setRenderer(r);
        return chart;        
    }
    
    private void handlePHSliderChanged() {
        _pHValue.setText( String.valueOf( _pHSlider.getValue() ) );
        //XXX update chart
    }
    
    private void handleUnitsChanged() {
        //XXX
    }
    
    private void handleScaleChanged() {
        //XXX
    }
    
    public static void main( String args[] ) {
        TestBarGraph frame = new TestBarGraph();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 1024, 768 ) );
        frame.setVisible( true );
    }
}
