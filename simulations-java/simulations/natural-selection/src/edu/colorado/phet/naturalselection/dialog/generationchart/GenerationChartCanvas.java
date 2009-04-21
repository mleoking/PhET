package edu.colorado.phet.naturalselection.dialog.generationchart;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.module.naturalselection.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.view.GenerationChartNode;
import edu.colorado.phet.naturalselection.view.HeredityChartNode;
import edu.umd.cs.piccolo.PNode;

public class GenerationChartCanvas extends PhetPCanvas {

    public static final int TYPE_HEREDITY = 0;
    public static final int TYPE_GENERATION = 1;

    public static int lastType = TYPE_HEREDITY;

    private NaturalSelectionModel model;
    private GenerationChartNode generationChartNode;
    private HeredityChartNode heredityChartNode;

    public GenerationChartCanvas( NaturalSelectionModel model ) {


        super( NaturalSelectionDefaults.chartSize );

        setPreferredSize( NaturalSelectionDefaults.chartSize );

        this.model = model;

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        //setBorder( null );


        PNode rootNode = new PNode();
        addWorldChild( rootNode );

        heredityChartNode = new HeredityChartNode( model );
        generationChartNode = new GenerationChartNode( model );

        rootNode.addChild( heredityChartNode );
        rootNode.addChild( generationChartNode );

        select( lastType );
    }

    public void reset() {
        heredityChartNode.reset();
        generationChartNode.reset();
    }

    public void select( int lastType ) {
        this.lastType = lastType;

        if ( lastType == TYPE_HEREDITY ) {
            heredityChartNode.setVisible( true );
            generationChartNode.setVisible( false );
        }
        else {
            generationChartNode.setVisible( true );
            heredityChartNode.setVisible( false );
        }
    }


}
