/**
 * Class: Toolbox
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Oct 8, 2003
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Toolbox extends JToolBar {

    public Toolbox( JPanel workingPane ) {

        JToggleButton pressureButton = new JToggleButton( new PressureButtonAction( workingPane ) );
        this.add( pressureButton );
        pressureButton.setText( SimStrings.get( "Toolbox.PressureButton" ) );

        DragButton btn = new DragButton( SimStrings.get( "Toolbox.DragButton" ) );
        btn.setIcon(new ImageIcon( "images/thermometer.gif"));
        this.add( btn );
    }

    static class DropTargetPanel extends JPanel {
         private int acceptableActions = DnDConstants.ACTION_COPY;
         private DropTargetListener dtListener;
         private DropTarget dropTarget;

         public DropTargetPanel() {
             this.dtListener = new DTListener();
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
                 DataFlavor[] flavors = ToolTransferable.flavors;
                 DataFlavor chosen = null;
                 for( int i = 0; i < flavors.length && chosen == null; i++ ) {
                     DataFlavor flavor = flavors[i];
                     if( dtde.isDataFlavorSupported( flavor )) {
                         chosen = flavors[i];
                         int sourceActions = dtde.getSourceActions();
                         result = ( sourceActions & DropTargetPanel.this.acceptableActions ) != 0;
                     }
                 }
                 return result;
             }
         }
     }

    /**
     *
     */
    static class DropButton extends JButton {
        private int acceptableActions = DnDConstants.ACTION_COPY;
        private DropTargetListener dtListener;
        private DropTarget dropTarget;

        public DropButton( String s ) {
            this.setText( s );
            this.dtListener = new DTListener();
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
                    DropButton.this.setBackground( Color.blue );
                    dtde.acceptDrag( DropButton.this.acceptableActions );
                }
            }

            public void dragOver( DropTargetDragEvent dtde ) {
                if( !isDragOk( dtde ) ) {
                    dtde.rejectDrag();
                    return;
                }
                else {
                    dtde.acceptDrag( DropButton.this.acceptableActions );
                }
            }

            public void dropActionChanged( DropTargetDragEvent dtde ) {
                if( !isDragOk( dtde ) ) {
                    dtde.rejectDrag();
                    return;
                }
                else {
                    dtde.acceptDrag( DropButton.this.acceptableActions );
                }
            }

            public void dragExit( DropTargetEvent dte ) {
                DropButton.this.setBackground( Color.gray );
            }

            public void drop( DropTargetDropEvent dtde ) {
                System.out.println( "drop" );
            }

            private boolean isDragOk( DropTargetDragEvent dtde ) {
                boolean result = false;
                DataFlavor[] flavors = ToolTransferable.flavors;
                DataFlavor chosen = null;
                for( int i = 0; i < flavors.length && chosen == null; i++ ) {
                    DataFlavor flavor = flavors[i];
                    if( dtde.isDataFlavorSupported( flavor )) {
                        chosen = flavors[i];
                        int sourceActions = dtde.getSourceActions();
                        result = ( sourceActions & DropButton.this.acceptableActions ) != 0;
                    }
                }
                return result;
            }
        }
    }


    class DragButton extends JButton {
        private DragSource dragSource;
        private DragGestureListener dgListener;
        private DragSourceListener dsListener;

        public DragButton( String s ) {
            this.setText( s );
            this.dragSource = DragSource.getDefaultDragSource();
            this.dgListener = new DGListener();
            this.dsListener = new DSListener();

            this.dragSource.createDefaultDragGestureRecognizer( this, DnDConstants.ACTION_COPY, this.dgListener );

            // The following line is missing from the Java World example
            dragSource.addDragSourceListener( dsListener );
        }
    }


    static class ToolTransferable implements Transferable {

        public static final DataFlavor[] flavors = { DataFlavor.stringFlavor };

        public synchronized DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        private List flavorList() {
            return Arrays.asList( flavors );
        }
        public boolean isDataFlavorSupported( DataFlavor flavor ) {
            return flavorList().contains( flavor );
        }

        public Object getTransferData( DataFlavor flavor ) throws UnsupportedFlavorException, IOException {
            Object result = null;
            if( flavorList().contains( flavor ) ) {
                System.out.println( "getTransferData successful" );
                result = new String("it worked!!");
            }
            else {
                throw new UnsupportedFlavorException( flavor );
            }
            return result;
        }
    }

    static class DGListener implements DragGestureListener {

        public void dragGestureRecognized( DragGestureEvent dge ) {
            DragSourceListener dsListener = null;
            System.out.println( "gesture heard" );
            Transferable transferable = new ToolTransferable();
            dge.startDrag( DragSource.DefaultCopyNoDrop, transferable, dsListener );
        }
    }

    static class DSListener implements DragSourceListener {
        public void dragEnter( DragSourceDragEvent dsde ) {
            DragSourceContext context = dsde.getDragSourceContext();
            int action = dsde.getDropAction();
            if( ( action & DnDConstants.ACTION_COPY ) != 0 ) {
                context.setCursor( DragSource.DefaultCopyDrop );
            }
            else {
                context.setCursor( DragSource.DefaultCopyNoDrop );
            }
        }

        public void dragOver( DragSourceDragEvent dsde ) {
        }

        public void dropActionChanged( DragSourceDragEvent dsde ) {
        }

        public void dragExit( DragSourceEvent dse ) {
        }

        public void dragDropEnd( DragSourceDropEvent dsde ) {
            if( dsde.getDropSuccess() ) {
                int dropAction = dsde.getDropAction();
                if( dropAction == DnDConstants.ACTION_COPY ) {
                    System.out.println( "DROPPED!!!" );
                }
            }
        }
    }

    private class PressureButtonAction extends AbstractAction {
        private Container pane;
        private boolean toolVisible;
        private Component tool;

        public PressureButtonAction( Container pane ) {
            this.pane = pane;
            tool = new JLabel( SimStrings.get( "Toolbox.PressureToolTitle" ) );
        }

        public void actionPerformed( ActionEvent e ) {
            if( toolVisible ) {
                pane.remove( tool );
                pane.setBackground( Color.green );
                toolVisible = false;
            }
            else {
                pane.add( tool );
                pane.setBackground( Color.yellow );
                toolVisible = true;
            }
            System.out.println( "PRESSURE!" );
        }
    }


    public static void main( String[] args ) {
        JFrame frame = new JFrame( SimStrings.get( "Toolbox.DialogTitle" ) );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        Container mainPane = frame.getContentPane();

        JPanel centerPane = new JPanel();
        centerPane.setPreferredSize( new Dimension( 500, 400 ) );
        mainPane.add( new Toolbox( centerPane ), BorderLayout.NORTH );
        mainPane.add( centerPane, BorderLayout.CENTER );
        DropTargetPanel dtp = new DropTargetPanel();
        dtp.setPreferredSize( new Dimension( 500, 600 ));
        dtp.setBackground( Color.white );
        centerPane.add( dtp );
        dtp.add( new DropButton( "foo" ) );
        frame.pack();
        frame.show();
    }
}
