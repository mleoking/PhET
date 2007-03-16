/* ====================================================================
 * Copyright (c) 2001-2003 OYOAHA. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. The names "OYOAHA" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. 
 *    For written permission, please contact email@oyoaha.com.
 *
 * 3. Products derived from this software may not be called "OYOAHA",
 *    nor may "OYOAHA" appear in their name, without prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL OYOAHA OR ITS CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oyoaha.swing.plaf.oyoaha;

import com.oyoaha.swing.plaf.oyoaha.border.*;
import com.oyoaha.swing.plaf.oyoaha.icon.OyoahaCheckBoxIcon;
import com.oyoaha.swing.plaf.oyoaha.icon.OyoahaIcon;
import com.oyoaha.swing.plaf.oyoaha.icon.OyoahaRadioButtonIcon;
import com.oyoaha.swing.plaf.oyoaha.icon.OyoahaSliderThumbIcon;
import com.oyoaha.swing.plaf.oyoaha.pool.OyoahaBumpPool;
import com.oyoaha.swing.plaf.oyoaha.pool.OyoahaIconPool;
import com.oyoaha.swing.plaf.oyoaha.ui.OyoahaListCellRendererUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class OyoahaLookAndFeel extends MetalLookAndFeel {
    protected OyoahaTheme oyoahaTheme;
    protected OyoahaThemeScheme oyoahaThemeScheme;
    protected boolean forceRollover;

    public OyoahaLookAndFeel() {
        this( true );
    }

    public OyoahaLookAndFeel( boolean forceRollover ) {
        if( oyoahaThemeScheme == null ) {
            oyoahaThemeScheme = new OyoahaThemeScheme();
        }

        oyoahaTheme = null;

        this.forceRollover = forceRollover;
    }

    public boolean getDefaultRolloverPolicy( Component c ) {
        if( c instanceof MenuElement ) {
            return true;
        }

        return forceRollover;
    }

    public void initialize() {
        OyoahaUtilities.initialize( this );
    }

    /**
     *
     */
    public void uninitialize() {
        OyoahaUtilities.uninitialize( this );
    }

    /**
     *
     */
    public String getName() {
        return "Oyoaha LookAndFeel";
    }

    /**
     *
     */
    public String getID() {
        return "3.0";
    }

    /**
     *
     */
    public String getDescription() {
        StringBuffer buf = new StringBuffer();

        buf.append( "For more information visit http://www.oyoaha.com" );
        buf.append( "\n  Copyright (c) 2000, 2002 oyoaha" );

        if( oyoahaTheme != null ) {
            buf.append( "\n  Use custom OyoahaTheme" );

            buf.append( "\n\n  OyoahaTheme Class Name: " );
            buf.append( oyoahaTheme.getClassName() );
            buf.append( "\n  OyoahaTheme Name: " );
            buf.append( oyoahaTheme.getName() );
            buf.append( "\n  OyoahaTheme Version: " );
            buf.append( oyoahaTheme.getVersion() );
            buf.append( "\n  OyoahaTheme Copyright: " );
            buf.append( oyoahaTheme.getCopyright() );
        }
        else {
            buf.append( "\n  Use default OyoahaTheme" );
        }

        return buf.toString();
    }

