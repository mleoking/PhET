/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.view.icons.MoleculeIcon;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * PumpGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PumpGraphic extends PNode implements Resetable {
    private PImage pumpBodyPI;
    private PImage pumpHandlePI;
    private int initHandleYLoc = -15;
    private Point2D pumpBaseLocation;
    private Class currentMoleculeType;
    private MRModel model;
    private PumpGraphic.MoleculeTypeSelector moleculeTypeSelector;

    /**
     *
     */
    public PumpGraphic( MRModule module ) {

        model = module.getMRModel();

        // Pump Handle
        pumpHandlePI = PImageFactory.create( MRConfig.PUMP_HANDLE_IMAGE_FILE );
        pumpHandlePI.setOffset( 48, initHandleYLoc );
        addChild( pumpHandlePI );
        pumpHandlePI.addInputEventListener( new PumpHandleMouseHandler() );

        // The pump body and hose
        pumpBodyPI = PImageFactory.create( MRConfig.PUMP_BODY_IMAGE_FILE );
        addChild( pumpBodyPI );

        // Molecule selector
        moleculeTypeSelector = new MoleculeTypeSelector();
        PSwing moleculeSelector = new PSwing( (PSwingCanvas)module.getSimulationPanel(),
                                              moleculeTypeSelector );

        moleculeSelector.setOffset( 15 + pumpBodyPI.getWidth() / 2 - moleculeSelector.getWidth() / 2,
                                    2 + pumpBodyPI.getHeight() );
        addChild( moleculeSelector );

        pumpBaseLocation = new Point2D.Double( 0, pumpBodyPI.getHeight() );
    }

    public Point2D getPumpBaseLocation() {
        return pumpBaseLocation;
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    /**
     * Mouse handler
     */
    private class PumpHandleMouseHandler extends PBasicInputEventHandler {
        double yStart;
        double dySinceLastMolecule;

        public void mouseEntered( PInputEvent event ) {
            PhetUtilities.getActiveModule().getSimulationPanel().setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
        }

        public void mouseExited( PInputEvent event ) {
            PhetUtilities.getActiveModule().getSimulationPanel().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
        }

        public void mousePressed( PInputEvent event ) {
            yStart = event.getPosition().getY();
        }

        /**
         * Moves the pump handle graphic and adds molecules to the model
         *
         * @param event
         */
        public void mouseDragged( PInputEvent event ) {
            double dy = event.getDelta().getHeight();
            double yLoc = pumpHandlePI.getOffset().getY() + dy;

            // Constrain the motion of the handle to be within the bounds of the PNode containing
            // the PumpGraphic, and the initial location of the handle.
            if( yLoc <= initHandleYLoc && yLoc > -getOffset().getY() ) {
                pumpHandlePI.setOffset( pumpHandlePI.getOffset().getX(), yLoc );

                // Add molecules to the model
                if( dy > 0 ) {
                    if( dySinceLastMolecule > 20 ) {
                        AbstractMolecule newMolecule = createMolecule();
                        model.addModelElement( newMolecule );
                        // If the molecule create is a composite, add its components
                        if( newMolecule instanceof CompositeMolecule ) {
                            CompositeMolecule cm = (CompositeMolecule)newMolecule;
                            SimpleMolecule[] aSm = cm.getComponentMolecules();
                            for( int i = 0; i < aSm.length; i++ ) {
                                model.addModelElement( aSm[i] );
                            }
                        }
                        dySinceLastMolecule = 0;
                    }
                    else {
                        dySinceLastMolecule += dy;
                    }
                }
            }
        }

        private AbstractMolecule createMolecule() {
            double x = model.getBox().getMaxX() - 20;
            double y = model.getBox().getMaxY() - 80;
            Rectangle2D creationBounds = new Rectangle2D.Double( x, y, 1, 1 );
            MoleculeParamGenerator moleculeParamGenerator = new ConstantTemperatureMoleculeParamGenerator( creationBounds,
                                                                                                    model,
                                                                                                    .1,
                                                                                                    Math.PI * 3 / 4,
                                                                                                    Math.PI * 5 / 4,
                                                                                                    currentMoleculeType );
//            RandomMoleculeParamGenerator moleculeParamGenerator = new RandomMoleculeParamGenerator( creationBounds,
//                                                                                                    MRConfig.MAX_SPEED,
//                                                                                                    .1,
//                                                                                                    Math.PI * 3 / 4,
//                                                                                                    Math.PI * 5 / 4 );
            AbstractMolecule newMolecule = MoleculeFactory.createMolecule( currentMoleculeType,
                                                                           moleculeParamGenerator );
            return newMolecule;
        }
    }

    private class MoleculeTypeSelector extends JPanel implements Resetable {
        private JRadioButton aRB;
        private JRadioButton cRB;
        private JRadioButton abRB;
        private JRadioButton bcRB;
        private JLabel iconA = new JLabel();
        private JLabel iconBC = new JLabel();
        private JLabel iconAB = new JLabel();
        private JLabel iconC = new JLabel();

        public MoleculeTypeSelector() {

            // Add listeners to the icons so they will actively select the radio buttons next to them
            iconA.addMouseListener( new MoleculeIconMouseAdapter( aRB ) );
            iconBC.addMouseListener( new MoleculeIconMouseAdapter( bcRB ) );
            iconAB.addMouseListener( new MoleculeIconMouseAdapter( abRB ) );
            iconC.addMouseListener( new MoleculeIconMouseAdapter( cRB ) );

            // Listen for changes in the energy profile, and update the icons when they occur
            model.addListener( new MRModel.ModelListener() {
                public void energyProfileChanged( EnergyProfile profile ) {
                    updateIcons();
                }
            } );
            updateIcons();

            setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "Control.moleculeType" ) ) );
            setBackground( MRConfig.SPATIAL_VIEW_BACKGROUND );

            ButtonGroup bg = new ButtonGroup();
            aRB = new JRadioButton();
            cRB = new JRadioButton();
            abRB = new JRadioButton();
            bcRB = new JRadioButton();
            bg.add( aRB );
            bg.add( cRB );
            bg.add( abRB );
            bg.add( bcRB );

            aRB.addActionListener( new MoleculeSelectorRBAction() );
            cRB.addActionListener( new MoleculeSelectorRBAction() );
            abRB.addActionListener( new MoleculeSelectorRBAction() );
            bcRB.addActionListener( new MoleculeSelectorRBAction() );

            aRB.setBackground( MRConfig.SPATIAL_VIEW_BACKGROUND );
            cRB.setBackground( MRConfig.SPATIAL_VIEW_BACKGROUND );
            abRB.setBackground( MRConfig.SPATIAL_VIEW_BACKGROUND );
            bcRB.setBackground( MRConfig.SPATIAL_VIEW_BACKGROUND );

            setLayout( new GridBagLayout() );
            int rbAnchor = GridBagConstraints.CENTER;
            int iconAnchor = GridBagConstraints.CENTER;
            Insets insets = new Insets( 3, 3, 3, 3 );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             rbAnchor,
                                                             GridBagConstraints.HORIZONTAL,
                                                             insets, 0, 0 );
            add( aRB, gbc );
            add( bcRB, gbc );
            add( abRB, gbc );
            add( cRB, gbc );
            gbc.gridy = 0;

            gbc.gridy = GridBagConstraints.RELATIVE;
            gbc.gridx = 1;
            gbc.anchor = iconAnchor;

            add( iconA, gbc );
            add( iconBC, gbc );
            add( iconAB, gbc );
            add( iconC, gbc );

            aRB.setSelected( true );
            currentMoleculeType = MoleculeA.class;
        }

        private void updateIcons() {
            iconA.setIcon( new MoleculeIcon( MoleculeA.class, model.getEnergyProfile() ) );
            iconA.setHorizontalAlignment( JLabel.HORIZONTAL );
            iconBC.setIcon( new MoleculeIcon( MoleculeBC.class, model.getEnergyProfile() ) );
            iconBC.setHorizontalAlignment( JLabel.HORIZONTAL );
            iconAB.setIcon( new MoleculeIcon( MoleculeAB.class, model.getEnergyProfile() ) );
            iconAB.setHorizontalAlignment( JLabel.HORIZONTAL );
            iconC.setIcon( new MoleculeIcon( MoleculeC.class, model.getEnergyProfile() ) );
            iconC.setHorizontalAlignment( JLabel.HORIZONTAL );
        }

        private class MoleculeSelectorRBAction extends AbstractAction {

            public void actionPerformed( ActionEvent e ) {
                setMoleculeType();
            }
        }

        private void setMoleculeType() {
            if( aRB.isSelected() ) {
                currentMoleculeType = MoleculeA.class;
            }
            if( cRB.isSelected() ) {
                currentMoleculeType = MoleculeC.class;
            }
            if( abRB.isSelected() ) {
                currentMoleculeType = MoleculeAB.class;
            }
            if( bcRB.isSelected() ) {
                currentMoleculeType = MoleculeBC.class;
            }
        }

        public void reset() {
            aRB.setSelected( true );
            currentMoleculeType = MoleculeA.class;
        }

        private class MoleculeIconMouseAdapter extends MouseAdapter {
            JToggleButton toggleBtn;

            public MoleculeIconMouseAdapter( JToggleButton toggleBtn ) {
                this.toggleBtn = toggleBtn;
            }

            public void mouseClicked( MouseEvent e ) {
                toggleBtn.setSelected( true );
                setMoleculeType();
            }
        }
    }

    public void reset() {
        moleculeTypeSelector.reset();
    }
}
