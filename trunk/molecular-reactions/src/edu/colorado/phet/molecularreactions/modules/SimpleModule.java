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
 * MRModule
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

    public SimpleModule() {
        super( "Simple" );

        // Set up the model
        MRModel model = (MRModel)getModel();

        getSpatialView().addGraphicFactory( new ModelElementGraphicManager.GraphicFactory( Launcher.class,
                                                                                           getSpatialView().getTopLayer() ) {
            public PNode createGraphic( ModelElement modelElement ) {
                return new LauncherGraphic( (Launcher)modelElement );
            }
        } );

        // Set the location for the launcher and  add it's Swing control
        launcherTipLocation = new Point2D.Double( ( model.getBox().getMinX() + model.getBox().getMaxX() ) / 2,
                                                  model.getBox().getMaxY() );
        PSwing launcherMoleculeSelector = new PSwing( getPCanvas(),
                                                      new LauncherLoadPanel( this ) );
        getSpatialView().addChild( launcherMoleculeSelector );
        launcherMoleculeSelector.setOffset( launcherTipLocation.getX() - launcherMoleculeSelector.getFullBounds().getWidth() - 70,
                                            launcherTipLocation.getY() + 15 );

        // Set up the molecules
        setInitialConditions( model );

        // create the control panel
        controlPanel = new SimpleMRControlPanel( this );
        getControlPanel().addControl( controlPanel );

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
//        Point2D launcherTipLocation = new Point2D.Double( (model.getBox().getMinX() + model.getBox().getMaxX()) / 2,
//                                                          model.getBox().getMaxY() );
        launcher = new Launcher( launcherTipLocation );
        launcher.setTipLocation( launcherTipLocation );
        model.addModelElement( launcher );

        setMolecules( model, new MoleculeC() );
    }

    /**
     * Sets the molecules that will be used in this module
     * @param model
     * @param launcherMolecule
     */
    public void setMolecules( MRModel model, SimpleMolecule launcherMolecule ) {

        if( this.launcherMolecule != null ) {
            model.removeModelElement( this.launcherMolecule );
        }
        if( moleculeB != null ) {
            model.removeModelElement( moleculeB );
        }
        if( m3 != null ) {
            model.removeModelElement( m3 );
        }
        if( cm != null ){
            model.removeModelElement( cm );
        }

        this.launcherMolecule = launcherMolecule;
        launcherMolecule.setPosition( launcher.getTipLocation().getX(), launcher.getTipLocation().getY() - launcherMolecule.getRadius() );
        model.addModelElement( launcherMolecule );
        launcher.setBodyToLaunch( launcherMolecule );
        launcher.setMovementType( Launcher.ONE_DIMENSIONAL );

        moleculeB = new MoleculeB();
        double yLoc = model.getBox().getMinY() + model.getBox().getHeight() / 2;
        moleculeB.setPosition( launcherMolecule.getPosition().getX(), yLoc );
        moleculeB.setVelocity( 0, 0 );
        model.addModelElement( moleculeB );

        m3 = null;
        if( launcherMolecule instanceof MoleculeC ) {
            m3 = new MoleculeA();
        }
        else {
            m3 = new MoleculeC();
        }

        m3.setPosition( moleculeB.getPosition().getX(), yLoc - moleculeB.getRadius() - m3.getRadius() );
        m3.setVelocity( 0, 0 );
        model.addModelElement( m3 );

        cm = null;
        if( launcherMolecule instanceof MoleculeC ) {
            cm = new SimpleMoleculeAB( new SimpleMolecule[]{moleculeB, m3} );
//            cm = new MoleculeAB( new SimpleMolecule[]{moleculeB, m3} );
        }
        else {
            cm = new SimpleMoleculeBC( new SimpleMolecule[]{moleculeB, m3} );
//            cm = new MoleculeBC( new SimpleMolecule[]{moleculeB, m3} );
        }

        cm.setOmega( 0 );
        cm.setVelocity( 0, 0 );
        model.addModelElement( cm );

        launcherMolecule.setSelectionStatus( Selectable.SELECTED );
    }

    public void reset() {
        super.reset();
        ( (MRModel)getModel() ).setInitialConditions();
        setInitialConditions( (MRModel)getModel() );
        controlPanel.reset();
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    /**
     * These classes attempt tokeep the composite molecule from rotating when the mode is
     * 1D
     */
    private class SimpleMoleculeAB extends MoleculeAB {
        public SimpleMoleculeAB( SimpleMolecule[] components ) {
            super( components );
        }

        public void stepInTime( double dt ) {
            setOmega( 0 );
            super.stepInTime( dt );
            setOmega( 0 );
        }
    }

    private class SimpleMoleculeBC extends MoleculeBC {
        public SimpleMoleculeBC( SimpleMolecule[] components ) {
            super( components );
        }

        public void stepInTime( double dt ) {
            setOmega( 0 );
            super.stepInTime( dt );
            setOmega( 0 );
        }
    }

}

