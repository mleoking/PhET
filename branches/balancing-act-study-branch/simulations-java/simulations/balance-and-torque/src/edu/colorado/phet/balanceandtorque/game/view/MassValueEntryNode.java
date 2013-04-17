// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.game.model.BalanceGameChallenge;
import edu.colorado.phet.balanceandtorque.game.model.BalanceGameModel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class presents a dialog to the user and allows them to enter a mass value.
 *
 * @author John Blanco
 */
public class MassValueEntryNode extends PNode {

    private static final Font TEXT_FONT = new PhetFont( 18 );
    private static final int ANSWER_ENTRY_FIELD_COLUMNS = 4;
    private static final Color BACKGROUND_COLOR = new Color( 234, 234, 174 );

    private final JFormattedTextField numberEntryField;
    private final BalanceGameModel model;
    private TextButtonNode checkAnswerButton;
    private final PCanvas canvas;

    /**
     * Constructor.
     */
    public MassValueEntryNode( final BalanceGameModel balanceGameModel, PCanvas canvas ) {
        this.model = balanceGameModel;
        this.canvas = canvas;

        // Add the textual prompt.
        PText prompt = new PText( BalanceAndTorqueResources.Strings.MASS_EQUALS );
        prompt.setFont( TEXT_FONT );

        // Create the sub-panel that will contain the edit box for entering
        // the numerical value.
        JPanel numericalValueEntryPanel = new JPanel();
        numericalValueEntryPanel.setBackground( BACKGROUND_COLOR );
        numberEntryField = new JFormattedTextField( NumberFormat.getNumberInstance() );
        numberEntryField.setColumns( ANSWER_ENTRY_FIELD_COLUMNS );
        numberEntryField.setFont( TEXT_FONT );
        numberEntryField.setBorder( BorderFactory.createEtchedBorder() );
        numberEntryField.requestFocus();
        numericalValueEntryPanel.add( numberEntryField );

        // Add the units label.
        PText unitsLabel = new PText( BalanceAndTorqueResources.Strings.KG );
        unitsLabel.setFont( TEXT_FONT );

        // Add a handler for the case where the user presses the Enter key.
        numberEntryField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent arg0 ) {
                submitProposedAnswer();
            }
        } );

        // Wrap the value entry panel in a PSwing.
        PSwing valueEntryPSwing = new PSwing( numericalValueEntryPanel );

        // Create the button for checking the answer.
        checkAnswerButton = new TextButtonNode( BalanceAndTorqueResources.Strings.CHECK_ANSWER, new PhetFont( 20 ), Color.YELLOW );

        // Register to send the user's guess when the button is pushed.
        checkAnswerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                submitProposedAnswer();
            }
        } );

        // Lay out the node.
        addChild( new ControlPanelNode( new VBox( 10, new HBox( 5, prompt, valueEntryPSwing, unitsLabel ), checkAnswerButton ), BACKGROUND_COLOR ) );
    }

    /**
     * Clear the entry field.
     */
    public void clear() {
        numberEntryField.setText( "" );
    }

    /**
     * Submit the age guess by interpreting the value and notifying any
     * listeners.
     */
    private void submitProposedAnswer() {

        double value;

        // Interpret the data in the text field.
        if ( numberEntryField.getValue() != null ) {
            value = ( (Number) numberEntryField.getValue() ).doubleValue();
        }
        else {
            value = Double.NaN;
        }

        // Submit the proposed answer to the model.
        model.checkAnswer( value );

        // Once an answer is submitted, set the focus back to the canvas.  This
        // was added because several users used the "Enter" key to submit their
        // answer, and then tried to use that key again to go to the next
        // challenge.  Without this explicit focus change, the second "Enter"
        // key gets consumed by this node and has no effect.  By setting focus
        // back to the canvas, the "Enter" key will work for moving to the next
        // challenge.
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                canvas.requestFocusInWindow();
            }
        } );
    }

    public static class DisplayAnswerNode extends PNode {

        private static final NumberFormat FORMATTER = new DecimalFormat( "##.#" );

        private final MassValueEntryNode massValueEntryNode;

        /**
         * Constructor.
         */
        public DisplayAnswerNode( final BalanceGameModel balanceGameModel, PCanvas canvas ) {
            massValueEntryNode = new MassValueEntryNode( balanceGameModel, canvas );
            addChild( massValueEntryNode );
            massValueEntryNode.checkAnswerButton.setVisible( false );
            massValueEntryNode.numberEntryField.setEnabled( false );
            update();
        }

        public void update() {
            BalanceGameChallenge balanceGameChallenge = massValueEntryNode.model.getCurrentChallenge();
            if ( balanceGameChallenge != null ) {
                massValueEntryNode.numberEntryField.setText( FORMATTER.format( balanceGameChallenge.getFixedMassValueTotal() ) );
            }
            else {
                massValueEntryNode.numberEntryField.setText( "" );
            }
        }
    }
}
