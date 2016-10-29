/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 * Based upon work presented here http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/MqttConnectDialog.htm
 * 
 ******************************************************************************/
package org.eclipse.ecf.mgmt.framework.eclipse.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EcfTcpConnectDialog extends Dialog {
	private static final int RESET_ID = IDialogConstants.NO_TO_ALL_ID + 1;

	private Text hostnameField;

	private Text portField;
	
	private String hostnameDefault;
	private String portDefault;

	private String title;

	public EcfTcpConnectDialog(Shell parentShell, String title, String hostnameDefault, String portDefault) {
		super(parentShell);
		this.hostnameDefault = hostnameDefault==null?"":hostnameDefault;
		this.portDefault = portDefault==null?"":portDefault;
		this.title = title;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}

	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);

		GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = 2;

		Label hostnameLabel = new Label(comp, SWT.RIGHT);
		hostnameLabel.setText("Hostname: ");
		hostnameField = new Text(comp, SWT.SINGLE);
		hostnameField.setText(hostnameDefault);

		Label portLabel = new Label(comp, SWT.RIGHT);
		portLabel.setText("Port: ");

		portField = new Text(comp, SWT.SINGLE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		portField.setLayoutData(data);
		portField.setText(portDefault);

		return comp;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		createButton(parent, RESET_ID, "Reset All", false);
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == RESET_ID) {
			hostnameField.setText(this.hostnameDefault);
			portField.setText(this.portDefault);
		} else 
			super.buttonPressed(buttonId);
	}

	private String hostname;
	private String port;

	@Override
	protected void okPressed() {
		this.hostname = hostnameField.getText();
		this.port = portField.getText();
		super.okPressed();
	}

	public String getHostname() {
		if (hostname == null || "".equals(hostname))
			return null;
		return hostname;
	}

	public String getPort() {
		if (port == null || "".equals(port))
			return null;
		return port;
	}

}