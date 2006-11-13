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
 * <p>
 * This started out to be an action that would be shared by a set of radio buttons
 * and a combo box, but after writing them, it didn't seem to pan out cleanly. The
 * way that the choice of reaction is selected is through the command attribute
 * of the ActionEvent, which is a string. Not what I would use if I weren't tied to
 * using an AbstractAction. 
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SelectReactionAction extends AbstractAction {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------
    public static String R1_ACTION = "R1";
    public static String R2_ACTION = "R2";
    public static String R3_ACTION = "R3";
    public static String DESIGN_YOUR_OWN_ACTION = "design your own";

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

    public SelectReactionAction( MRModule module ) {
        this.module = module;
    }

    public void actionPerformed( ActionEvent e ) {
        setReaction( e.getActionCommand() );
    }

    private void setReaction( String actionCommand ) {
        boolean designYourOwn = false;
        if( actionCommand.equals( "R1" ) ) {
            currentReaction = R1;
        }
        else if( actionCommand.equals( "R2" ) ) {
            currentReaction = R2;
        }
        else if( actionCommand.equals( "R3" ) ) {
            currentReaction = R3;
        }
        else if( actionCommand.equals( "design your own" ) ) {
            designYourOwn = true;
        }
        else {
            throw new IllegalArgumentException();
        }
        module.getEnergyView().setProfileManipulable( designYourOwn );
        if( currentReaction != null ) {
            module.getMRModel().getReaction().setEnergyProfile( currentReaction.getEnergyProfile() );
        }
    }
}
