/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.awml.AwmlNamespace;
import org.aswing.awml.AwmlParser;
import org.aswing.awml.ButtonGroupManager;
import org.aswing.Box;
import org.aswing.ButtonGroup;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.overflow.JAccordion;
import org.aswing.overflow.JAdjuster;
import org.aswing.JAttachPane;
import org.aswing.JButton;
import org.aswing.JCheckBox;
import org.aswing.JCheckBoxMenuItem;
import org.aswing.JComboBox;
import org.aswing.JFrame;
import org.aswing.JLabel;
import org.aswing.overflow.JList;
import org.aswing.overflow.JListTree;
import org.aswing.overflow.JLoadPane;
import org.aswing.overflow.JMenu;
import org.aswing.overflow.JMenuBar;
import org.aswing.overflow.JMenuItem;
import org.aswing.JPanel;
import org.aswing.overflow.JPopup;
import org.aswing.overflow.JPopupMenu;
import org.aswing.overflow.JProgressBar;
import org.aswing.JRadioButton;
import org.aswing.JRadioButtonMenuItem;
import org.aswing.JScrollBar;
import org.aswing.JScrollPane;
import org.aswing.JSeparator;
import org.aswing.JSlider;
import org.aswing.JSpacer;
import org.aswing.overflow.JSplitPane;
import org.aswing.overflow.JTabbedPane;
import org.aswing.overflow.JTable;
import org.aswing.JTextArea;
import org.aswing.JTextField;
import org.aswing.overflow.JToggleButton;
import org.aswing.JToolBar;
import org.aswing.overflow.JTree;
import org.aswing.overflow.JViewport;
import org.aswing.JWindow;
import org.aswing.MCPanel;
import org.aswing.SoftBox;
import org.aswing.util.HashMap;

/**
 * Privides public API allowed to access components created using AWML.
 * 
 * @author Igor Sadovskiy
 */
class org.aswing.awml.AwmlManager {
    
	/** 
	 * New component with the existed ID will be appended. Existed component 
	 * with the same ID will stay. 
	 */
	public static var STRATEGY_APPEND:Number = AwmlParser.STRATEGY_APPEND;
	/**
	 * New component with the existed ID will replace old one. Old one will be 
	 * destroyed.
	 */
	public static var STRATEGY_REPLACE:Number = AwmlParser.STRATEGY_REPLACE;
	/**
	 * New component with the existed ID will be ignored. 
	 */
	public static var STRATEGY_IGNORE:Number = AwmlParser.STRATEGY_IGNORE; 
	/**
	 * In new component will have already existed ID Exception will be thrown. 
	 */
	public static var STRATEGY_EXCEPTION:Number = AwmlParser.STRATEGY_EXCEPTION; 
    
    
    /** Root namespace */
    private static var rootNamespace:AwmlNamespace;
    
    
    /**
     * Initializes and returns root namespace.
     * 
     * @return the root namespace 
     */
    public static function getRootNamespace():AwmlNamespace {
        if (rootNamespace == null) {
            rootNamespace = new AwmlNamespace();
        }
        return rootNamespace;   
    }

    /**
     * Searches for the namespace with the specified {@code name}. If {@code name} is
     * <code>undefined</code> returns root namespace.
     * 
     * @param name the name of the namespace to search
     * @return the found namespace ot <code>null</code>  
     */
    public static function getNamespace(name:String):AwmlNamespace {
        return (name != undefined) ? getRootNamespace().findNamespace(name) : getRootNamespace(); 
    }
    
	/**
	 * Sets new component parsing strategy.
	 * Specifies how parser should behave if new component
	 * has ID already existed in the namespace.
	 * 
	 * @param strategy the new strategy
	 * @see #STRATEGY_APPEND
	 * @see #STRATEGY_REPLACE
	 * @see #STRATEGY_IGNORE
	 * @see #STRATEGY_EXCEPTION
	 */
	public static function setComponentParsingStrategy(strategy:Number):Void {
		AwmlParser.setParsingStrategy(strategy);
	}

	/**
	 * Returns currently configured component parsing strategy.
	 * Specifies how parser should behave if new component
	 * has ID already existed in the namespace.
	 * 
	 * @return component add strategy 
	 * @see #STRATEGY_APPEND
	 * @see #STRATEGY_REPLACE
	 * @see #STRATEGY_IGNORE
	 * @see #STRATEGY_EXCEPTION
	 */
	public static function getComponentParsingStrategy(Void):Number {
		return AwmlParser.getParsingStrategy();	
	}
    
	/**
	 * Defines new property map.
	 * 
	 * @param properties the new property map
	 */
	public static function setProperties(properties:HashMap):Void {
		AwmlParser.setProperties(properties);
	}

