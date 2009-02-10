package edu.colorado.phet.acidbasesolutions.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;


public class StrengthControlPanel extends JPanel {
    
    private static final String TITLE = ABSStrings.TITLE_STRENGTH;
    
    private final JRadioButton _strongRadioButton, _weakRadioButton;
    private final JLabel _strongStrengthLabel;
    private final WeakStrengthControl _weakStrengthControl;
    
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
        _strongRadioButton.setSelected( true );
        
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
        
        update();
    }

    private void update() {
        _strongStrengthLabel.setEnabled( _strongRadioButton.isSelected() );
        _weakStrengthControl.setEnabled( _weakRadioButton.isSelected() );
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
