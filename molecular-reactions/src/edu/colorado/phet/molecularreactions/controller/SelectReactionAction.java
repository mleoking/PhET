/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.controller;

import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.MRConfig;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * SelectReactionAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SelectReactionAction extends AbstractAction {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static EnergyProfile r1Profile = new EnergyProfile( MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                100 );

    private static EnergyProfile r2Profile = new EnergyProfile( MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .7,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .4,
                                                                100 );

    private static EnergyProfile r3Profile = new EnergyProfile( MRConfig.DEFAULT_REACTION_THRESHOLD * .7,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .7,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                100 );
    private AbstractAction selectionAction;

    private static class Reaction {
        EnergyProfile energyProfile;

        public Reaction( EnergyProfile energyProfile ) {
            this.energyProfile = energyProfile;
        }

        public EnergyProfile getEnergyProfile() {
            return energyProfile;
        }
    }

    private static Reaction R1 = new Reaction( r1Profile );
    private static Reaction R2 = new Reaction( r2Profile );
    private static Reaction R3 = new Reaction( r3Profile );


    private Reaction currentReaction;

    private MRModule module;
    private JToggleButton r1Btn;
    private JToggleButton r2Btn;
    private JToggleButton r3Btn;
    private JToggleButton designYourOwnBtn;

    public SelectReactionAction( MRModule module, JToggleButton r1Btn, JToggleButton r2Btn, JToggleButton r3Btn, JToggleButton deisgnYourOwnBtn ) {
        this.module = module;
        this.r1Btn = r1Btn;
        this.r2Btn = r2Btn;
        this.r3Btn = r3Btn;
        this.designYourOwnBtn = deisgnYourOwnBtn;
    }

    public void actionPerformed( ActionEvent e ) {
        setReaction();
    }

    private void setReaction() {
        if( r1Btn.isSelected() ) {
            currentReaction = R1;
        }
        if( r2Btn.isSelected() ) {
            currentReaction = R2;
        }
        if( r3Btn.isSelected() ) {
            currentReaction = R3;
        }
        module.getEnergyView().setProfileManipulable( designYourOwnBtn.isSelected() );
        module.getMRModel().getReaction().setEnergyProfile( currentReaction.getEnergyProfile() );
    }
}
