package edu.colorado.phet.fitness.control;

import edu.colorado.phet.fitness.model.CalorieSet;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Apr 23, 2008 at 11:35:09 AM
 */
public class PlateTopSummaryNode extends PNode {
    private PNode layer = new PNode();
    private CalorieSet calorieSet;
    private PNode plate;

    public PlateTopSummaryNode( CalorieSet calorieSet, PNode plate ) {
        this.calorieSet = calorieSet;
        this.plate = plate;
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

    public void relayout() {
        if ( layer.getChildrenCount() > 0 ) {
            layer.getChild( 0 ).setOffset( plate.getFullBounds().getX(), plate.getFullBounds().getY()-layer.getChild( 0 ).getFullBounds().getHeight());
            for ( int i = 1; i < layer.getChildrenCount(); i++ ) {
                PNode prevNode = layer.getChild( i - 1 );
                layer.getChild( i ).setOffset( prevNode.getOffset().getX(), prevNode.getFullBounds().getMinY()-layer.getChild( i ).getFullBounds().getHeight());
            }
        }

    }

}