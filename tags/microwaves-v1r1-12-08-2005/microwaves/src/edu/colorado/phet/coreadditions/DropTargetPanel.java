/**
 * Class: DropTargetPanel
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Oct 8, 2003
 */
package edu.colorado.phet.coreadditions;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;

public class DropTargetPanel extends JPanel {
    private int acceptableActions = DnDConstants.ACTION_COPY;
    private DropTargetListener dtListener;
    private DropTarget dropTarget;

    public DropTargetPanel() {
        this.dtListener = new DropTargetPanel.DTListener();
        this.dropTarget = new DropTarget( this,
                                          this.acceptableActions,
                                          this.dtListener,
                                          true );
    }

    class DTListener implements DropTargetListener {
        public void dragEnter( DropTargetDragEvent dtde ) {
            if( !isDragOk( dtde ) ) {
                dtde.rejectDrag();
                return;
            }
            else {
                DropTargetPanel.this.setBackground( Color.blue );
                dtde.acceptDrag( DropTargetPanel.this.acceptableActions );
            }
        }

        public void dragOver( DropTargetDragEvent dtde ) {
            if( !isDragOk( dtde ) ) {
                dtde.rejectDrag();
                return;
            }
            else {
                dtde.acceptDrag( DropTargetPanel.this.acceptableActions );
            }
        }

        public void dropActionChanged( DropTargetDragEvent dtde ) {
            if( !isDragOk( dtde ) ) {
                dtde.rejectDrag();
                return;
            }
            else {
                dtde.acceptDrag( DropTargetPanel.this.acceptableActions );
            }
        }

        public void dragExit( DropTargetEvent dte ) {
            DropTargetPanel.this.setBackground( Color.gray );
        }

        public void drop( DropTargetDropEvent dtde ) {
            System.out.println( "drop" );
        }

        private boolean isDragOk( DropTargetDragEvent dtde ) {
            boolean result = false;
            DataFlavor[] flavors = Toolbox.ToolTransferable.flavors;
            DataFlavor chosen = null;
            for( int i = 0; i < flavors.length && chosen == null; i++ ) {
                DataFlavor flavor = flavors[i];
                if( dtde.isDataFlavorSupported( flavor ) ) {
                    chosen = flavors[i];
                    int sourceActions = dtde.getSourceActions();
                    result = ( sourceActions & DropTargetPanel.this.acceptableActions ) != 0;
                }
            }
            return result;
        }
    }
}
