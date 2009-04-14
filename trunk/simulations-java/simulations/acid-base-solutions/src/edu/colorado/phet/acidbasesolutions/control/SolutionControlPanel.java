package edu.colorado.phet.acidbasesolutions.control;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ISolute;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;


public class SolutionControlPanel extends JPanel {
    
    private static final String TITLE = ABSStrings.TITLE_SOLUTION;
    
    private final SoluteComboBox _soluteComboBox;

    public SolutionControlPanel() {
        super();
        
        setBorder( new TitledBorder( TITLE ) );

        // components
        _soluteComboBox = new SoluteComboBox();
        _soluteComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                if ( e.getStateChange() == ItemEvent.SELECTED ) {
                   handleSoluteChanged();
                }
            }
        } );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addFilledComponent( _soluteComboBox, row++, column, GridBagConstraints.HORIZONTAL );
        
        // default state
        handleSoluteChanged();
    }
    
    private void handleSoluteChanged() {
        ISolute solute = _soluteComboBox.getSelectedSolute();
        System.out.println( "SolutionsControlPanel solute=" + solute.getName() );
        //XXX
    }
    
    // test
    public static void main( String[] args ) {
        
        SolutionControlPanel panel = new SolutionControlPanel();
        
        JFrame frame = new JFrame();
        frame.getContentPane().add( panel );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
