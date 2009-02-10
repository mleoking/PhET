package edu.colorado.phet.acidbasesolutions.control;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.control.StrengthControlPanel.StrengthControlPanelState;
import edu.colorado.phet.acidbasesolutions.model.IMolecule;
import edu.colorado.phet.acidbasesolutions.model.PureWater;
import edu.colorado.phet.acidbasesolutions.model.acids.CustomWeakAcid;
import edu.colorado.phet.acidbasesolutions.model.acids.IStrongAcid;
import edu.colorado.phet.acidbasesolutions.model.acids.IWeakAcid;
import edu.colorado.phet.acidbasesolutions.model.bases.CustomWeakBase;
import edu.colorado.phet.acidbasesolutions.model.bases.IStrongBase;
import edu.colorado.phet.acidbasesolutions.model.bases.IWeakBase;
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
        _moleculeComboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                if ( e.getStateChange() == ItemEvent.SELECTED ) {
                   handleMoleculeChanged();
                }
            }
        } );
        _concentrationControl = new ConcentrationControl();
        _strengthControlPanel = new StrengthControlPanel();
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addFilledComponent( _moleculeComboBox, row++, column, GridBagConstraints.HORIZONTAL );
        layout.addFilledComponent( _concentrationControl, row++, column, GridBagConstraints.HORIZONTAL);
        layout.setMinimumHeight( row, _strengthControlPanel.getPreferredSize().height );
        layout.addFilledComponent( _strengthControlPanel, row++, column, GridBagConstraints.HORIZONTAL );
        
        // default state
        handleMoleculeChanged();
    }
    
    private void handleMoleculeChanged() {
        IMolecule molecule = _moleculeComboBox.getSelectedMolecule();
        System.out.println( "SolutionsControlPanel molecule=" + molecule.getName() );
        if ( molecule != null ) {
            _strengthControlPanel.setVisible( !( molecule instanceof PureWater ) );
            if ( ( molecule instanceof CustomWeakAcid ) || ( molecule instanceof CustomWeakBase ) ) {
                _strengthControlPanel.setState( StrengthControlPanelState.CUSTOM_WEAK );
            }
            else if ( ( molecule instanceof IWeakAcid ) || ( molecule instanceof IWeakBase ) ) {
                _strengthControlPanel.setState( StrengthControlPanelState.SPECIFIC_WEAK );
            }
            else if ( ( molecule instanceof IStrongAcid ) || ( molecule instanceof IStrongBase ) ) {
                _strengthControlPanel.setState( StrengthControlPanelState.SPECIFIC_STRONG );
            }
        }
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
