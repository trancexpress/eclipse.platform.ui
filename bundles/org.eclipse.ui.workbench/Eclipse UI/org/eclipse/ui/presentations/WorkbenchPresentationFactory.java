/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.presentations;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.preferences.IDynamicPropertyMap;
import org.eclipse.ui.internal.presentations.defaultpresentation.DefaultMultiTabListener;
import org.eclipse.ui.internal.presentations.defaultpresentation.DefaultSimpleTabListener;
import org.eclipse.ui.internal.presentations.defaultpresentation.DefaultTabFolder;
import org.eclipse.ui.internal.presentations.defaultpresentation.DefaultTabPositionListener;
import org.eclipse.ui.internal.presentations.defaultpresentation.DefaultThemeListener;
import org.eclipse.ui.internal.presentations.newapi.PresentablePartFolder;
import org.eclipse.ui.internal.presentations.newapi.StandardEditorSystemMenu;
import org.eclipse.ui.internal.presentations.newapi.StandardViewSystemMenu;
import org.eclipse.ui.internal.presentations.newapi.TabbedStackPresentation;

/**
 * The default presentation factory for the Workbench.
 * 
 * @since 3.0
 */
public class WorkbenchPresentationFactory extends AbstractPresentationFactory {

    /* (non-Javadoc)
     * @see org.eclipse.ui.presentations.AbstractPresentationFactory#createEditorPresentation(org.eclipse.swt.widgets.Composite, org.eclipse.ui.presentations.IStackPresentationSite)
     */
    public StackPresentation createEditorPresentation(Composite parent,
            IStackPresentationSite site) {
        DefaultTabFolder folder = new DefaultTabFolder(parent, SWT.BORDER, 
                site.supportsState(IStackPresentationSite.STATE_MINIMIZED), 
                site.supportsState(IStackPresentationSite.STATE_MAXIMIZED));
        
        PresentablePartFolder partFolder = new PresentablePartFolder(folder);
        
        TabbedStackPresentation result = new TabbedStackPresentation(site, partFolder, 
                new StandardEditorSystemMenu(site));
        
        DefaultThemeListener themeListener = new DefaultThemeListener(folder, result.getTheme());
        result.getTheme().addListener(themeListener);
        
        IDynamicPropertyMap workbenchPreferences = result.getPluginPreferences(WorkbenchPlugin.getDefault()); 
        
        DefaultTabPositionListener tabListener = new DefaultTabPositionListener(
                workbenchPreferences, 
                IPreferenceConstants.EDITOR_TAB_POSITION, folder);
        
        DefaultMultiTabListener multiTabListener = new DefaultMultiTabListener(workbenchPreferences,
                IPreferenceConstants.SHOW_MULTIPLE_EDITOR_TABS, folder);

        DefaultSimpleTabListener simpleTabListener = new DefaultSimpleTabListener(
                result.getApiPreferences(),
                IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, folder);        
        
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.presentations.AbstractPresentationFactory#createViewPresentation(org.eclipse.swt.widgets.Composite, org.eclipse.ui.presentations.IStackPresentationSite)
     */
    public StackPresentation createViewPresentation(Composite parent,
            IStackPresentationSite site) {
        
        DefaultTabFolder folder = new DefaultTabFolder(parent, SWT.BORDER, 
                site.supportsState(IStackPresentationSite.STATE_MINIMIZED), 
                site.supportsState(IStackPresentationSite.STATE_MAXIMIZED));

        PresentablePartFolder partFolder = new PresentablePartFolder(folder);
        
        folder.setUnselectedCloseVisible(false);
        folder.setUnselectedImageVisible(false);
        
        TabbedStackPresentation result = new TabbedStackPresentation(site, partFolder, 
                new StandardViewSystemMenu(site));
        
        DefaultThemeListener themeListener = new DefaultThemeListener(folder, result.getTheme());
        result.getTheme().addListener(themeListener);
        
        DefaultTabPositionListener tabListener = new DefaultTabPositionListener(
                result.getPluginPreferences(WorkbenchPlugin.getDefault()), 
                IPreferenceConstants.VIEW_TAB_POSITION, folder);
        
        DefaultSimpleTabListener simpleTabListener = new DefaultSimpleTabListener(result.getApiPreferences(),
                IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, folder);

        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.presentations.AbstractPresentationFactory#createStandaloneViewPresentation(org.eclipse.swt.widgets.Composite, org.eclipse.ui.presentations.IStackPresentationSite, boolean)
     */
    public StackPresentation createStandaloneViewPresentation(Composite parent,
            IStackPresentationSite site, boolean showTitle) {
        return createViewPresentation(parent, site);
    }    

}