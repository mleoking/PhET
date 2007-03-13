/* Copyright 2007, University of Colorado */
package edu.colorado.phet.molecularreactions.util;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.PhetPCanvas;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Adds an open/close button that makes the specified node's children visible/invisible.
 */
public class PNodeViewableOption {
    private static final String CLOSE_BUTTON_RESOURCE = "minimizeButton.png";
    private static final String OPEN_BUTTON_RESOURCE  = "maximizeButton.png";

    private final static ImageIcon CLOSE_IMAGE_ICON;
    private final static ImageIcon OPEN_IMAGE_ICON;

    private static final int PADDING = 2;

    private final PNode pnode;
    private final List savedChildren = new ArrayList();

    private final JButton button;

    private final PSwing buttonNode;

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


    public PNodeViewableOption( PNode pnode, PhetPCanvas canvas ) {
        this.pnode      = pnode;
        this.button     = new JButton();
        this.buttonNode = new PSwing( canvas, button );
        this.canvas     = canvas;
        
        pnode.addChild( buttonNode );

        buttonNode.addPropertyChangeListener( new PropertyChangeListener() {
            private volatile boolean propertyIsChanging = false;
            
            public void propertyChange( PropertyChangeEvent evt ) {
                if (!propertyIsChanging) {
                    propertyIsChanging = true;

                    setButtonPosition();
                }
            }
        });

        button.addActionListener( new TogglingActionListener() );

        close();
        open();
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void detach() {
        if (pnode.getChildrenReference().contains(buttonNode)) {
            pnode.removeChild( buttonNode );
        }

        buttonNode.removeFromSwingWrapper();
    }

    public void close() {
        if (isClosed()) return;

        detach();

        savedChildren.clear();
        savedChildren.addAll(pnode.getChildrenReference());

        pnode.removeAllChildren();
        pnode.addChild( buttonNode );

        setClosedStatus( true );
    }

    public void open() {
        if (!isClosed()) return;

        pnode.removeAllChildren();
        pnode.addChildren( savedChildren );
        pnode.addChild( buttonNode );

        setClosedStatus( false );
    }

    private void setClosedStatus(boolean closed) {
        this.isClosed = closed;

        setButtonIcons();
        setButtonPosition();
    }

    private void setButtonIcons() {
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

    private void setButtonPosition() {
        PBounds bounds = pnode.getBounds();

        buttonNode.setOffset(
            bounds.getMaxX() - buttonNode.getWidth() - PADDING,
            bounds.getMinY() + PADDING
        );

        if (buttonNode.getParent() != null && buttonNode.getChildrenReference().contains(buttonNode)) {
            buttonNode.moveToFront();
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
}
