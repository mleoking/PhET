/**
 * Copyright (C) 1998-2000 by University of Maryland, College Park, MD 20742, USA
 * All rights reserved.
 */
package edu.colorado.phet.piccolo.pswing;

import edu.umd.cs.piccolo.PCanvas;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Vector;

/**
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

    /**
     * Get the most recent ZMouseEvent
     */
    private MouseEvent getCurrentEvent() {
        return currentEvent;
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

                // We need to modify the y position to reflect the true
                // height of the ComboBox given the Jazz transformation
                Point2D pt = getNodeLocationInFrame();
                Rectangle2D bounds = new Rectangle2D.Double( pt.getX(), pt.getY(), (double)comboBox.getBounds().width, (double)comboBox.getBounds().height );
//                currentEvent.getPath().getCamera().localToCamera( bounds, currentEvent.getNode() );
                py = (int)( bounds.getHeight() + 0.5 );

                Rectangle absBounds;
                Rectangle r = new Rectangle( px, py, pw, ph );
                boolean inModalDialog = inModalDialog();
                /** Workaround for modal dialogs. See also JPopupMenu.java **/
                /** We don't need to worry about this in Jazz because Dialogs
                 aren't supported in Jazz - We'll leave it in just in case
                 ZCanvas is added to a Dialog **/
                if( inModalDialog ) {
                    Dialog dlg = getDialog();
                    Point p;
                    if( dlg instanceof JDialog ) {
                        JRootPane rp = ( (JDialog)dlg ).getRootPane();
                        p = rp.getLocationOnScreen();
                        absBounds = rp.getBounds();
                        absBounds.x = p.x;
                        absBounds.y = p.y;
                    }
                    else {
                        absBounds = dlg.getBounds();
                    }
                    p = new Point( absBounds.x, absBounds.y );
                    SwingUtilities.convertPointFromScreen( p, comboBox );
                    absBounds.x = p.x;
                    absBounds.y = p.y;
                }
                else {
                    Point p;
                    Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
                    absBounds = new Rectangle();

                    // We get the true ComboBox location on screen to calculate
                    // where to put the Popup component
                    p = getComboLocationOnScreen();

                    absBounds.x = -p.x;
                    absBounds.y = -p.y;
                    absBounds.width = scrSize.width;
                    absBounds.height = scrSize.height;

                }

                Point offset = getJazzComboOffset();

                // In this case we can do a normal pull down
                if( SwingUtilities.isRectangleContainingRectangle( absBounds, r ) ) {
                    r.x = r.x + offset.x;
                    r.y = r.y + offset.y;
                    return r;
                }
                else {
                    Rectangle r2 = new Rectangle( 0, -r.height, r.width, r.height );
                    // In this case we couldn't do pull down but we can do
                    // pull up
                    if( SwingUtilities.isRectangleContainingRectangle( absBounds, r2 ) ) {
                        r2.x = offset.x;
                        r2.y = offset.y + r2.y;
                        return r2;
                    }

                    // Here we couldn't pull-down so we'll take the better of
                    // the two possibilities cause we're in a dialog.
                    if( inModalDialog ) {
                        SwingUtilities.computeIntersection( absBounds.x, absBounds.y, absBounds.width, absBounds.height, r );
                        SwingUtilities.computeIntersection( absBounds.x, absBounds.y, absBounds.width, absBounds.height, r2 );

                        if( r.height > r2.height ) {
                            r.x = r.x + offset.x;
                            r.y = r.y + offset.y;

                            return r;
                        }
                        else {
                            r2.x = offset.x;
                            r2.y = offset.y + r2.y;

                            return r2;
                        }
                    }
                    // We couldn't really do pull-down or up optimally so we
                    // just go with up
                    else {
                        r2.x = offset.x;
                        r2.y = offset.y + r2.y;

                        return r2;
                    }
                }
            }
            else {
                return super.computePopupBounds( px, py, pw, ph );
            }

        }

        /**
         * Gets the true location for the ComboBox on the screen
         *
         * @return The true location of the ComboBox on the screen
         */
        private Point getComboLocationOnScreen() {
            Point position = null;

            if( comboBox.isShowing() ) {
                Point2D pt = new Point2D.Double( 0.0, 0.0 );
                Component c;

                // We don't want to get the offset of the Swing Component
                // from the SwingWrapper (in ZCanvas) so we stop when we get
                // to the top Swing component below the SwingWrapper
                for( c = comboBox; !( c.getParent().getParent() instanceof PCanvas ); c = c.getParent() ) {
                    Point location = c.getLocation();
                    pt.setLocation( pt.getX() + location.getX(), pt.getY() + location.getY() );

                }
                pt = getNodeLocationInFrame();
//                PCamera camera = currentEvent.getPath().getTopCamera();
//                camera.localToCamera( pt, currentEvent.getNode() );
                position = new Point( (int)( pt.getX() + 0.5 ), (int)( pt.getY() + 0.5 ) );

                Point canvasOffset = c.getParent().getLocationOnScreen();

                position.setLocation( position.getX() + canvasOffset.getX(), position.getY() + canvasOffset.getY() );
            }

            return position;
        }


        /**
         * The
         *
         * @return The offset from the expected screen location and the
         *         actual screen location.
         */
        private Point getJazzComboOffset() {
            Point swing = comboBox.getLocationOnScreen();
            Point jazz = getComboLocationOnScreen();
            jazz.setLocation( jazz.getLocation().getX() - swing.getLocation().getX(),
                              jazz.getLocation().getY() - swing.getLocation().getY() );
            return jazz;
        }

        /**
         * Copied directly from BasicComboPopup
         */
        private Dialog getDialog() {
            Container parent;
            for( parent = comboBox.getParent(); parent != null && !( parent instanceof Dialog )
                                                && !( parent instanceof Window ); parent = parent.getParent() ) {
                ;
            }
            if( parent instanceof Dialog ) {
                return (Dialog)parent;
            }
            else {
                return null;
            }
        }

        /**
         * Copied directly from BasicComboPopup
         */
        private boolean inModalDialog() {
            return ( getDialog() != null );
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

    private Point2D getNodeLocationInFrame() {
        if( pSwing == null || canvas == null ) {
            throw new RuntimeException( "PComboBox.setEnvironment( swing, pCanvas );//has to be done manually at present" );
        }
        Point2D r1c = pSwing.getBounds().getOrigin();
        pSwing.localToGlobal( r1c );
        canvas.getCamera().viewToLocal( r1c );
        return r1c;
    }

}













