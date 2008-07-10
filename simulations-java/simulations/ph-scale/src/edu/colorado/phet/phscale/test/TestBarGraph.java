/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.test;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.TextAnchor;

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
        
        CategoryDataset dataset = createDataset();
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

    private static CategoryDataset createDataset() {
        double[][] data = new double[][] { { 4.0, 3.0, 2.0 } };
        return DatasetUtilities.createCategoryDataset( "Series ", "Category ", data );
    }
    
    private static JFreeChart createChart( CategoryDataset dataset ) {
        
        JFreeChart chart = ChartFactory.createBarChart(
            "",      // chart title
            "default X-axis label",
            "default Y-axis label",
            dataset,
            PlotOrientation.VERTICAL,
            false,  // legend
            false, // tooltips
            false // urls
        );

        // get a reference to the plot for further customization...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setNoDataMessage( "NO DATA!" );

        // render each category in a different color
        CategoryItemRenderer renderer = new CustomRenderer( new Paint[] { Color.RED, Color.GREEN, Color.BLUE } );
        renderer.setItemLabelGenerator( new StandardCategoryItemLabelGenerator() );
        renderer.setItemLabelsVisible( true );
        ItemLabelPosition p = new ItemLabelPosition( ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, -45.0 );
        renderer.setPositiveItemLabelPosition( p );
        plot.setRenderer( renderer );
        
        // change the margin at the top of the range (y) axis
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        rangeAxis.setLowerMargin( 0.15 );
        rangeAxis.setUpperMargin( 0.15 );

        return chart;      
    }
    
    private void handlePHSliderChanged() {
        _pHValue.setText( String.valueOf( _pHSlider.getValue() ) );
        //XXX update chart
    }
    
    private void handleUnitsChanged() {
        if ( _concentrationRadioButton.isSelected() ) {
            //XXX change series values for moles/L
        }
        else {
            //XXX change series values for moles
        }
    }
    
    private void handleScaleChanged() {
        if ( _logRadioButton.isSelected() ) {
            //XXX log
        }
        else {
            //XXX linear
        }
    }
    
    /**
     * A custom renderer that returns a different color for each item in a 
     * single series.
     */
    static class CustomRenderer extends BarRenderer {

        /** The colors. */
        private Paint[] colors;

        /**
         * Creates a new renderer.
         *
         * @param colors  the colors.
         */
        public CustomRenderer( Paint[] colors ) {
            this.colors = colors;
        }

        /**
         * Returns the paint for an item.  
         * Overrides the default behavior inherited from AbstractSeriesRenderer.
         *
         * @param row  the series.
         * @param column  the category.
         *
         * @return The item color.
         */
        public Paint getItemPaint( int row, int column ) {
            return this.colors[column % this.colors.length];
        }
    }
    
    public static void main( String args[] ) {
        TestBarGraph frame = new TestBarGraph();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 1024, 768 ) );
        frame.setVisible( true );
    }
}
