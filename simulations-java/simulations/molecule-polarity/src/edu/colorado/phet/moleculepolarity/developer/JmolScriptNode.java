// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.developer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.moleculepolarity.common.view.JmolViewerNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User interface for sending commands to a Jmol viewer.
 * Command are in the Jmol scripting language, described at http://chemapps.stolaf.edu/jmol/docs
 * Errors are printed to the console by the Jmol viewer.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JmolScriptNode extends PhetPNode {

    public JmolScriptNode( final JmolViewerNode viewerNode ) {

        final JTextArea textArea = new JTextArea() {{
            setLineWrap( true );
            setBackground( new Color( 245, 245, 245 ) );
        }};
        JScrollPane scrollPane = new JScrollPane( textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        scrollPane.setPreferredSize( new Dimension( 200, 100 ) );
        PSwing scrollPaneNode = new PSwing( scrollPane );
        addChild( scrollPaneNode );

        TextButtonNode executeButtonNode = new TextButtonNode( "Run Jmol Script" ) {{
            setFont( new PhetFont( 10 ) );
            setBackground( Color.YELLOW );
        }};
        addChild( executeButtonNode );
        executeButtonNode.setOffset( 0, scrollPaneNode.getFullBoundsReference().getMaxY() + 3 );

        executeButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Object status = viewerNode.doScriptStatus( textArea.getText() );
                if ( status != null ) {
                    System.out.println( "Jmol:\n" + status.toString() );
                }
            }
        } );
    }
}
