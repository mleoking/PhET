package edu.colorado.phet.naturalselection.view;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

public class GenerationCountNode extends PNode implements NaturalSelectionModel.NaturalSelectionModelListener {
    private PText label;

    public GenerationCountNode( NaturalSelectionModel model ) {
        label = new PText( "Generations: 1" );
        label.setFont( new PhetFont( 14, true ) );
        addChild( label );
        model.addListener( this );
    }

    public double getTextWidth() {
        return label.getWidth();
    }

    public void onGenerationChange( int generation ) {
        label.setText( "Generations: " + String.valueOf( generation + 1 ) );
    }

    public void onNewBunny( Bunny bunny ) {

    }

    public void onClimateChange( int climate ) {

    }

    public void onSelectionFactorChange( int selectionFactor ) {

    }
}
