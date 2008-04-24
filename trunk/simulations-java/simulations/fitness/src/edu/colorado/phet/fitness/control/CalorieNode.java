package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by: Sam
 * Apr 23, 2008 at 11:55:28 AM
 */
public class CalorieNode extends PNode {
    private PText plusNode;
    private CalorieSet available;
    private CalorieSet calorieSet;
    private JDialog dialog;

    public CalorieNode( String editButtonText, Color editButtonColor, final CalorieSet available, final CalorieSet calorieSet ) {
        this.available = available;
        this.calorieSet = calorieSet;
        GradientButtonNode gradientButtonNode = new GradientButtonNode( editButtonText, 18, editButtonColor );
        gradientButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( dialog == null ) {
                    createDialog();
                }

                dialog.setVisible( true );
                JComponent panel = (JComponent) dialog.getContentPane();
                panel.paintImmediately( 0, 0, panel.getWidth(), panel.getHeight() );
            }
        } );
        addChild( gradientButtonNode );

        PText baseDietNode = new PText( "Balanced Diet" );
        baseDietNode.setFont( new PhetDefaultFont( 20, true ) );
        addChild( baseDietNode );

        plusNode = new PText( "Plus:" );
        plusNode.setFont( new PhetDefaultFont( 15, true ) );
        addChild( plusNode );

        SummaryNode summaryNode = new SummaryNode( calorieSet );

        addChild( summaryNode );

        baseDietNode.setOffset( 0, gradientButtonNode.getFullBounds().getMaxY() );
        plusNode.setOffset( 0, baseDietNode.getFullBounds().getMaxY() );
        summaryNode.setOffset( 0, plusNode.getFullBounds().getMaxY() );

        calorieSet.addListener( new CalorieSet.Listener() {
            public void itemAdded( CaloricItem item ) {
                updatePlusNodeVisible();
            }

            public void itemRemoved( CaloricItem item ) {
                updatePlusNodeVisible();
            }
        } );
        updatePlusNodeVisible();
    }

    private void createDialog() {
        this.dialog = new JDialog();
        CalorieSelectionPanel panel = new CalorieSelectionPanel( available, calorieSet );
        panel.addListener( new CalorieSelectionPanel.Listener() {
            public void donePressed() {
                dialog.hide();
            }
        } );
        dialog.setContentPane( panel );
        dialog.pack();
        dialog.setSize( 800, 600 );
        SwingUtils.centerWindowOnScreen( dialog );
    }

    private void updatePlusNodeVisible() {
        plusNode.setVisible( calorieSet.getItemCount() != 0 );
    }
}
