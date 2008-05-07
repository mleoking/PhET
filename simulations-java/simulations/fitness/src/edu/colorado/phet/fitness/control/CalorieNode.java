package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Apr 23, 2008 at 11:55:28 AM
 */
public class CalorieNode extends PNode {
    private Frame parentFrame;
    private CalorieSet available;
    private CalorieSet calorieSet;
    private JDialog dialog;
    private String selectedTitle;
    private String availableTitle;
    private GradientButtonNode editButton;

    public CalorieNode( Frame parentFrame, String editButtonText, Color editButtonColor, final CalorieSet available, final CalorieSet calorieSet, String availableTitle, String selectedTitle ) {
        this.parentFrame = parentFrame;
        this.available = available;
        this.calorieSet = calorieSet;
        this.availableTitle = availableTitle;
        this.selectedTitle = selectedTitle;
        editButton = new GradientButtonNode( editButtonText, 18, editButtonColor );
        editButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( dialog == null ) {
                    createDialog();
                }

                dialog.setVisible( true );
                JComponent panel = (JComponent) dialog.getContentPane();
                panel.paintImmediately( 0, 0, panel.getWidth(), panel.getHeight() );
            }
        } );
        addChild( editButton );

        SummaryNode summaryNode = new SummaryNode( calorieSet );
        addChild( summaryNode );

        summaryNode.setOffset( 0, editButton.getFullBounds().getMaxY() );

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
        this.dialog = new JDialog( parentFrame, false );
        ICalorieSelectionPanel panel = createCalorieSelectionPanel();
        panel.addListener( new CalorieSelectionPanel.Listener() {
            public void donePressed() {
                dialog.hide();
            }
        } );
        dialog.setContentPane( (JPanel)panel );//todo: remove need for cast
        dialog.pack();
        dialog.setSize( 1024, 400 );


        Rectangle parentBounds = parentFrame.getBounds();
        Rectangle dialogBounds = new Rectangle( (int) ( parentBounds.getMinX() + parentBounds.getWidth() / 2 - dialog.getWidth() / 2 ),
                                                (int) ( parentBounds.getMaxY() - dialog.getHeight() ),
                                                dialog.getWidth(), dialog.getHeight() );
        dialog.setBounds( dialogBounds );
    }

    protected ICalorieSelectionPanel createCalorieSelectionPanel() {
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

    public PNode getEditButton() {
        return editButton;
    }
}
