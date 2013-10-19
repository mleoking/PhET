// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.fractions.research_november_2013;

/**
 * An activity is something the user started (and possibly never finished), which can have nested activities and computed metrics (such as elapsed time).
 * A user cannot be doing two activity 'leaves' at the same time, but will usually be doing an entire stack of nested activities:
 * Sim->Tab->Scene/Representation.
 * At different levels in the hierarchy, Activities are mutually exclusive (cannot be in two sims at once, two tabs at once, or two mutually exclusive representations at once).
 * <p/>
 * TODO: how to resume an activity?  Or is it just a stack for visualization and we will tell by color if it is same or not?
 * Can this be wired into Property<T> for visualization?  Visualize all of the Property<T> vs time?  Even numbers can be plotted within the bars?
 * Add a property for FrameActive, add Property<Module> for active module, then when events occur, track them via the stack of properties?
 * Property will need to be instrumented or wrapped so we can track its value vs. time
 * TODO: will doing that for every property kill the performance?  If so, may just need to keep summary stats in each Property?
 * Let's try a ticker tape that shows colors (for enum) and lines (for number)
 */
public class Activity {
    long startTime;

    public Activity() {
        this.startTime = System.currentTimeMillis();
    }
}
