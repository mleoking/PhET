// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.model.CalorieSet;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

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
    private HTMLImageButtonNode editButton;
    private ArrayList closedListeners = new ArrayList();
    private PImage dropTarget;
    private PlateTopSummaryNode plateTopSummaryNode;
    private CalorieDragStrip calorieDragStrip;
    private static final double SPACING_BETWEEN_PLATE_AND_TOOLBOX = 20;
    private ArrayList itemPressedListeners = new ArrayList();
    private ArrayList overlapTargets = new ArrayList();

    public CalorieNode( Frame parentFrame, String editButtonText, Color editButtonColor, final CalorieSet available, final CalorieSet calorieSet, String availableTitle, String selectedTitle, String dropTargetIcon ) {
        this.parentFrame = parentFrame;
        this.available = available;
        this.calorieSet = calorieSet;
        this.availableTitle = availableTitle;
        this.selectedTitle = selectedTitle;
        editButton = new HTMLImageButtonNode( editButtonText, new PhetFont( Font.BOLD, 18 ), editButtonColor );
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

        dropTarget = new PImage( BufferedImageUtils.multiScaleToHeight( EatingAndExerciseResources.getImage( dropTargetIcon ), 120 ) );
        addChild( dropTarget );

        plateTopSummaryNode = new PlateTopSummaryNode( calorieSet, dropTarget );
        addChild( plateTopSummaryNode );

        calorieDragStrip = new CalorieDragStrip( available );
        calorieDragStrip.addListener( new CalorieDragStrip.Adapter() {
            public void nodeDragged( CalorieDragStrip.DragNode node ) {
                setContainsItem( node.getItem(), nodeOverlapsDropTarget( node ) );
            }

            public void nodePressed() {
                notifyNodePressed();
            }

            public void nodeDropped( final CalorieDragStrip.DragNode node ) {
                if ( shouldDispose( node ) ) {
                    dispose( node );
                }
                else if ( shouldMoveToTarget( node ) ) {
                    moveToDropTarget( node );
                }
            }

            private void dispose( final CalorieDragStrip.DragNode node ) {
                final Timer timer = new Timer( 30, null );
                timer.addActionListener( new ActionListener() {
                    int count = 0;

                    public void actionPerformed( ActionEvent e ) {
                        node.getPNode().scaleAboutPoint( 0.82, node.getPNodeIcon().getFullBounds().getWidth() / 2, node.getPNodeIcon().getFullBounds().getHeight() / 2 );
                        count++;
                        if ( count >= 20 ) {
                            timer.stop();
                            setContainsItem( node.getItem(), false );
                            if ( node.getPNode().getParent() != null ) {//todo: remove the need for this workaround
                                node.getPNode().getParent().removeChild( node.getPNode() );//todo: clean up this line, looks awkward
                            }
                        }
                    }
                } );
                timer.start();
            }
        } );

        addPreExistingItems();
        calorieSet.addListener( new CalorieSet.Adapter() {
            public void itemRemoved( CaloricItem item ) {
                calorieDragStrip.itemRemoved( item );
            }
        } );
        addChild( calorieDragStrip );

        calorieSet.addListener( new CalorieSet.Adapter() {
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

    Random random = new Random();

    private void moveToDropTarget( CalorieDragStrip.DragNode node ) {
        Point2D target = dropTarget.getGlobalFullBounds().getCenter2D();
        node.getPNodeIcon().globalToLocal( target );

        PDimension targetBounds = new PDimension( dropTarget.getGlobalFullBounds().getWidth() / 2,
                                                  dropTarget.getGlobalFullBounds().getHeight() / 2 );
        node.getPNodeIcon().globalToLocal( targetBounds );

        PDimension d = new PDimension( node.getPNodeIcon().getGlobalFullBounds().width / 2, node.getPNodeIcon().getGlobalFullBounds().height / 2 );
        node.getPNodeIcon().globalToLocal( d );

        double offsetW = 2 * ( random.nextDouble() - 0.5 ) * targetBounds.width * 0.75;
        double offsetH = 2 * ( random.nextDouble() - 0.5 ) * targetBounds.height * 0.75;
        node.getPNodeIcon().animateToPositionScaleRotation( target.getX() - d.width + offsetW,
                                                            target.getY() - d.height + offsetH,
                                                            1, 0, 750 );
    }

    private boolean shouldMoveToTarget( CalorieDragStrip.DragNode node ) {
        return nodeOverlapsOtherTarget( node );
    }

    private void notifyNodePressed() {
        for ( int i = 0; i < itemPressedListeners.size(); i++ ) {
            ( (ActionListener) itemPressedListeners.get( i ) ).actionPerformed( new ActionEvent( this, 0, "command" ) );
        }
    }

    private boolean shouldDispose( CalorieDragStrip.DragNode node ) {
        //only delete the item if dropped back in the toolbox
        //        return node.getPNodeIcon().getGlobalFullBounds().intersects( calorieDragStrip.getGlobalFullSourceBounds() );

        //delete the item if it is not on the drop target
        return !nodeOverlapsDropTarget( node );
    }

    private boolean nodeOverlapsDropTarget( CalorieDragStrip.DragNode node ) {
        boolean nodeOverlapsPlate = dropTarget.getGlobalFullBounds().contains( node.getPNodeIcon().getGlobalFullBounds().getCenter2D() );

        boolean nodeOverlapsOtherTarget = nodeOverlapsOtherTarget( node );
        return nodeOverlapsPlate || nodeOverlapsOtherTarget;
    }

    private boolean nodeOverlapsOtherTarget( CalorieDragStrip.DragNode node ) {
        boolean overlaps = false;
        for ( int i = 0; i < overlapTargets.size(); i++ ) {
            PNode pNode = (PNode) overlapTargets.get( i );
            overlaps = overlaps || pNode.getGlobalFullBounds().contains( node.getPNodeIcon().getGlobalFullBounds().getCenter2D() );
        }
        return overlaps;

    }

    private void addPreExistingItems() {
        for ( int i = 0; i < calorieSet.getItemCount(); i++ ) {
            final PNode node = calorieDragStrip.addItemNode( calorieSet.getItem( i ) );
            dropTarget.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    node.setOffset( dropTarget.getFullBounds().getCenterX() - node.getFullBounds().getWidth() / 2 + node.getFullBounds().getWidth(),
                                    dropTarget.getFullBounds().getY() - node.getFullBounds().getHeight() * 1.1 );
                }
            } );
            node.setOffset( dropTarget.getFullBounds().getCenterX() - node.getFullBounds().getWidth() / 2 + node.getFullBounds().getWidth(),
                            dropTarget.getFullBounds().getY() - node.getFullBounds().getHeight() * 1.1 );
        }
    }

    public PImage getDropTarget() {
        return dropTarget;
    }

    private void setContainsItem( CaloricItem item, boolean shouldContain ) {
        if ( !calorieSet.contains( item ) && shouldContain ) {
            calorieSet.addItem( item );
        }
        else if ( !shouldContain ) {
            while ( calorieSet.contains( item ) ) {
                calorieSet.removeItem( item );
            }
        }
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

    private void relayout() {
        dropTarget.setOffset( 0, calorieDragStrip.getFullBounds().getMaxY() + SPACING_BETWEEN_PLATE_AND_TOOLBOX );
        plateTopSummaryNode.relayout();
        calorieDragStrip.setOffset( dropTarget.getFullBounds().getWidth() / 2 - calorieDragStrip.getStripPanelWidth() / 2, 2 );
    }

    public double getCalorieDragStripMaxY() {
        return calorieDragStrip.getFullBounds().getMaxY();
    }

    public PNode getTooltipLayer() {
        return calorieDragStrip.getTooltipLayer();
    }

    public void resetAll() {
        calorieDragStrip.resetAll();
        addPreExistingItems();
    }

    public double getPlateBottomY() {
        return dropTarget.getFullBounds().getMaxY();
    }

    public void addItemPressedListener( ActionListener actionListener ) {
        itemPressedListeners.add( actionListener );
    }

    public void addOverlapTarget( PNode overlapTarget ) {
        overlapTargets.add( overlapTarget );
    }
}
