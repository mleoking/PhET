mvc-example

This example demonstrates 3 approaches for connecting components
in PhET's MVC (Model-View-Controller) framework.

Approach A uses the Observer pattern, as implemented by java.util.Observable.
All classes with an 'A' prefix are related to this method.

Approach B uses relection as implemented by DynamicControllerListener.
All MVC connection are encapsulated in BConnectionsManager.
All classes with a 'B' prefix are related to this method.

Approach C defines its own listener interface and directly manages its own listener list.
Instead of listening for Piccolo PropertyChangeEvents, it uses a Piccolo InputEventListener
to update the model while dragging.
All classes with a 'C' prefix are related to this method.

--