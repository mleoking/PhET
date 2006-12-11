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

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.controller.ManualControlAction;
import edu.colorado.phet.molecularreactions.controller.RunAction;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
import edu.colorado.phet.molecularreactions.view.LauncherGraphic;
import edu.colorado.phet.molecularreactions.view.LauncherLoadPanel;
import edu.colorado.phet.molecularreactions.view.SimpleMoleculeGraphic;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.piccolo.help.WiggleMe;
import edu.colorado.phet.piccolo.help.MotionHelpBalloon;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.*;

/**
 * SimpleMRModule
 * <p/>
 * Module has just a few molecules
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleModule extends MRModule {
    private Launcher launcher;
    private SimpleMRControlPanel controlPanel;
    private Point2D launcherTipLocation;
    private CompositeMolecule cm;
    private SimpleMolecule launcherMolecule;
    private Class launcherMoleculeClass = MRConfig.DEFAULT_LAUNCHER_MOLECULE_CLASS;
    private LauncherLoadPanel launcherLoadPanel;
    private LauncherGraphic launcherGraphic;

    /**
     *
     */
    public SimpleModule() {
        super( SimStrings.get( "Module.simpleModuleTitle" ), MRConfig.MOLECULE_SEPARATION_PANE_SIZE );

        // Set up the model
        MRModel model = (MRModel)getModel();

        // Add a graphic factory for the launcher graphic
        getSpatialView().addGraphicFactory( new ModelElementGraphicManager.GraphicFactory( Launcher.class,
                                                                                           getSpatialView().getTopLayer() ) {
            public PNode createGraphic( ModelElement modelElement ) {
                launcherGraphic = new LauncherGraphic( (Launcher)modelElement );
                return launcherGraphic;
            }
        } );

        // Set the location for the launcher and  add it's Swing control
        launcherTipLocation = new Point2D.Double( ( model.getBox().getMinX() + model.getBox().getMaxX() ) / 2,
                                                  model.getBox().getMaxY() );
        launcherLoadPanel = new LauncherLoadPanel( this );
        PSwing launcherMoleculeSelector = new PSwing( getPCanvas(),
                                                      launcherLoadPanel );
        getSpatialView().addChild( launcherMoleculeSelector );
        launcherMoleculeSelector.setOffset( launcherTipLocation.getX() - launcherMoleculeSelector.getFullBounds().getWidth() - 70,
                                            launcherTipLocation.getY() + 15 );

        // Set up the molecules
        setInitialConditions( model );

        // create the control panel
        controlPanel = new SimpleMRControlPanel( this );
        getControlPanel().addControl( controlPanel );

        // Disable user manipulation of the profile
        getEnergyView().setProfileManipulable( false );

        // Create a wiggle-me
        createWiggleMe();

    }

    public void activate() {
        super.activate();
        // Disable marking of the selected molecule and its nearest neighbor
        SimpleMoleculeGraphic.setMarkSelectedMolecule( false );
    }

    public Launcher getLauncher() {
        return launcher;
    }

    /**
     * @param model
     */
    protected void setInitialConditions( MRModel model ) {

        // Place the heat source to the right of center
        TemperatureControl tempCtrl = model.getTemperatureControl();
        tempCtrl.setPosition( model.getBox().getMaxX() - 50, tempCtrl.getPosition().getY() );

        // Add the launcher and its graphic
        if( launcher == null ) {
            launcher = new Launcher( launcherTipLocation );
        }
        launcher.setMovementType( Launcher.ONE_DIMENSIONAL );
        launcher.setExtension( 0.0 );
        model.addModelElement( launcher );
        resetMolecules( model );


    }

    public void resetMolecules( MRModel model ) {
        // Create the appropriate molecule for the launcher
        SimpleMolecule launcherMolecule = null;
        if( launcherMoleculeClass == MoleculeC.class ) {
            launcherMolecule = new MoleculeC();
        }
        else if( launcherMoleculeClass == MoleculeA.class ) {
            launcherMolecule = new MoleculeA();
        }

        // Create the molecules
        SwingUtilities.invokeLater( new MoleculeCreator( model, launcherMolecule ) );
    }

    /**
     * Sets the molecules that will be used in this module
     *
     * @param model
     * @param launcherMolecule
     */
    public void setMolecules( MRModel model, SimpleMolecule launcherMolecule ) {

        // Clear the molecules from the model
        model.removeAllMolecules();

        // Save the class so we know what to make if we are asked to reload
        launcherMoleculeClass = launcherMolecule.getClass();

        if( this.launcherMolecule != null ) {
            model.removeModelElement( this.launcherMolecule );
        }
        if( cm != null ) {
            model.removeModelElement( cm );
            for( int i = 0; i < cm.getComponentMolecules().length; i++ ) {
                SimpleMolecule simpleMolecule = cm.getComponentMolecules()[i];
                model.removeModelElement( simpleMolecule );
            }
        }

        this.launcherMolecule = launcherMolecule;
        launcherMolecule.setPosition( launcher.getRestingTipLocation().getX(), launcher.getRestingTipLocation().getY() - launcherMolecule.getRadius() );
        model.addModelElement( launcherMolecule );
        launcher.setBodyToLaunch( launcherMolecule );
        launcher.setTheta( 0 );

        cm = null;
        Class compositeMoleculeClass = null;
        MoleculeParamGenerator moleculeParamGenerator = new MoleculeParamGenerator( launcherMolecule, model );
        if( launcherMolecule instanceof MoleculeC ) {
            compositeMoleculeClass = MoleculeAB.class;
        }
        else {
            compositeMoleculeClass = MoleculeBC.class;
        }
        cm = (CompositeMolecule)MoleculeFactory.createMolecule( compositeMoleculeClass,
                                                                moleculeParamGenerator );
        cm.rotate( Math.PI / 2 );
        cm.setOmega( 0 );
        cm.setVelocity( 0, 0 );
        model.addModelElement( cm );
        for( int i = 0; i < cm.getComponentMolecules().length; i++ ) {
            SimpleMolecule simpleMolecule = cm.getComponentMolecules()[i];
            model.addModelElement( simpleMolecule );
        }

        launcherMolecule.setSelectionStatus( Selectable.SELECTED );
    }

    public void reset() {
        super.reset();
        setInitialConditions( (MRModel)getModel() );
        controlPanel.reset();
    }

    public void reload() {
        Launcher.MovementType movementType = launcher.getMovementType();
        launcherMoleculeClass = this.launcherMolecule.getClass();
        resetMolecules( getMRModel() );
        launcherLoadPanel.setMolecule( launcherMolecule );
        launcher.setMovementType( movementType );
    }

    /**
     * Add a wiggle-me that will go away when the user clicks on the red knob
     */
    private void createWiggleMe() {
        final PhetPCanvas pCanvas = (PhetPCanvas)getSimulationPanel();
        final MotionHelpBalloon wiggleMe = new MotionHelpBalloon( pCanvas, SimStrings.get( "Application.wiggleMe" ) );
        wiggleMe.setOffset( 0, 0 );
        wiggleMe.setBalloonFillPaint( new Color( 255, 255, 100 ) );
        wiggleMe.setBalloonVisible( true );
        wiggleMe.setBalloonStroke( new BasicStroke( 1 ) );
        pCanvas.addWorldChild( wiggleMe );
        wiggleMe.setVisible( true );
        wiggleMe.animateTo( launcher.getRestingTipLocation().getX() - wiggleMe.getFullBounds().getWidth() - 15,
                            launcher.getRestingTipLocation().getY() + 85  );
        launcherGraphic.addInputEventListener( new PBasicInputEventHandler() {

            public void mousePressed( PInputEvent event ) {
                super.mousePressed( event );
                launcherGraphic.removeInputEventListener( this );
                pCanvas.removeWorldChild( wiggleMe );
            }
        } );
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    /**
     * Parameter generator for the composite molecules used in this module.
     */
    private static class MoleculeParamGenerator implements edu.colorado.phet.molecularreactions.model.MoleculeParamGenerator {
        private final SimpleMolecule launcherMolecule;
        private final MRModel model;

        public MoleculeParamGenerator( SimpleMolecule launcherMolecule, MRModel model ) {
            this.launcherMolecule = launcherMolecule;
            this.model = model;
        }

        public Params generate() {
            return new Params( new Point2D.Double( launcherMolecule.getPosition().getX(),
                                                   model.getBox().getMinY() + model.getBox().getHeight() / 2 ),
                               new Vector2D.Double(), 0 );
        }
    }

    private class MoleculeCreator implements Runnable {
        private final MRModel model;
        private final SimpleMolecule launcherMolecule;

        public MoleculeCreator( MRModel model, SimpleMolecule launcherMolecule ) {
            this.model = model;
            this.launcherMolecule = launcherMolecule;
        }

        public void run() {
            setMolecules( model, launcherMolecule );
            launcherLoadPanel.setMolecule( launcherMolecule );
        }
    }
}

