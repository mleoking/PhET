package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.nuclearphysics2.util.GradientButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by: Sam
 * Apr 23, 2008 at 11:55:28 AM
 */
public class IONode extends PNode {
    public IONode( String editButtonText, Color editButtonColor ) {
        GradientButtonNode gradientButtonNode = new GradientButtonNode( editButtonText, 18, editButtonColor );
        gradientButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JDialog dialog = new JDialog();
                DietControlPanel panel = new DietControlPanel();
                dialog.setContentPane( panel );
                dialog.pack();
                dialog.setVisible( true );
                SwingUtils.centerWindowOnScreen( dialog );
                panel.paintImmediately( 0, 0, panel.getWidth(), panel.getHeight() );
            }
        } );
        addChild( gradientButtonNode );

        PText baseDietNode = new PText( "Balanced Diet" );
        baseDietNode.setFont( new PhetDefaultFont( 20, true ) );
        addChild( baseDietNode );

        PText plusNode = new PText( "Plus:" );
        plusNode.setFont( new PhetDefaultFont( 15, true ) );
        addChild( plusNode );

        SummaryNode summaryNode = new SummaryNode();
        summaryNode.addItem( new SummaryNode.Item( "banana split", "bananasplit.png", 100, 1 ) );
        summaryNode.addItem( new SummaryNode.Item( "burger", "burger.png", 100, 2 ) );
        addChild( summaryNode );

        baseDietNode.setOffset( 0, gradientButtonNode.getFullBounds().getMaxY() );
        plusNode.setOffset( 0, baseDietNode.getFullBounds().getMaxY() );
        summaryNode.setOffset( 0, plusNode.getFullBounds().getMaxY() );
    }
}
