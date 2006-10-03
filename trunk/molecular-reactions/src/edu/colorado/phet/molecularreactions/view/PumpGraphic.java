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
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.model.*;
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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * PumpGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PumpGraphic extends PNode {
    private PImage pumpBodyPI;
    private PImage pumpHandlePI;
    private int initHandleYLoc = -15;
    private Point2D pumpBaseLocation;
    private Class currentMoleculeType;
    private MRModel model;

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
        PSwing moleculeSelector = new PSwing( (PSwingCanvas)module.getSimulationPanel(),
                                              new MoleculeTypeSelector() );

        moleculeSelector.setOffset( 15 + pumpBodyPI.getWidth() / 2 - moleculeSelector.getWidth() / 2,
                                    2 + pumpBodyPI.getHeight() );
        addChild( moleculeSelector );

        pumpBaseLocation = new Point2D.Double( 0, pumpBodyPI.getHeight() );
    }

    public Point2D getPumpBaseLocation() {
        return pumpBaseLocation;
    }

    private class PumpHandleMouseHandler extends PBasicInputEventHandler {
        double yStart;
        double dySinceLastMolecule;

        public void mouseEntered( PInputEvent event ) {
            PhetUtilities.getPhetFrame().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }

        public void mouseExited( PInputEvent event ) {
            PhetUtilities.getPhetFrame().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
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
            double y = model.getBox().getMaxY() - 100;
            Rectangle2D creationBounds = new Rectangle2D.Double( x, y, 1, 1 );
            RandomMoleculeParamGenerator moleculeParamGenerator = new RandomMoleculeParamGenerator( creationBounds,
                                                                                                    MRConfig.MAX_SPEED,
                                                                                                    .1,
                                                                                                    Math.PI * 3 / 4,
                                                                                                    Math.PI * 5 / 4 );
            AbstractMolecule newMolecule = MoleculeFactory.createMolecule( currentMoleculeType,
                                                                   moleculeParamGenerator );
            return newMolecule;
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    private class MoleculeTypeSelector extends JPanel {
        private JRadioButton aRB;
        private JRadioButton cRB;
        private JRadioButton abRB;
        private JRadioButton bcRB;

        public MoleculeTypeSelector() {
            setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "Control.moleculeType")) );
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
            add( new JLabel( new MoleculeIcon( MoleculeA.class ) ), gbc );
            add( new JLabel( new MoleculeIcon( MoleculeBC.class ) ), gbc );
            add( new JLabel( new MoleculeIcon( MoleculeAB.class ) ), gbc );
            add( new JLabel( new MoleculeIcon( MoleculeC.class ) ), gbc );

            aRB.setSelected( true );
            currentMoleculeType = MoleculeA.class;
        }

        private class MoleculeSelectorRBAction extends AbstractAction {

            public void actionPerformed( ActionEvent e ) {
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
        }
    }
}