//------------------------------------------------------------------------------
// THEME SUPPORT
//------------------------------------------------------------------------------

    public static void setCurrentTheme( URL _url ) {
        setCurrentTheme( new OyoahaMetalTheme( _url ) );
    }

    public static void setCurrentTheme( InputStream _inputStream ) {
        setCurrentTheme( new OyoahaMetalTheme( _inputStream ) );
    }

    public static void setCurrentTheme( File _file ) {
        setCurrentTheme( new OyoahaMetalTheme( _file ) );
    }

    public void setOyoahaTheme( URL _url ) {
        System.out.println( "set oyoaha theme: " + _url );
        OyoahaTheme otm = null;

        if( _url != null ) {
            otm = new OyoahaTheme( _url );
        }

        setOyoahaTheme( otm );
    }

    public void setOyoahaTheme( InputStream _inputStream ) {
        OyoahaTheme otm = null;

        if( _inputStream != null ) {
            otm = new OyoahaTheme( _inputStream );
        }

        setOyoahaTheme( otm );
    }

    public void setOyoahaTheme( File _file ) {
        OyoahaTheme otm = null;

        if( _file != null || _file.exists() ) {
            otm = new OyoahaTheme( _file );
        }

        setOyoahaTheme( otm );
    }

    protected void setOyoahaTheme( OyoahaTheme _oyoahaTheme ) {
        if( oyoahaTheme != null ) {
            //dispose oyoahaTheme
            disposeOyoahaThemeSchemeListener();
            oyoahaTheme.dispose();
        }

        oyoahaTheme = _oyoahaTheme;

        //check is the current scheme is also the default one
        if( oyoahaThemeScheme.isDefaultMetalTheme() || oyoahaThemeScheme.isDefaultOyoahaTheme() ) {
            //ok load default...
            if( oyoahaTheme != null ) {
                OyoahaThemeSchemeChanged changed = oyoahaTheme.loadDefaultOyoahaThemeScheme( oyoahaThemeScheme );
                fireOyoahaThemeSchemeListener( oyoahaThemeScheme, changed );
            }
        }
    }

    public OyoahaTheme getOyoahaTheme() {
        return oyoahaTheme;
    }

    /**
     * return a OyoahaThemeScheme filled with value of the current MetalTheme
     */
    public OyoahaThemeScheme getOyoahaThemeScheme() {
        return oyoahaThemeScheme;
    }

    public static void setCurrentTheme( MetalTheme _theme ) {
        MetalLookAndFeel.setCurrentTheme( _theme );

        //try to find the current oyoahalookandfeel
        LookAndFeel lnf = UIManager.getLookAndFeel();

        if( lnf != null && lnf instanceof OyoahaLookAndFeel ) {
            ( (OyoahaLookAndFeel)lnf ).updateOyoahaThemeScheme( _theme );
        }
    }

    public void updateOyoahaThemeScheme( MetalTheme _theme ) {
        OyoahaThemeSchemeChanged changed = null;

        if( OyoahaThemeScheme.isDefaultMetalTheme( _theme ) ) {
            //check is the current scheme is also the default
            if( !oyoahaThemeScheme.isDefaultMetalTheme() && !oyoahaThemeScheme.isDefaultOyoahaTheme() ) {
                //ok load default...
                if( oyoahaTheme != null ) {
                    changed = oyoahaTheme.loadDefaultOyoahaThemeScheme( oyoahaThemeScheme, _theme );
                }
                else {
                    changed = oyoahaThemeScheme.load( _theme );
                }
            }
        }
        else {
            //use this metal theme
            changed = oyoahaThemeScheme.load( _theme );
        }

        //if changed !=null fire listener...
        if( changed != null ) {
            fireOyoahaThemeSchemeListener( oyoahaThemeScheme, changed );
        }
    }

    protected Vector OyoahaThemeSchemeListeners;

    public void addOyoahaThemeSchemeListener( OyoahaThemeSchemeListener _listener ) {
        if( OyoahaThemeSchemeListeners == null ) {
            OyoahaThemeSchemeListeners = new Vector();
        }

        if( _listener != null && !OyoahaThemeSchemeListeners.contains( _listener ) ) {
            OyoahaThemeSchemeListeners.addElement( _listener );
        }
    }

    public void removeOyoahaThemeSchemeListener( OyoahaThemeSchemeListener _listener ) {
        if( OyoahaThemeSchemeListeners == null ) {
            return;
        }

        OyoahaThemeSchemeListeners.removeElement( _listener );
    }

    protected void fireOyoahaThemeSchemeListener( OyoahaThemeScheme _scheme, OyoahaThemeSchemeChanged _changed ) {
        OyoahaUtilities.updateOyoahaThemeScheme( _changed );

        if( OyoahaThemeSchemeListeners == null ) {
            return;
        }

        for( int i = 0; i < OyoahaThemeSchemeListeners.size(); i++ ) {
            OyoahaThemeSchemeListener listener = (OyoahaThemeSchemeListener)OyoahaThemeSchemeListeners.elementAt( i );
            listener.updateThemeScheme( _scheme, _changed );
        }
    }

    protected void disposeOyoahaThemeSchemeListener() {
        OyoahaUtilities.updateOyoahaTheme();

        if( OyoahaThemeSchemeListeners == null ) {
            return;
        }

        for( int i = 0; i < OyoahaThemeSchemeListeners.size(); i++ ) {
            OyoahaThemeSchemeListener listener = (OyoahaThemeSchemeListener)OyoahaThemeSchemeListeners.elementAt( i );
            listener.dispose();
            OyoahaThemeSchemeListeners.removeElement( listener );
        }
    }

