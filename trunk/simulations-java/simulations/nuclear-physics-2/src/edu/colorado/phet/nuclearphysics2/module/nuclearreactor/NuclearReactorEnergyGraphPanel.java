/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.nuclearreactor;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * This class represents a panel which tracks and displays the energy released
 * per second and the total energy released by the nuclear reactor.
 *
 * @author John Blanco
 */
public class NuclearReactorEnergyGraphPanel extends JPanel {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    private static final double GRAPH_RANGE = 100; // Sets the overall range of the graph.
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private NuclearReactorModel    _nuclearReactorModel;
    private DefaultCategoryDataset _dataset;
    private JFreeChart             _chart;
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public NuclearReactorEnergyGraphPanel( NuclearReactorModel nuclearReactorModel) {
        
        _nuclearReactorModel = nuclearReactorModel;
        
        // Add the border around the chart.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
//                NuclearPhysics2Strings.;
                "JPB TBD",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        // Set the layout.
        setLayout( new GridLayout(0, 1) );

        // Add the two charts that appear on this panel.
        _dataset = new DefaultCategoryDataset();
        _dataset.setValue(GRAPH_RANGE, "Joules", "Total");
        _dataset.setValue(GRAPH_RANGE, "Joules", "Per Second");
        _chart = ChartFactory.createBarChart("JPB TBD - Energy Produced",
                null, "Joules", _dataset, PlotOrientation.VERTICAL, false,
                false, false);
        
        ChartPanel chartPanel = new ChartPanel(_chart);
        add(chartPanel);
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    /**
     * A method that allows standalone testing of this class.
     */
    public static void main( String[] args ) {
        NuclearReactorEnergyGraphPanel testGraph = new NuclearReactorEnergyGraphPanel(null);
        
        JFrame frame = new JFrame();
        frame.getContentPane().add( testGraph );
        frame.setSize( 300, 600 );
        frame.setVisible( true );
        
        try {
            Thread.sleep(1000);
        }
        catch ( Exception e ) {
            // Do nothing.
        }
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void updateLevels(){
        // TODO: JPB TBD
    }
    

}
