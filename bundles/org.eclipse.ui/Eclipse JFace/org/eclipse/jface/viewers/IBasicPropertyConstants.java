package org.eclipse.jface.viewers;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

/**
 * Predefined property names used for elements displayed in viewers.
 *
 * @see StructuredViewer#update(Object, String[])
 * @see StructuredViewer#update(Object[], String[])
 * @see IBaseLabelProvider#isLabelProperty
 * @see ViewerSorter#isSorterProperty
 * @see ViewerFilter#isFilterProperty
 */
public interface IBasicPropertyConstants {
	
	/**
	 * Property name constant (value <code>"org.eclipse.jface.text"</code>)
	 * for an element's label text.
	 *
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText
	 */
	public static final String P_TEXT = "org.eclipse.jface.text"; //$NON-NLS-1$

	
	/**
	 * Property name constant (value <code>"org.eclipse.jface.image"</code>)
	 * for an element's label image.
	 *
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage
	 */
	public static final String P_IMAGE = "org.eclipse.jface.image"; //$NON-NLS-1$
	
	
	/**
	 * Property name constant (value <code>"org.eclipse.jface.children"</code>)
	 * for an element's children.
	 */
	public static final String P_CHILDREN = "org.eclipse.jface.children"; //$NON-NLS-1$
	
	/**
	 * Property name constant (value <code>"org.eclipse.jface.parent"</code>)
	 * for an element's parent object.
	 */
	public static final String P_PARENT = "org.eclipse.jface.parent"; //$NON-NLS-1$
	
}
