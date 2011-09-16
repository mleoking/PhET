// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.colorado.phet.balanceandtorque.game.model.BalanceGameModel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class presents a dialog to the user and allows them to enter a value.
 *
 * @author John Blanco
 */
public class MassValueEntryNode extends PNode {

    private static final Font TEXT_FONT = new PhetFont( 18 );
    private static final int BORDER_THICKNESS = 2;
    private static final int ANSWER_ENTRY_FIELD_COLUMNS = 12;
    private static final Color BACKGROUND_COLOR = Color.YELLOW;

    private final JFormattedTextField numberEntryField;
    private final BalanceGameModel model;

    /**
     * Constructor.
     */
    public MassValueEntryNode( final BalanceGameModel balanceGameModel ) {
        this.model = balanceGameModel;

        // Create the background.
        PPath background = new PPath();
        background.setStroke( new BasicStroke( BORDER_THICKNESS ) );
        background.setPaint( BACKGROUND_COLOR );

        // Add the textual prompt at the top.
        // TODO: i18n
        PText prompt = new PText( "Mass = " );
        prompt.setFont( TEXT_FONT );

        // Create the sub-panel that will contain the edit box for entering
        // the numerical value.
        JPanel numericalValueEntryPanel = new JPanel();
        numericalValueEntryPanel.setBackground( BACKGROUND_COLOR );
        numberEntryField = new JFormattedTextField( NumberFormat.getNumberInstance() );
        numberEntryField.setColumns( ANSWER_ENTRY_FIELD_COLUMNS );
        numberEntryField.setFont( TEXT_FONT );
        numberEntryField.setBorder( BorderFactory.createEtchedBorder() );
        numericalValueEntryPanel.add( numberEntryField );

        // Add a handler to catch when the user hits the "Check Answer" key.
        numberEntryField.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent arg0 ) {
                balanceGameModel.checkAnswer( (Double) numberEntryField.getValue() );
            }
        } );

        // Wrap the value entry panel in a PSwing.
        PSwing valueEntryPSwing = new PSwing( numericalValueEntryPanel );
        background.addChild( valueEntryPSwing );

        // Create the button for checking the answer.
        // TODO: i18n
        final TextButtonNode checkAnswerButton = new TextButtonNode( "Check Answer", new PhetFont( 20 ), Color.YELLOW );

        // Register to send the user's guess when the button is pushed.
        checkAnswerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                submitProposedAnswer();
            }
        } );

        // Lay out the node.
        addChild( new ControlPanelNode( new VBox( 10, new HBox( prompt, valueEntryPSwing ), checkAnswerButton ) ) );
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
    }

    /**
     * Main routine that constructs a PhET Piccolo canvas in a window.
     *
     * @param args
     */
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 400, 300 );

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( stageSize.getWidth() * 0.5 ), (int) Math.round( stageSize.getHeight() * 0.50 ) ),
                1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        canvas.getLayer().addChild( new MassValueEntryNode( new BalanceGameModel() ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }

}
