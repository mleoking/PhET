package edu.colorado.phet.fitness.control;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.model.CalorieSet;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by: Sam
 * Apr 23, 2008 at 11:35:09 AM
 */
public class SummaryNode extends PNode {
    private PNode layer = new PNode();
    private CalorieSet calorieSet;

    public SummaryNode( CalorieSet calorieSet ) {
        this.calorieSet = calorieSet;
        addChild( layer );
        for ( int i = 0; i < calorieSet.size(); i++ ) {
            addItem( calorieSet.getItem( i ) );
        }
        calorieSet.addListener( new CalorieSet.Listener() {
            public void itemAdded( CaloricItem item ) {
                addItem( item );
            }

            public void itemRemoved( CaloricItem item ) {
                for ( int i = 0; i < layer.getChildrenCount(); i++ ) {
                    SummaryItemNode child = (SummaryItemNode) layer.getChild( i );
                    if ( child.item == item ) {
                        layer.removeChild( child );
                        break;//remove first match
                    }
                }
            }
        } );
    }

    public static class SummaryItemNode extends PNode {
        private CaloricItem item;

        public SummaryItemNode( CaloricItem item, int count ) {
            this.item = item;
//            addChild( new PText( item.getName() ) );

            PImage imageNode = new PImage( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( item.getImage() ), 30 ) );
            addChild( imageNode );
            PText textNode = new PText( "=" + item.getCalories() + " kcal/day" );
            addChild( textNode );
            textNode.setOffset( imageNode.getFullBounds().getWidth(), imageNode.getFullBounds().getCenterY() - textNode.getFullBounds().getHeight() / 2 );
            if ( count != 1 ) {
                PText countNode = new PText( "" + count + " x" );
                countNode.setOffset( imageNode.getFullBounds().getX() - countNode.getFullBounds().getWidth(), imageNode.getFullBounds().getCenterY() - countNode.getFullBounds().getHeight() / 2 );
                addChild( countNode );
            }
        }

    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        PhetPCanvas contentPane = new PhetPCanvas();
        SummaryNode node = new SummaryNode( new CalorieSet() );
        node.addItem( new CaloricItem( "banana split", "bananasplit.png", 100 ) );
        node.addItem( new CaloricItem( "burger", "burger.png", 100 ) );
        node.addItem( new CaloricItem( "strawberry", "strawberry.png", 100 ) );
        contentPane.addScreenChild( node );
        node.setOffset( 100, 100 );
        frame.setContentPane( contentPane );
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }

    public void addItem( CaloricItem item ) {
        SummaryItemNode summaryItemNode = new SummaryItemNode( item, 1 );
        layer.addChild( summaryItemNode );
        relayout();
    }

    private void relayout() {
        if ( layer.getChildrenCount() > 0 ) {
            layer.getChild( 0 ).setOffset( 0, 0 );
            for ( int i = 1; i < layer.getChildrenCount(); i++ ) {
                PNode prevNode = layer.getChild( i - 1 );
                layer.getChild( i ).setOffset( prevNode.getOffset().getX(), prevNode.getFullBounds().getMaxY() );
            }
        }

    }
}