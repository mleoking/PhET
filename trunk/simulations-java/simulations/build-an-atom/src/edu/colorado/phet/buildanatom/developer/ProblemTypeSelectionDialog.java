/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.developer;

import java.awt.Dimension;
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

    // Checkboxes that control the problem types allowed.
    private final JCheckBox symbolToSchematicProblemAllowed = new JCheckBox( "Symbol To Schematic Problem Allowed", true );
    private final JCheckBox schematicToSymbolProblemAllowed = new JCheckBox( "Schematic To Symbol Problem Allowed", true );
    private final JCheckBox symbolToCountsProblemAllowed = new JCheckBox( "Symbol To Counts Problem Allowed", true );
    private final JCheckBox countsToSymbolProblemAllowed = new JCheckBox( "Counts To Symbol Problem Allowed", true );
    private final JCheckBox schematicToElementProblemAllowed = new JCheckBox( "Schematic To Element Problem Allowed", true );
    private final JCheckBox countsToElementProblemAllowed = new JCheckBox( "Counts To Element Problem Allowed", true );

    private final ArrayList<JCheckBox> checkBoxList = new ArrayList<JCheckBox>() {{
        add( symbolToSchematicProblemAllowed );
        add( symbolToCountsProblemAllowed );
        add( schematicToSymbolProblemAllowed );
        add( schematicToElementProblemAllowed );
        add( countsToSymbolProblemAllowed );
        add( countsToElementProblemAllowed );
    }};

    /**
     * Constructor.  This class is a singleton, so the constructor is not
     * intended for direct invocation.
     */
    private ProblemTypeSelectionDialog(Frame parent) {
        super(parent, "Select Allowed Problem Types");

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Center the window on the screen (initially - it will retain its
        // position if moved after that).
        setLocationRelativeTo(null);

        setPreferredSize( new Dimension( 300, 400 ) );
        setLayout( new GridLayout( 7, 1 ) );
        add( symbolToCountsProblemAllowed );
        add( symbolToSchematicProblemAllowed );
        add( schematicToSymbolProblemAllowed );
        add( schematicToElementProblemAllowed );
        add( countsToSymbolProblemAllowed );
        add( countsToElementProblemAllowed );
        JPanel buttonPanel = new JPanel( new FlowLayout() );
        buttonPanel.add( new JButton( "Check All" ){{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    for ( JCheckBox checkBox : checkBoxList){
                        checkBox.setSelected( true );
                    }
                }
            });
        }});
        buttonPanel.add( new JButton( "Clear All" ){{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    for ( JCheckBox checkBox : checkBoxList){
                        checkBox.setSelected( false );
                    }
                }
            });
        }});
        add( buttonPanel );
        pack();
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

    public boolean isSymbolToSchematicProblemAllowed() {
        return symbolToSchematicProblemAllowed.isSelected();
    }

    public boolean isSchematicToSymbolProblemAllowed() {
        return schematicToSymbolProblemAllowed.isSelected();
    }

    public boolean isSymbolToCountsProblemAllowed() {
        return symbolToCountsProblemAllowed.isSelected();
    }

    public boolean isCountsToSymbolProblemAllowed() {
        return countsToSymbolProblemAllowed.isSelected();
    }

    public boolean isSchematicToElementProblemAllowed() {
        return schematicToElementProblemAllowed.isSelected();
    }

    public boolean isCountsToElementProblemAllowed() {
        return countsToElementProblemAllowed.isSelected();
    }
}
