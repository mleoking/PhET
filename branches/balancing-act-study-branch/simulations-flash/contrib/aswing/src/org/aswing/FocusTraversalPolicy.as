/*
 Copyright aswing.org, see the LICENCE.txt.
*/
 
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.JWindow;

/**
 * A FocusTraversalPolicy defines the order in which Components with a
 * particular focus cycle root are traversed. Instances can apply the policy to
 * arbitrary focus cycle roots, allowing themselves to be shared across
 * Containers. They do not need to be reinitialized when the focus cycle roots
 * of a Component hierarchy change.
 * <p>
 * The core responsibility of a FocusTraversalPolicy is to provide algorithms
 * determining the next and previous Components to focus when traversing
 * forward or backward in a UI. Each FocusTraversalPolicy must also provide
 * algorithms for determining the first, last, and default Components in a
 * traversal cycle. First and last Components are used when normal forward and
 * backward traversal, respectively, wraps. The default Component is the first
 * to receive focus when traversing down into a new focus traversal cycle.
 * A FocusTraversalPolicy can optionally provide an algorithm for determining
 * a Window's initial Component. The initial Component is the first to receive
 * focus when a Window is first made visible.
 * <p>
 * When searching for first/last/next/previous Component,
 * if a focus traversal policy provider is encountered, its focus traversal
 * policy is used to perform the search operation.
 *
 * @see Container#setFocusTraversalPolicy()
 * @see Container#getFocusTraversalPolicy()
 * @see Container#setFocusCycleRoot()
 * @see Container#isFocusCycleRoot()
 * @see Container#setFocusTraversalPolicyProvider()
 * @see Container#isFocusTraversalPolicyProvider()
 * @see org.aswing.FocusManager#setDefaultFocusTraversalPolicy()
 * @see org.aswing.FocusManager#getDefaultFocusTraversalPolicy()
 * @author iiley
 */
interface org.aswing.FocusTraversalPolicy {
    /**
     * Returns the Component that should receive the focus after aComponent.
     * aContainer must be a focus cycle root of aComponent or a focus traversal
     * policy provider.
     *
     * @param aContainer a focus cycle root of aComponent or focus traversal
     *        policy provider
     * @param aComponent a (possibly indirect) child of aContainer, or
     *        aContainer itself
     * @return the Component that should receive the focus after aComponent, or
     *         null if no suitable Component can be found
     * @throws Error if aContainer is not a focus cycle
     *         root of aComponent or a focus traversal policy provider, or if 
     *         either aContainer or aComponent is null
     */	
	public function getComponentAfter(aContainer:Container, aComponent:Component):Component;
	
    /**
     * Returns the Component that should receive the focus before aComponent.
     * aContainer must be a focus cycle root of aComponent or a focus traversal
     * policy provider.
     *
     * @param aContainer a focus cycle root of aComponent or focus traversal
     *        policy provider
     * @param aComponent a (possibly indirect) child of aContainer, or
     *        aContainer itself
     * @return the Component that should receive the focus before aComponent,
     *         or null if no suitable Component can be found
     * @throws Error if aContainer is not a focus cycle
     *         root of aComponent or a focus traversal policy provider, or if 
     *         either aContainer or aComponent is null
     */	
	public function getComponentBefore(aContainer:Container, aComponent:Component):Component;
	
    /**
     * Returns the first Component in the traversal cycle. This method is used
     * to determine the next Component to focus when traversal wraps in the
     * forward direction.
     *
     * @param aContainer the focus cycle root or focus traversal policy provider
     *        whose first Component is to be returned
     * @return the first Component in the traversal cycle of aContainer,
     *         or null if no suitable Component can be found
     * @throws Error if aContainer is null
     */	
	public function getFirstComponent(aContainer:Container):Component;
	
    /**
     * Returns the last Component in the traversal cycle. This method is used
     * to determine the next Component to focus when traversal wraps in the
     * reverse direction.
     *
     * @param aContainer the focus cycle root or focus traversal policy
     *        provider whose last Component is to be returned
     * @return the last Component in the traversal cycle of aContainer,
     *         or null if no suitable Component can be found
     * @throws Error if aContainer is null
     */	
	public function getLastComponent(aContainer:Container):Component;
	
    /**
     * Returns the default Component to focus. This Component will be the first
     * to receive focus when traversing down into a new focus traversal cycle
     * rooted at aContainer.
     *
     * @param aContainer the focus cycle root or focus traversal policy
     *        provider whose default Component is to be returned
     * @return the default Component in the traversal cycle of aContainer, 
     *         or null if no suitable Component can be found
     * @throws Error if aContainer is null
     */	
	public function getDefaultComponent(aContainer:Container):Component;
	
    /**
     * Returns the Component that should receive the focus when a Window is
     * made visible for the first time. Once the Window has been made visible
     * by a call to <code>show()</code> or <code>setVisible(true)</code>, the
     * initial Component will not be used again. Instead, if the Window loses
     * and subsequently regains focus, or is made invisible or undisplayable
     * and subsequently made visible and displayable, the Window's most
     * recently focused Component will become the focus owner. The default
     * implementation of this method returns the default Component.
     *
     * @param window the Window whose initial Component is to be returned
     * @return the Component that should receive the focus when window is made
     *         visible for the first time, or null if no suitable Component can
     *         be found
     * @see #getDefaultComponent()
     * @throws Error if window is null
     */	
	public function getInitialComponent(window:JWindow):Component;
	
}
