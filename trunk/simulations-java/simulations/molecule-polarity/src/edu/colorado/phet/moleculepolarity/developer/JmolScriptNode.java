// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.developer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
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

        final JTextArea textArea = new JTextArea( 5, 20 ) {{
            setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
        }};
        PSwing textAreaNode = new PSwing( textArea );
        addChild( new PSwing( textArea ) );

        TextButtonNode executeButtonNode = new TextButtonNode( "Run Jmol Script" ) {{
            setFont( new PhetFont( 10 ) );
            setBackground( Color.YELLOW );
        }};
        addChild( executeButtonNode );
        executeButtonNode.setOffset( 0, textAreaNode.getFullBoundsReference().getMaxY() + 3 );

        executeButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                viewerNode.doScript( textArea.getText() );
            }
        } );
    }
}
