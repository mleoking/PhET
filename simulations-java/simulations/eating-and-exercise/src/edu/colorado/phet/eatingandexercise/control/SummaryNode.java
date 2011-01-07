// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.control;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.eatingandexercise.model.CalorieSet;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.umd.cs.piccolo.PNode;

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
        calorieSet.addListener( new CalorieSet.Adapter() {
            public void itemAdded( CaloricItem item ) {
                addItem( item );
            }

            public void itemRemoved( CaloricItem item ) {
                for ( int i = 0; i < layer.getChildrenCount(); i++ ) {
                    SummaryItemNode child = (SummaryItemNode) layer.getChild( i );
                    if ( child.getItem() == item ) {
                        layer.removeChild( child );
                        break;//remove first match
                    }
                }
                relayout();
            }
        } );
        relayout();
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

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        PhetPCanvas contentPane = new PhetPCanvas();
        SummaryNode node = new SummaryNode( new CalorieSet() );
        node.addItem( new ExerciseItem( "banana split", "bananasplit.png", 100, 0, 160, new Human() ) );
        node.addItem( new ExerciseItem( "burger", "burger.png", 100, 0, 160, new Human() ) );
        node.addItem( new ExerciseItem( "strawberry", "strawberry.png", 100, 0, 160, new Human() ) );
        contentPane.addScreenChild( node );
        node.setOffset( 100, 100 );
        frame.setContentPane( contentPane );
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }

}