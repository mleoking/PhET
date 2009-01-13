/*
 Copyright aswing.org, see the LICENCE.txt.
*/
  
/**
 * This interface is used to mark objects created by ComponentUI delegates.
 * The <code>ComponentUI.installUI()</code> and 
 * <code>ComponentUI.uninstallUI()</code> methods can use this interface
 * to decide if a properties value has been overridden.  For example, the
 * JPanel border property is initialized by BasicPanelUI.installUI(),
 * only if it's initial value is undefined or an UIResource:
 * <pre>
 * if (panel.getBorder() === undefined || panel.getBorder() instanceof UIResource) {
 *     panel.setBorder(UIManager.getBorder("Panel.border"));
 * }
 * </pre>
 * At uninstallUI() time we reset the property to undefined if its value
 * is an instance of UIResource:
 * <pre>
 * if (panel.getBorder() instanceof UIResource) {
 *     panel.setBorder(undefined);
 * }
 *</pre>
 * <p>
 * Some other type value like Number, Boolean, will be indicate to undefined, and 
 * UI delegated value will stored in component's uiProperties.
 * @see ComponentUI
 * @see org.aswing.Component#setUIProperty()
 * @author iiley
 */
interface org.aswing.plaf.UIResource {
}
