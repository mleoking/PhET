package edu.colorado.phet.common.motion.graphs;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.motion.util.GraphicsUtil;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:16:34 AM
 */

public class GraphSelectionControl extends JPanel {

    public GraphSelectionControl( GraphSuiteSet graphSuiteSet, final GraphSetModel graphSetModel ) {
        setLayout( new GridBagLayout() );
        GridBagConstraints gridBagConstraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 );
        JLabel label = new JLabel( PhetCommonResources.getString( "charts.show-graphs" ));
        label.setFont( new PhetFont( Font.PLAIN, 16 ) );
        add( label, gridBagConstraints );
        for ( int i = 0; i < graphSuiteSet.getNumGraphSuites(); i++ ) {
            add( new GraphSuiteRadioButton( graphSetModel, graphSuiteSet.getGraphSuite( i ) ), gridBagConstraints );
        }
    }

    public static class GraphSuiteRadioButton extends JRadioButton {
        private GraphSetModel graphSetPanel;
        private GraphSuite graphSuite;

        public GraphSuiteRadioButton( final GraphSetModel graphSetModel, final GraphSuite graphSuite ) {
            super( graphSuite.getLabel(), graphSetModel.getGraphSuite() == graphSuite );
            this.graphSetPanel = graphSetModel;
            this.graphSuite = graphSuite;
            setFont( new PhetFont( Font.PLAIN, 16 ) );
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
