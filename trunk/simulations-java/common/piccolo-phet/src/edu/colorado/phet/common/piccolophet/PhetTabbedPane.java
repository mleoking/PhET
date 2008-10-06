/* Copyright 2003-2008, University of Colorado */

package edu.colorado.phet.common.piccolophet;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.view.LogoPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.ToolTipHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * The PhetTabbedPane is a Piccolo implementation of a tabbed pane.  In general, the interface resembles JTabbedPane.
 * This class is meant to be visually appealing, and to provide fine-grained control over the look and feel of the tabs,
 * including gradients, overlapping, hand cursors, etc.
 * <p/>
 * At most one tab can be 'selected' at a time.
 *
 * @author Sam Reid
 */

public class PhetTabbedPane extends JPanel {

    public static final String IMAGE_PHET_LOGO = LogoPanel.IMAGE_PHET_LOGO;

    public static final Font DEFAULT_TAB_FONT = new PhetFont( 16, true /* bold */ );

    private TabPane tabPane;
    /**
     * A piccolo canvas that displays the tabs
     */
    private JComponent component;
    /**
     * The Swing component that corresponds to the selected tab.
     */
    private Color selectedTabColor;
    private ArrayList changeListeners = new ArrayList();
    private Font tabFont = DEFAULT_TAB_FONT;
    private TabNodeFactory tabNodeFactory = new TabNodeFactory();

    /**
     * The default foreground color for the selected tab.
     */
    public static final Color DEFAULT_SELECTED_TAB_COLOR = new Color( 150, 150, 255 );

    /**
     * Constructs a PhetTabbedPane using the default color scheme.
     */
    public PhetTabbedPane() {
        this( DEFAULT_SELECTED_TAB_COLOR );
    }

    /**
     * Constructs a PhetTabbedPane with the specified color for the selected tab.
     *
     * @param selectedTabColor
     */
    public PhetTabbedPane( Color selectedTabColor ) {
        super( new BorderLayout() );
        this.selectedTabColor = selectedTabColor;
        component = new JPanel();//empty component to start
        tabPane = new TabPane( selectedTabColor );
        add( tabPane, BorderLayout.NORTH );
        setComponent( component );
        ComponentListener relayoutHandler = new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                relayoutComponents();
            }

