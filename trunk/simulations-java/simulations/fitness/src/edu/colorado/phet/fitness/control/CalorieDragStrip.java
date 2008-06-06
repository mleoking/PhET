package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.GradientButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ToolTipNode;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.umd.cs.piccolo.PNode;
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
    private int count = 5;
    private ArrayList panels = new ArrayList();
    private Color buttonColor = new Color( 128, 128, 255 );

    public CalorieDragStrip( final CalorieSet available ) {
        for ( int i = 0; i < available.getItemCount(); i += count ) {
            panels.add( getPanel( available, i, Math.min( i + count, available.getItemCount() ) ) );
        }
        stripPanel = (PNode) panels.get( 0 );
        addChild( stripPanel );

        GradientButtonNode leftButton = new GradientButtonNode( "<html>&gt;</html>", 13, buttonColor );
        leftButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                nextPanel( +1 );
            }
        } );
        addChild( leftButton );
        leftButton.setOffset( getMaxPanelWidth(), getMaxPanelHeight() / 2 - leftButton.getFullBounds().getHeight() / 2 );

        GradientButtonNode rightButton = new GradientButtonNode( "<html>&lt;</html>", 13, buttonColor );
        rightButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                nextPanel( -1 );
            }
        } );
        addChild( rightButton );
        rightButton.setOffset( -rightButton.getFullBounds().getWidth(), getMaxPanelHeight() / 2 - rightButton.getFullBounds().getHeight() / 2 );
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

    private void nextPanel( int increment ) {
        removeChild( stripPanel );
        stripPanel = (PNode) panels.get( nextIndex( increment ) );
        addChild( stripPanel );
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
            final PNode node = createNode( available.getItem( i ) );
            final int i1 = i;
            node.addInputEventListener( new PDragSequenceEventHandler() {
                private DefaultDragNode createdNode = null;

                protected void startDrag( PInputEvent e ) {
                    super.startDrag( e );
                    CaloricItem caloricItem = (CaloricItem) available.getItem( i1 ).clone();

                    createdNode = createNode( caloricItem );
                    createdNode.addDragHandler();
                    createdNode.getPNode().setOffset( node.getOffset() );
                    addChild( createdNode.getPNode() );
                }

                protected void drag( PInputEvent event ) {
                    super.drag( event );
                    createdNode.getPNode().translate( event.getDelta().getWidth(), event.getDelta().getHeight() );
                    notifyDragged( createdNode );
                }

                protected void endDrag( PInputEvent e ) {
                    super.endDrag( e );
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

    private class DefaultDragNode extends PNode implements DragNode {
        private CaloricItem item;
        private PNode node;

        public DefaultDragNode( PNode node, CaloricItem item ) {
            this.item = item;
            this.node = node;
            addChild( node );
            node.addInputEventListener( new CursorHandler() );
        }

        public void addDragHandler() {
            node.addInputEventListener( new PDragSequenceEventHandler() {
                protected void drag( PInputEvent event ) {
                    super.drag( event );
                    getPNode().translate( event.getDelta().getWidth(), event.getDelta().getHeight() );
                    notifyDragged( DefaultDragNode.this );
                }

                protected void endDrag( PInputEvent e ) {
                    notifyDropped( DefaultDragNode.this );
                }
            } );
        }

        public PNode getPNode() {
            return this;
        }

        public CaloricItem getItem() {
            return item;
        }
    }

    private DefaultDragNode createNode( CaloricItem item ) {
        if ( item.getImage() != null && item.getImage().trim().length() > 0 ) {
            DefaultDragNode dragNode = new DefaultDragNode( new PImage( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( item.getImage() ), HEIGHT ) ), item );
            ToolTipNode toolTipNode = new ToolTipNode( "<html>" + item.getName() + " (" + item.getCalories() + " " + FitnessResources.getString( "units.cal" ) + ")</html>", dragNode );
            toolTipNode.setFont( new PhetFont( 16, true ) );

            tooltipLayer.addChild( toolTipNode );
            return dragNode;
        }
        else {
            final Color color = new Color( random.nextInt( 255 ), random.nextInt( 255 ), random.nextInt( 255 ) );
            return new DefaultDragNode( new PhetPPath( new Rectangle( 0, 0, 10, 10 ), color ), item );
        }
    }

    public static interface DragNode {
        PNode getPNode();

        CaloricItem getItem();
    }

    public static interface Listener {
        void nodeDropped( DragNode node );

        void notifyDragged( DragNode createdNode );
    }

    public static class Adapter implements Listener {
        public void nodeDropped( DragNode node ) {
        }

        public void notifyDragged( DragNode createdNode ) {
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
            ( (Listener) listeners.get( i ) ).notifyDragged( createdNode );
        }
    }
}
