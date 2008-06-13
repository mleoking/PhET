package edu.colorado.phet.fitness.control;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.fitness.FitnessPText;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.FitnessStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Created by: Sam
 * May 27, 2008 at 7:56:11 AM
 */
public class SummaryItemNode extends PNode {
    private CaloricItem item;

    public SummaryItemNode( CaloricItem item, int count ) {
        this.item = item;
        PNode imageNode;
        if ( item.getImage() != null && item.getImage().trim().length() > 0 ) {
            imageNode = new PImage( BufferedImageUtils.multiScaleToHeight( FitnessResources.getImage( item.getImage() ), 30 ) );
        }
        else {
            imageNode = new FitnessPText( item.getName() );
        }
        addChild( imageNode );
        PText textNode = new FitnessPText( "=" + FitnessStrings.KCAL_PER_DAY_FORMAT.format( item.getCalories() ) + " " + FitnessStrings.KCAL_PER_DAY );
        addChild( textNode );
        textNode.setOffset( imageNode.getFullBounds().getWidth(), imageNode.getFullBounds().getCenterY() - textNode.getFullBounds().getHeight() / 2 );
        if ( count != 1 ) {
            PText countNode = new FitnessPText( "" + count + " x" );
            countNode.setOffset( imageNode.getFullBounds().getX() - countNode.getFullBounds().getWidth(), imageNode.getFullBounds().getCenterY() - countNode.getFullBounds().getHeight() / 2 );
            addChild( countNode );
        }
    }

    public CaloricItem getItem() {
        return item;
    }
}
