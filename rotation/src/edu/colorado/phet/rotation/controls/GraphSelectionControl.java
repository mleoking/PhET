package edu.colorado.phet.rotation.controls;

import edu.colorado.phet.rotation.graphs.GraphSetPanel;
import edu.colorado.phet.rotation.graphs.GraphSuite;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;
import edu.colorado.phet.rotation.util.GraphicsUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:16:34 AM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class GraphSelectionControl extends JPanel {

    public GraphSelectionControl( RotationGraphSet rotationGraphSet, final GraphSetPanel graphSetPanel ) {
        JLabel label = new JLabel( "Show graphs for:" );
        add( label );
        for( int i = 0; i < rotationGraphSet.getGraphSuites().length; i++ ) {
            add( new GraphSuiteRadioButton( graphSetPanel, rotationGraphSet.getGraphSuites()[i] ) );
        }
    }

    static class GraphSuiteRadioButton extends JRadioButton {
        GraphSetPanel graphSetPanel;
        GraphSuite graphSuite;

        public GraphSuiteRadioButton( final GraphSetPanel graphSetPanel, final GraphSuite graphSuite ) {
            super( graphSuite.getLabel(), graphSetPanel.getRotationGraphSuite() == graphSuite );
            this.graphSetPanel = graphSetPanel;
            this.graphSuite = graphSuite;
            setFont( new Font( "Lucida Sans", Font.PLAIN, 20 ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    graphSetPanel.setRotationGraphSuite( graphSuite );
                }
            } );
            graphSetPanel.addListener( new GraphSetPanel.Listener() {
                public void graphSuiteChanged() {
                    setSelected( graphSetPanel.getRotationGraphSuite() == graphSuite );
                }
            } );
        }

        protected void paintComponent( Graphics g ) {
            boolean aa = GraphicsUtil.antialias( g, true );
            super.paintComponent( g );
            GraphicsUtil.antialias( g, aa );
        }
    }

}
