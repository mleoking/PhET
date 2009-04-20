package edu.colorado.phet.naturalselection.control;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.model.Bunny;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.view.PopulationGraphNode;
import edu.umd.cs.piccolo.PNode;

public class PopulationCanvas extends PhetPCanvas implements NaturalSelectionModel.NaturalSelectionModelListener {

    private PNode rootNode;

    public static Dimension canvasSize = new Dimension( 80, 200 );
    private PopulationGraphNode populationGraphNode;
    private NaturalSelectionModel model;

    public PopulationCanvas( NaturalSelectionModel _model ) {
        super( canvasSize );

        model = _model;

        rootNode = new PNode();
        addWorldChild( rootNode );

        populationGraphNode = new PopulationGraphNode( 2 );
        rootNode.addChild( populationGraphNode );

        setPreferredSize( canvasSize );

        setBorder( null );

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );

        model.addListener( this );
    }

    public void reset() {
        populationGraphNode.reset();
    }

    public void updateLayout() {

    }

    public void onMonthChange( String monthName ) {

    }

    public void onGenerationChange( int generation ) {

    }

    public void onNewBunny( Bunny bunny ) {
        populationGraphNode.updatePopulation( model.getPopulation() );
    }

    public void onClimateChange( int climate ) {

    }

    public void onSelectionFactorChange( int selectionFactor ) {

    }
}
