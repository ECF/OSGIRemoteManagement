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

public class MqttConnectDialog extends Dialog {
	private static final int RESET_ID = IDialogConstants.NO_TO_ALL_ID + 1;

	private Text brokerUrlField;

	private Text usernameField;

	private Text passwordField;

	private String brokerUrlDefault;

	private String title;

	public MqttConnectDialog(Shell parentShell, String title, String brokerUrlDefault) {
		super(parentShell);
		this.brokerUrlDefault = brokerUrlDefault;
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

		Label brokerUrlLabel = new Label(comp, SWT.RIGHT);
		brokerUrlLabel.setText("Broker URL: ");
		brokerUrlField = new Text(comp, SWT.SINGLE);
		if (brokerUrlDefault != null)
			brokerUrlField.setText(brokerUrlDefault);

		Label usernameLabel = new Label(comp, SWT.RIGHT);
		usernameLabel.setText("Username: ");

		usernameField = new Text(comp, SWT.SINGLE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		usernameField.setLayoutData(data);

		Label passwordLabel = new Label(comp, SWT.RIGHT);
		passwordLabel.setText("Password: ");

		passwordField = new Text(comp, SWT.SINGLE | SWT.PASSWORD);
		data = new GridData(GridData.FILL_HORIZONTAL);
		passwordField.setLayoutData(data);

		return comp;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		createButton(parent, RESET_ID, "Reset All", false);
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == RESET_ID) {
			brokerUrlField.setText(this.brokerUrlDefault);
			usernameField.setText("");
			passwordField.setText("");
		} else {
			super.buttonPressed(buttonId);
		}
	}

	private String brokerUrl;
	private String username;
	private String password;

	@Override
	protected void okPressed() {
		this.brokerUrl = brokerUrlField.getText();
		this.username = usernameField.getText();
		this.password = passwordField.getText();
		super.okPressed();
	}

	public String getBrokerUrl() {
		if (brokerUrl == null || "".equals(brokerUrl))
			return null;
		return brokerUrl;
	}

	public String getUsername() {
		if (username == null || "".equals(username))
			return null;
		return username;
	}

	public String getPassword() {
		if (password == null || "".equals(password))
			return null;
		return password;
	}
}