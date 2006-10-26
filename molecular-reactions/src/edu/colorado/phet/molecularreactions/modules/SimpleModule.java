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
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.view.AbstractSimpleMoleculeGraphic;
import edu.colorado.phet.molecularreactions.view.LauncherGraphic;
import edu.colorado.phet.molecularreactions.view.MoleculeIcon;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.*;

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
    private SimpleMolecule m1;
    private SimpleMolecule m1a;
    private CompositeMolecule cm;
    private SimpleMolecule m2;

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
        PSwing launcherMoleculeSelector = new PSwing( getPCanvas(), new LauncherLoadPanel() );
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

    private void setMolecules( MRModel model, SimpleMolecule m2 ) {

        if( this.m2 != null ) {
            model.removeModelElement( this.m2 );
        }
        if( m1 != null ) {
            model.removeModelElement( m1 );
        }
        if( m1a != null ) {
            model.removeModelElement( m1a);
        }
        if( cm != null ){
            model.removeModelElement( cm );
        }

        this.m2 = m2;
        m2.setPosition( launcher.getTipLocation().getX(), launcher.getTipLocation().getY() - m2.getRadius() );
        model.addModelElement( m2 );
        launcher.setBodyToLaunch( m2 );
        launcher.setMovementType( Launcher.ONE_DIMENSIONAL );

        m1 = new MoleculeB();
        double yLoc = model.getBox().getMinY() + model.getBox().getHeight() / 2;
        m1.setPosition( m2.getPosition().getX(), yLoc );
        m1.setVelocity( 0, 0 );
        model.addModelElement( m1 );

        m1a = null;
        if( m2 instanceof MoleculeC ) {
            m1a = new MoleculeA();
        }
        else {
            m1a = new MoleculeC();
        }

        m1a.setPosition( m1.getPosition().getX(), yLoc - m1.getRadius() - m1a.getRadius() );
        m1a.setVelocity( 0, 0 );
        model.addModelElement( m1a );

        cm = null;
        if( m2 instanceof MoleculeC ) {
            cm = new MoleculeAB( new SimpleMolecule[]{m1, m1a} );
        }
        else {
            cm = new MoleculeBC( new SimpleMolecule[]{m1, m1a} );
        }

        cm.setOmega( 0 );
        cm.setVelocity( 0, 0 );
        model.addModelElement( cm );

        m2.setSelectionStatus( Selectable.SELECTED );
    }

    public void reset() {
        super.reset();
        ( (MRModel)getModel() ).setInitialConditions();
        setInitialConditions( (MRModel)getModel() );
        controlPanel.reset();
    }


    class LauncherLoadPanel extends JPanel {
        private JRadioButton aRB;
        private JRadioButton cRB;
        private Class currentMoleculeType;

        public LauncherLoadPanel() {
            setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "Control.launcherType" ) ) );
            setBackground( MRConfig.SPATIAL_VIEW_BACKGROUND );

            ButtonGroup bg = new ButtonGroup();
            aRB = new JRadioButton();
            cRB = new JRadioButton();
            bg.add( aRB );
            bg.add( cRB );

            aRB.addActionListener( new MoleculeSelectorRBAction() );
            cRB.addActionListener( new MoleculeSelectorRBAction() );

            aRB.setBackground( MRConfig.SPATIAL_VIEW_BACKGROUND );
            cRB.setBackground( MRConfig.SPATIAL_VIEW_BACKGROUND );

            setLayout( new GridBagLayout() );
            int rbAnchor = GridBagConstraints.CENTER;
            int iconAnchor = GridBagConstraints.CENTER;
            Insets insets = new Insets( 3, 15, 3, 15 );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             rbAnchor,
                                                             GridBagConstraints.HORIZONTAL,
                                                             insets, 0, 0 );
            add( aRB, gbc );
            add( cRB, gbc );
            gbc.gridy = 0;
            gbc.gridy = GridBagConstraints.RELATIVE;
            gbc.gridx = 1;
            gbc.anchor = iconAnchor;
            add( new JLabel( new MoleculeIcon( MoleculeA.class ) ), gbc );
            add( new JLabel( new MoleculeIcon( MoleculeC.class ) ), gbc );

            cRB.setSelected( true );
            currentMoleculeType = MoleculeA.class;
        }

        private class MoleculeSelectorRBAction extends AbstractAction {

            public void actionPerformed( ActionEvent e ) {
                if( aRB.isSelected() ) {
                    setMolecules( getMRModel(), new MoleculeA() );
                }
                if( cRB.isSelected() ) {
                    setMolecules( getMRModel(), new MoleculeC() );
                }
            }
        }
    }
}

