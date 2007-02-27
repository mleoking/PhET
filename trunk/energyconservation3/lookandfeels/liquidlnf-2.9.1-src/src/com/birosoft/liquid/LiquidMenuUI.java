/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*	Liquid Look and Feel                                                   *
*                                                                              *
*  Author, Miroslav Lazarevic                                                  *
*                                                                              *
*   For licensing information and credits, please refer to the                 *
*   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
*                                                                              *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.birosoft.liquid;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MenuListener;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;

/**
 * A default L&F implementation of MenuUI.  This implementation
 * is a "combined" view/controller.
 *
 * @version 1.144 04/24/02
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 */
public class LiquidMenuUI extends LiquidMenuItemUI
{
    protected ChangeListener changeListener;
    protected PropertyChangeListener propertyChangeListener;
    protected MenuListener menuListener;
    
    // Shared instance of the menuListener
    private static MenuListener sharedMenuListener;
    
    private int lastMnemonic = 0;
    
    /** Uses as the parent of the windowInputMap when selected. */
    private InputMap selectedWindowInputMap;
    
    /* diagnostic aids -- should be false for production builds. */
    private static final boolean TRACE = false; // trace creates and disposes
    private static final boolean VERBOSE = false; // show reuse hits/misses
    private static final boolean DEBUG = false; // show bad params, misc.
    
    private static boolean crossMenuMnemonic = true;
    
    public static ComponentUI createUI(JComponent x)
    {
        return new LiquidMenuUI();
    }
    
    public void installUI(JComponent c)
    {
        super.installUI(c);
        JMenu m=(JMenu)c;
    }
    
    
    protected void installDefaults()
    {
        super.installDefaults();
        ((JMenu) menuItem).setDelay(200);
        crossMenuMnemonic = UIManager.getBoolean("Menu.crossMenuMnemonic");
    }
    
    protected String getPropertyPrefix()
    {
        return "Menu";
    }
    
    protected void installListeners()
    {
        super.installListeners();
        
        if (changeListener == null)
            changeListener = createChangeListener(menuItem);
        
        if (changeListener != null)
            menuItem.addChangeListener(changeListener);
        
        if (propertyChangeListener == null)
            propertyChangeListener = createPropertyChangeListener(menuItem);
        
        if (propertyChangeListener != null)
            menuItem.addPropertyChangeListener(propertyChangeListener);
        
        if (menuListener == null)
            menuListener = createMenuListener(menuItem);
        
        if (menuListener != null)
            ((JMenu) menuItem).addMenuListener(menuListener);
    }
    
    protected void installKeyboardActions()
    {
        super.installKeyboardActions();
        updateMnemonicBinding();
    }
    
    void updateMnemonicBinding()
    {
        int mnemonic = menuItem.getModel().getMnemonic();
        int[] shortcutKeys = (int[]) UIManager.get("Menu.shortcutKeys");
        if (mnemonic == lastMnemonic || shortcutKeys == null)
        {
            return;
        }
        if (lastMnemonic != 0 && windowInputMap != null)
        {
            for (int i = 0; i < shortcutKeys.length; i++)
            {
                windowInputMap.remove(KeyStroke.getKeyStroke(lastMnemonic, shortcutKeys[i], false));
            }
        }
        if (mnemonic != 0)
        {
            if (windowInputMap == null)
            {
                windowInputMap = createInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                SwingUtilities.replaceUIInputMap(menuItem, JComponent.WHEN_IN_FOCUSED_WINDOW, windowInputMap);
            }
            for (int i = 0; i < shortcutKeys.length; i++)
            {
                windowInputMap.put(KeyStroke.getKeyStroke(mnemonic, shortcutKeys[i], false), "selectMenu");
            }
        }
        lastMnemonic = mnemonic;
    }
    
    protected void uninstallKeyboardActions()
    {
        super.uninstallKeyboardActions();
    }
    
    /**
     * The ActionMap for BasicMenUI can not be shared, this is subclassed
     * to create a new one for each invocation.
     */
    ActionMap getActionMap()
    {
        return createActionMap();
    }
    
