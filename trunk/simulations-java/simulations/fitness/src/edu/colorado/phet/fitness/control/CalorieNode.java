package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Apr 23, 2008 at 11:55:28 AM
 */
public class CalorieNode extends PNode {
    //    private PText plusNode;
    private CalorieSet available;
    private CalorieSet calorieSet;
    private JDialog dialog;
    private String selectedTitle;
    private String availableTitle;

    public CalorieNode( String editButtonText, Color editButtonColor, final CalorieSet available, final CalorieSet calorieSet, String availableTitle, String selectedTitle ) {
        this.available = available;
        this.calorieSet = calorieSet;
        this.availableTitle = availableTitle;
        this.selectedTitle = selectedTitle;
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

        SummaryNode summaryNode = new SummaryNode( calorieSet );
        addChild( summaryNode );

        summaryNode.setOffset( 0, gradientButtonNode.getFullBounds().getMaxY() );

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

    protected void createDialog() {
        this.dialog = new JDialog();
        CalorieSelectionPanel panel = createCalorieSelectionPanel();
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

    protected CalorieSelectionPanel createCalorieSelectionPanel() {
        return new CalorieSelectionPanel( available, calorieSet, availableTitle, selectedTitle );
    }

    public CalorieSet getAvailable() {
        return available;
    }

    public CalorieSet getCalorieSet() {
        return calorieSet;
    }

    public String getAvailableTitle() {
        return availableTitle;
    }

    public String getSelectedTitle() {
        return selectedTitle;
    }

    private void updatePlusNodeVisible() {
//        plusNode.setVisible( calorieSet.getItemCount() != 0 );
    }
}
