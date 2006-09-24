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

import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.A_AB_BC_C_Reaction;
import edu.colorado.phet.molecularreactions.controller.ManualControlAction;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;

/**
 * MRModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleModule extends MRModule {

    public SimpleModule() {
        super( "Simple");
        setInitialConditions( (MRModel)getModel() );

        // create the control panel
//        setControlPanel( new ControlPanel() );
        setControlPanel( new SimpleMRControlPanel( this ));
//        setControlPanel( new ControlPanel() );
//        mrControlPanel = new MRControlPanel( this );
//        getControlPanel().addControl( mrControlPanel );

        // Add Manual Control button
        JButton manualCtrlBtn = new JButton( SimStrings.get("Control.manualControl"));
        manualCtrlBtn.addActionListener( new ManualControlAction( this ) );
        RegisterablePNode ctrlBtnNode = new RegisterablePNode( new PSwing( getPCanvas(), manualCtrlBtn ));
        double btnX = ( getMRModel().getBox().getMaxX() + getSpatialView().getFullBounds().getWidth() ) / 2;
        ctrlBtnNode.setOffset( btnX, 50 );
        ctrlBtnNode.setRegistrationPoint( ctrlBtnNode.getFullBounds().getWidth() / 2, 0 );
        getSpatialView().addChild( ctrlBtnNode );
    }

    /**
     *
     *
     * @param model
     */
    void setInitialConditions( MRModel model ) {
        {
            model.setReaction( new A_AB_BC_C_Reaction( model ) );
            {
                SimpleMolecule m1 = new MoleculeB();
                double yLoc = model.getBox().getMinY() + model.getBox().getHeight() / 2;
                m1.setPosition( 180, yLoc );
                m1.setVelocity( 0, 0 );
                model.addModelElement( m1 );
                SimpleMolecule m1a = new MoleculeA();
                m1a.setPosition( m1.getPosition().getX() + m1.getRadius() + m1a.getRadius(), yLoc );
                m1a.setVelocity( 0, 0 );
                model.addModelElement( m1a );

                CompositeMolecule cm = new MoleculeAB( new SimpleMolecule[]{m1, m1a},
                                                       new Bond[]{new Bond( m1, m1a )} );
                cm.setOmega( 0 );
                cm.setVelocity( 0, 0 );
                model.addModelElement( cm );

                SimpleMolecule m2 = new MoleculeC();
                m2.setPosition( m1.getPosition().getX() - 130, m1.getPosition().getY() );
                m2.setVelocity( 4, 0 );
                model.addModelElement( m2 );

                m2.setSelectionStatus( Selectable.SELECTED );
            }
        }
    }
}