	/**
	 * Returns currently configured property map.
	 * 
	 * @return the property map
	 */
	public static function getProperties(Void):HashMap {
		return AwmlParser.getProperties();
	}
    
    /**
     * getComponent(awmlID:String)<br>
     * getComponent(awmlID:String, namespaceName:String)<br>
     * <p>
     * Returns AWML {@link org.aswing.Component} instance by <code>awmlID</code>
     * within the specified namespace. If <code>namespaceName</code> isn't 
     * specified, searches for the first entrty of the component with the
     * passed-in <code>awmlID</code> in the root and its children namespaces.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.Component} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.Component}
     */
    public static function getComponent(awmlID:String, namespaceName:String):Component {
    	var namespace:AwmlNamespace = getNamespace(namespaceName); 
        return (namespace != null) ? namespace.findComponent(awmlID) : null;
    }

    /**
     * Returns AWML {@link org.aswing.Container} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.Container} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.Container}
     */
    public static function getContainer(awmlID:String, namespaceName:String):Container {
        return Container(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JFrame} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JFrame} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JFrame}
     */
    public static function getFrame(awmlID:String, namespaceName:String):JFrame {
        return JFrame(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JWindow} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JWindow} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JWindow}
     */
    public static function getWindow(awmlID:String, namespaceName:String):JWindow {
        return JWindow(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JPopup} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JPopup} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JPopup}
     */
    public static function getPopup(awmlID:String, namespaceName:String):JPopup {
        return JPopup(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.MCPanel} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.MCPanel} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.MCPanel}
     */
    public static function getMCPanel(awmlID:String, namespaceName:String):MCPanel {
        return MCPanel(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JPopupMenu} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JPopupMenu} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JPopupMenu}
     */
    public static function getPopupMenu(awmlID:String, namespaceName:String):JPopupMenu {
        return JPopupMenu(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JTextField} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JTextField} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JTextField}
     */
    public static function getTextField(awmlID:String, namespaceName:String):JTextField {
        return JTextField(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JTextArea} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JTextArea} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JTextArea}
     */
    public static function getTextArea(awmlID:String, namespaceName:String):JTextArea {
        return JTextArea(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JSeparator} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JSeparator} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JSeparator}
     */
    public static function getSeparator(awmlID:String, namespaceName:String):JSeparator {
        return JSeparator(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JProgressBar} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JProgressBar} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JProgressBar}
     */
    public static function getProgressBar(awmlID:String, namespaceName:String):JProgressBar {
        return JProgressBar(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JLabel} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JLabel} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JLabel}
     */
    public static function getLabel(awmlID:String, namespaceName:String):JLabel {
        return JLabel(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JButton} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JButton} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JButton}
     */
    public static function getButton(awmlID:String, namespaceName:String):JButton {
        return JButton(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JToggleButton} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JToggleButton} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JToggleButton}
     */
    public static function getToggleButton(awmlID:String, namespaceName:String):JToggleButton {
        return JToggleButton(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JCheckBox} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JCheckBox} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JCheckBox}
     */
    public static function getCheckBox(awmlID:String, namespaceName:String):JCheckBox {
        return JCheckBox(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JRadioButton} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JRadioButton} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JRadioButton}
     */
    public static function getRadioButton(awmlID:String, namespaceName:String):JRadioButton {
        return JRadioButton(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JPanel} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JPanel} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JPanel}
     */
    public static function getPanel(awmlID:String, namespaceName:String):JPanel {
        return JPanel(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.Box} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.Box} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.Box}
     */
    public static function getBox(awmlID:String, namespaceName:String):Box {
        return Box(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.SoftBox} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.SoftBox} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.SoftBox}
     */
    public static function getSoftBox(awmlID:String, namespaceName:String):SoftBox {
        return SoftBox(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JToolBar} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JToolBar} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JToolBar}
     */
    public static function getToolBar(awmlID:String, namespaceName:String):JToolBar {
        return JToolBar(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JScrollBar} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JScrollBar} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JScrollBar}
     */
    public static function getScrollBar(awmlID:String, namespaceName:String):JScrollBar {
        return JScrollBar(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JSlider} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JSlider} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JSlider}
     */
    public static function getSlider(awmlID:String, namespaceName:String):JSlider {
        return JSlider(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JList} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JList} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JList}
     */
    public static function getList(awmlID:String, namespaceName:String):JList {
        return JList(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JComboBox} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JComboBox} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JComboBox}
     */
    public static function getComboBox(awmlID:String, namespaceName:String):JComboBox {
        return JComboBox(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JAccordion} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JAccordion} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JAccordion}
     */
    public static function getAccordion(awmlID:String, namespaceName:String):JAccordion {
        return JAccordion(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JTabbedPane} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JTabbedPane} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JTabbedPane}
     */
    public static function getTabbedPane(awmlID:String, namespaceName:String):JTabbedPane {
        return JTabbedPane(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JLoadPane} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JLoadPane} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JLoadPane}
     */
    public static function getLoadPane(awmlID:String, namespaceName:String):JLoadPane {
        return JLoadPane(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JAttachPane} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JAttachPane} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JAttachPane}
     */
    public static function getAttachPane(awmlID:String, namespaceName:String):JAttachPane {
        return JAttachPane(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JScrollPane} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JScrollPane} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JScrollPane}
     */
    public static function getScrollPane(awmlID:String, namespaceName:String):JScrollPane {
        return JScrollPane(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JViewport} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JViewport} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JViewport}
     */
    public static function getViewport(awmlID:String, namespaceName:String):JViewport {
        return JViewport(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JTable} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JTable} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JTable}
     */
    public static function getTable(awmlID:String, namespaceName:String):JTable {
        return JTable(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JTree} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JTree} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JTree}
     */
    public static function getTree(awmlID:String, namespaceName:String):JTree {
        return JTree(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JAdjuster} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JAdjuster} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JAdjuster}
     */
    public static function getAdjuster(awmlID:String, namespaceName:String):JAdjuster {
        return JAdjuster(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JSpacer} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JSpacer} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JSpacer}
     */
    public static function getSpacer(awmlID:String, namespaceName:String):JSpacer {
        return JSpacer(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JListTree} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JListTree} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JListTree}
     */
    public static function getListTree(awmlID:String, namespaceName:String):JListTree {
        return JListTree(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JSplitPane} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JSplitPane} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JSplitPane}
     */
    public static function getSplitPane(awmlID:String, namespaceName:String):JSplitPane {
        return JSplitPane(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JMenuBar} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JMenuBar} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JMenuBar}
     */
    public static function getMenuBar(awmlID:String, namespaceName:String):JMenuBar {
        return JMenuBar(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JMenu} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JMenu} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JMenu}
     */
    public static function getMenu(awmlID:String, namespaceName:String):JMenu {
        return JMenu(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.overflow.JMenuItem} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.overflow.JMenuItem} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.overflow.JMenuItem}
     */
    public static function getMenuItem(awmlID:String, namespaceName:String):JMenuItem {
        return JMenuItem(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JCheckBoxMenuItem} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JCheckBoxMenuItem} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JCheckBoxMenuItem}
     */
    public static function getCheckBoxMenuItem(awmlID:String, namespaceName:String):JCheckBoxMenuItem {
        return JCheckBoxMenuItem(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.JRadioButtonMenuItem} instance by <code>awmlID</code>.
     * 
     * @param awmlID the AWML ID of the {@link org.aswing.JRadioButtonMenuItem} to get
     * @param namespaceName (optional) the namespace to search component in
     * @return the AWML {@link org.aswing.JRadioButtonMenuItem}
     */
    public static function getRadioButtonMenuItem(awmlID:String, namespaceName:String):JRadioButtonMenuItem {
        return JRadioButtonMenuItem(getComponent(awmlID, namespaceName));
    }

    /**
     * Returns AWML {@link org.aswing.ButtonGroup} instance by its <code>id</code>.
     * 
     * @param id the ID of the {@link org.aswing.ButtonGroup} to get
     * @return the {@link org.aswing.ButtonGroup} instance or <code>null</code>
     * 
     * @see ButtonGroupManager#get
     */
    public static function getButtonGroup(id:String):ButtonGroup {
        return ButtonGroupManager.get(id);
    }
    
    
	/**
	 * addEventListener(id:String, object:Object)<br>
	 * addEventListener(id:String, class:Function)<br>
	 * <p>Registers object's instance or class as event listener within parser and
	 * associates it with the specified <code>id</code>.
	 * @param id the <code>id</code> of the instance or class to refer it from the AWML
	 * @param listener the reference to the class or object's instance
	 * @see AwmlParser#addEventListener 
	 */
	public static function addEventListener(id:String, listener:Object):Void {
		AwmlParser.addEventListener(id, listener);
	} 
	
	/**
	 * removeEventListener(id:String)<br>
	 * removeEventListener(object:Object)<br>
	 * removeEventListener(class:Function)<br>
	 * 
	 * <p>Unregister AWML event listener by specified <code>id</code> or reference to class
	 * or object's instance
	 * @param idOrListener the <code>id</code> or reference to the instance or class to remove
	 * @see AwmlParser#removeEventListener
	 */
	public static function removeEventListener(idOrListener:Object):Void {
		AwmlParser.removeEventListener(idOrListener);
	}
		
	/**
	 * Removes all registered AWML listeners.
	 * @see AwmlParser#removeAllEventListeners
	 */
	public static function removeAllEventListeners(Void):Void {
		AwmlParser.removeAllEventListeners();
	}
	    
    private function AwmlManager() {
        //  
    }
}