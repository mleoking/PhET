/**
 * Copyright (C) 1998-2000 by University of Maryland, College Park, MD 20742, USA
 * All rights reserved.
 */
package edu.colorado.phet.piccolo.pswing;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Vector;

/**
 * This PComboBox won't work properly if it is located in an abnormal hierarchy of Cameras.
 * Support is provided for only one (or zero) view transforms.
 * <p/>
 * A ComboBox for use in Jazz.  This still has an associated JPopupMenu
 * (which is always potentially heavyweight depending on component location
 * relative to containing window borders.)  However, this ComboBox places
 * the PopupMenu component of the ComboBox in the appropriate position
 * relative to the permanent part of the ComboBox.  The PopupMenu is never
 * transformed.
 * <p/>
 * This class was not designed for subclassing.  If different behavior
 * is required, it seems more appropriate to subclass JComboBox directly
 * using this class as a model.
 * <p/>
 * NOTE: There is currently a known bug, namely, if the ComboBox receives
 * focus through 'tab' focus traversal and the keyboard is used to interact
 * with the ComboBox, there may be unexpected results.
 * <p/>
 * <P>
 * <b>Warning:</b> Serialized and ZSerialized objects of this class will not be
 * compatible with future Jazz releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running the
 * same version of Jazz. A future release of Jazz will provide support for long
 * term persistence.
 *
 * @author Lance Good
 */
public class PComboBox extends JComboBox implements Serializable {

    private MouseEvent currentEvent;
    private PSwing pSwing;
    private PSwingCanvas canvas;

    /**
     * Creates a ZComboBox that takes its items from an existing ComboBoxModel.
     *
     * @param model The ComboBoxModel from which the list will be created
     */
    public PComboBox( ComboBoxModel model ) {
        super( model );
        init();
    }

    /**
     * Creates a ZComboBox that contains the elements in the specified array.
     *
     * @param items The items to populate the ZComboBox list
     */
    public PComboBox( final Object items[] ) {
        super( items );
        init();
    }

    /**
     * Creates a ZComboBox that contains the elements in the specified Vector.
     *
     * @param items The items to populate the ZComboBox list
     */
    public PComboBox( Vector items ) {
        super( items );
        init();
    }

    /**
     * Create an empty ZComboBox
     */
    public PComboBox() {
        super();
        init();
    }

    /**
     * Substitue our look and feel for the default
     */
    private void init() {
        setUI( new ZBasicComboBoxUI() );
    }

    /**
     * Stores the most recent mousePressed ZMouseEvent
     */
    private void setCurrentEvent( MouseEvent me ) {
        currentEvent = me;
    }

    public void setEnvironment( PSwing pSwing, PSwingCanvas canvas ) {
        this.pSwing = pSwing;
        this.canvas = canvas;
    }

    /**
     * The substitute look and feel - used to capture the mouse
     * events on the arrowButton and the component itself and
     * to create our PopupMenu rather than the default
     */
    class ZBasicComboBoxUI extends BasicComboBoxUI {
        EventGrabber eg = new EventGrabber();

        /**
         * Add our listener to the front of the button's list
         */
        public void configureArrowButton() {
            arrowButton.addMouseListener( eg );
            super.configureArrowButton();
        }

        /**
         * Add the listener to the front of the combo's list
         */
        protected void installListeners() {
            comboBox.addMouseListener( eg );
            super.installListeners();
        }

        /**
         * Create our Popup instead of theirs
         */
        protected ComboPopup createPopup() {
            ZBasicComboPopup popup = new ZBasicComboPopup( comboBox );
            popup.getAccessibleContext().setAccessibleParent( comboBox );
            return popup;
        }
    }

    /**
     * The substitute ComboPopupMenu that places itself correctly
     * for Jazz
     */
    class ZBasicComboPopup extends BasicComboPopup {

        /**
         * @param combo The parent ComboBox
         */
        public ZBasicComboPopup( JComboBox combo ) {
            super( combo );
        }


        /**
         * Correctly computes the bounds for the Popup in Jazz if a
         * ZMouseEvent has been received.  Otherwise, it uses the
         * default algorithm for placing the popup.
         *
         * @param px corresponds to the x coordinate of the popup
         * @param py corresponds to the y coordinate of the popup
         * @param pw corresponds to the width of the popup
         * @param ph corresponds to the height of the popup
         * @return The bounds for the PopupMenu
         */
        protected Rectangle computePopupBounds( int px, int py, int pw, int ph ) {
            if( currentEvent != null ) {

                Rectangle2D r = getNodeBoundsInCanvas();
                Rectangle sup = super.computePopupBounds( px, py, pw, ph );
                return new Rectangle( (int)r.getX(), (int)r.getMaxY(), (int)sup.getWidth(), (int)sup.getHeight() );
            }
            else {
                return super.computePopupBounds( px, py, pw, ph );
            }

        }

    }

    /**
     * Grabs mousePressed events to capture the appropriate node for this
     * ComboBox
     */
    class EventGrabber extends MouseAdapter {
        public void mousePressed( MouseEvent me ) {
            if( me instanceof MouseEvent ) {
                setCurrentEvent( (MouseEvent)me );
            }
        }
    }

    private Rectangle2D getNodeBoundsInCanvas() {
        if( pSwing == null || canvas == null ) {
            throw new RuntimeException( "PComboBox.setEnvironment( swing, pCanvas );//has to be done manually at present" );
        }
        Rectangle2D r1c = pSwing.getBounds();
        pSwing.localToGlobal( r1c );
        canvas.getCamera().globalToLocal( r1c );
        r1c = canvas.getCamera().getViewTransform().createTransformedShape( r1c ).getBounds2D();
        return r1c;
    }

}













