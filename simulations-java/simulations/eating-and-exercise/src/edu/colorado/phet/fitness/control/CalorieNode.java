package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.colorado.phet.fitness.model.Human;
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
    private CalorieDragStrip calorieDragStrip;

    public CalorieNode( Frame parentFrame, String editButtonText, Color editButtonColor, final CalorieSet available, final CalorieSet calorieSet, String availableTitle, String selectedTitle, String dropTargetIcon ) {
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

        plateImage = new PImage( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( dropTargetIcon ), 120 ) );
        addChild( plateImage );

        plateTopSummaryNode = new PlateTopSummaryNode( calorieSet, plateImage );
        addChild( plateTopSummaryNode );

        calorieDragStrip = new CalorieDragStrip( available );
        calorieDragStrip.setOffset( 0, 2 );
        calorieDragStrip.addListener( new CalorieDragStrip.Adapter() {
            public void nodeDragged( CalorieDragStrip.DragNode node ) {
                setContainsItem( node.getItem(), plateImage.getGlobalFullBounds().intersects( node.getPNode().getGlobalFullBounds() ) );
            }

            public void nodeDropped( final CalorieDragStrip.DragNode node ) {
                if ( !node.getItem().getImage().equals( Human.FOOD_PYRAMID ) && node.getPNode().getFullBounds().intersects( calorieDragStrip.getSourceBounds() ) ) {
                    final Timer timer = new Timer( 30, null );
                    timer.addActionListener( new ActionListener() {
                        int count = 0;

                        public void actionPerformed( ActionEvent e ) {
                            node.getPNode().scaleAboutPoint( 0.82, node.getPNode().getFullBounds().getWidth() / 2, node.getPNode().getFullBounds().getHeight() / 2 );
                            count++;
                            if ( count >= 20 ) {
                                timer.stop();
                                setContainsItem( node.getItem(), false );
                                node.getPNode().getParent().removeChild( node.getPNode() );//todo: clean up this line, looks awkward
                            }
                        }
                    } );
                    timer.start();
                }
            }
        } );

//        final PhetPPath child = new PhetPPath( calorieDragStrip.getSourceBounds(), Color.green );
//        calorieDragStrip.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
//            public void propertyChange( PropertyChangeEvent evt ) {
//                child.setPathTo( calorieDragStrip.getSourceBounds() );
//            }
//        } );
//        addChild( child );


        addPreExistingItems();
        calorieSet.addListener( new CalorieSet.Listener() {
            public void itemAdded( CaloricItem item ) {
//                calorieDragStrip.itemAdded( item );
            }

            public void itemRemoved( CaloricItem item ) {
                calorieDragStrip.itemRemoved( item );
            }
        } );
        addChild( calorieDragStrip );

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

    private void addPreExistingItems() {
        for ( int i = 0; i < calorieSet.getItemCount(); i++ ) {
            final PNode node = calorieDragStrip.addItemNode( calorieSet.getItem( i ) );
            plateImage.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    node.setOffset( plateImage.getFullBounds().getCenterX() - node.getFullBounds().getWidth() / 2,
                                    plateImage.getFullBounds().getCenterY() - node.getFullBounds().getHeight() / 2 );
                }
            } );
            node.setOffset( plateImage.getFullBounds().getCenterX() - node.getFullBounds().getWidth() / 2,
                            plateImage.getFullBounds().getCenterY() - node.getFullBounds().getHeight() / 2 );
        }
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

    public void setMaxY( double maxY ) {
        this.maxY = maxY;
        relayout();
    }

    private void relayout() {
        editButton.setOffset( 0, maxY - editButton.getFullBounds().getHeight() );
        plateImage.setOffset( 0, editButton.getFullBounds().getY() - plateImage.getFullBounds().getHeight() );
        plateTopSummaryNode.relayout();
    }

    public PNode getTooltipLayer() {
        return calorieDragStrip.getTooltipLayer();
    }

    public void resetAll() {
        calorieDragStrip.resetAll();
        addPreExistingItems();
    }
}
