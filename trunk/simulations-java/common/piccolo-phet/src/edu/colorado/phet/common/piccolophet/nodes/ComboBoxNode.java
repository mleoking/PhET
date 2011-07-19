package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PComponent;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.common.phetcommon.view.util.SwingUtils.centerWindowOnScreen;
import static java.util.Arrays.asList;

/**
 * Piccolo combo box.  This is written as a pure piccolo component since the PComboBox + PSwing was awkward to use and not rendering the pop-up in the correct location. See #2982
 *
 * @author Sam Reid
 */
public class ComboBoxNode<T> extends PNode {

    /**
     * Property that can be used to view or set the currently selected item.  Note that the selected item does not need to be in the list of available items
     */
    public final Property<T> selectedItem;

    //Node to show the selected item, with a triangle for a drop-down box.  Clicking anywhere in this node will show the popup
    //Not final because the instance is re-created and changed when the selected item changes
    private PNode selectedItemNode;

    //Node to show for the popup
    private final PNode popup;

    //Canvases that can be clicked on to dismiss the popup dialog, discovered in the event that shows the popup dialog
    private final ArrayList<PCanvas> listeningTo = new ArrayList<PCanvas>();

    //Method that hides the popup when the user clicks away from it
    final PBasicInputEventHandler dismissPopup = new PBasicInputEventHandler() {
        @Override public void mousePressed( PInputEvent event ) {
            hidePopup();
        }
    };

    //Default font to use if converting items with the default ToString node generator
    private static final Font DEFAULT_FONT = new PhetFont( 18 );

    /**
     * Create a CombBoxNode with the specified items, using the toString function to create strings for each item
     *
     * @param items the items to show in the combo box
     */
    public ComboBoxNode( T... items ) {
        this( asList( items ) );
    }

    /**
     * Create a CombBoxNode with the specified items, using the toString method to create strings for each item using the default font.
     *
     * @param items the items to show in the combo box
     */
    public ComboBoxNode( List<T> items ) {
        this( items, new ToString<T>( DEFAULT_FONT ) );
    }