            public void componentShown( ComponentEvent e ) {
                relayoutComponents();
            }
        };
        addComponentListener( relayoutHandler );
    }

    public void setLogoVisible( boolean logoVisible ) {
        tabPane.setLogoVisible( logoVisible );
    }

    /**
     * Returns the number of tabs.
     *
     * @return the number of tabs.
     */
    public int getTabCount() {
        return tabPane.getTabCount();
    }

    /**
     * Sets the bounds of each tab JComponent to mimic behavior in JTabbedPane
     */
    private void relayoutComponents() {
//todo I originally believed this to be necessary in QWI to get tabs content JComponent to appear at the right size the first time they appear, but this code causes an error in 1.5: no content JComponent shows initially.
//todo This fixed version works correctly for the TestPhetTabbedPane test under 1.4 and 1.5, so maybe the fault was originally with QWI.  This warrants further investigation.
        Rectangle bounds = component.getBounds();
        for ( int i = 0; i < getTabCount(); i++ ) {
            tabPane.getTabs()[i].getComponent().setSize( bounds.width, bounds.height );//to mimic behavior in JTabbedPane
            updateLayout( tabPane.getTabs()[i].getComponent() );
        }
    }

    /**
     * Todo: Identify why this workaround is necessary and delete it.
     * <p/>
     * This is a utility function for ensuring that all layouts in this component hierarchy
     * have a chance to reflect their new parent's sizes.
     * <p/>
     * We added this to solve the problem visible in bound states on 6-21-2006 5:24pm that
     * when changing tabs, there would be a visible resize after selection.
     *
     * @param component
     */
    private void updateLayout( Component component ) {
        component.doLayout();
        if ( component instanceof Container ) {
            Container c = (Container) component;
            for ( int i = 0; i < c.getComponentCount(); i++ ) {
                updateLayout( c.getComponent( i ) );
            }
        }
    }

    /**
     * Removes the tab at the specified index.  If the removed tab was selected and there are still remaining tabs,
     * a new tab becomes selected.  If one exists, the next tab will become selected, otherwise the previous tab becomes selected.
     *
     * @param i
     */
    public void removeTabAt( int i ) {
        AbstractTabNode tabToRemove = getTab( i );
        AbstractTabNode selectedTab = getSelectedTab();

        tabPane.removeTabAt( i );
        if ( selectedTab == tabToRemove && getTabCount() > 0 ) {//need to set a new selected tab.
            if ( i < getTabCount() ) {
                setSelectedIndex( i );
            }
            else {
                setSelectedIndex( i - 1 );
            }
        }
    }

    /**
     * Adds a ChangeListener to observe changes in tab selection.
     *
     * @param changeListener
     */
    public void addChangeListener( ChangeListener changeListener ) {
        changeListeners.add( changeListener );
    }

    /**
     * Returns the index of the selected tab.
     *
     * @return the index of the selected tab.
     */
    public int getSelectedIndex() {
        return tabPane.getSelectedIndex();
    }

    /**
     * Sets the base foreground color for selected tabs.
     *
     * @param color
     */
    public void setSelectedTabColor( Color color ) {
        this.selectedTabColor = color;
        tabPane.setSelectedTabColor( color );
    }

    /**
     * Gets the base foreground color for selected tabs.
     *
     * @return Color
     */
    public Color getSelectedTabColor() {
        return selectedTabColor;
    }

    /**
     * Adds the text or HTML label as a tab for the specified component.  If this is the first tab, it becomes selected.
     *
     * @param title   text or HTML
     * @param content
     */
    public void addTab( String title, JComponent content ) {
        final AbstractTabNode tab = createTab( title, content );
        tab.addInputEventListener( new TabInputListener( tab ) );
        if ( tabPane.getTabs().length == 0 ) {
            setSelectedTab( tab );
            tabPane.setSelectedTab( tab );
        }
        else {
            tab.setSelected( false );
        }
        tabPane.addTab( tab );
        relayoutComponents();
    }

    /**
     * Creates an AbstractTabNode from the specified title and component.  This implementation uses the
     * TabNodeFactory.  This implementation can be overriden.
     *
     * @param title
     * @param content
     * @return the AbstractTabNode
     */
    protected AbstractTabNode createTab( String title, JComponent content ) {
        return tabNodeFactory.createTabNode( title, content, selectedTabColor, tabFont );
    }

    /**
     * Sets the selected tab to be the one with the specified index.
     *
     * @param index
     */
    public void setSelectedIndex( int index ) {
        if ( index < 0 || index >= getTabCount() ) {
            throw new RuntimeException( "Illegal tab index: " + index + ", tab count=" + getTabCount() );
        }
        setSelectedTab( tabPane.getTabs()[index] );
    }

    /**
     * Sets the selected tab.  Should not be used by normal clients.
     *
     * @param tab
     */
    protected void setSelectedTab( AbstractTabNode tab ) {
        setComponent( tab.getComponent() );
        tab.setSelected( true );
        for ( int i = 0; i < tabPane.getTabs().length; i++ ) {
            AbstractTabNode t = tabPane.getTabs()[i];
            if ( t != tab ) {
                t.setSelected( false );
            }
        }
        tabPane.setSelectedTab( tab );
        notifySelectionChanged();
    }

    /**
     * Sends a message to change listeners that the selected tab changed.  This method is called internally, and should
     * not normally be called directly.
     */
    private void notifySelectionChanged() {
        ChangeEvent changeEvent = new ChangeEvent( this );
        for ( int i = 0; i < changeListeners.size(); i++ ) {
            ChangeListener changeListener = (ChangeListener) changeListeners.get( i );
            changeListener.stateChanged( changeEvent );
        }
    }

    /**
     * Returns the selected tab object, may be null.
     *
     * @return selected tab object, may be null.
     */
    private AbstractTabNode getSelectedTab() {
        return tabPane.getSelectedTab();
    }

    /**
     * Sets the main body component of the PhetTabbedPane.
     *
     * @param component
     */
    private void setComponent( final JComponent component ) {
        if ( component != this.component ) {
            if ( this.component != null ) {
                remove( this.component );
            }
            this.component = component;
            add( component, BorderLayout.CENTER );
            revalidate();
            repaint();
        }
    }

    /**
     * Returns the tab at the specified index.
     *
     * @param i
     * @return the tab at the specified index.
     */
    public AbstractTabNode getTab( int i ) {
        return tabPane.getTab( i );
    }

    /**
     * Sets the font for all tabs.
     *
     * @param font
     */
    public void setTabFont( Font font ) {
        this.tabFont = font;
        for ( int i = 0; i < getTabCount(); i++ ) {
            getTab( i ).setFont( font );
        }
        tabPane.relayout();
        revalidate();
    }

    /**
     * Returns the text of the specified tab.
     *
     * @param i the tab index
     * @return the text of the specified tab.
     */
    public String getTitleAt( int i ) {
        return getTab( i ).getText();
    }

    public boolean getLogoVisible() {
        return tabPane.getLogoVisible();
    }

    /**
     * Determines what concrete subtype of AbstractTabNodes are used.
     */
    public static class TabNodeFactory {
        public AbstractTabNode createTabNode( String text, JComponent component, Color selectedTabColor, Font tabFont ) {
            return new HTMLTabNode( text, component, selectedTabColor, tabFont );
        }
    }

    /**
     * Sets the TabNodeFactory used to createPrimaryBorder subsequent tab nodes.
     *
     * @param tabNodeFactory
     */
    public void setTabNodeFactory( TabNodeFactory tabNodeFactory ) {
        this.tabNodeFactory = tabNodeFactory;
    }

    /**
     * This AbstractTabNode renders HTML.
     */
    public static class HTMLTabNode extends AbstractTabNode {
        private HTMLNode htmlNode;

        public HTMLTabNode( String text, JComponent component, Color selectedTabColor, Font tabFont ) {
            super( text, component, selectedTabColor, tabFont );
        }

        protected PNode createTextNode( String text, Color selectedTabColor ) {
            this.htmlNode = new HTMLNode( text, getTabFont(), Color.black );
            return this.htmlNode;
        }

        protected void updateTextNode() {
            this.htmlNode.setFont( getTabFont() );
            this.htmlNode.setHTMLColor( (Color) getTextPaint() );
        }

        public void updateFont( Font font ) {
            htmlNode.setFont( font );
            updateShape();
        }
    }

    /**
     * This AbstractTabNode renders text (non-html).
     */
    public static class TextTabNode extends AbstractTabNode {
        private PText pText;

        public TextTabNode( String text, JComponent component, Color selectedTabColor, Font tabFont ) {
            super( text, component, selectedTabColor, tabFont );
        }

        protected PNode createTextNode( String text, Color selectedTabColor ) {
            this.pText = new PText( text );
            this.pText.setFont( getTabFont() );
            return pText;
        }

        protected void updateTextNode() {
            pText.setFont( getTabFont() );
            pText.setTextPaint( getTextPaint() );
        }

        public void updateFont( Font font ) {
            pText.setFont( font );
            super.updateShape();
        }
    }

    /**
     * The AbstractTabNode is the graphic PNode for one tab.
     */
    public static abstract class AbstractTabNode extends PNode {
        private String text;/*The text for the tab*/
        private JComponent component;/*The swing component associated with this tab.*/
        private PNode textNode;/*The PNode that draws the text*/
        private PhetPPath background; /*Draws the gradient background, use PhetPPath for Gradient workaround on Mac OS 10.4 */
        private boolean selected;/*True if this tab is selected.*/
        private float tiltWidth = 11; /*Amount the tab sticks out over adjacent tabs*/
        private Color selectedTabColor; /*Color when selected*/
        private PPath outlineNode; /*Draws the tab outline.*/
        private Font tabFont;
        private boolean textIsCentered = true;

        private static final Insets tabInsets = new Insets( 2, 15, 0, 15 );/* Insets for the text in the tab.*/

        public AbstractTabNode( String text, JComponent component, Color selectedTabColor, Font tabFont ) {
            this.tabFont = tabFont;
            this.selectedTabColor = selectedTabColor;
            this.text = text;
            this.component = component;
            textNode = createTextNode( text, selectedTabColor );
            outlineNode = new PPath( createTabTopBorder( textNode.getFullBounds().getWidth(), textNode.getFullBounds().getHeight() ) );
            background = new PhetPPath( createTabShape( textNode.getFullBounds().getWidth(), textNode.getFullBounds().getHeight() ) );
            background.setPaint( selectedTabColor );
            background.setStroke( null );
            addChild( background );
            addChild( textNode );
            addChild( outlineNode );
        }

        public void updateShape() {
            outlineNode.setPathTo( createTabTopBorder( textNode.getFullBounds().getWidth(), textNode.getFullBounds().getHeight() ) );
            background.setPathTo( createTabShape( textNode.getFullBounds().getWidth(), textNode.getFullBounds().getHeight() ) );
        }

        protected abstract PNode createTextNode( String text, Color selectedTabColor );

        protected abstract void updateTextNode();

        public void setTabTextHeight( double tabHeight ) {
            background.setPathTo( createTabShape( textNode.getFullBounds().getWidth(), tabHeight ) );
            outlineNode.setPathTo( createTabTopBorder( textNode.getFullBounds().getWidth(), tabHeight ) );
            if ( textIsCentered ) {
                textNode.setOffset( 0, tabHeight / 2 - textNode.getHeight() / 2 );
            }
        }

        /**
         * Creates an outline over just the sides and top of the tab.
         *
         * @param textWidth
         * @param textHeight
         * @return the path.
         */
        private GeneralPath createTabTopBorder( double textWidth, double textHeight ) {
            GeneralPath outline = new GeneralPath();
            outline.moveTo( -tabInsets.left, (float) ( textHeight + tabInsets.bottom ) );
            outline.lineTo( -tabInsets.left, -tabInsets.top );
            outline.lineTo( (float) ( textWidth + tabInsets.right ), -tabInsets.top );
            outline.lineTo( (float) textWidth + tabInsets.right + tiltWidth, (float) ( textHeight + tabInsets.bottom ) );
            return outline;
        }

        /**
         * Creates a path that goes entirely around the tab graphic.
         *
         * @param textWidth
         * @param textHeight
         * @return the path.
         */
        private GeneralPath createTabShape( double textWidth, double textHeight ) {
            GeneralPath path = new GeneralPath();
            path.moveTo( -tabInsets.left, -tabInsets.top );
            path.lineTo( (float) ( textWidth + tabInsets.right ), -tabInsets.top );
            path.lineTo( (float) textWidth + tabInsets.right + tiltWidth, (float) ( textHeight + tabInsets.bottom ) );
            path.lineTo( -tabInsets.left, (float) ( textHeight + tabInsets.bottom ) );
            path.closePath();
            return path;
        }

        /**
         * Returns the swing component associated with this tab.
         *
         * @return the swing component associated with this tab.
         */
        public JComponent getComponent() {
            return component;
        }

        /**
         * Sets whether this tab and its associated component are selected.
         *
         * @param selected
         */
        public void setSelected( boolean selected ) {
            this.selected = selected;
            updateTextNode();
            background.setStroke( getBorderStroke() );
            outlineNode.setVisible( selected );
            updatePaint();
        }

        /**
         * Returns whether this tab is selected.
         *
         * @return true if this tab is selected.
         */
        public boolean isSelected() {
            return selected;
        }

        private void updatePaint() {
            background.setPaint( getBackgroundPaint() );
            background.setStrokePaint( getBorderStrokePaint() );
        }

        private Paint getBorderStrokePaint() {
            return Color.gray;
        }

        private Stroke getBorderStroke() {
            return ( !selected ) ? new BasicStroke( 1.0f ) : null;
        }

        private Paint getBackgroundPaint() {
            if ( selected ) {
                return new GradientPaint( 0, (float) background.getFullBounds().getY() - 2, selectedTabColor.brighter(), 0, (float) ( background.getFullBounds().getY() + 6 ), selectedTabColor );
            }
            else {
                return new GradientPaint( 0, 0, new Color( 240, 240, 240 ), 0, 30, new Color( 200, 200, 200 ) );//grayed out
            }
        }

        protected Paint getTextPaint() {
            return selected ? Color.black : Color.darkGray;
        }

        public Font getTabFont() {
            return tabFont;
        }

        public String getText() {
            return text;
        }

        public void setSelectedTabColor( Color color ) {
            this.selectedTabColor = color;
            updatePaint();
        }

        public double getTextHeight() {
            return textNode.getFullBounds().getHeight();
        }

        public void setFont( Font font ) {
            this.tabFont = font;
            updateFont( tabFont );
        }

        protected abstract void updateFont( Font tabFont );

    }

    /**
     * The TabBase is a PNode graphic for the horizontal bar under the tabs.
     */
    public static class TabBase extends PNode {
        private final PhetPPath path; // use PhetPPath for Gradient workaround on Mac OS 10.4
        private int tabBaseHeight = 6;
        private Color selectedTabColor;

        public TabBase( Color selectedTabColor ) {
            this.selectedTabColor = selectedTabColor;
            path = new PhetPPath( new Rectangle( 0, 0, 200, tabBaseHeight ) );
            path.setPaint( selectedTabColor );
            path.setPaint( new GradientPaint( 0, 0, selectedTabColor, 0, tabBaseHeight + 4, selectedTabColor.darker() ) );
            path.setStroke( null );
            addChild( path );
            updatePaint();
        }

        public void setTabBaseWidth( int width ) {
            path.setPathTo( new Rectangle( 0, 0, width, tabBaseHeight ) );
        }

        public void updatePaint() {
            path.setPaint( new GradientPaint( 0, 0, selectedTabColor, 0, tabBaseHeight, darker( selectedTabColor, 75 ) ) );
        }

        public void setSelectedTabColor( Color color ) {
            this.selectedTabColor = color;
            updatePaint();
        }
    }

    private static int darker( int value, int d ) {
        return Math.max( 0, value - d );
    }

    /**
     * Returns a new Color that is darker by the specified amount in each R G and B.
     *
     * @param a
     * @param d
     * @return the new Color.
     */
    private static Color darker( Color a, int d ) {
        return new Color( darker( a.getRed(), d ), darker( a.getGreen(), d ), darker( a.getBlue(), d ) );
    }

    /**
     * The TabPane is the Piccolo PCanvas container for AbstractTabNode PNodes.
     */
    public static class TabPane extends PSwingCanvas {
        private ArrayList tabs = new ArrayList();
        private double distBetweenTabs = -6;
        private TabBase tabBase;
        private int tabTopInset = 3;
        private PImage logo;
        private AbstractTabNode selectedTab;
        private static final int LEFT_TAB_INSET = 10;
        private boolean logoObscured = false;
        private boolean logoVisible = true;

        public TabPane( Color selectedTabColor ) {
            Image image = PhetCommonResources.getInstance().getImage( IMAGE_PHET_LOGO );
            logo = new PImage( image );
            logo.addInputEventListener( new CursorHandler() );
            logo.addInputEventListener( new ToolTipHandler( PhetCommonResources.getInstance().getLocalizedString( "Common.About.WebLink" ), this ) );
            logo.addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    PhetServiceManager.showPhetPage();
                }
            } );
            tabBase = new TabBase( selectedTabColor );
            setPanEventHandler( null );
            setZoomEventHandler( null );
            setOpaque( false );

            getLayer().addChild( logo );
            getLayer().addChild( tabBase );
            addComponentListener( new ComponentListener() {
                public void componentHidden( ComponentEvent e ) {
                }

                public void componentMoved( ComponentEvent e ) {
                }

                public void componentResized( ComponentEvent e ) {
                    relayout();
                }

                public void componentShown( ComponentEvent e ) {
                    relayout();
                }
            } );
            relayout();
        }

        public AbstractTabNode getSelectedTab() {
            return selectedTab;
        }

        /**
         * Adds the specified tab- if this is the first tab, it becomes selected.
         *
         * @param tab
         */
        public void addTab( AbstractTabNode tab ) {
            tabs.add( tab );
            getLayer().addChild( 0, tab );
            relayout();
            setSelectedTab( getSelectedTab() == null ? tab : getSelectedTab() );//updates
        }

        private void relayout() {
            tabBase.setTabBaseWidth( getWidth() );
            int x = AbstractTabNode.tabInsets.left + LEFT_TAB_INSET;
            double maxTabTextHeight = getMaxTabTextHeight();
            for ( int i = 0; i < tabs.size(); i++ ) {
                AbstractTabNode tabNode = (AbstractTabNode) tabs.get( i );
                tabNode.setOffset( x, tabTopInset );
                tabNode.setTabTextHeight( maxTabTextHeight );
                x += tabNode.getFullBounds().getWidth() + distBetweenTabs;
            }
            double tabBaseY = getHeight() - tabBase.getFullBounds().getHeight();
            tabBase.setOffset( 0, tabBaseY );
            relayoutLogo( tabBaseY );
            for ( int i = 0; i < tabs.size(); i++ ) {
                getTab( i ).updatePaint();
            }
            tabBase.updatePaint();
        }

        private void relayoutLogo( double tabBaseY ) {
            if ( logo.getImage().getHeight( null ) > getHeight() ) {
                double scale = ( getHeight() - 5.0 ) / ( (double) logo.getImage().getHeight( null ) );
                logo.setScale( Math.max( scale, 0.5 ) );
            }
            else {
                logo.setScale( 1 );
            }
            logo.setOffset( getWidth() - logo.getFullBounds().getWidth(), tabBaseY / 2 - logo.getFullBounds().getHeight() / 2 );

            if ( tabs.size() > 0 ) {
                AbstractTabNode lastTab = (AbstractTabNode) tabs.get( tabs.size() - 1 );
                if ( logo.getXOffset() < lastTab.getFullBounds().getMaxX() ) {
                    logoObscured = true;
                    updateLogoVisible();
                }
                else {
                    logoObscured = false;
                    updateLogoVisible();
                }
            }
        }

        public Dimension getPreferredSize() {
            relayout();
            int h = getMaxTabHeight();
            int width = (int) getLayer().getFullBounds().getWidth();
            width = Math.max( width, super.getPreferredSize().width );
            return new Dimension( width, (int) ( h + tabBase.getFullBounds().getHeight() ) );
        }

        private double getMaxTabTextHeight() {
            double h = 0;
            for ( int i = 0; i < tabs.size(); i++ ) {
                AbstractTabNode tabNode = (AbstractTabNode) tabs.get( i );
                h = Math.max( h, tabNode.getTextHeight() ) + getTabInsetFudgeFactor();
            }
            return h;
        }

        private double getTabInsetFudgeFactor() {
            if ( System.getProperty( "os.name" ).toLowerCase().indexOf( "mac os x" ) >= 0 ) {
                return 2;
            }
            else {
                return 0;
            }
        }

        private int getMaxTabHeight() {
            int h = 0;
            for ( int i = 0; i < tabs.size(); i++ ) {
                AbstractTabNode tabNode = (AbstractTabNode) tabs.get( i );
                h = (int) Math.max( h, tabNode.getFullBounds().getHeight() );
            }
            return h;
        }

        /**
         * Returns the tabs in this TabPane.
         *
         * @return the tabs in this TabPane.
         */
        public AbstractTabNode[] getTabs() {
            return (AbstractTabNode[]) tabs.toArray( new AbstractTabNode[0] );
        }

        /**
         * Sets the selected tab.
         *
         * @param tab
         */
        public void setSelectedTab( AbstractTabNode tab ) {
            this.selectedTab = tab;
            /*Ensure the tabs appear in the z-order in which they are listed.*/
            for ( int i = 0; i < getTabs().length; i++ ) {
                if ( getLayer().getChildrenReference().contains( getTabs()[i] ) ) {
                    getLayer().removeChild( getTabs()[i] );
                }
            }
            for ( int i = getTabs().length - 1; i >= 0; i-- ) {
                if ( getTabs()[i] != tab ) {
                    getLayer().addChild( getTabs()[i] );
                }
            }
            /*Then show the TabBase*/
            getLayer().removeChild( tabBase );
            getLayer().addChild( tabBase );
            /**Last, show the selected tab on top.*/
            getLayer().addChild( tab );
        }

        public int getSelectedIndex() {
            return tabs.indexOf( selectedTab );
        }

        public int getTabCount() {
            return tabs.size();
        }

        public String getTitleAt( int i ) {
            return getTab( i ).getText();
        }

        protected AbstractTabNode getTab( int i ) {
            return (AbstractTabNode) tabs.get( i );
        }

        public void removeTabAt( int i ) {
            tabs.remove( i );
        }

        /**
         * Sets what the color should be when this tab is selected.
         *
         * @param color
         */
        public void setSelectedTabColor( Color color ) {
            for ( int i = 0; i < tabs.size(); i++ ) {
                AbstractTabNode tabNode = (AbstractTabNode) tabs.get( i );
                tabNode.setSelectedTabColor( color );
            }
            tabBase.setSelectedTabColor( color );
        }

        public void setLogoVisible( boolean logoVisible ) {
            this.logoVisible = logoVisible;
            updateLogoVisible();
        }

        public boolean getLogoVisible() {
            return logoVisible;
        }

        private void updateLogoVisible() {
            logo.setVisible( logoVisible && !logoObscured );
        }

    }

    /**
     * Handles input events to the tab, including selection events and cursor changes.
     */
    protected class TabInputListener extends PBasicInputEventHandler {
        private AbstractTabNode tab;

        public TabInputListener( AbstractTabNode tab ) {
            this.tab = tab;
        }

        public void mouseReleased( PInputEvent e ) {
            if ( tab.getFullBounds().contains( e.getCanvasPosition() ) ) {
                setSelectedTab( tab );
            }
        }

        public void mouseEntered( PInputEvent event ) {
            PhetTabbedPane.this.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }

        public void mouseExited( PInputEvent event ) {
            PhetTabbedPane.this.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
        }
    }

}