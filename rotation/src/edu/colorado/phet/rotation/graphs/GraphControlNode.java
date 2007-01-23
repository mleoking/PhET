package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.nodes.ShadowPText;
import edu.colorado.phet.rotation.model.SimulationVariable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 8:55:43 AM
 * Copyright (c) Dec 29, 2006 by Sam Reid
 */

public class GraphControlNode extends PNode {
    private PSwing goStopButton;
    private PSwing clearButton;
    private PNode seriesLayer = new PNode();
    private boolean editable = true;
    private boolean constructed = false;

    public GraphControlNode( PSwingCanvas pSwingCanvas, GraphTimeSeries graphTimeSeries ) {
        addChild( seriesLayer );

        goStopButton = new PSwing( pSwingCanvas, new GoStopButton( graphTimeSeries ) );
        addChild( goStopButton );

        clearButton = new PSwing( pSwingCanvas, new ClearButton( graphTimeSeries ) );
        addChild( clearButton );

        constructed = true;
        relayout();
    }

    public GraphControlNode( PSwingCanvas pSwingCanvas, String title, SimulationVariable simulationVariable, GraphTimeSeries graphTimeSeries ) {
        this( pSwingCanvas, title, simulationVariable, graphTimeSeries, Color.black );
    }

    public GraphControlNode( PSwingCanvas pSwingCanvas, String title, SimulationVariable simulationVariable, GraphTimeSeries graphTimeSeries, Color color ) {
        this( pSwingCanvas, graphTimeSeries );
        addVariable( title, color, simulationVariable, pSwingCanvas );
        relayout();
    }

    public void addVariable( String title, Color color, SimulationVariable simulationVariable, PSwingCanvas pSwingCanvas ) {
        SeriesNode seriesNode = new SeriesNode( title, color, simulationVariable, pSwingCanvas );
        seriesNode.setEditable( editable );
        seriesNode.setOffset( 0, seriesLayer.getFullBounds().getHeight() + 5 );
        seriesLayer.addChild( seriesNode );
        relayout();
    }

    static class SeriesNode extends PNode {
        private ShadowPText shadowPText;
        private PSwing textBox;
        private TextBox box;

        public SeriesNode( String title, Color color, SimulationVariable simulationVariable, PSwingCanvas pSwingCanvas ) {
            shadowPText = new ShadowPText( title );
            shadowPText.setFont( new Font( "Lucida Sans", Font.BOLD, 16 ) );
            shadowPText.setTextPaint( color );
            shadowPText.setShadowColor( Color.black );
            addChild( shadowPText );

            box = new TextBox( title, simulationVariable );
            textBox = new PSwing( pSwingCanvas, box );
            addChild( textBox );
        }

        public void relayout( double dy ) {
            shadowPText.setOffset( 0, 0 );
            textBox.setOffset( 0, shadowPText.getFullBounds().getMaxY() + dy );
        }

        public void setEditable( boolean editable ) {
            box.setEditable( editable );
        }
    }

    private void relayout() {
        if( constructed ) {
            double dy = 5;
            seriesLayer.setOffset( 0, 0 );
            for( int i = 0; i < seriesLayer.getChildrenCount(); i++ ) {
                SeriesNode child = (SeriesNode)seriesLayer.getChild( i );
                child.relayout( dy );
            }
//        seriesNode.setOffset( 0, 0 );
//        seriesNode.relayout( dy );

            goStopButton.setOffset( 0, seriesLayer.getFullBounds().getMaxY() + dy );
            clearButton.setOffset( 0, goStopButton.getFullBounds().getMaxY() + dy );
        }
    }

    public void setEditable( boolean editable ) {
        this.editable = editable;
        for( int i = 0; i < seriesLayer.getChildrenCount(); i++ ) {
            SeriesNode child = (SeriesNode)seriesLayer.getChild( i );
            child.setEditable( editable );
        }
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
            try {
                setIcon( new ImageIcon( ImageLoader.loadBufferedImage( goButton ? "images/go.png" : "images/stop.png" ) ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }

        private boolean isGoButton() {
            return goButton;
        }
    }

    static class TextBox extends JPanel {
        private JTextField textField;

        public TextBox( String valueAbbreviation, final SimulationVariable simulationVariable ) {
            add( new JLabel( valueAbbreviation + " =" ) );
            textField = new JTextField( "0.0", 6 );
            textField.setHorizontalAlignment( JTextField.RIGHT );
            add( textField );
            setBorder( BorderFactory.createLineBorder( Color.black ) );
            final DecimalFormat decimalFormat = new DecimalFormat( "0.00" );
            simulationVariable.addListener( new SimulationVariable.Listener() {
                public void valueChanged() {
                    textField.setText( decimalFormat.format( simulationVariable.getValue() ) );
                }
            } );
            textField.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    simulationVariable.setValue( Double.parseDouble( textField.getText() ) );
                }
            } );
        }

        public void setEditable( boolean editable ) {
            textField.setEditable( editable );
        }
    }
}
