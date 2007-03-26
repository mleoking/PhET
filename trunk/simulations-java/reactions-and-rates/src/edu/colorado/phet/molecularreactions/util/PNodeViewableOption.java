/* Copyright 2007, University of Colorado */
package edu.colorado.phet.molecularreactions.util;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds an open/close button that makes the specified node's children visible/invisible.
 */
public class PNodeViewableOption {
    private static final String CLOSE_BUTTON_RESOURCE = "images/buttons/minimizeButton.png";
    private static final String OPEN_BUTTON_RESOURCE  = "images/buttons/maximizeButton.png";

    private final static ImageIcon CLOSE_IMAGE_ICON;
    private final static ImageIcon OPEN_IMAGE_ICON;

    private static final boolean OLD_STYLE_RESTORE = true;

    private static final int PADDING = 2;

    private final PNode parent;
    private final List savedChildren = new ArrayList();

    private final JButton button;

    private final PSwing buttonNode;

    private JButton restoreButton;
    private PSwing restoreButtonNode;

    private final PhetPCanvas canvas;

    private volatile boolean isClosed = false;

    static {
        ImageIcon close = null, open = null;

        try {
            close = new ImageIcon( ImageLoader.loadBufferedImage( CLOSE_BUTTON_RESOURCE ), "" );
            open  = new ImageIcon( ImageLoader.loadBufferedImage( OPEN_BUTTON_RESOURCE ),  "" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        CLOSE_IMAGE_ICON = close;
        OPEN_IMAGE_ICON  = open;
    }

    public PNodeViewableOption( PNode parent, PhetPCanvas canvas, String restoreButtonName ) {
        this.parent     = parent;
        this.button     = new JButton();
        this.buttonNode = new PSwing(button );
        this.canvas     = canvas;

        initButton( parent );

        if ( OLD_STYLE_RESTORE ) {
            initRestoreButton( restoreButtonName, canvas, parent );
        }

        close();
        open();
    }

    private void initButton( PNode parent ) {
        parent.addChild( buttonNode );
        buttonNode.addPropertyChangeListener( new PositionSettingListener() );
        button.addActionListener( new TogglingActionListener() );
    }

    private void initRestoreButton( String restoreButtonName, PhetPCanvas canvas, PNode pnode ) {
        restoreButton = new JButton();

        restoreButton.setText( SimStrings.get( restoreButtonName ) );

        restoreButtonNode = new PSwing(restoreButton );

        pnode.addChild( restoreButtonNode );

        restoreButtonNode.addPropertyChangeListener( new PositionSettingListener() );
        restoreButton.addActionListener( new TogglingActionListener() );
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void uninstall() {
        uninstallPSwing(buttonNode);
        uninstallPSwing(restoreButtonNode);
    }

    private void uninstallPSwing(PSwing node) {
        if (node != null) {
            if ( parent.getChildrenReference().contains(node)) {
                parent.removeChild(node);
            }
            node.removeFromSwingWrapper();
        }
    }

    public void close() {
        if (isClosed()) return;

        uninstall();

        savedChildren.clear();
        savedChildren.addAll( parent.getChildrenReference());

        parent.removeAllChildren();
        restoreButtonNodes();

        setClosedStatus( true );
    }

    private void restoreButtonNodes() {
        parent.addChild( buttonNode );

        if (OLD_STYLE_RESTORE) {
            parent.addChild( restoreButtonNode );
        }
    }

    public void open() {
        if (!isClosed()) return;

        parent.removeAllChildren();
        parent.addChildren( savedChildren );
        restoreButtonNodes();

        setClosedStatus( false );
    }

    private void setClosedStatus(boolean closed) {
        this.isClosed = closed;

        if (OLD_STYLE_RESTORE) {
            buttonNode.setVisible( !closed );
            restoreButtonNode.setVisible( closed );
        }

        setButtonContent();
        setButtonPosition();
        setRestoreButtonPosition();
    }

    private void setButtonContent() {
        if (isClosed()) {
            setButtonIcon( OPEN_IMAGE_ICON );
        }
        else {
            setButtonIcon( CLOSE_IMAGE_ICON );
        }
    }

    private void setButtonIcon( ImageIcon icon ) {
        if ( icon != null ) {
            button.setIcon( icon );

            button.setBorder(BorderFactory.createEmptyBorder());
        }
    }

    private void setButtonPositions() {
        setButtonPosition();
        setRestoreButtonPosition();
    }

    private void setButtonPosition() {
        PBounds bounds = parent.getBounds();

        buttonNode.setOffset(
            bounds.getMaxX() - buttonNode.getWidth() - PADDING,
            bounds.getMinY() + PADDING
        );

        if (buttonNode.getParent() != null && buttonNode.getParent().getChildrenReference().contains(buttonNode)) {
            buttonNode.moveToFront();
        }
    }

    private void setRestoreButtonPosition() {
        if (OLD_STYLE_RESTORE) {
            PBounds bounds = parent.getBounds();

            restoreButtonNode.setOffset(
                bounds.getMinX() + PADDING,
                bounds.getMaxY() - restoreButtonNode.getHeight() - PADDING
            );

            if (restoreButtonNode.getParent() != null && restoreButtonNode.getParent().getChildrenReference().contains(restoreButtonNode)) {
                restoreButtonNode.moveToFront();
            }
        }
    }

    private class TogglingActionListener implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            if (isClosed()) {
                open();
            }
            else {
                close();
            }
        }
    }

    private class PositionSettingListener implements PropertyChangeListener {
        private volatile boolean propertyIsChanging = false;

        public void propertyChange( PropertyChangeEvent evt ) {
            if (!propertyIsChanging) {
                propertyIsChanging = true;

                try {
                    setButtonPositions();
                }
                finally {
                    propertyIsChanging = false;
                }
            }
        }
    }
}
