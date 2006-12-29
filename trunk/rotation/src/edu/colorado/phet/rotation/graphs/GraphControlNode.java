package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.piccolo.nodes.ShadowPText;
import edu.colorado.phet.rotation.model.SimulationVariable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 8:55:43 AM
 * Copyright (c) Dec 29, 2006 by Sam Reid
 */

public class GraphControlNode extends PNode {
    private ShadowPText shadowPText;
    private PSwing textBox;
    private PSwing goStopButton;
    private PSwing clearButton;

    public GraphControlNode( PSwingCanvas pSwingCanvas, SimulationVariable simulationVariable, GraphTimeSeries graphTimeSeries ) {
        shadowPText = new ShadowPText( "Title" );
        shadowPText.setFont( new Font( "Lucida Sans", Font.BOLD, 16 ) );
        shadowPText.setTextPaint( Color.blue );
        shadowPText.setShadowColor( Color.black );
        addChild( shadowPText );

        textBox = new PSwing( pSwingCanvas, new TextBox( simulationVariable ) );
        addChild( textBox );

        goStopButton = new PSwing( pSwingCanvas, new GoStopButton( graphTimeSeries ) );
        addChild( goStopButton );

        clearButton = new PSwing( pSwingCanvas, new ClearButton( graphTimeSeries ) );
        addChild( clearButton );

        relayout();
    }

    private void relayout() {
        double x = 0;
        double dy = 5;
        shadowPText.setOffset( x, 0 );
        textBox.setOffset( x, shadowPText.getFullBounds().getMaxY() + dy );
        goStopButton.setOffset( x, textBox.getFullBounds().getMaxY() + dy );
        clearButton.setOffset( x, goStopButton.getFullBounds().getMaxY() + dy );
    }

    static class ClearButton extends JButton {
        private GraphTimeSeries graphTimeSeries;

        public ClearButton( final GraphTimeSeries graphTimeSeries ) {
            super( "Clear" );
            this.graphTimeSeries = graphTimeSeries;
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    graphTimeSeries.clear();
                }
            } );
            graphTimeSeries.addListener( new GraphTimeSeries.Listener() {
                public void started() {
                }

                public void stopped() {
                }

                public void cleared() {
                }

                public void emptyStateChanged() {
                    updateEnabledState();
                }
            } );
            updateEnabledState();
        }

        private void updateEnabledState() {
            setEnabled( !graphTimeSeries.isEmpty() );
        }
    }

    static class GoStopButton extends JButton {
        private boolean goButton = true;
        private GraphTimeSeries graphTimeSeries;

        public GoStopButton( final GraphTimeSeries graphTimeSeries ) {
            super( "Go" );
            this.graphTimeSeries = graphTimeSeries;
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( isGoButton() ) {
                        graphTimeSeries.go();
                    }
                    else {
                        graphTimeSeries.stop();
                    }
                }
            } );
            graphTimeSeries.addListener( new GraphTimeSeries.Listener() {
                public void started() {
                    updateGoState();
                }

                public void stopped() {
                    updateGoState();
                }

                public void cleared() {
                }

                public void emptyStateChanged() {
                }
            } );
            updateGoState();
        }

        private void updateGoState() {
            setGoButton( !graphTimeSeries.isRunning() );
        }

        private void setGoButton( boolean b ) {
            this.goButton = b;
            setText( goButton ? "Go!" : "Stop" );
        }

        private boolean isGoButton() {
            return goButton;
        }
    }

    static class TextBox extends JPanel {

        public TextBox( final SimulationVariable simulationVariable ) {
            add( new JLabel( "x=" ) );
            final JTextField field = new JTextField( "0.0", 6 );
            field.setHorizontalAlignment( JTextField.RIGHT );
            add( field );
            setBorder( BorderFactory.createLineBorder( Color.black ) );
            final DecimalFormat decimalFormat = new DecimalFormat( "0.00" );
            simulationVariable.addListener( new SimulationVariable.Listener() {
                public void valueChanged() {
                    field.setText( decimalFormat.format( simulationVariable.getValue() ) );
                }
            } );
            field.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    simulationVariable.setValue( Double.parseDouble( field.getText() ) );
                }
            } );
        }
    }
}
