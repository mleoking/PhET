// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.messages;

/**
 * Enum for components manipulated by the user.
 * These objects can be used automatically by the system (like if the system automatically hides a dialog), so mark them as SystemObjects as well.
 */
public enum UserComponents implements UserComponent, SystemObject {
    playPauseButton, menu, tab, stepButton, stepBackButton, rewindButton, resetAllConfirmationDialog, sponsorDialog, dataCollectionLogMenuItem,

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
