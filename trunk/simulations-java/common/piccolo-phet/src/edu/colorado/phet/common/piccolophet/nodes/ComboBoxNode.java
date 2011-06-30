package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.common.phetcommon.view.util.SwingUtils.centerWindowOnScreen;

/**
 * Piccolo combo box.  This is written as a pure piccolo component since the PComboBox + PSwing was awkward to use and not rendering the pop-up in the correct location. See #2982
 * TODO: We still need to find a way to make the popup disappear when the user clicks outside of the popup (whether on the parent JComponent background or on another PNode).
 *
 * @author Sam Reid
 */
public class ComboBoxNode<T> extends PNode {

    /**
     * Property that can be used to view or set the currently selected item.  Note that the selected item does not need to be in the list of available items
     */
    public final Property<T> selectedItem;

    //Font to use for showing the selected item as well as the list of available items
    private final PhetFont itemFont;

    //Node to show the selected item, with a triangle for a drop-down box.  Clicking anywhere in this node will show the popup
    private PNode selectedItemNode;

    //Node to show for the popup
    private PNode popup;

    //Nodes that we are listening to for dismissing the popup, so that if the user clicks away from the shown popup, it will hide
    private ArrayList<PNode> listened = new ArrayList<PNode>();

    //Listener that will hide the popup when the user clicks away from it
    private PBasicInputEventHandler popupHider = new PBasicInputEventHandler() {
        @Override public void mousePressed( PInputEvent event ) {
            hidePopup();
        }
    };

    /**
     * Create a CombBoxNode with the specified items, using the toString function to create strings for each item
     *
     * @param items the items to show in the combo box
     */
    public ComboBoxNode( List<T> items ) {
        this( items, new Function1<T, String>() {
            public String apply( T t ) {
                return t.toString();
            }
        } );
    }

    /**
     * Create a ComboBoxNode with the specified items and specified way to convert the items to strings
     *
     * @param items            items the items to show in the combo box
     * @param toStringFunction the function to use to convert the T items to strings
     */
    public ComboBoxNode( final List<T> items, final Function1<T, String> toStringFunction ) {
        itemFont = new PhetFont( 18 );

        //Default to use the first item as the selected item
        selectedItem = new Property<T>( items.get( 0 ) );

        //Create the text nodes for the drop down box for determining their metrics (so they can all be created with equal widths)
        PText[] textNodes = new PText[items.size()];
        double maxWidth = 0;
        for ( int i = 0; i < items.size(); i++ ) {
            textNodes[i] = new PText( toStringFunction.apply( items.get( i ) ) ) {{
                setFont( itemFont );
                //Let pick events fall through to the background so the entire width of the ptext is selectable
                setPickable( false );
                setChildrenPickable( false );
            }};
            maxWidth = Math.max( maxWidth, textNodes[i].getFullBounds().getWidth() );
        }

        //Create the items in the drop down list
        PNode[] choices = new PNode[items.size()];
        for ( int i = 0; i < items.size(); i++ ) {
            choices[i] = new ListItem<T>( items.get( i ), textNodes[i], maxWidth ) {{
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
                setPickable( isVisible );
                setChildrenPickable( isVisible );
                if ( isVisible ) {
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            listenForClicks( getRoot() );
                        }
                    } );
                }
            }
        };
        addChild( popup );

        //When the selected item changes, show the new selected item.  This is done by discarding the old selected item node and replacing it with a new one.
        //Since this uses automatic callbacks in observer binding, this also creates the selected item node on initialization
        final double finalMaxWidth = maxWidth;
        selectedItem.addObserver( new VoidFunction1<T>() {
            public void apply( T t ) {
                removeChild( selectedItemNode );
                selectedItemNode = new SelectedItemNode( toStringFunction.apply( selectedItem.get() ), finalMaxWidth );
                addChild( selectedItemNode );
            }
        } );

        //Show the popup beneath the selected item displayer
        popup.setOffset( 0, selectedItemNode.getFullBounds().getHeight() + 2 );
    }

    //Recursively listen to the specified node and its subtree, so that when the user clicks away from the popup it will hide
    private void listenForClicks( PNode node ) {
        if ( node != this && node != popup ) {
            ListIterator it = node.getChildrenIterator();
            while ( it.hasNext() ) {
                PNode next = (PNode) it.next();
                if ( next.getVisible() && next.getPickable() ) {
                    next.addInputEventListener( popupHider );
                    listened.add( next );
                    listenForClicks( next );
                }
            }
        }
    }

    //Hide the popup and unregister all registered listeners
    private void hidePopup() {
        popup.setVisible( false );
        for ( PNode pNode : listened ) {
            pNode.removeInputEventListener( popupHider );
        }
        listened.clear();
    }

    //Create the graphic to use to show the currently selected item, which allows the user to show the popup of other choices
    private class SelectedItemNode extends ControlPanelNode {
        public SelectedItemNode( String text, double maxWidth ) {
            super( new HBox(
                    new TextWithBackground( new PText( text ) {{
                        setFont( itemFont );
                    }}, maxWidth ),

                    //Show the triangle that looks customary for ComboBoxes
                    new PNode() {{
                        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 20, 20 ), Color.lightGray ) );
                        addChild( new PhetPPath( new DoubleGeneralPath( 2, 8 ) {{
                            lineToRelative( 8, 8 );
                            lineToRelative( 8, -8 );
                            closePath();
                        }}.getGeneralPath(), Color.darkGray ) );
                    }}
            ), Color.white, new BasicStroke( 1 ), Color.black, 3, 5, false );

            //When clicked, toggle whether the popup is visible
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    popup.setVisible( !popup.getVisible() );
                }
            } );
            addInputEventListener( new CursorHandler() );
        }
    }

    //Shows a text with an optional background.  This makes it so the PText fills the specified width and so that the entire background highlights when moused over.
    private static class TextWithBackground extends PNode {
        private PhetPPath background;

        public TextWithBackground( PText text, double width ) {
            final Color plainColor = Color.white;
            background = new PhetPPath( new Rectangle2D.Double( -2, 0, width + 2 + 2, text.getFullBounds().getHeight() ), plainColor );
            background.setStroke( new BasicStroke( 1 ) );
            background.setStrokePaint( null );
            addChild( background );
            addChild( text );
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

        public ListItem( T item, final PText ptext, double width ) {
            this.item = item;
            final TextWithBackground text = new TextWithBackground( ptext, width );
            final Color highlightColor = new Color( 84, 226, 243 );
            addChild( text );

            //Highlight when moused over
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseEntered( PInputEvent event ) {
                    text.setPaint( highlightColor );
                    text.setStrokePaint( Color.darkGray );
                }

                @Override public void mouseExited( PInputEvent event ) {
                    text.setPaint( Color.white );
                    text.setStrokePaint( null );
                }
            } );
            addInputEventListener( new CursorHandler() );
        }
    }

    //Test application
    public static void main( String[] args ) {
        new JFrame() {{
            setContentPane( new PhetPCanvas() {{
                setBackground( Color.blue );
                addScreenChild( new ComboBoxNode<String>( Arrays.asList( "sugar", "salt", "ethanol", "sodium nitrate" ) ) {{
                    setOffset( 100, 100 );
                }} );
                addScreenChild( new TextButtonNode( "Unrelated button" ) {{
                    setOffset( 400, 100 );
                }} );
                setZoomEventHandler( getZoomEventHandler() );
            }} );
            setDefaultCloseOperation( EXIT_ON_CLOSE );
            setSize( 800, 600 );
            centerWindowOnScreen( this );
        }}.setVisible( true );
    }
}