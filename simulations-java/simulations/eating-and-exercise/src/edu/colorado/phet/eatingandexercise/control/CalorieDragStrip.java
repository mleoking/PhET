package edu.colorado.phet.eatingandexercise.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.colorado.phet.eatingandexercise.model.CalorieSet;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.CaloricFoodItem;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Created by: Sam
 * May 26, 2008 at 10:14:57 AM
 */
public class CalorieDragStrip extends PNode {
    private static Random random = new Random();
    private ArrayList listeners = new ArrayList();
    private static final int HEIGHT = 45;
    private PNode tooltipLayer = new PNode();
    private PNode stripPanel;
    public static int ITEMS_PER_PAGE = 4;
    private int count = ITEMS_PER_PAGE + 1;
    private ArrayList panels = new ArrayList();
    private Color buttonColor = new Color( 128, 128, 255 );
    private TogglePClip stripPanelClip;
    private ArrayList balancedDietDialogs = new ArrayList();

    public CalorieDragStrip( final CalorieSet available ) {
        for ( int i = 0; i < available.getItemCount(); i += count ) {
            panels.add( getPanel( available, i, Math.min( i + count, available.getItemCount() ) ) );
        }
        stripPanelClip = new TogglePClip();
        stripPanel = (PNode) panels.get( 0 );
        stripPanelClip.addChild( stripPanel );
        stripPanelClip.setPaint( new Color( 200, 220, 220 ) );

        GradientButtonNode leftButton = new GradientButtonNode( EatingAndExerciseResources.getString( "control.more.right" ), 13, buttonColor );
        leftButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                nextPanel( +1 );
            }
        } );
        addChild( leftButton );
        leftButton.setOffset( getMaxPanelWidth(), getMaxPanelHeight() / 2 - leftButton.getFullBounds().getHeight() / 2 );

        GradientButtonNode rightButton = new GradientButtonNode( EatingAndExerciseResources.getString( "control.more.left" ), 13, buttonColor );
        rightButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                nextPanel( -1 );
            }
        } );
        addChild( rightButton );
        addChild( stripPanelClip );
        rightButton.setOffset( -rightButton.getFullBounds().getWidth(), getMaxPanelHeight() / 2 - rightButton.getFullBounds().getHeight() / 2 );

        centerItems();
        stripPanelClip.setPathTo( new Rectangle2D.Double( 0, 0, getMaxPanelWidth(), getMaxPanelHeight() ) );
    }

    private void centerItems() {
        for ( int i = 0; i < panels.size(); i++ ) {
            PNode pNode = (PNode) panels.get( i );
            for ( int k = 0; k < pNode.getChildrenCount(); k++ ) {
                PNode child = pNode.getChild( k );
                if ( child instanceof DefaultDragNode ) {
                    child.setOffset( getMaxPanelWidth() / 2 - child.getFullBounds().getWidth() / 2, child.getOffset().getY() );
                }
            }
        }
    }

    public double getStripPanelWidth() {
        return stripPanelClip.getFullBounds().getWidth();
    }

    private double getMaxPanelHeight() {
        double max = Double.NaN;
        for ( int i = 0; i < panels.size(); i++ ) {
            PNode pNode = (PNode) panels.get( i );
            if ( Double.isNaN( max ) || pNode.getFullBounds().getHeight() > max ) {
                max = pNode.getFullBounds().getHeight();
            }
        }
        return max;
    }

    private double getMaxPanelWidth() {
        double max = Double.NaN;
        for ( int i = 0; i < panels.size(); i++ ) {
            PNode pNode = (PNode) panels.get( i );
            if ( Double.isNaN( max ) || pNode.getFullBounds().getWidth() > max ) {
                max = pNode.getFullBounds().getWidth();
            }
        }
        return max;
    }

    Timer timer = null;

    private void nextPanel( final int increment ) {
        if ( timer != null && timer.isRunning() ) {
            return;
        }
        stripPanelClip.setClipEnabled( true );
        final PNode oldStripPanel = stripPanel;
        stripPanel = (PNode) panels.get( nextIndex( increment ) );
        stripPanelClip.addChild( stripPanel );
        stripPanel.setOffset( 100 * increment, 0 );

        timer = new Timer( 30, null );
        timer.addActionListener( new ActionListener() {
            int count = 0;

            public void actionPerformed( ActionEvent e ) {

                oldStripPanel.translate( -10 * increment, 0 );
                stripPanel.translate( -10 * increment, 0 );
                count++;
                if ( count >= 10 ) {
                    stripPanelClip.removeChild( oldStripPanel );
                    stripPanelClip.setClipEnabled( false );
                    timer.stop();
                }
            }
        } );
        timer.start();
    }

    private int nextIndex( int increment ) {
        int index = panels.indexOf( stripPanel );
        int newIndex = index + increment;
        if ( newIndex >= panels.size() ) {
            newIndex = 0;
        }
        if ( newIndex < 0 ) {
            newIndex = panels.size() - 1;
        }
        return newIndex;
    }

    private PNode getPanel( final CalorieSet available, int min, int max ) {
        ArrayList nodes = new ArrayList();
        PNode sourceLayer = new PNode();
        for ( int i = min; i < max; i++ ) {
            final DefaultDragNode node = createNode( available.getItem( i ) );
            final int i1 = i;
            node.addInputEventListener( new PDragSequenceEventHandler() {
                private DefaultDragNode createdNode = null;

                protected void startDrag( PInputEvent e ) {
                    super.startDrag( e );
                    CaloricItem caloricItem = (CaloricItem) available.getItem( i1 ).clone();

                    createdNode = createNode( caloricItem );
                    createdNode.addDragHandler();
                    createdNode.getPNode().setOffset( node.getOffset() );
                    createdNode.setDragging( true );
                    addChild( createdNode.getPNode() );
                }

                protected void drag( PInputEvent event ) {
                    super.drag( event );
                    createdNode.getPNode().translate( event.getDelta().getWidth(), event.getDelta().getHeight() );

                    notifyDragged( createdNode );
                }

                protected void endDrag( PInputEvent e ) {
                    super.endDrag( e );
                    createdNode.setDragging( false );
                    notifyDropped( createdNode );
                }
            } );

            nodes.add( node );
        }
        int COLS = 1;
        for ( int i = 1; i < nodes.size(); i++ ) {
            int row = i / COLS;
            int col = i % COLS;
            PNode pNode = (PNode) nodes.get( i );
            PNode prev = (PNode) nodes.get( i - 1 );
            pNode.setOffset( col == 0 ? 0 : prev.getFullBounds().getMaxX(), row * HEIGHT );

        }
        for ( int i = 0; i < nodes.size(); i++ ) {
            sourceLayer.addChild( (PNode) nodes.get( i ) );
        }
        return sourceLayer;
    }

    //To be used in an external layer in order to simplify the layout code 
    public PNode getTooltipLayer() {
        return tooltipLayer;
    }

    public void removeItem( DragNode droppedNode ) {
        removeChild( droppedNode.getPNode() );
    }

    public void itemRemoved( CaloricItem item ) {
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            PNode child = getChild( i );
            if ( child instanceof DefaultDragNode ) {
                DefaultDragNode dragNode = (DefaultDragNode) child;
                if ( !dragNode.isDragging() && dragNode.getItem() == item ) {
                    removeChild( child );
                    i--;
                }
            }
        }
    }

    public DefaultDragNode getNode( CaloricItem item ) {
        //should only handle events from sources other than this
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            PNode child = getChild( i );
            if ( child instanceof DefaultDragNode ) {
                DefaultDragNode dragNode = (DefaultDragNode) child;
                if ( dragNode.getItem() == item ) {
                    return (DefaultDragNode) child;
                }
            }
        }
        return null;
    }

    public PNode addItemNode( CaloricItem item ) {
        final DefaultDragNode node = createNode( item );
        node.addDragHandler();
        addChild( node );
        return node.getPNode();
    }

    public Rectangle2D getGlobalFullSourceBounds() {
        return stripPanel.getGlobalFullBounds();
    }

    public void resetAll() {
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            PNode child = getChild( i );
            if ( child instanceof DefaultDragNode ) {
                DefaultDragNode dragNode = (DefaultDragNode) child;
                if ( !dragNode.isDragging() ) {
                    removeChild( child );
                    i--;
                }
            }
        }
        for ( int i = 0; i < balancedDietDialogs.size(); i++ ) {
            ( (BalancedDietDialog) balancedDietDialogs.get( i ) ).resetAll();
        }
        balancedDietDialogs.clear();
    }

    private class DefaultDragNode extends PNode implements DragNode {
        private CaloricItem item;
        private PNode node;
        private boolean dragging = false;//todo: could coalesce with PDragSequenceEventHandler.isDragging
        private CaloricItemLabelNode labelNode;

        public DefaultDragNode( PNode iconNode, CaloricItem item ) {
            this.item = item;
            this.node = iconNode;
            addChild( iconNode );
            addInputEventListener( new CursorHandler() );

            labelNode = new CaloricItemLabelNode( getLabelText() );
            item.addListener( new CaloricItem.Listener() {
                public void caloriesChanged() {
                    labelNode.setText( getLabelText() );
                }
            } );
            labelNode.setOffset( -labelNode.getFullBounds().getWidth() - 3, iconNode.getFullBounds().getHeight() / 2 - labelNode.getFullBounds().getHeight() / 2 );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseEntered( PInputEvent event ) {
                    if ( !getChildrenReference().contains( labelNode ) ) {
                        addChild( labelNode );
                    }
                }

                public void mouseExited( PInputEvent event ) {
                    while ( getChildrenReference().contains( labelNode ) ) {
                        removeChild( labelNode );
                    }
                }
            } );
        }

        private String getLabelText() {
            return "<html>" + item.getName() + " (" + EatingAndExerciseStrings.KCAL_PER_DAY_FORMAT.format( item.getCalories() ) + " " + EatingAndExerciseResources.getString( "units.cal" ) + ")</html>";
        }

        public void addDragHandler() {
            node.addInputEventListener( new PDragSequenceEventHandler() {
                protected void startDrag( PInputEvent e ) {
                    super.startDrag( e );
                    setDragging( true );
                    moveToFront();
                }

                protected void drag( PInputEvent event ) {
                    super.drag( event );
                    setDragging( true );//todo: remove this workaround, which was necessary because setDragging(true) from startDrag wasn't being called at the right time
                    getPNode().translate( event.getDelta().getWidth(), event.getDelta().getHeight() );
                    notifyDragged( DefaultDragNode.this );
                }

                protected void endDrag( PInputEvent e ) {
                    setDragging( false );
                    notifyDropped( DefaultDragNode.this );
                }
            } );
        }

        public boolean isDragging() {
            return dragging;
        }

        public PNode getPNode() {
            return this;
        }

        public CaloricItem getItem() {
            return item;
        }

        public PNode getPNodeIcon() {
            return node;
        }

        public void setDragging( boolean b ) {
            this.dragging = b;
//            System.out.println( "CalorieDragStrip$DefaultDragNode.setDragging: " + b );
        }
    }

    private DefaultDragNode createNode( final CaloricItem item ) {
        if ( item.getImage() != null && item.getImage().trim().length() > 0 ) {
            final DefaultDragNode dragNode = new DefaultDragNode( new PImage( BufferedImageUtils.multiScaleToHeight( EatingAndExerciseResources.getImage( item.getImage() ), HEIGHT ) ), item );

            if ( item.getImage().equals( Human.FOOD_PYRAMID ) && item instanceof CaloricFoodItem ) {
                decorateFoodPyramid( (CaloricFoodItem) item, dragNode );
            }
            return dragNode;
        }
        else {
            return new DefaultDragNode( new PhetPPath( new Rectangle( 0, 0, 10, 10 ), new Color( random.nextInt( 255 ), random.nextInt( 255 ), random.nextInt( 255 ) ) ), item );
        }
    }

    private void decorateFoodPyramid( final CaloricFoodItem item, DefaultDragNode dragNode ) {
        final BalancedDietDialog dialog = new BalancedDietDialog( item );
        balancedDietDialogs.add( dialog );

        GradientButtonNode gradientButtonNode = new GradientButtonNode( EatingAndExerciseResources.getString( "edit.edit" ), 12, Color.red );
        gradientButtonNode.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dialog.setVisible( true );
            }
        } );
        dragNode.addChild( gradientButtonNode );
        gradientButtonNode.setOffset( dragNode.getFullBounds().getMaxX() - gradientButtonNode.getFullBounds().getWidth() / 2, dragNode.getFullBounds().getY() );
    }

    public static interface DragNode {
        PNode getPNode();

        CaloricItem getItem();

        /*
         * Returns just the graphical icon child of the Node represented by this DragNode.
         *
         * This allows bounds computations using the icon, and ignoring labels, etc
         */
        PNode getPNodeIcon();
    }

    public static interface Listener {
        void nodeDropped( DragNode node );

        void nodeDragged( DragNode createdNode );
    }

    public static class Adapter implements Listener {
        public void nodeDropped( DragNode node ) {
        }

        public void nodeDragged( DragNode createdNode ) {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyDropped( DragNode createdNode ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).nodeDropped( createdNode );
        }
    }

    public void notifyDragged( DragNode createdNode ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).nodeDragged( createdNode );
        }
    }
}
