package edu.colorado.phet.acidbasesolutions.control;

import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;


public class SolutionControlPanel extends JPanel {
    
    private static final String TITLE = ABSStrings.TITLE_SOLUTION;
    
    private final MoleculeComboBox _moleculeComboBox;
    private final ConcentrationControl _concentrationControl;
    private final StrengthControlPanel _strengthControlPanel;

    public SolutionControlPanel() {
        super();
        
        setBorder( new TitledBorder( TITLE ) );
        
        // components
        _moleculeComboBox = new MoleculeComboBox();
        _concentrationControl = new ConcentrationControl();
        _strengthControlPanel = new StrengthControlPanel();
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addFilledComponent( _moleculeComboBox, row++, column, GridBagConstraints.HORIZONTAL );
        layout.addFilledComponent( _concentrationControl, row++, column, GridBagConstraints.HORIZONTAL);
        layout.addFilledComponent( _strengthControlPanel, row++, column, GridBagConstraints.HORIZONTAL );
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
