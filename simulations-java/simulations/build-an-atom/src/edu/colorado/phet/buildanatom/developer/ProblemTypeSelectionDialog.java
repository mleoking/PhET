// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.developer;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.colorado.phet.buildanatom.modules.game.model.ProblemType;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;

/**
 * Dialog for use in the developer controls that allows the user to select the
 * problem types that are allowed on the game tab.  This was requested to make
 * it easier to test users' responses to the various types of problems
 * presented in the game.
 *
 * @author John Blanco
 */
public class ProblemTypeSelectionDialog extends PaintImmediateDialog {

    private final ArrayList<ProblemTypeCheckBox> checkBoxList = new ArrayList<ProblemTypeCheckBox>() {{
        add( new ProblemTypeCheckBox( "Schematic To Symbol All Problem Allowed", ProblemType.SCHEMATIC_TO_SYMBOL_ALL ) );
        add( new ProblemTypeCheckBox( "Schematic To Symbol Charge Problem Allowed", ProblemType.SCHEMATIC_TO_SYMBOL_CHARGE ) );
        add( new ProblemTypeCheckBox( "Schematic To Symbol Mass Problem Allowed", ProblemType.SCHEMATIC_TO_SYMBOL_MASS ) );
        add( new ProblemTypeCheckBox( "Schematic To Symbol Proton Count Problem Allowed", ProblemType.SCHEMATIC_TO_SYMBOL_PROTON_COUNT ) );
        add( new ProblemTypeCheckBox( "Schematic To Element Problem Allowed", ProblemType.SCHEMATIC_TO_ELEMENT ) );
        add( new ProblemTypeCheckBox( "Schematic To Charge Question Problem Allowed", ProblemType.SCHEMATIC_TO_CHARGE_QUESTION ) );
        add( new ProblemTypeCheckBox( "Schematic To Mass Question Problem Allowed", ProblemType.SCHEMATIC_TO_MASS_QUESTION ) );
        add( new ProblemTypeCheckBox( "Symbol To Schematic Problem Allowed", ProblemType.SYMBOL_TO_SCHEMATIC ) );
        add( new ProblemTypeCheckBox( "Symbol To Counts Problem Allowed", ProblemType.SYMBOL_TO_COUNTS ) );
        add( new ProblemTypeCheckBox( "Counts To Charge Question Problem Allowed", ProblemType.COUNTS_TO_CHARGE_QUESTION ) );
        add( new ProblemTypeCheckBox( "Counts To Mass Question Problem Allowed", ProblemType.COUNTS_TO_MASS_QUESTION ) );
        add( new ProblemTypeCheckBox( "Counts To Symbol All Problem Allowed", ProblemType.COUNTS_TO_SYMBOL_ALL ) );
        add( new ProblemTypeCheckBox( "Counts To Symbol Charge Problem Allowed", ProblemType.COUNTS_TO_SYMBOL_CHARGE ) );
        add( new ProblemTypeCheckBox( "Counts To Symbol Mass Problem Allowed", ProblemType.COUNTS_TO_SYMBOL_MASS ) );
        add( new ProblemTypeCheckBox( "Counts To Symbol Proton Count Problem Allowed", ProblemType.COUNTS_TO_SYMBOL_PROTON_COUNT ) );
        add( new ProblemTypeCheckBox( "Counts To Element Problem Allowed", ProblemType.COUNTS_TO_ELEMENT ) );
    }};

    /**
     * Constructor.  This class is a singleton, so the constructor is not
     * intended for direct invocation.
     */
    private ProblemTypeSelectionDialog(Frame parent) {
        super( parent, "Select Allowed Problem Types" );

        setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );

        setLayout( new GridLayout( checkBoxList.size() + 1, 1 ) );
        for ( JCheckBox checkBox : checkBoxList ){
            add( checkBox );
        }
        JPanel buttonPanel = new JPanel( new FlowLayout() );
        buttonPanel.add( new JButton( "Check All" ){{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setAllSelected();
                }
            });
        }});
        buttonPanel.add( new JButton( "Clear All" ){{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setNoneSelected();
                }
            });
        }});
        add( buttonPanel );
        pack();
        setLocation( parent.getX(), parent.getHeight() / 2 + parent.getY() - getHeight() / 2 );
    }

    // Get the instance of this singleton.
    static ProblemTypeSelectionDialog instance = null;
    public static ProblemTypeSelectionDialog getInstance(){
        assert instance != null; // Must be explicitly created before this can be called.
        return instance;
    }

    public static ProblemTypeSelectionDialog createInstance(Frame parent){
        if (instance == null){
            instance = new ProblemTypeSelectionDialog( parent );
        }
        else{
            System.err.println( "Warning: Multiple attempts to create this singleton." );
        }
        return instance;
    }

    public boolean isProblemTypeAllowed( ProblemType problemType ){
        ProblemTypeCheckBox problemTypeCheckBox = null;
        for (ProblemTypeCheckBox checkBox : checkBoxList ){
            if ( problemType == checkBox.getProblemType() ){
                // Found the check box for the corresponding prob type.
                problemTypeCheckBox = checkBox;
                break;
            }
        }
        if (problemTypeCheckBox == null){
            System.err.println( getClass().getName() + " - Error: No check box found for problem type " + problemType );
            return false;
        }
        return problemTypeCheckBox.isSelected();
    }

    public void setAllSelected(){
        for ( JCheckBox checkBox : checkBoxList){
            checkBox.setSelected( true );
        }
    }

    private void setNoneSelected(){
        for ( JCheckBox checkBox : checkBoxList){
            checkBox.setSelected( false );
        }
    }

    /**
     * Convenience class for mapping the check boxes to problem types.
     *
     * @author John Blanco
     */
    private static class ProblemTypeCheckBox extends JCheckBox {
        private final ProblemType problemType;

        public ProblemTypeCheckBox ( String text, ProblemType problemType ){
            super( text, true );
            this.problemType = problemType;
        }

        public ProblemType getProblemType(){
            return problemType;
        }
    }
}
