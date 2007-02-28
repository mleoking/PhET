test-location:

This sample application was written to demonstrate a proposed change to
the semantics of PhetGraphic's "location".  Originally, location was 
loosely related to bounds.  The new semantics made location a translation
that is applied after local transforms and before the parent's net transform.
This effectively moves the graphic's registration point to the specified 
location, relative to the parent container.  (After demonstrating the 
power of the new semantics, this change to PhetGraphic was adopted 
and checked in to CVS.)

Besides being a "proof of concept", this application also demonstrates how
registration point, location and transforms can be combined to create 
"self-contained behaviors" that can be combined in composite graphics.