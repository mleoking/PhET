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
import edu.colorado.phet.molecularreactions.model.reactions.Profiles;

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
    public static String DEFAULT_ACTION = "default";

    private static class Reaction {
        EnergyProfile energyProfile;

        public Reaction( EnergyProfile energyProfile ) {
            this.energyProfile = energyProfile;
        }

        public EnergyProfile getEnergyProfile() {
            return energyProfile;
        }
    }

    private static Reaction R1 = new Reaction( Profiles.R1 );
    private static Reaction R2 = new Reaction( Profiles.R2 );
    private static Reaction R3 = new Reaction( Profiles.R3 );
    private static Reaction DEFAULT = new Reaction( Profiles.DEFAULT );
    private static Reaction DYO = new Reaction( Profiles.DYO );

    
    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

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
        System.out.println( "actionCommand = " + actionCommand );
        if( actionCommand.equals( R1_ACTION ) ) {
            currentReaction = R1;
        }
        else if( actionCommand.equals( R2_ACTION ) ) {
            currentReaction = R2;
        }
        else if( actionCommand.equals( R3_ACTION ) ) {
            currentReaction = R3;
        }
        else if( actionCommand.equals( DESIGN_YOUR_OWN_ACTION ) ) {
            currentReaction = DYO;
            designYourOwn = true;
        }
        else if( actionCommand.equals( DEFAULT_ACTION ) ) {
            currentReaction = DEFAULT;
        }
        else {
            throw new IllegalArgumentException();
        }

        if( currentReaction != null ) {
            // I'm not sure why the profile has to be set before the module is reset,
            // but it does
            module.getMRModel().setEnergyProfile( currentReaction.getEnergyProfile() );
            module.reset();
            module.getMRModel().setEnergyProfile( currentReaction.getEnergyProfile() );
        }

        // This must come after the energy profile is set. Otherwise, it has no effect
        // on the new profile
        module.getEnergyView().setProfileManipulable( designYourOwn );
    }
}
