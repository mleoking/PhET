// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import edu.colorado.phet.common.phetcommon.util.ObservableList;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingConstants.User.UserComponent;

/**
 * Reusable sim-sharing strings, grouped by their role in sim-sharing events.  Uses marker interfaces to ensure that all strings are "sancitioned" i.e. appearing
 * in a specified list and not as scattered and error-prone string literals.  Dynamic strings can still be constructed but must be put implementing interface.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 */
public class SimSharingConstants {

    public static enum PhetCommonMessageSource implements IMessageSource {
        phetcommon
    }

    //Parameters and values are top level so they can be shared between different namespaces, i.e. so you do not have to declare different parameters for model vs user, and so that
    //sims can use these values
    public static interface ParameterKey {
    }

    public static enum ParameterKeys implements ParameterKey {
        canvasPositionX, canvasPositionY, componentType, description, height, interactive, item, isSelected, key, part, text, title, value, width, window, x, y,
        isPlaying,

        //For system:
        time, name, version, project, flavor, locale, distributionTag, javaVersion, osName, osVersion, parserVersion, study, id, machineCookie, messageCount, messageIndex, shouldReset,

        errorMessage
    }

    public static interface ComponentType {
    }

    public static enum ComponentTypes implements ComponentType {
        button, checkBox, menuItem, radioButton, spinner, checkBoxMenuItem, icon, menu
    }

    //Use this class when a single componentID is ambiguous, such as when there are multiple file->save buttons in the sim (for saving different things)
    //This makes the text appear in the form: simSharingLog.fileSaveButton
    public static class ComponentChain implements UserComponent {
        UserComponent[] components;

        public ComponentChain( UserComponent... components ) {
            this.components = components;
        }

        @Override public String toString() {
            return new ObservableList<UserComponent>( components ).mkString( "." );
        }

        public static ComponentChain chain( UserComponent... components ) {
            return new ComponentChain( components );
        }
    }

    public static class User {
        public static interface UserComponent {
        }

        public static interface UserAction {
        }

        //These objects can be used automatically by the system (like if the system automatically hides a dialog), so mark them as SystemObjects as well.
        public static enum UserComponents implements UserComponent, System.SystemObject {
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

        public static enum UserActions implements UserAction, System.SystemAction {
            activated, changed, closed, exited, deactivated, deiconified, drag, endDrag, focusLost, iconified, invalidInput, keyPressed, moved, pressed, released, resized, selected, startDrag, windowCloseButtonPressed
        }

        public static class UserMessage extends SimSharingMessage<UserComponent, UserAction> {
            public UserMessage( IMessageSource source, IMessageType messageType, UserComponent object, UserAction action, Parameter... parameters ) {
                super( source, messageType, object, action, parameters );
            }
        }
    }

    public static class Model {
        public static interface ModelObject {
        }

        public static interface ModelAction {
        }

        public static class ModelMessage extends SimSharingMessage<ModelObject, ModelAction> {
            public ModelMessage( IMessageSource source, IMessageType messageType, ModelObject object, ModelAction action, Parameter... parameters ) {
                super( source, messageType, object, action, parameters );
            }
        }
    }

    public static class System {

        //Extend IComponent here since the system sometimes acts on user items, like automatically closing a dialog
        public static interface SystemObject extends UserComponent {
        }

        public static interface SystemAction {
        }

        public static enum SystemObjects implements SystemObject {
            simsharingManager, application
        }

        public static enum SystemActions implements SystemAction {
            started, stopped, connectedToServer, sentEvent, exited, shown
        }

        public static class SystemMessage extends SimSharingMessage<SystemObject, SystemAction> {
            public SystemMessage( IMessageSource source, IMessageType messageType, SystemObject object, SystemAction systemAction, Parameter... parameters ) {
                super( source, messageType, object, systemAction, parameters );
            }
        }
    }
}