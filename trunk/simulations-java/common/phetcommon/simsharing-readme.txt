Notes for adding simsharing (sim event messages) to sims
Sam Reid
1/9/2012

PhET Simulations have a feature that allow usage data to be collected for analysis.  This feature is called Sim Event Data Collection, but is sometimes referred to as Sim Sharing.
This document describes how a simulation can be instrumented with this feature.

General pattern for development:
1. Run your code with command line args: -dev -study
This study is easy to test with since it does not show a dialog on startup, but you can still see the logging messages in the console or in the menu system.
2. Work through the features in the sim and see if they are being outputted for each user activity and important model activity (important is defined by the sim or research team)
3. When you find something missing a message, convert it to the corresponding SimSharing subclass, such as converting ButtonNode to SimSharingButtonNode.  For the SimSharing* classes, the UserComponent will be the first field.
4. Use static imports (alt-enter) to make the code read like:
		SimSharingManager.sendUserEvent( userComponent, pressed, componentType( checkBox ), param( isSelected, isSelected() ) );
instead of like:
		SimSharingManager.sendUserEvent( userComponent, UserActions.pressed, Parameter.componentType( ComponentTypes.checkBox ), Parameter.param( ParameterKeys.isSelected, isSelected() ) );

5. Running in a debugger and recompiling code in the same JVM can be an efficient way to iterate on adding these features.
6. If you need to use a SimSharing* class that does not exist, please create it in a package simsharing that is a sibling of the original object.  For instance, for edu.colorado.phet.common.piccolophet.nodes.TextButtonNode create edu.colorado.phet.common.piccolophet.nodes.simsharing.SimSharingTextButtonNode.  Make sure the message is sent before any other listeners are notified so that events don't appear out of order.
Add sub-consructors for all constructors in the parent class, make UserComponent the new first arg in all, store as a field and use in an overriden fireActionPerformed() or similar method.
7. Try to send system response in param where possible
8. Instead of subclassing PiccoloModule, subclass SimSharingPiccoloModule and provide the ID to use for the tab (OK to be null if no tabs in sim)

Details and design notes:

Messages have the form:
message := time TAB messageType TAB object TAB action TAB parameterList
parameterList := (parameter TAB)*
parameter := parameterKey = parameterValue

system, object, action, parameterKey are marked with interfaces to encourage reusing defined constants (typically in enums that implement the marker interface).
This also makes it possible to use autocompletion on these values and to assist in postprocessing (e.g., since we can easily iterate over enum values).

Dynamic strings can still be constructed but must implementing the marker interface.  Dynamic strings should all contain a "." to indicate they were dynamically constructed and won't appear explicitly in the enum.

For drag events, use SimSharingDragSequenceEventHandler2 instead of PBasicInputEventListener (once other usages are converted to SimSharingDragSequenceEventHandler2, SimSharingDragSequenceEventHandler will be deleted)

Where necessary (and possible), assign ID's to objects that are created and removed.

Non-interactive components should send messages too, so we can see that the user is clicking on them with no effect.  The code is:
faucetNode.addInputEventListener( new NonInteractiveUserComponent( UserComponents.faucetImage ) );

Related tickets:
Sim Sharing upgrades: 									https://phet.unfuddle.com/a#/projects/9404/tickets/by_number/3209
provide sim-sharing object names for Swing components: 	https://phet.unfuddle.com/a#/projects/9404/tickets/by_number/3202
data processing for research studies: 					https://phet.unfuddle.com/a#/projects/9404/tickets/by_number/3147
add data collection feature to support research studies: https://phet.unfuddle.com/a#/projects/9404/tickets/by_number/3136