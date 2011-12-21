// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.control.EvaporationControlNode;
import edu.colorado.phet.beerslawlab.control.SoluteControlNode;
import edu.colorado.phet.beerslawlab.view.BLLCanvas;
import edu.colorado.phet.beerslawlab.view.DropperNode;
import edu.colorado.phet.beerslawlab.view.ShakerNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;

/**
 * Canvas for the "Concentration" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationCanvas extends BLLCanvas {

    public ConcentrationCanvas( final ConcentrationModel model, Frame parentFrame ) {

        // Solute controls
        SoluteControlNode soluteControlNode = new SoluteControlNode( model.getSolutes(), model.solute, model.soluteForm );
        // Shaker
        ShakerNode shakerNode = new ShakerNode( model.shaker, model.soluteForm );
        // Dropper
        DropperNode dropperNode = new DropperNode( model.dropper, model.soluteForm );

        // Evaporation control
        final DoubleProperty evaporationRate = new DoubleProperty( 0d ); //TODO move to model
        final DoubleProperty waterVolume = new DoubleProperty( 1d ); //TODO move to model
        final Property<Boolean> clockRunning = new Property<Boolean>( true ); //TODO move to model
        EvaporationControlNode evaporationControlNode = new EvaporationControlNode( evaporationRate, waterVolume, clockRunning );
        // Remove Solute button
        TextButtonNode removeSoluteButtonNode = new TextButtonNode( Strings.REMOVE_SOLUTE, new PhetFont( 18 ), Color.ORANGE ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    //TODO
                }
            } );
        }};
        // Reset All button
        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( model, parentFrame, 18, Color.BLACK, Color.ORANGE ) {{
            setConfirmationEnabled( false );
        }};

        // rendering order
        {
            addChild( shakerNode );
            addChild( dropperNode );
            addChild( evaporationControlNode );
            addChild( removeSoluteButtonNode );
            addChild( resetAllButtonNode );
            addChild( soluteControlNode ); // on top, because it has a combo box popup
        }

        // layout
        {
            final double xMargin = 20;
            final double yMargin = 20;
            // upper right
            soluteControlNode.setOffset( getStageSize().getWidth() - soluteControlNode.getFullBoundsReference().getWidth() - xMargin, yMargin );
            // bottom left
            evaporationControlNode.setOffset( xMargin,
                                              getStageSize().getHeight() - evaporationControlNode.getFullBoundsReference().getHeight() - yMargin );
            // left of evaporation control
            removeSoluteButtonNode.setOffset( evaporationControlNode.getFullBoundsReference().getMaxX() + 10,
                                              evaporationControlNode.getFullBoundsReference().getCenterY() - ( removeSoluteButtonNode.getFullBoundsReference().getHeight() / 2 ) );
            // lower right
            resetAllButtonNode.setOffset( getStageSize().getWidth() - resetAllButtonNode.getFullBoundsReference().getWidth() - xMargin,
                                          getStageSize().getHeight() - resetAllButtonNode.getFullBoundsReference().getHeight() - yMargin );
        }
        //TODO enable this after more layout is fleshed out
//        scaleRootNodeToFitStage();
//        centerRootNodeOnStage();
    }
}
