/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.presentations.newapi;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.presentations.IPresentablePart;

/**
 * @since 3.1
 */
public final class PartInfo {
    public String name;
    public String title;
    public String contentDescription;
    public String toolTip;
    public Image image;
    public boolean dirty;
    
    public PartInfo() {
        name = "";
        title = "";
        contentDescription = "";
        toolTip = "";
        image = null;
    }
    
    public PartInfo(IPresentablePart part) {
        set(part);
    }
    
    public void set(IPresentablePart part) {
        name = part.getName();
        title = part.getTitle();
        contentDescription = part.getTitleStatus();
        image = part.getTitleImage();
        toolTip = part.getTitleToolTip();
        dirty = part.isDirty();
    }
}
