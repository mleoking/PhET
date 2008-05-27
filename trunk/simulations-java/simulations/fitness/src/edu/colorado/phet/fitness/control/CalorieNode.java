package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

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
    private ArrayList closedListeners = new ArrayList();
    private double maxY;
    private PImage plateImage;
    private PlateTopSummaryNode plateTopSummaryNode;

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
        editButton.setOffset( 0, 10 );
        addChild( editButton );

        plateImage = new PImage( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( "platter.png" ), 40 ) );
        addChild( plateImage );

        plateTopSummaryNode = new PlateTopSummaryNode( calorieSet, plateImage );
//        plateTopSummaryNode.setOffset( 0, editButton.getFullBounds().getMaxY() );
        addChild( plateTopSummaryNode );

        CalorieDragStrip calorieDragStrip = new CalorieDragStrip( available );
        calorieDragStrip.addListener( new CalorieDragStrip.Listener() {
            public void nodeDropped( CalorieDragStrip.DragNode droppedNode ) {
                if ( plateImage.getGlobalFullBounds().intersects( droppedNode.getPNode().getGlobalFullBounds() ) ) {
                    calorieSet.addItem( droppedNode.getItem() );
                }
            }
        } );
        addChild( calorieDragStrip );

//        SummaryNode summaryNode = new SummaryNode( calorieSet );
//        summaryNode.setOffset( 0, editButton.getFullBounds().getMaxY() );
//        addChild( summaryNode );


        calorieSet.addListener( new CalorieSet.Listener() {
            public void itemAdded( CaloricItem item ) {
                updatePlusNodeVisible();
            }

            public void itemRemoved( CaloricItem item ) {
                updatePlusNodeVisible();
            }
        } );
        updatePlusNodeVisible();
        relayout();
    }

    protected void createDialog() {
        this.dialog = new JDialog( parentFrame, false );
        ICalorieSelectionPanel panel = createCalorieSelectionPanel();
        panel.addListener( new CalorieSelectionPanel.Listener() {
            public void donePressed() {
                dialog.hide();
                notifyClosing();
            }
        } );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                notifyClosing();
            }
        } );
        dialog.setContentPane( (JPanel) panel );//todo: remove need for cast
        dialog.pack();
        dialog.setSize( 1024, 400 );


        Rectangle parentBounds = parentFrame.getBounds();
        Rectangle dialogBounds = new Rectangle( (int) ( parentBounds.getMinX() + parentBounds.getWidth() / 2 - dialog.getWidth() / 2 ),
                                                (int) ( parentBounds.getMaxY() - dialog.getHeight() ),
                                                dialog.getWidth(), dialog.getHeight() );
        dialog.setBounds( dialogBounds );
    }

    private void notifyClosing() {
        for ( int i = 0; i < closedListeners.size(); i++ ) {
            ( (ActionListener) closedListeners.get( i ) ).actionPerformed( null );
        }
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

    public void addEditorClosedListener( final ActionListener actionListener ) {
        closedListeners.add( actionListener );
    }

    public void setMaxY( double maxY ) {
        this.maxY = maxY;
        relayout();
    }

    private void relayout() {
        editButton.setOffset( 0, maxY - editButton.getFullBounds().getHeight() );
        plateImage.setOffset( 0, editButton.getFullBounds().getY() - plateImage.getFullBounds().getHeight() );
        plateTopSummaryNode.relayout();
    }
}
