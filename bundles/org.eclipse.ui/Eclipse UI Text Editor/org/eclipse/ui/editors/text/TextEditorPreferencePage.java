package org.eclipse.ui.editors.text;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.DialogPageContextComputer;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.AbstractTextEditor;

/**
 * A preference page to set the font used in the default text editor.
 * This preference page uses the text editor's preference bundle and
 * uses the key <code>"PreferencePage.description"</code> to look up
 * the page description. In addition, it uses <code>"PreferencePage.fontEditor"</code>
 * for the editor description.
 */
public class TextEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	
	private ResourceBundle fBundle;
	
	/**
	 * Create the preference page.
	 */
	public TextEditorPreferencePage() {
		super(GRID);
		
		setDescription(getResourceString("PreferencePage.description"));		
		Plugin plugin= Platform.getPlugin(PlatformUI.PLUGIN_ID);
		if (plugin instanceof AbstractUIPlugin) {
			AbstractUIPlugin uiPlugin= (AbstractUIPlugin) plugin;
			setPreferenceStore(uiPlugin.getPreferenceStore());
		}
	}
	/*
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		WorkbenchHelp.setHelp(getControl(), new DialogPageContextComputer(this, ITextEditorHelpContextIds.TEXT_EDITOR_PREFERENCE_PAGE));
	}
	/*
	 * @see FieldEditorPreferencePage#createFieldEditors
	 */
	public void createFieldEditors() {
		addField(new FontFieldEditor(AbstractTextEditor.PREFERENCE_FONT, getResourceString("PreferencePage.fontEditor"), getFieldEditorParent()));
	}
	/**
	 * Returns the resource string for the given key.
	 * 
	 * @param key the key to be used
	 */
	private String getResourceString(String key) {
		
		try {
			if (fBundle == null)
				fBundle= ResourceBundle.getBundle("org.eclipse.ui.editors.text.TextEditorResources");
			return fBundle.getString(key);
		} catch (MissingResourceException x) {
		}
		
		return key;
	}
	/*
	 * @see IWorkbenchPreferencePage#init
	 */
	public void init(IWorkbench workbench) {
		Font font= JFaceResources.getTextFont();
		if (font != null) {
			FontData[] data= font.getFontData();
			if (data != null && data.length > 0)
				PreferenceConverter.setDefault(getPreferenceStore(), AbstractTextEditor.PREFERENCE_FONT, data[0]);
		}
	}
}