//------------------------------------------------------------------------------
// REAL LOOKANDFEEL STUFF
//------------------------------------------------------------------------------

    protected void initClassDefaults( UIDefaults table ) {
        super.initClassDefaults( table );

        boolean javaVersion = OyoahaUtilities.isVersion( "1.3" );


        Properties properties = new Properties();

        try {
            if( javaVersion ) {
                properties.load( srrLoadResource( "/com/oyoaha/swing/plaf/oyoaha/uidefaults2.properties" ).openStream() );
            }
            else {
                properties.load( srrLoadResource(  "/com/oyoaha/swing/plaf/oyoaha/uidefaults.properties" ).openStream() );
            }
        }
        catch( Exception e ) {

        }

        try {
            //load from a text file
            Object[] uiDefaults = new Object[properties.size() * 2];

            Enumeration keys = properties.keys();
            int count = 0;

            while( keys.hasMoreElements() ) {
                uiDefaults[count++] = keys.nextElement();
                uiDefaults[count] = properties.get( uiDefaults[count - 1] );
                count++;
            }

            table.putDefaults( uiDefaults );

            ClassLoader cl = getClass().getClassLoader();

            if( cl != null ) {
                table.put( "ClassLoader", cl );
            }
        }
        catch( Exception e ) {

        }
    }

    /**
     *
     */
    protected void initComponentDefaults( UIDefaults table ) {
        super.initComponentDefaults( table );

        //INITIALIZE BORDER

        Border buttonBorder = new OyoahaButtonBorder( 0, 3 );
        Border empty = new OyoahaEmptyBorder( 2 );
        Border empty2 = new OyoahaEmptyBorder( 0 );
        Border list = new OyoahaEmptyBorder( 1 );
        Border menu = new OyoahaMenuBorder();
        Border menuBar = new OyoahaMenuBarBorder();
        Border field = new OyoahaTextFieldBorder();
        Border frame = new OyoahaInternalFrameBorder( 0, 3 );
        Border split = new OyoahaEmptyBorder( 6 );
        Border treelist = new OyoahaTreeListRendererBorder();
        Border tableHeaderBorder = new OyoahaTableHeaderBorder();

        //END INITIALIZE BORDER
        //INITIALIZE ICON

        Icon sliderIcon = new OyoahaSliderThumbIcon();
        Icon radioButtonIcon = new OyoahaRadioButtonIcon();
        Icon checkBoxIcon = new OyoahaCheckBoxIcon();

        Object desktop = new LazyIconLoader( "rc/file_desktop.gif" );
        Object drive = new LazyIconLoader( "rc/file_hard.gif" );
        Object floppy = new LazyIconLoader( "rc/file_floppy.gif" );

        Object treeOpenIcon = new LazyIconLoader( "rc/tree_open.gif" );
        Object treeClosedIcon = new LazyIconLoader( "rc/tree_close.gif" );
        Object treeLeaf = new LazyIconLoader( "rc/tree_leaf.gif" );
        Object treeExpanded = new LazyIconLoader( "rc/tree_expa.gif" );
        Object treeCollapsed = new LazyIconLoader( "rc/tree_coll.gif" );

        Object newFolderIcon = new LazyIconLoader( "rc/icon_new.gif" );
        Object upFolderIcon = new LazyIconLoader( "rc/icon_up.gif" );
        Object sortIcon = new LazyIconLoader( "rc/icon_sort.gif" );

        //Object InternalFrameIcon = new LazyIconLoader("rc/frame_sys.gif");
        //Object InternalFrameMaximizeIcon = new LazyIconLoader("rc/frame_max.gif");
        //Object InternalFrameIconifyIcon = new LazyIconLoader("rc/frame_ico.gif");
        //Object InternalFrameMinimizeIcon = new LazyIconLoader("rc/frame_min.gif");
        //Object InternalFrameCloseIcon = new LazyIconLoader("rc/frame_close.gif");

        OyoahaIconPool p = new OyoahaIconPool( this, new ImageIcon( srrLoadResource( "rc/frame_sys.gif" ) ).getImage(), null );
        OyoahaIcon InternalFrameIcon = new OyoahaIcon( p, null );

        p = new OyoahaIconPool( this, new ImageIcon( srrLoadResource( "rc/frame_max.gif" ) ).getImage(), null );
        OyoahaIcon InternalFrameMaximizeIcon = new OyoahaIcon( p, null );

        p = new OyoahaIconPool( this, new ImageIcon( srrLoadResource( "rc/frame_ico.gif" ) ).getImage(), null );
        OyoahaIcon InternalFrameIconifyIcon = new OyoahaIcon( p, null );

        p = new OyoahaIconPool( this, new ImageIcon( srrLoadResource( "rc/frame_min.gif" ) ).getImage(), null );
        OyoahaIcon InternalFrameMinimizeIcon = new OyoahaIcon( p, null );

        p = new OyoahaIconPool( this, new ImageIcon( srrLoadResource( "rc/frame_close.gif" ) ).getImage(), null );
        OyoahaIcon InternalFrameCloseIcon = new OyoahaIcon( p, null );


        Object errorIcon = new LazyIconLoader( "rc/dial_error.gif" );
        Object informationIcon = new LazyIconLoader( "rc/dial_inform.gif" );
        Object warningIcon = new LazyIconLoader( "rc/dial_warn.gif" );
        Object questionIcon = new LazyIconLoader( "rc/dial_question.gif" );

        //END INITIALIZE ICON
        //INITIALIZE OBJECT

        Object ListCellRender = new UIDefaults.ActiveValue() {
            public Object createValue( UIDefaults table ) {
                return new OyoahaListCellRendererUI();
            }
        };

        //END INITIALIZE OBJECT
        //INITIALIZE BUMP

        OyoahaBumpPool bumpPool = new OyoahaBumpPool( this, new ImageIcon( srrLoadResource( "rc/bump.gif" ) ).getImage(), null );
        OyoahaBumpObject bump = new OyoahaBumpObject( bumpPool );

        //END INITIALIZE BUMP

        Object[] defaults =
                {
                        "oyoaha3dBorder", new Boolean( true ),

                        // *** Bump
                        "oyoahabump", bump,

                        // *** OptionPane
                        "OptionPane.errorIcon", errorIcon,
                        "OptionPane.informationIcon", informationIcon,
                        "OptionPane.warningIcon", warningIcon,
                        "OptionPane.questionIcon", questionIcon,

                        // *** FileView
                        "FileView.directoryIcon", treeClosedIcon,
                        "FileView.fileIcon", treeLeaf,
                        "FileView.computerIcon", desktop,
                        "FileView.hardDriveIcon", drive,
                        "FileView.floppyDriveIcon", floppy,

                        // *** FileChooser
                        "FileChooser.newFolderIcon", newFolderIcon,
                        "FileChooser.upFolderIcon", upFolderIcon,
                        "FileChooser.sortIcon", sortIcon,

                        // *** Tree
                        "Tree.openIcon", treeOpenIcon,
                        "Tree.closedIcon", treeClosedIcon,
                        "Tree.leafIcon", treeLeaf,
                        "Tree.expandedIcon", treeExpanded,
                        "Tree.collapsedIcon", treeCollapsed,

                        // *** Button
                        "Button.border", buttonBorder,
//"Button.realInsets", new InsetsUIResource(0,0,4,4),     

                        // *** ComboBox
                        "ComboBox.border", buttonBorder,
//"ComboBox.realInsets", new InsetsUIResource(0,0,4,4), 

                        // *** ToggleButton
                        "ToggleButton.border", buttonBorder,
//"ToggleButton.realInsets", new InsetsUIResource(0,0,4,4),       

                        // *** RadioButton
                        "RadioButton.border", empty,
                        "RadioButton.icon", radioButtonIcon,

                        // *** CheckBox
                        "CheckBox.border", empty,
                        "CheckBox.icon", checkBoxIcon,

                        // *** PopupMenu
                        "PopupMenu.border", menu,

                        // *** MenuBar
                        "MenuBar.border", menuBar,

                        // *** Menu
                        "Menu.border", empty,

                        // *** MenuItem
                        "MenuItem.border", empty,

                        // *** CheckBoxMenuItem,
                        "CheckBoxMenuItem.checkIcon", checkBoxIcon,
                        "CheckBoxMenuItem.border", empty,

                        // *** RadioButtonMenuItem
                        "RadioButtonMenuItem.checkIcon", radioButtonIcon,
                        "RadioButtonMenuItem.border", empty,

                        // *** TextField
                        "TextField.border", field,
                        "Spinner.border", empty,
                        "PasswordField.border", field,
                        "FormattedTextField.border", field,

                        // *** ToolBar
                        "ToolBar.border", new OyoahaToolBarBorder(),

                        // *** Table
                        //"TableHeader.foreground", table.get("controlText"),
                        //"TableHeader.background", new Color(255,255,255,0),
                        "TableHeader.cellBorder", tableHeaderBorder,

                        // *** Slider
                        "Slider.horizontalThumbIcon", sliderIcon,
                        "Slider.verticalThumbIcon", sliderIcon,

                        // *** List
                        "List.border", list,
                        "List.cellRenderer", ListCellRender,
                        "List.drawsFocusBorderAroundIcon", new Boolean( true ),
                        "List.selectionBorder", treelist,

                        // *** Tree
                        "Tree.border", list,
                        "Tree.selectionBorder", treelist,

                        // *** TextArea
                        "TextArea.border", empty2,

                        // *** EditorPane
                        "EditorPane.border", empty2,

                        // *** EditorPane
                        "TextPane.border", empty2,

                        // *** ScrollPane
                        "ScrollPane.border", new OyoahaTextFieldBorder( 5, 5, 4, 4 ),

                        // *** Label
                        "Label.foreground", getBlack(),

                        // *** SplitPane
                        "SplitPane.dividerSize", new Integer( 7 ),
                        "SplitPane.border", split,
                        "SplitPaneDivider.border", list,

                        // *** InternalFrame
                        "InternalFrame.border", frame,
                        "InternalFrame.optionDialogBorder", frame,
                        "InternalFrame.paletteBorder", frame,
                        "InternalFrame.background", getControl(),
//"InternalFrame.realInsets", new InsetsUIResource(0,0,4,4), 

                        // Internal Frame Defaults
                        "InternalFrame.paletteCloseIcon", InternalFrameCloseIcon,
                        "InternalFrame.icon", InternalFrameIcon,
                        "InternalFrame.closeIcon", InternalFrameCloseIcon,
                        "InternalFrame.maximizeIcon", InternalFrameMaximizeIcon,
                        "InternalFrame.iconifyIcon", InternalFrameIconifyIcon,
                        "InternalFrame.minimizeIcon", InternalFrameMinimizeIcon,
                        "InternalFrame.buttonBorder", buttonBorder,

                        // *** Separator
                        "Separator.foreground", getBlack(),
                        "Separator.shadow", getBlack(),
                        "Separator.highlight", getWhite(),
                        "Separator.background", getWhite(),

                        // *** TitledBorder
                        "TitledBorder.border", menu,

                        // *** TabbedPane
                        //"TabbedPane.contentBorderInsets", new InsetsUIResource(1,1,2,2),
                        //"TabbedPane.tabInsets", new InsetsUIResource(4,8,4,4),
                        //"TabbedPane.tabAreaInsets", new InsetsUIResource(5,5,0,5),
                        //"TabbedPane.tabRunOverlay", new Integer(30),


                        "TabbedPane.contentBorderInsets", new InsetsUIResource( 1, 1, 2, 2 ),
                        "TabbedPane.tabInsets", new InsetsUIResource( 3, 3, 3, 3 ),
                        "TabbedPane.tabAreaInsets", new InsetsUIResource( 6, 2, 0, 2 ),
                        "TabbedPane.selectedTabPadInsets", new InsetsUIResource( 4, 2, 0, 2 ),
                        "TabbedPane.tabRunOverlay", new Integer( 1 ),
                };

        table.putDefaults( defaults );
    }

    public static URL srrLoadResource( String url ) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource( url );
        if( resource == null ) {
            new RuntimeException( "Null resource: url=" + url ).printStackTrace();
        }
        return resource;
    }

    public UIDefaults getDefaults() {
        UIDefaults table = super.getDefaults();

        if( oyoahaTheme != null ) {
            oyoahaTheme.installUIDefaults( this, table );
        }
        else {
            try {
                //try to load a theme
                File f = new File( System.getProperty( "user.home" ), ".lnf" + File.separatorChar + "oyoaha" + File.separatorChar + "default.ini" );

                if( f.exists() ) {
                    Properties p = new Properties();
                    InputStream in = new FileInputStream( f );
                    p.load( in );
                    in.close();

                    String otm = p.getProperty( "oyoahatheme" );

                    if( otm != null ) {
                        f = new File( otm );

                        if( !f.exists() ) {
                            f = new File( System.getProperty( "user.home" ), ".lnf" + File.separatorChar + "oyoaha" + File.separatorChar + otm );
                        }

                        if( f.exists() ) {
                            setOyoahaTheme( f );
                            oyoahaTheme.installUIDefaults( this, table );
                        }
                    }
                }
            }
            catch( Exception ex ) {

            }
        }

        Object s1 = table.get( "oyoaha.animated" );

        if( s1 != null && s1.toString().equalsIgnoreCase( "true" ) ) {
            OyoahaUtilities.initializeAnimation( true, true );
        }

        Object s2 = table.get( "Sound.pressed" );
        Object s3 = table.get( "Sound.scrolled" );

        OyoahaUtilities.initializeSound( ( s2 != null ) ? s2.toString() : null, ( s3 != null ) ? s3.toString() : null );

        return table;
    }
}