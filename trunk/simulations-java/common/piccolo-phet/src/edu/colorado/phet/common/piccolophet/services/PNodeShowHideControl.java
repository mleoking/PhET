// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.services;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class adds a control to a specified node that enables the user to
 * hide/show the contents of that node.
 */
public class PNodeShowHideControl {

    private final static ImageIcon HIDE_IMAGE_ICON;
    private final static ImageIcon SHOW_IMAGE_ICON;

    private static final int DEFAULT_PADDING = 2;

    private final PNode parent;
    private final List savedChildren = new ArrayList();

    private final JButton showHideButton;

    private final PSwing showHideButtonNode;
    private final PSwing restoreLabelNode;

    private volatile boolean isHidden = false;

    static {
        HIDE_IMAGE_ICON = new ImageIcon( PhetCommonResources.getMinimizeButtonImage() );
        SHOW_IMAGE_ICON = new ImageIcon( PhetCommonResources.getMaximizeButtonImage() );
    }

    /**
     * Constructs a new show/hide control.
     *
     * @param parent            The parent, whose children will be hidden/shown.
     * @param restoreButtonName The string to associate with showing the children.
     */
    public PNodeShowHideControl( PNode parent, String restoreButtonName ) {
        this.parent = parent;
        this.showHideButton = new JButton();
        this.showHideButtonNode = new PSwing( showHideButton );
        this.restoreLabelNode = new PSwing( new JLabel( restoreButtonName ) );

        initButton();

        // This forces the correct sizing
        hide();
        show();
    }

    /**
     * Determines if the children are hidden.
     *
     * @return <code>true</code> if the children are hidden,
     *         <code>false</code> otherwise.
     */
    public boolean isHidden() {
        return isHidden;
    }

    /**
     * Determines if the children are visible.
     *
     * @return <code>true</code> if the children are visible,
     *         <code>false</code> otherwise.
     */
    public boolean isVisible() {
        return !isHidden();
    }

    /**
     * Uninstalls the control. If the children are hidden prior to the
     * invocation of this method, they will be made visible.
     */
    public void uninstall() {
        if ( isHidden() ) {
            show();
        }

        uninstallPSwing( restoreLabelNode );
        uninstallPSwing( showHideButtonNode );
    }

    /**
     * Hides the node's children.
     */
    public void hide() {
        if ( isHidden() ) {
            return;
        }

        savedChildren.clear();
        savedChildren.addAll( parent.getChildrenReference() );

        parent.removeAllChildren();

        restoreButtonNodes();

        setClosedStatus( true );
    }

    /**
     * Shows the node's children.
     */
    public void show() {
        if ( !isHidden() ) {
            return;
        }

        parent.removeAllChildren();
        parent.addChildren( savedChildren );

        setClosedStatus( false );
    }

    private void uninstallPSwing( PSwing node ) {
        if ( node != null ) {
            if ( parent.getChildrenReference().contains( node ) ) {
                parent.removeChild( node );
            }
            node.removeFromSwingWrapper();
        }
    }

    private void initButton() {
        restoreButtonNodes();
        showHideButtonNode.addPropertyChangeListener( new PositionSettingListener() );
        showHideButton.addActionListener( new StateTogglingActionListener() );
    }

    private void restoreButtonNodes() {
        parent.addChild( showHideButtonNode );
        parent.addChild( restoreLabelNode );
    }

    private void setClosedStatus( boolean closed ) {
        this.isHidden = closed;

        setStateData();
        setPositions();
    }

    private void setStateData() {
        if ( isHidden() ) {
            setButtonIcon( SHOW_IMAGE_ICON );

            restoreLabelNode.setVisible( true );
        }
        else {
            setButtonIcon( HIDE_IMAGE_ICON );

            restoreLabelNode.setVisible( false );
        }
    }

    private void setButtonIcon( ImageIcon icon ) {
        if ( icon != null ) {
            showHideButton.setIcon( icon );

            // Force button size to the dimensions of the icon:
            showHideButton.setBorder( BorderFactory.createEmptyBorder() );
        }
    }

    private void setPositions() {
        PBounds parentBounds = parent.getBounds();

        double buttonNodeX = parentBounds.getMaxX() - showHideButtonNode.getWidth() - DEFAULT_PADDING;
        double buttonNodeY = parentBounds.getMinY() + DEFAULT_PADDING;

        showHideButtonNode.setOffset(
                buttonNodeX,
                buttonNodeY
        );

        if ( showHideButtonNode.getParent() != null && showHideButtonNode.getParent().getChildrenReference().contains( showHideButtonNode ) ) {
            showHideButtonNode.moveToFront();
        }

        restoreLabelNode.setOffset(
                buttonNodeX - restoreLabelNode.getWidth() - DEFAULT_PADDING,
                buttonNodeY
        );

        if ( restoreLabelNode.getParent() != null && restoreLabelNode.getParent().getChildrenReference().contains( restoreLabelNode ) ) {
            restoreLabelNode.moveToFront();
        }
    }

    private class StateTogglingActionListener implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            if ( isHidden() ) {
                show();
            }
            else {
                hide();
            }
        }
    }

    private class PositionSettingListener implements PropertyChangeListener {
        private volatile boolean propertyIsChanging = false;

        public void propertyChange( PropertyChangeEvent evt ) {
            if ( !propertyIsChanging ) {
                propertyIsChanging = true;

                try {
                    setPositions();
                }
                finally {
                    propertyIsChanging = false;
                }
            }
        }
    }
}
