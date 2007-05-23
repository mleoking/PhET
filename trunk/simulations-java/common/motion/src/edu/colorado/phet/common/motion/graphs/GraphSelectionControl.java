package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.motion.util.GraphicsUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:16:34 AM
 */

public class GraphSelectionControl extends JPanel {

    public GraphSelectionControl( GraphSuiteSet rotationGraphSet, final GraphSetModel graphSetModel ) {
        setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        JLabel label = new JLabel( "Show graphs for:" );
        label.setFont( new Font( "Lucida Sans", Font.PLAIN, 20 ) );
        add( label, gridBagConstraints );
        for( int i = 0; i < rotationGraphSet.getNumGraphSuites(); i++ ) {
            add( new GraphSuiteRadioButton( graphSetModel, rotationGraphSet.getGraphSuite( i ) ), gridBagConstraints );
        }
    }

    public static class GraphSuiteRadioButton extends JRadioButton {
        private GraphSetModel graphSetPanel;
        private GraphSuite graphSuite;

        public GraphSuiteRadioButton( final GraphSetModel graphSetModel, final GraphSuite graphSuite ) {
            super( graphSuite.getLabel(), graphSetModel.getGraphSuite() == graphSuite );
            this.graphSetPanel = graphSetModel;
            this.graphSuite = graphSuite;
            setFont( new Font( "Lucida Sans", Font.PLAIN, 16 ) );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    graphSetModel.setGraphSuite( graphSuite );
                }
            } );
            graphSetModel.addListener( new GraphSetModel.Listener() {
                public void graphSuiteChanged() {
                    setSelected( graphSetModel.getGraphSuite() == graphSuite );
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
