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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.molecularreactions.controller.ManualControlAction;
import edu.colorado.phet.molecularreactions.controller.RunAction;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;
import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
import edu.colorado.phet.molecularreactions.view.AbstractSimpleMoleculeGraphic;
import edu.colorado.phet.molecularreactions.view.LauncherGraphic;
import edu.colorado.phet.molecularreactions.view.LauncherLoadPanel;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

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
    private SimpleMolecule moleculeB;
    private SimpleMolecule m3;
    private CompositeMolecule cm;
    private SimpleMolecule launcherMolecule;
    private Class launcherMoleculeClass = MoleculeC.class;
    private LauncherLoadPanel launcherLoadPanel;

    /**
     *
     */
    public SimpleModule() {
        super( SimStrings.get( "Module.simpleModuleTitle" ) );

        // Set up the model
        MRModel model = (MRModel)getModel();

        // Add a graphic factory for the launcher graphic
        getSpatialView().addGraphicFactory( new ModelElementGraphicManager.GraphicFactory( Launcher.class,
                                                                                           getSpatialView().getTopLayer() ) {
            public PNode createGraphic( ModelElement modelElement ) {
                return new LauncherGraphic( (Launcher)modelElement );
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

        // Add Manual and Run Control buttons
//        createManualRunButtons();

    }

    public void activate() {
        super.activate();
        // Disable marking of the selected molecule and its nearest neighbor
        AbstractSimpleMoleculeGraphic.setMarkSelectedMolecule( false );
    }

    public Launcher getLauncher() {
        return launcher;
    }

    private void createManualRunButtons() {
        final JButton manualCtrlBtn = new JButton( SimStrings.get( "Control.manualControl" ) );
        manualCtrlBtn.addActionListener( new ManualControlAction( this ) );
        RegisterablePNode ctrlBtnNode = new RegisterablePNode( new PSwing( getPCanvas(), manualCtrlBtn ) );
        double btnX = ( getMRModel().getBox().getMaxX() + getSpatialView().getFullBounds().getWidth() ) / 2;
        ctrlBtnNode.setOffset( btnX, 50 );
        ctrlBtnNode.setRegistrationPoint( ctrlBtnNode.getFullBounds().getWidth() / 2, 0 );
        getSpatialView().addChild( ctrlBtnNode );

        final JButton runBtn = new JButton( SimStrings.get( "Control.run" ) );
        runBtn.addActionListener( new RunAction( this ) );
        RegisterablePNode runBtnNode = new RegisterablePNode( new PSwing( getPCanvas(), runBtn ) );
        runBtnNode.setOffset( btnX, 120 );
        runBtnNode.setRegistrationPoint( runBtnNode.getFullBounds().getWidth() / 2, 0 );
        getSpatialView().addChild( runBtnNode );

        // Add listeners that will enable/disable the buttons appropriately
        manualCtrlBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                runBtn.setEnabled( true );
                manualCtrlBtn.setEnabled( false );
            }
        } );

        runBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                runBtn.setEnabled( false );
                manualCtrlBtn.setEnabled( true );
            }
        } );
        runBtn.setEnabled( false );
        manualCtrlBtn.setEnabled( true );
    }

    /**
     * @param model
     */
    protected void setInitialConditions( MRModel model ) {

        model.setReaction( new A_BC_AB_C_Reaction( model ) );

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

        // Create the appropriate molecule for the launcher
        SimpleMolecule launcherMolecule = null;
        if( launcherMoleculeClass == MoleculeC.class ) {
            launcherMolecule = new MoleculeC();
        }
        else if( launcherMoleculeClass == MoleculeA.class ) {
            launcherMolecule = new MoleculeA();
        }
        setMolecules( model, launcherMolecule );
        launcherLoadPanel.setMolecule( launcherMolecule );
    }

    /**
     * Sets the molecules that will be used in this module
     *
     * @param model
     * @param launcherMolecule
     */
    public void setMolecules( MRModel model, SimpleMolecule launcherMolecule ) {

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
        reset();
        launcherLoadPanel.setMolecule( launcherMolecule );
        launcher.setMovementType( movementType );
    }

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
}

