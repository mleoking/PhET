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
        add( schematicToSymbolProblemAllowed );
        add( symbolToCountsProblemAllowed );
        add( countsToSymbolProblemAllowed );
        add( schematicToElementProblemAllowed );
        add( countsToElementProblemAllowed );
    }};

    /**
     * Constructor.
     */
    public ProblemTypeSelectionDialog( Frame frame ) {
        super( frame, "Select Problem Types Allowed" );
        setPreferredSize( new Dimension( 300, 400 ) );
        setLayout( new GridLayout( 7, 1 ) );
        add( symbolToCountsProblemAllowed );
        add( schematicToSymbolProblemAllowed );
        add( symbolToCountsProblemAllowed );
        add( countsToSymbolProblemAllowed );
        add( schematicToElementProblemAllowed );
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
