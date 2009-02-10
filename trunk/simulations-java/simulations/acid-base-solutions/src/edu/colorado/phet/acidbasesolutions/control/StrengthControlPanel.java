package edu.colorado.phet.acidbasesolutions.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;


public class StrengthControlPanel extends JPanel {
    
    public static class StrengthControlPanelState {
        
        public static final StrengthControlPanelState SPECIFIC_STRONG = new StrengthControlPanelState( "specific strong" );
        public static final StrengthControlPanelState SPECIFIC_WEAK = new StrengthControlPanelState( "specific weak" );
        public static final StrengthControlPanelState CUSTOM_WEAK = new StrengthControlPanelState( "custom weak" );
        
        private final String _name;

        private StrengthControlPanelState( String name ) {
            _name = name;
        }

        public String getName() {
            return _name;
        }
        
        public String toString() {
            return getName();
        }
    }
    
    private static final String TITLE = ABSStrings.TITLE_STRENGTH;
    
    private final JRadioButton _strongRadioButton, _weakRadioButton;
    private final JLabel _strongStrengthLabel;
    private final WeakStrengthControl _weakStrengthControl;
    
    private StrengthControlPanelState _state;
    
    public StrengthControlPanel() {
        super();

        setBorder( new TitledBorder( TITLE ) );
        
        // radio buttons
        _strongRadioButton = new JRadioButton( "strong:" );
        _strongRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
            }
            
        });
        _weakRadioButton = new JRadioButton( "weak:" );
        _weakRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
            }
            
        });
        
        // button group
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _strongRadioButton );
        buttonGroup.add( _weakRadioButton );
        
        // strong value label
        _strongStrengthLabel = new JLabel( "large" );
        
        // weak value control
        _weakStrengthControl = new WeakStrengthControl();
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( _strongRadioButton, row, column++ );
        layout.addComponent( _strongStrengthLabel, row++, column );
        column = 0;
        layout.addComponent( _weakRadioButton, row, column++ );
        layout.addComponent( _weakStrengthControl, row++, column );
        
        // default state
        _strongRadioButton.setSelected( true );
        _state = StrengthControlPanelState.CUSTOM_WEAK;
        update();
    }
    
    public void setState( StrengthControlPanelState state ) {
        if ( state != _state ) {
            _state = state;
            update();
        }
    }
    
    public StrengthControlPanelState getState() {
        return _state;
    }
    
    public void setWeakStrength( double strength ) {
        _weakStrengthControl.setValue( strength );
    }
    
    public double getWeakStrength() {
        return _weakStrengthControl.getValue();
    }

    private void update() {
        if ( _state == StrengthControlPanelState.CUSTOM_WEAK ) {
            _strongRadioButton.setEnabled( false );
            _strongStrengthLabel.setEnabled( false );
            _weakRadioButton.setEnabled( true );
            _weakStrengthControl.setEnabled( true );
        }
        else {
            final boolean strong = ( _state == StrengthControlPanelState.SPECIFIC_STRONG );
            // radio buttons indicate weak/strong type
            _strongRadioButton.setEnabled( strong );
            _strongRadioButton.setSelected( strong );
            _weakRadioButton.setEnabled( !strong );
            _weakRadioButton.setSelected( !strong );
            // controls are disabled
            _strongStrengthLabel.setEnabled( strong );
            _weakStrengthControl.setEnabled( false );
        }
    }
    
    // test
    public static void main( String[] args ) {
        
        StrengthControlPanel panel = new StrengthControlPanel();
        
        JFrame frame = new JFrame();
        frame.getContentPane().add( panel );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