    /**
     * Invoked to create the ActionMap.
     */
    ActionMap createActionMap()
    {
        ActionMap am = super.createActionMap();
        if (am != null)
        {
            am.put("selectMenu", new PostAction((JMenu) menuItem, true));
        }
        return am;
    }
    
    protected MouseInputListener createMouseInputListener(JComponent c)
    {
        return new MouseInputHandler();
    }
    
    protected MenuListener createMenuListener(JComponent c)
    {
        if (sharedMenuListener == null)
        {
            sharedMenuListener = new MenuHandler();
        }
        return sharedMenuListener;
    }
    
    protected ChangeListener createChangeListener(JComponent c)
    {
        return null;
    }
    
    protected PropertyChangeListener createPropertyChangeListener(JComponent c)
    {
        return new PropertyChangeHandler();
    }
    
    protected void uninstallDefaults()
    {
        menuItem.setArmed(false);
        menuItem.setSelected(false);
        menuItem.resetKeyboardActions();
        super.uninstallDefaults();
    }
    
    protected void uninstallListeners()
    {
        super.uninstallListeners();
        
        if (changeListener != null)
            menuItem.removeChangeListener(changeListener);
        
        if (propertyChangeListener != null)
            menuItem.removePropertyChangeListener(propertyChangeListener);
        
        if (menuListener != null)
            ((JMenu) menuItem).removeMenuListener(menuListener);
        
        changeListener = null;
        propertyChangeListener = null;
        menuListener = null;
    }
    
    protected MenuDragMouseListener createMenuDragMouseListener(JComponent c)
    {
        return new MenuDragMouseHandler();
    }
    
    protected MenuKeyListener createMenuKeyListener(JComponent c)
    {
        return new MenuKeyHandler();
    }
    
    public Dimension getMaximumSize(JComponent c)
    {
        if (((JMenu) menuItem).isTopLevelMenu() == true)
        {
            Dimension d = c.getPreferredSize();
            return new Dimension(d.width, Short.MAX_VALUE);
        }
        return null;
    }
    
    protected void setupPostTimer(JMenu menu)
    {
        Timer timer = new Timer(menu.getDelay(), new PostAction(menu, false));
        timer.setRepeats(false);
        timer.start();
    }
    
    private static void appendPath(MenuElement[] path, MenuElement elem)
    {
        MenuElement newPath[] = new MenuElement[path.length + 1];
        System.arraycopy(path, 0, newPath, 0, path.length);
        newPath[path.length] = elem;
        MenuSelectionManager.defaultManager().setSelectedPath(newPath);
    }
    
    private static class PostAction extends AbstractAction
    {
        JMenu menu;
        boolean force = false;
        
        PostAction(JMenu menu, boolean shouldForce)
        {
            this.menu = menu;
            this.force = shouldForce;
        }
        
        public void actionPerformed(ActionEvent e)
        {
            if (!crossMenuMnemonic)
            {
                JPopupMenu pm = getActivePopupMenu();
                if (pm != null && pm != menu.getParent())
                {
                    return;
                }
            }
            
            final MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
            if (force)
            {
                Container cnt = menu.getParent();
                if (cnt != null && cnt instanceof JMenuBar)
                {
                    MenuElement me[];
                    MenuElement subElements[];
                    
                    subElements = menu.getPopupMenu().getSubElements();
                    if (subElements.length > 0)
                    {
                        me = new MenuElement[4];
                        me[0] = (MenuElement) cnt;
                        me[1] = (MenuElement) menu;
                        me[2] = (MenuElement) menu.getPopupMenu();
                        me[3] = subElements[0];
                    } else
                    {
                        me = new MenuElement[3];
                        me[0] = (MenuElement) cnt;
                        me[1] = menu;
                        me[2] = (MenuElement) menu.getPopupMenu();
                    }
                    defaultManager.setSelectedPath(me);
                }
            } else
            {
                MenuElement path[] = defaultManager.getSelectedPath();
                if (path.length > 0 && path[path.length - 1] == menu)
                {
                    appendPath(path, menu.getPopupMenu());
                }
            }
        }
        
        public boolean isEnabled()
        {
            return menu.getModel().isEnabled();
        }
    }
    