    /**
     * Create a ComboBoxNode with the specified items and specified way to convert the items to strings
     *
     * @param items         items the items to show in the combo box
     * @param nodeGenerator the function to use to convert the T items to PNodes to show in the drop down box or in the selection region
     */
    public ComboBoxNode( final List<T> items, final Function1<T, PNode> nodeGenerator ) {
        //Default to use the first item as the selected item
        selectedItem = new Property<T>( items.get( 0 ) );

        //Create the text nodes for the drop down box for determining their metrics (so they can all be created with equal widths)
        PNode[] itemNodes = new PNode[items.size()];
        double maxWidth = 0;
        for ( int i = 0; i < items.size(); i++ ) {

            //Create the item, and let pick events fall through to the background so the entire width of the node is selectable
            itemNodes[i] = nodeGenerator.apply( items.get( i ) );
            itemNodes[i].setPickable( false );
            itemNodes[i].setChildrenPickable( false );

            maxWidth = Math.max( maxWidth, itemNodes[i].getFullBounds().getWidth() );
        }

        //Create the items in the drop down list
        PNode[] choices = new PNode[items.size()];
        for ( int i = 0; i < items.size(); i++ ) {
            choices[i] = new ListItem<T>( items.get( i ), itemNodes[i], maxWidth ) {{
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {
                        selectedItem.set( item );
                    }
                } );
            }};
        }

        //Create the entire popup, and populate it with ListItems created above
        popup = new ControlPanelNode( new VBox( 2, true, choices ), Color.white, new BasicStroke( 1 ), Color.black, 2, 8, false ) {
            {
                //Make it so that the popup disappears when an item is selected
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mouseReleased( PInputEvent event ) {
                        setVisible( false );
                    }
                } );

                //Don't show the popup until requested
                setVisible( false );
            }

            //Make it so the node is not pickable when invisible
            @Override public void setVisible( boolean isVisible ) {
                super.setVisible( isVisible );

                //Add and remove the popup from the scene so that ComboBoxNode.getFullBounds() won't account for the invisible popup box
                if ( isVisible && !ComboBoxNode.this.getChildrenReference().contains( popup ) ) {
                    ComboBoxNode.this.addChild( popup );
                }
                else if ( !isVisible && ComboBoxNode.this.getChildrenReference().contains( popup ) ) {
                    ComboBoxNode.this.removeChild( popup );
                }

                setPickable( isVisible );
                setChildrenPickable( isVisible );
            }
        };

        //When the selected item changes, show the new selected item.  This is done by discarding the old selected item node and replacing it with a new one.
        //Since this uses automatic callbacks in observer binding, this also creates the selected item node on initialization
        final double finalMaxWidth = maxWidth;
        selectedItem.addObserver( new VoidFunction1<T>() {
            public void apply( T t ) {
                removeChild( selectedItemNode );
                selectedItemNode = new SelectedItemNode( nodeGenerator.apply( selectedItem.get() ), finalMaxWidth );
                addChild( selectedItemNode );
            }
        } );

        //Show the popup beneath the selected item displayer
        popup.setOffset( 0, selectedItemNode.getFullBounds().getHeight() + 2 );
    }

    //Hide the popup and unregister all registered listeners
    private void hidePopup() {
        popup.setVisible( false );
        for ( PCanvas pComponent : listeningTo ) {
            pComponent.removeInputEventListener( dismissPopup );
        }
        listeningTo.clear();
    }

    /**
     * This node shows the triangular icon that points down and indicates the user can press for a popup (though they can press anywhere on the selected item or icon to get the popup)
     */
    private static class TriangleNode extends PNode {
        private TriangleNode() {
            addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 20, 20 ), Color.lightGray ) );
            addChild( new PhetPPath( new DoubleGeneralPath( 2, 8 ) {{
                lineToRelative( 8, 8 );
                lineToRelative( 8, -8 );
                closePath();
            }}.getGeneralPath(), Color.darkGray ) );
        }
    }

    //Create the graphic to use to show the currently selected item, which allows the user to show the popup of other choices
    private class SelectedItemNode extends ControlPanelNode {
        public SelectedItemNode( PNode node, double maxWidth ) {
            super( new HBox(
                    new NodeWithBackground( node, maxWidth ),

                    //Show the triangle that looks customary for ComboBoxes
                    new TriangleNode()
            ), Color.white, new BasicStroke( 1 ), Color.black, 3, 5, false );

            //When clicked, toggle whether the popup is visible
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( final PInputEvent event ) {
                    popup.setVisible( !popup.getVisible() );

                    //Register a listener that will make the popup disappear when the user clicks away
                    //Have to schedule this for later or the popup never shows (because of how we are handling dismissing the popup when clicking else where in the canvas).
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            PComponent component = event.getComponent();
                            if ( component instanceof PCanvas ) {
                                PCanvas canvas = (PCanvas) component;
                                if ( !listeningTo.contains( canvas ) ) {
                                    listeningTo.add( canvas );
                                    canvas.addInputEventListener( dismissPopup );
                                }
                            }
                        }
                    } );
                }
            } );
            addInputEventListener( new CursorHandler() );
        }
    }

    //Shows a selectable (or selected) node with an optional background.  This makes it so the PNode fills the specified width and so that the entire background highlights when moused over.
    private static class NodeWithBackground extends PNode {
        private PhetPPath background;

        public NodeWithBackground( PNode node, double width ) {
            final Color plainColor = Color.white;
            background = new PhetPPath( new Rectangle2D.Double( -2, 0, width + 2 + 2, node.getFullBounds().getHeight() ), plainColor );
            background.setStroke( new BasicStroke( 1 ) );
            background.setStrokePaint( null );
            addChild( background );
            addChild( node );
        }

        public void setStrokePaint( Color color ) {
            background.setStrokePaint( color );
        }

        //Used to change the highlighting if the mouse is over the item
        @Override public void setPaint( Paint paint ) {
            super.setPaint( paint );
            background.setPaint( paint );
        }
    }

    //PNode for items shown in the popup box, which are highlighted when moused over
    private static class ListItem<T> extends PNode {
        public final T item;

        public ListItem( T item, final PNode node, double width ) {
            this.item = item;
            final NodeWithBackground nodeWithBackground = new NodeWithBackground( node, width );
            final Color highlightColor = new Color( 84, 226, 243 );
            addChild( nodeWithBackground );

            //Highlight when moused over
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseEntered( PInputEvent event ) {
                    nodeWithBackground.setPaint( highlightColor );
                    nodeWithBackground.setStrokePaint( Color.darkGray );
                }

                @Override public void mouseExited( PInputEvent event ) {
                    nodeWithBackground.setPaint( Color.white );
                    nodeWithBackground.setStrokePaint( null );
                }
            } );
            addInputEventListener( new CursorHandler() );
        }
    }

    //The default strategy for converting T items to PNodes is to show a PText with their toString representation
    public static class ToString<T> implements Function1<T, PNode> {
        private final Font font;

        public ToString( Font font ) {
            this.font = font;
        }

        public PNode apply( T t ) {
            return new PText( t.toString() ) {{
                setFont( font );
            }};
        }
    }

    //Test application
    public static void main( String[] args ) {
        new JFrame() {{
            setContentPane( new PhetPCanvas() {{
                setBackground( Color.blue );
                addScreenChild( new ComboBoxNode<String>( asList( "sugar", "salt", "ethanol", "sodium nitrate" ) ) {{
                    setOffset( 100, 100 );
                }} );
                addScreenChild( new TextButtonNode( "Unrelated button" ) {{
                    setOffset( 400, 100 );
                }} );

                addScreenChild( new ComboBoxNode<Integer>( asList( 1, 2, 3, 4 ), new Function1<Integer, PNode>() {
                    Color[] colors = new Color[] { Color.red, Color.green, Color.blue, Color.yellow, Color.red };

                    public PNode apply( Integer integer ) {
                        return new HBox( new HTMLNode( "The number<br><center>" + integer + "</center>" ), new SphericalNode( 20, colors[integer], false ) );
                    }
                } ) {{
                    setOffset( 100, 300 );
                }} );
                setZoomEventHandler( getZoomEventHandler() );
            }} );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
            setSize( 800, 600 );
            centerWindowOnScreen( this );
        }}.setVisible( true );
    }
}