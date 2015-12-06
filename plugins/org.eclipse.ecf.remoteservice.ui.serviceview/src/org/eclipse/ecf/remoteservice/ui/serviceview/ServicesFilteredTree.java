/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.remoteservice.ui.serviceview;

import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

public class ServicesFilteredTree extends FilteredTree {

	private AbstractServicesView servicesView;
	
	public ServicesFilteredTree(AbstractServicesView view, Composite parent, int treeStyle, PatternFilter filter) {
		super(parent, treeStyle, filter, true);
		this.servicesView = view;
	}
	
	protected void createControl(Composite parent, int treeStyle) {
		super.createControl(parent, treeStyle);

		// add 2px margin around filter text

		FormLayout layout = new FormLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		setLayout(layout);

		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(100, 0);
		if (showFilterControls) {
			FormData filterData= new FormData();
			filterData.top = new FormAttachment(0, 2);
			filterData.left = new FormAttachment(0, 2);
			filterData.right = new FormAttachment(100, -2);
			filterComposite.setLayoutData(filterData);
			data.top = new FormAttachment(filterComposite, 2);
		} else {
			data.top = new FormAttachment(0, 0);
		}
		treeComposite.setLayoutData(data);
	}

	protected void updateToolbar(boolean visible) {
		super.updateToolbar(visible);

		// update view title on viewer's toolbar update
		servicesView.updateTitle();
	}

}
