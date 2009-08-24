package edu.colorado.phet.titration.prototype;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartPanel;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;


public class TitrationPrototype extends JFrame {
    
    public TitrationPrototype() {
        super( "Titration model prototype : " + TPConstants.VERSION );
   
        TPChart chart = new TPChart();
        ChartPanel chartPanel = new ChartPanel( chart );
        JPanel controlPanel = new TPControlPanel( chart );
        
        // layout
        setLayout( new BorderLayout() );
        add( chartPanel, BorderLayout.CENTER );
        add( controlPanel, BorderLayout.EAST );
        pack();
    }
    
    public static void main( String[] args ) {
        JFrame frame = new TitrationPrototype();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

}
