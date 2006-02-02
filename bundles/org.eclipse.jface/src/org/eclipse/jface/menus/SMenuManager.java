/******************************************************************************* * Copyright (c) 2005 IBM Corporation and others. * All rights reserved. This program and the accompanying materials * are made available under the terms of the Eclipse Public License v1.0 * which accompanies this distribution, and is available at * http://www.eclipse.org/legal/epl-v10.html * * Contributors: *     IBM Corporation - initial API and implementation ******************************************************************************/package org.eclipse.jface.menus;import java.util.ArrayList;import java.util.Collection;import java.util.HashMap;import java.util.HashSet;import java.util.Map;import java.util.Set;import org.eclipse.core.commands.common.HandleObject;import org.eclipse.core.commands.common.HandleObjectManager;import org.eclipse.jface.util.IPropertyChangeListener;import org.eclipse.jface.util.PropertyChangeEvent;/** * <p> * Manages a group of menu elements. These menu elements include things like * menus, groups, and items. The manager is responsible for handling the layout * of these items with respect to each other. Visibility is controlled * externally by updating the visibility of the menu elements themselves. The * painting of these menu elements on the application window(s) is handler by a * renderer. * </p> * <p> * Clients may instantiate, but must not extend. * </p> * <p> * <strong>EXPERIMENTAL</strong>. This class or interface has been added as * part of a work in progress. There is a guarantee neither that this API will * work nor that it will remain the same. Please do not use this API without * consulting with the Platform/UI team. * </p> *  * @since 3.2 */public final class SMenuManager extends HandleObjectManager implements		IPropertyChangeListener {	/**	 * The map of action set identifiers (<code>String</code>) to action set (	 * <code>SActionSet</code>). This collection may be empty, but it is	 * never <code>null</code>.	 */	private final Map actionSetsById = new HashMap();	/**	 * The set of action sets that are defined. This value may be empty, but it	 * is never <code>null</code>.	 */	private final Set definedActionSets = new HashSet();	/**	 * The set of groups that are defined. This value may be empty, but it is	 * never <code>null</code>.	 */	private final Set definedGroups = new HashSet();	/**	 * The set of identifiers for those menus that are defined. This value may	 * be empty, but it is never <code>null</code>.	 */	private final Set definedMenus = new HashSet();	/**	 * The set of identifiers for those widgets that are defined. This value may	 * be empty, but it is never <code>null</code>.	 */	private final Set definedWidgets = new HashSet();	/**	 * The map of group identifiers (<code>String</code>) to groups (	 * <code>SGroup</code>). This collection may be empty, but it is never	 * <code>null</code>.	 */	private final Map groupsById = new HashMap();	/**	 * The layout for this menu manager. This layout is <code>null</code> if	 * the menu manager has changed since the last layout was built.	 */	private SMenuLayout layout = null;	/**	 * The map of menu identifiers (<code>String</code>) to menus (	 * <code>SMenu</code>). This collection may be empty, but it is never	 * <code>null</code>.	 */	private final Map menusById = new HashMap();	/**	 * The map of widget identifiers (<code>String</code>) to widgets (	 * <code>SWidget</code>). This collection may be empty, but it is never	 * <code>null</code>.	 */	private final Map widgetsById = new HashMap();	/**	 * Adds a listener to this menu manager that will be notified when this	 * manager's state changes.	 * 	 * @param listener	 *            The listener to be added; must not be <code>null</code>.	 */	public final void addListener(final IMenuManagerListener listener) {		addListenerObject(listener);	}	/**	 * Notifies listeners to this menu manager that it has changed in some way.	 * 	 * @param event	 *            The event to fire; may be <code>null</code>.	 */	private final void fireMenuManagerChanged(final MenuManagerEvent event) {		if (event == null) {			return;		}		final Object[] listeners = getListeners();		for (int i = 0; i < listeners.length; i++) {			final IMenuManagerListener listener = (IMenuManagerListener) listeners[i];			listener.menuManagerChanged(event);		}	}	/**	 * Gets the action set with the given identifier. If no such action set	 * currently exists, then the action set will be created (but will be	 * undefined).	 * 	 * @param actionSetId	 *            The identifier to find; must not be <code>null</code> and	 *            must not be zero-length.	 * @return The action set with the given identifier; this value will never	 *         be <code>null</code>, but it might be undefined.	 * @see SActionSet	 */	public final SActionSet getActionSet(final String actionSetId) {		checkId(actionSetId);		SActionSet actionSet = (SActionSet) actionSetsById.get(actionSetId);		if (actionSet == null) {			actionSet = new SActionSet(actionSetId);			actionSetsById.put(actionSetId, actionSet);			actionSet.addListener(this);		}		return actionSet;	}	/**	 * Returns those action sets that are defined.	 * 	 * @return The defined action sets; this value may be empty, but it is never	 *         <code>null</code>.	 */	public final SActionSet[] getDefinedActionSets() {		return (SActionSet[]) definedActionSets				.toArray(new SActionSet[definedActionSets.size()]);	}	/**	 * Returns those groups that are defined.	 * 	 * @return The defined groups; this value may be empty, but it is never	 *         <code>null</code>.	 */	public final SGroup[] getDefinedGroups() {		return (SGroup[]) definedGroups				.toArray(new SGroup[definedGroups.size()]);	}	/**	 * Returns those items that are defined.	 * 	 * @return The defined items; this value may be empty, but it is never	 *         <code>null</code>.	 */	public final SItem[] getDefinedItems() {		return (SItem[]) definedHandleObjects				.toArray(new SItem[definedHandleObjects.size()]);	}	/**	 * Returns those menus that are defined.	 * 	 * @return The defined menus; this value may be empty, but it is never	 *         <code>null</code>.	 */	public final SMenu[] getDefinedMenus() {		return (SMenu[]) definedMenus.toArray(new SMenu[definedMenus.size()]);	}	/**	 * Returns those widgets that are defined.	 * 	 * @return The defined widgets; this value may be empty, but it is never	 *         <code>null</code>.	 */	public final SWidget[] getDefinedWidgets() {		return (SWidget[]) definedWidgets.toArray(new SWidget[definedWidgets				.size()]);	}	/**	 * Gets the group with the given identifier. If no such group currently	 * exists, then the group will be created (but will be undefined).	 * 	 * @param groupId	 *            The identifier to find; must not be <code>null</code> and	 *            must not be zero-length.	 * @return The group with the given identifier; this value will never be	 *         <code>null</code>, but it might be undefined.	 * @see SGroup	 */	public final SGroup getGroup(final String groupId) {		checkId(groupId);		SGroup group = (SGroup) groupsById.get(groupId);		if (group == null) {			group = new SGroup(groupId);			groupsById.put(groupId, group);			group.addListener(this);		}		return group;	}	/**	 * Gets the item with the given identifier. If no such item currently	 * exists, then the item will be created (but will be undefined).	 * 	 * @param itemId	 *            The identifier to find; must not be <code>null</code> and	 *            must not be zero-length.	 * @return The item with the given identifier; this value will never be	 *         <code>null</code>, but it might be undefined.	 * @see SItem	 */	public final SItem getItem(final String itemId) {		checkId(itemId);		SItem item = (SItem) handleObjectsById.get(itemId);		if (item == null) {			item = new SItem(itemId);			handleObjectsById.put(itemId, item);			item.addListener(this);		}		return item;	}	/**	 * <p>	 * Retrieves the layout for the menu elements held by this menu manager.	 * This layout does not consider visibility or whether the elements are	 * currently shown. It is simply the layout if everything was visible and	 * showing. It also does not consider dynamic menu elements, which will be	 * asked to make changes to the layout before the menu element is shown.	 * </p>	 * <p>	 * The result of this computation is cached between subsequent calls. So, if	 * no changes are made to the menu elements, the layout can be retrieved in	 * constant time. Otherwise, it will take <code>O(n)</code> time to	 * compute, where <code>n</code> is the number of menu elements held by	 * this manager.	 * </p>	 * 	 * @return The menu layout; never <code>null</code>.	 */	public final SMenuLayout getLayout() {		if (layout == null) {			// Generate the layout			final Collection menuElements = new ArrayList(definedMenus.size()					+ definedGroups.size() + definedHandleObjects.size()					+ definedWidgets.size());			menuElements.addAll(definedMenus);			menuElements.addAll(definedGroups);			menuElements.addAll(definedHandleObjects);			menuElements.addAll(definedWidgets);			layout = SMenuLayout.computeLayout(menuElements);		}		return layout;	}	/**	 * Gets the menu with the given identifier. If no such menu currently	 * exists, then the menu will be created (but will be undefined).	 * 	 * @param menuId	 *            The identifier to find; must not be <code>null</code> and	 *            must not be zero-length.	 * @return The menu with the given identifier; this value will never be	 *         <code>null</code>, but it might be undefined.	 * @see SMenu	 */	public final SMenu getMenu(final String menuId) {		checkId(menuId);		SMenu menu = (SMenu) menusById.get(menuId);		if (menu == null) {			menu = new SMenu(menuId);			menusById.put(menuId, menu);			menu.addListener(this);		}		return menu;	}	/**	 * Gets the widget with the given identifier. If no such widget currently	 * exists, then the widget will be created (but will be undefined).	 * 	 * @param widgetId	 *            The identifier to find; must not be <code>null</code> and	 *            must not be zero-length.	 * @return The item with the given identifier; this value will never be	 *         <code>null</code>, but it might be undefined.	 * @see SWidget	 */	public final SWidget getWidget(final String widgetId) {		checkId(widgetId);		SWidget widget = (SWidget) widgetsById.get(widgetId);		if (widget == null) {			widget = new SWidget(widgetId);			widgetsById.put(widgetId, widget);			widget.addListener(this);		}		return widget;	}	public final void propertyChange(final PropertyChangeEvent event) {		if (MenuElement.PROPERTY_DEFINED.equals(event.getProperty())) {			final HandleObject handle = (HandleObject) event.getSource();			final String id = handle.getId();			final boolean added = ((Boolean) event.getNewValue())					.booleanValue();			if (added) {				if (handle instanceof SActionSet) {					definedActionSets.add(handle);					if (isListenerAttached()) {						fireMenuManagerChanged(new MenuManagerEvent(this, null,								false, null, false, null, false, null, false,								id, added));					}				} else if (handle instanceof SGroup) {					definedGroups.add(handle);					if (isListenerAttached()) {						fireMenuManagerChanged(new MenuManagerEvent(this, id,								added, null, false, null, false, null, false,								null, false));					}				} else if (handle instanceof SItem) {					definedHandleObjects.add(handle);					if (isListenerAttached()) {						fireMenuManagerChanged(new MenuManagerEvent(this, null,								false, id, added, null, false, null, false,								null, false));					}				} else if (handle instanceof SMenu) {					definedMenus.add(handle);					if (isListenerAttached()) {						fireMenuManagerChanged(new MenuManagerEvent(this, null,								false, null, false, id, added, null, false,								null, false));					}				} else if (handle instanceof SWidget) {					definedWidgets.add(handle);					if (isListenerAttached()) {						fireMenuManagerChanged(new MenuManagerEvent(this, null,								false, null, false, null, false, id, added,								null, false));					}				}			} else {				if (handle instanceof SActionSet) {					definedActionSets.remove(handle);					if (isListenerAttached()) {						fireMenuManagerChanged(new MenuManagerEvent(this, null,								false, null, false, null, false, null, false,								id, added));					}				} else if (handle instanceof SGroup) {					definedGroups.remove(handle);					if (isListenerAttached()) {						fireMenuManagerChanged(new MenuManagerEvent(this, id,								added, null, false, null, false, null, false,								null, false));					}				} else if (handle instanceof SItem) {					definedHandleObjects.remove(handle);					if (isListenerAttached()) {						fireMenuManagerChanged(new MenuManagerEvent(this, null,								false, id, added, null, false, null, false,								null, false));					}				} else if (handle instanceof SMenu) {					definedMenus.remove(handle);					if (isListenerAttached()) {						fireMenuManagerChanged(new MenuManagerEvent(this, null,								false, null, false, id, added, null, false,								null, false));					}				} else if (handle instanceof SWidget) {					definedWidgets.remove(handle);					if (isListenerAttached()) {						fireMenuManagerChanged(new MenuManagerEvent(this, null,								false, null, false, null, false, id, added,								null, false));					}				}			}		}	}	/**	 * Removes a listener from this menu manager.	 * 	 * @param listener	 *            The listener to be removed; must not be <code>null</code>.	 */	public final void removeListener(final IMenuManagerListener listener) {		removeListenerObject(listener);	}}