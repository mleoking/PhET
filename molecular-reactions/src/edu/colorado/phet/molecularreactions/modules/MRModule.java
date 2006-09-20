/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.modules;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.molecularreactions.view.SpatialView;
import edu.colorado.phet.molectularreactions.view.energy.EnergyView;
import edu.colorado.phet.molecularreactions.model.*;

import java.awt.*;

/**
 * MRModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRModule extends Module {

    private Dimension size = new Dimension( 600, 500 );
    private MRControlPanel mrControlPanel;

    public MRModule( String name ) {
        super( name, new SwingClock( 40, 1 ) );

        // Create the model
        MRModel model = new MRModel( getClock() );
        setModel( model );

        // create the control panel
        setControlPanel( new ControlPanel() );
        mrControlPanel = new MRControlPanel( this );
        getControlPanel().addControl( mrControlPanel );

        // Create the basic graphics
        PhetPCanvas canvas = new PhetPCanvas( size );
        setSimulationPanel( canvas );

        // Set up the sizes and locations of the views
        Insets insets = new Insets( 10, 10, 10, 10 );

        // Create spatial view
        Dimension spatialViewSize = new Dimension( 400, 450 );
        model.getBox().setBounds( model.getBox().getBounds().getMinX(),
                                  model.getBox().getBounds().getMinY(),
                                  spatialViewSize.getWidth() - 30,
                                  spatialViewSize.getHeight() - 100 );
        SpatialView spatialView = new SpatialView( model, canvas, spatialViewSize );
        spatialView.setOffset( insets.left, insets.top );
        canvas.addScreenChild( spatialView );

        // Create energy view
        EnergyView energyView = new EnergyView( model );
        energyView.setOffset( insets.left + spatialView.getFullBounds().getWidth() + insets.right + insets.left,
                              insets.top );
        canvas.addScreenChild( energyView );
    }

    protected MRControlPanel getMRControlPanel() {
        return mrControlPanel;
    }

    public void setCountersEditable( boolean editable ) {
        MRControlPanel controlPanel = (MRControlPanel)getMRControlPanel();
        controlPanel.getMoleculeInstanceControlPanel().setCountersEditable( editable );
    }

}
