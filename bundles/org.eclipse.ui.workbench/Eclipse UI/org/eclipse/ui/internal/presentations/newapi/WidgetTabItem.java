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

import org.eclipse.swt.widgets.Widget;

/**
 * @since 3.1
 */
public abstract class WidgetTabItem extends AbstractTabItem {

    private Object data;
    private Widget widget;
    
    public WidgetTabItem(Widget theWidget) {
        this.widget = theWidget;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.presentations.newapi.AbstractTabItem#dispose()
     */
    public void dispose() {
        widget.dispose();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.presentations.newapi.AbstractTabItem#getData()
     */
    public Object getData() {
        return data;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.presentations.newapi.AbstractTabItem#setData(java.lang.Object)
     */
    public void setData(Object data) {
        this.data = data;
    }

    public Widget getWidget() {
        return widget;
    }
}