    private class PropertyChangeHandler implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e)
        {
            String prop = e.getPropertyName();
            if (prop.equals(AbstractButton.MNEMONIC_CHANGED_PROPERTY))
            {
                updateMnemonicBinding();
            }
        }
    }
    
    /**
     * Instantiated and used by a menu item to handle the current menu selection
     * from mouse events. A MouseInputHandler processes and forwards all mouse events
     * to a shared instance of the MenuSelectionManager.
     * <p>
     * This class is protected so that it can be subclassed by other look and
     * feels to implement their own mouse handling behavior. All overridden
     * methods should call the parent methods so that the menu selection
     * is correct.
     *
     * @see javax.swing.MenuSelectionManager
     * @since 1.4
     */
    protected class MouseInputHandler implements MouseInputListener
    {
        public void mouseClicked(MouseEvent e)
        {
        }
        
        /**
         * Invoked when the mouse has been clicked on the menu. This
         * method clears or sets the selection path of the
         * MenuSelectionManager.
         *
         * @param e the mouse event
         */
        public void mousePressed(MouseEvent e)
        {
            JMenu menu = (JMenu) menuItem;
            if (!menu.isEnabled())
                return;
            
            MenuSelectionManager manager = MenuSelectionManager.defaultManager();
            if (menu.isTopLevelMenu())
            {
                if (menu.isSelected())
                {
                    manager.clearSelectedPath();
                } else
                {
                    Container cnt = menu.getParent();
                    if (cnt != null && cnt instanceof JMenuBar)
                    {
                        MenuElement me[] = new MenuElement[2];
                        me[0] = (MenuElement) cnt;
                        me[1] = menu;
                        manager.setSelectedPath(me);
                    }
                }
            }
            
            MenuElement selectedPath[] = manager.getSelectedPath();
            if (selectedPath.length > 0 && selectedPath[selectedPath.length - 1] != menu.getPopupMenu())
            {
                
                if (menu.isTopLevelMenu() || menu.getDelay() == 0)
                {
                    appendPath(selectedPath, menu.getPopupMenu());
                } else
                {
                    setupPostTimer(menu);
                }
            }
        }
        
        /**
         * Invoked when the mouse has been released on the menu. Delegates the
         * mouse event to the MenuSelectionManager.
         *
         * @param e the mouse event
         */
        public void mouseReleased(MouseEvent e)
        {
            JMenu menu = (JMenu) menuItem;
            if (!menu.isEnabled())
                return;
            MenuSelectionManager manager = MenuSelectionManager.defaultManager();
            manager.processMouseEvent(e);
            if (!e.isConsumed())
                manager.clearSelectedPath();
        }
        
        /**
         * Invoked when the cursor enters the menu. This method sets the selected
         * path for the MenuSelectionManager and handles the case
         * in which a menu item is used to pop up an additional menu, as in a
         * hierarchical menu system.
         *
         * @param e the mouse event; not used
         */
        public void mouseEntered(MouseEvent e)
        {
            JMenu menu = (JMenu) menuItem;
            if (!menu.isEnabled())
                return;
            
            menu.putClientProperty("rollover",Boolean.TRUE);
            menu.repaint();
            MenuSelectionManager manager = MenuSelectionManager.defaultManager();
            MenuElement selectedPath[] = manager.getSelectedPath();
            if (!menu.isTopLevelMenu())
            {
                if (!(selectedPath.length > 0 && selectedPath[selectedPath.length - 1] == menu.getPopupMenu()))
                {
                    if (menu.getDelay() == 0)
                    {
                        appendPath(getPath(), menu.getPopupMenu());
                    } else
                    {
                        manager.setSelectedPath(getPath());
                        setupPostTimer(menu);
                    }
                }
            } else
            {
                if (selectedPath.length > 0 && selectedPath[0] == menu.getParent())
                {
                    MenuElement newPath[] = new MenuElement[3];
                    // A top level menu's parent is by definition
                    // a JMenuBar
                    newPath[0] = (MenuElement) menu.getParent();
                    newPath[1] = menu;
                    newPath[2] = menu.getPopupMenu();
                    manager.setSelectedPath(newPath);
                }
            }
            menu.repaint();
        }
        
        public void mouseExited(MouseEvent e)
        {
            JMenu menu = (JMenu) menuItem;
            menu.putClientProperty("rollover",Boolean.FALSE);
            menu.repaint();
        }
        
        /**
         * Invoked when a mouse button is pressed on the menu and then dragged.
         * Delegates the mouse event to the MenuSelectionManager.
         *
         * @param e the mouse event
         * @see java.awt.event.MouseMotionListener#mouseDragged
         */
        public void mouseDragged(MouseEvent e)
        {
            JMenu menu = (JMenu) menuItem;
            if (!menu.isEnabled())
                return;
            MenuSelectionManager.defaultManager().processMouseEvent(e);
        }
        public void mouseMoved(MouseEvent e)
        {
        }
    }
    
    private static class MenuHandler implements MenuListener
    {
        public void menuSelected(MenuEvent e)
        {
        }
        public void menuDeselected(MenuEvent e)
        {
        }
        public void menuCanceled(MenuEvent e)
        {
            JMenu m = (JMenu) e.getSource();
            MenuSelectionManager manager = MenuSelectionManager.defaultManager();
            if (manager.isComponentPartOfCurrentMenu(m))
                MenuSelectionManager.defaultManager().clearSelectedPath();
        }
        
    }
    
    /**
     * As of Java 2 platform 1.4, this previously undocumented class
     * is now obsolete. KeyBindings are now managed by the popup menu.
     */
    public class ChangeHandler implements ChangeListener
    {
        public JMenu menu;
        public LiquidMenuUI ui;
        public boolean isSelected = false;
        public Component wasFocused;
        
        public ChangeHandler(JMenu m, LiquidMenuUI ui)
        {
            menu = m;
            this.ui = ui;
        }
        
        public void stateChanged(ChangeEvent e)
        {
        }
    }
    
    private class MenuDragMouseHandler implements MenuDragMouseListener
    {
        public void menuDragMouseEntered(MenuDragMouseEvent e)
        {
        }
        public void menuDragMouseDragged(MenuDragMouseEvent e)
        {
            if (menuItem.isEnabled() == false)
                return;
            
            MenuSelectionManager manager = e.getMenuSelectionManager();
            MenuElement path[] = e.getPath();
            
            Point p = e.getPoint();
            if (p.x >= 0 && p.x < menuItem.getWidth() && p.y >= 0 && p.y < menuItem.getHeight())
            {
                JMenu menu = (JMenu) menuItem;
                MenuElement selectedPath[] = manager.getSelectedPath();
                if (!(selectedPath.length > 0 && selectedPath[selectedPath.length - 1] == menu.getPopupMenu()))
                {
                    if (menu.isTopLevelMenu() || menu.getDelay() == 0 || e.getID() == MouseEvent.MOUSE_DRAGGED)
                    {
                        appendPath(path, menu.getPopupMenu());
                    } else
                    {
                        manager.setSelectedPath(path);
                        setupPostTimer(menu);
                    }
                }
            } else if (e.getID() == MouseEvent.MOUSE_RELEASED)
            {
                Component comp = manager.componentForPoint(e.getComponent(), e.getPoint());
                if (comp == null)
                    manager.clearSelectedPath();
            }
            
        }
        public void menuDragMouseExited(MenuDragMouseEvent e)
        {
        }
        public void menuDragMouseReleased(MenuDragMouseEvent e)
        {
        }
    }
    
    static JPopupMenu getActivePopupMenu()
    {
        MenuElement[] path = MenuSelectionManager.defaultManager().getSelectedPath();
        for (int i = path.length - 1; i >= 0; i--)
        {
            MenuElement elem = path[i];
            if (elem instanceof JPopupMenu)
            {
                return (JPopupMenu) elem;
            }
        }
        return null;
    }
    
    /**
     * Handles the mnemonic handling for the JMenu and JMenuItems.
     */
    private class MenuKeyHandler implements MenuKeyListener
    {
        
        // fields for handling duplicate mnemonics.
        private int indexes[];
        private char lastMnemonic;
        private int lastIndex;
        private int matches;
        
        /**
         * Opens the SubMenu
         */
        public void menuKeyTyped(MenuKeyEvent e)
        {
            if (DEBUG)
            {
                System.out.println("in LiquidMenuUI.menuKeyTyped for " + menuItem.getText());
            }
            if (!crossMenuMnemonic)
            {
                JPopupMenu pm = getActivePopupMenu();
                if (pm != null && pm != menuItem.getParent())
                {
                    return;
                }
            }
            
            int key = menuItem.getMnemonic();
            if (key == 0)
                return;
            MenuElement path[] = e.getPath();
            if (lower((char) key) == lower(e.getKeyChar()))
            {
                JPopupMenu popupMenu = ((JMenu) menuItem).getPopupMenu();
                MenuElement sub[] = popupMenu.getSubElements();
                if (sub.length > 0)
                {
                    MenuSelectionManager manager = e.getMenuSelectionManager();
                    MenuElement newPath[] = new MenuElement[path.length + 2];
                    System.arraycopy(path, 0, newPath, 0, path.length);
                    newPath[path.length] = popupMenu;
                    newPath[path.length + 1] = sub[0];
                    manager.setSelectedPath(newPath);
                }
                e.consume();
            }
        }
        
        /**
         * Handles the mnemonics for the menu items. Will also handle duplicate mnemonics.
         * Perhaps this should be moved into BasicPopupMenuUI. See 4670831
         */
        public void menuKeyPressed(MenuKeyEvent e)
        {
            if (DEBUG)
            {
                System.out.println("in LiquidMenuUI.menuKeyPressed for " + menuItem.getText());
            }
            // Handle the case for Escape or Enter...
            char keyChar = e.getKeyChar();
            if (!Character.isLetterOrDigit(keyChar))
                return;
            
            MenuSelectionManager manager = e.getMenuSelectionManager();
            MenuElement path[] = e.getPath();
            MenuElement selectedPath[] = manager.getSelectedPath();
            
            for (int i = selectedPath.length - 1; i >= 0; i--)
            {
                if (selectedPath[i] == menuItem)
                {
                    JPopupMenu popupMenu = ((JMenu) menuItem).getPopupMenu();
                    MenuElement items[] = popupMenu.getSubElements();
                    
                    if (indexes == null || lastMnemonic != keyChar)
                    {
                        matches = 0;
                        lastIndex = 0;
                        indexes = new int[items.length];
                        for (int j = 0; j < items.length; j++)
                        {
                            int key = ((JMenuItem) items[j]).getMnemonic();
                            if (lower((char) key) == lower(keyChar))
                            {
                                indexes[matches++] = j;
                            }
                        }
                        lastMnemonic = keyChar;
                    }
                    if (matches == 0)
                    {
                        ; // no op (consume)
                    } else if (matches == 1)
                    {
                        // Invoke the menu action
                        JMenuItem item = (JMenuItem) items[indexes[0]];
                        if (!(item instanceof JMenu))
                        {
                            // Let Submenus be handled by menuKeyTyped
                            manager.clearSelectedPath();
                            item.doClick();
                        }
                    } else
                    {
                        // Select the menu item with the matching mnemonic. If
                        // the same mnemonic has been invoked then select the next
                        // menu item in the cycle.
                        if (lastIndex == matches)
                        {
                            // Take care of the situation in which the
                            // mnemonic wraps.
                            lastIndex = 0;
                        }
                        MenuElement menuItem = items[indexes[lastIndex++]];
                        
                        MenuElement newPath[] = new MenuElement[path.length + 2];
                        System.arraycopy(path, 0, newPath, 0, path.length);
                        newPath[path.length] = popupMenu;
                        newPath[path.length + 1] = menuItem;
                        manager.setSelectedPath(newPath);
                    }
                    e.consume();
                    return;
                }
            }
        }
        
        public void menuKeyReleased(MenuKeyEvent e)
        {
        }
        
        private char lower(char keyChar)
        {
            return Character.toLowerCase(keyChar);
        }
    }
    
    
}