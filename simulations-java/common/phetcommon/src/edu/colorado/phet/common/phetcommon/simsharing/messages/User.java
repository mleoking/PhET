// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

/**
 * @author Sam Reid
 */
public class User {

    //These objects can be used automatically by the system (like if the system automatically hides a dialog), so mark them as SystemObjects as well.
    public static enum UserComponents implements UserComponent, SystemObject {
        playPauseButton, menu, system, tab, stepButton, stepBackButton, rewindButton, resetAllConfirmationDialog, sponsorDialog, dataCollectionLogMenuItem,

        simSharingLogFileDialog,
        fileChooserCancelButton, fileChooserSaveButton, replaceFileNoButton, replaceFileYesButton, saveButton,

        phetFrame,
        fileMenu, exitMenuItem,
        helpMenu, helpMenuItem, megaHelpMenuItem, aboutMenuItem, checkForSimulationUpdateMenuItem,
        saveMenuItem, loadMenuItem, preferencesMenuItem,

        optionsMenu,
        teacherMenu,
        resetAllButton,

        faucetImage,
        spinner
    }

    public static enum UserActions implements UserAction, SystemAction {
        activated, changed, closed, exited, deactivated, deiconified, drag, endDrag, focusLost, iconified, invalidInput, keyPressed, moved, pressed, released, resized, selected, startDrag, windowCloseButtonPressed
    }

}
