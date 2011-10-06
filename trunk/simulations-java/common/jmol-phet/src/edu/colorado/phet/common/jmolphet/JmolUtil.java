// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jmolphet;

import org.jmol.api.JmolViewer;

/**
 * Jmol utilities.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JmolUtil {

    /*
     * Unbinds the mouse from all Jmol actions.
     * Jmol does not provide a way to unbind all actions, or to specify only those actions to bind.
     * So we're stuck with this brute-force method of unbinding each action separately.
     */
    public static void unbindMouse( JmolViewer viewer ) {
        viewer.scriptWait( "unbind \"_clickFrank\"" );
        viewer.scriptWait( "unbind \"_depth\"" );
        viewer.scriptWait( "unbind \"_dragDrawObject\"" );
        viewer.scriptWait( "unbind \"_dragDrawPoint\"" );
        viewer.scriptWait( "unbind \"_dragLabel\"" );
        viewer.scriptWait( "unbind \"_dragSelected\"" );
        viewer.scriptWait( "unbind \"_navTranslate\"" );
        viewer.scriptWait( "unbind \"_pickAtom\"" );
        viewer.scriptWait( "unbind \"_pickIsosurface\"" );
        viewer.scriptWait( "unbind \"_pickMeasure\"" );
        viewer.scriptWait( "unbind \"_pickLabel\"" );
        viewer.scriptWait( "unbind \"_pickNavigate\"" );
        viewer.scriptWait( "unbind \"_pickPoint\"" );
        viewer.scriptWait( "unbind \"_popupMenu\"" );
        viewer.scriptWait( "unbind \"_reset\"" );
        viewer.scriptWait( "unbind \"_rotate\"" );
        viewer.scriptWait( "unbind \"_rotateSelected\"" );
        viewer.scriptWait( "unbind \"_rotateZ\"" );
        viewer.scriptWait( "unbind \"_rotateZorZoom\"" );
        viewer.scriptWait( "unbind \"_select\"" );
        viewer.scriptWait( "unbind \"_selectAndNot\"" );
        viewer.scriptWait( "unbind \"_selectNone\"" );
        viewer.scriptWait( "unbind \"_selectOr\"" );
        viewer.scriptWait( "unbind \"_selectToggle\"" );
        viewer.scriptWait( "unbind \"_selectToggleOr\"" );
        viewer.scriptWait( "unbind \"_selectToggleOr\"" );
        viewer.scriptWait( "unbind \"_slab\"" );
        viewer.scriptWait( "unbind \"_slabAndDepth\"" );
        viewer.scriptWait( "unbind \"_slideZoom\"" );
        viewer.scriptWait( "unbind \"_spinDrawObjectCCW\"" );
        viewer.scriptWait( "unbind \"_spinDrawObjectCW\"" );
        viewer.scriptWait( "unbind \"_swipe\"" );
        viewer.scriptWait( "unbind \"_translate\"" );
        viewer.scriptWait( "unbind \"_wheelZoom\"" );
    }

    // Binds the left mouse button to Jmol's rotate action.
    public static void bindRotateLeft( JmolViewer viewer ) {
        viewer.scriptWait( "bind \"LEFT\" \"_rotate\"" );
    }
}
