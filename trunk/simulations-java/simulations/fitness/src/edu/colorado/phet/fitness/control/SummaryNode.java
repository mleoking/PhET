package edu.colorado.phet.fitness.control;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.fitness.FitnessResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by: Sam
 * Apr 23, 2008 at 11:35:09 AM
 */
public class SummaryNode extends PNode {
    private PNode layer = new PNode();

    public static class Item {
        private String name;
        private String image;
        private double cal;
        private int number;

        public Item( String name, String image, double cal ) {
            this( name, image, cal, 1 );
        }

        public Item( String name, String image, double cal, int number ) {
            this.name = name;
            this.image = image;
            this.cal = cal;
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }

        public double getCaloriesPerItem() {
            return cal;
        }

        public int getNumber() {
            return number;
        }

        public double getTotalCalories() {
            return cal * number;
        }
    }

    public SummaryNode() {
        addChild( layer );
    }

    public static class SummaryItemNode extends PNode {
        private Item item;

        public SummaryItemNode( Item item ) {
            this.item = item;
//            addChild( new PText( item.getName() ) );

            PImage imageNode = new PImage( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( item.getImage() ), 30 ) );
            addChild( imageNode );
            PText textNode = new PText( "=" + item.getTotalCalories() + " kcal/day" );
            addChild( textNode );
            textNode.setOffset( imageNode.getFullBounds().getWidth(), imageNode.getFullBounds().getCenterY() - textNode.getFullBounds().getHeight() / 2 );
            if ( item.getNumber() != 1 ) {
                PText countNode = new PText( "" + item.getNumber() + " x" );
                countNode.setOffset( imageNode.getFullBounds().getX() - countNode.getFullBounds().getWidth(), imageNode.getFullBounds().getCenterY() - countNode.getFullBounds().getHeight() / 2 );
                addChild( countNode );
            }
        }

    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        PhetPCanvas contentPane = new PhetPCanvas();
        SummaryNode node = new SummaryNode();
        node.addItem( new Item( "banana split", "bananasplit.png", 100 ) );
        node.addItem( new Item( "burger", "burger.png", 100, 2 ) );
        node.addItem( new Item( "strawberry", "strawberry.png", 100 ) );
        contentPane.addScreenChild( node );
        node.setOffset( 100, 100 );
        frame.setContentPane( contentPane );
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }

    public void addItem( Item item ) {
        SummaryItemNode summaryItemNode = new SummaryItemNode( item );
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