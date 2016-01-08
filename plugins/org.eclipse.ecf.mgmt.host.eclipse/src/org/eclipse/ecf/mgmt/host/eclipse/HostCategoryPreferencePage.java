/*******************************************************************************
 * Copyright (c) 2016 Composent, Inc. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Scott Lewis - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt.host.eclipse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.Constants;

public class HostCategoryPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	static final String EXPORT_LABEL = "  Export Services  ";
	static final String UNEXPORT_LABEL = "Unexport Services";

	private boolean enabled = true;
	private Composite editorComposite;
	private StringFieldEditor hostnameEditor;
	private IntegerFieldEditor portEditor;
	private Button rsaButton;
	private boolean changed = false;

	class LaunchServicesProgressMonitorDialog extends ProgressMonitorDialog {
		public LaunchServicesProgressMonitorDialog() {
			super(HostCategoryPreferencePage.this.getShell());
		}
	}

	public HostCategoryPreferencePage() {
	}

	public void init(IWorkbench workbench) {
		Activator.getDefault().stopCompositeDiscoveryBundle();
		setDescription(
				"To export services for remote access, specify this machine's external Hostname (or inet address), Port, and select 'Export Services'.");
		this.enabled = !Activator.getDefault().isServiceManagerRegistered();
		String localName = null;
		try {
			localName = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			localName = "localhost"; //$NON-NLS-1$
		}
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue(Activator.HOSTNAME_PREF, localName);
		setPreferenceStore(store);
	}

	@Override
	protected void createFieldEditors() {
		editorComposite = getFieldEditorParent();
		hostnameEditor = new StringFieldEditor(Activator.HOSTNAME_PREF, "Hostname", editorComposite);
		hostnameEditor.setEmptyStringAllowed(false);
		hostnameEditor.setEnabled(enabled, editorComposite);
		addField(hostnameEditor);
		portEditor = new IntegerFieldEditor(Activator.PORT_PREF, "Port", editorComposite, 5);
		portEditor.setEmptyStringAllowed(false);
		portEditor.setEnabled(enabled, editorComposite);
		addField(portEditor);
		noDefaultAndApplyButton();
	}

	private String getHostname() {
		return hostnameEditor.getStringValue();
	}

	private Integer getPort() {
		return portEditor.getIntValue();
	}

	private Dictionary<String, Object> getRSProperties() {
		Properties defaultProps = new Properties();
		try {
			defaultProps.load(Activator.getDefault().getBundle().getEntry("OSGI-INF/default.properties").openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Hashtable<String, Object> props = new Hashtable<String, Object>();
		for (Object p : defaultProps.keySet()) {
			String key = (String) p;
			props.put(key, defaultProps.get(key));
		}
		props.put(Constants.SERVICE_EXPORTED_CONFIGS, Activator.GENERIC_CONFIG);
		props.put(Activator.GENERIC_CONFIG_HOSTNAME, getHostname());
		props.put(Activator.GENERIC_CONFIG_PORT, getPort().toString());
		return props;
	}

	@Override
	protected void contributeButtons(Composite parent) {
		rsaButton = new Button(parent, SWT.PUSH);
		rsaButton.setText(enabled ? EXPORT_LABEL : UNEXPORT_LABEL);
		rsaButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleSelection();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				handleSelection();
			}
		});
		((GridLayout) parent.getLayout()).numColumns++;
	}

	private void setEnabled(boolean value) {
		rsaButton.setText(value ? EXPORT_LABEL : UNEXPORT_LABEL);
		hostnameEditor.setEnabled(value, editorComposite);
		portEditor.setEnabled(value, editorComposite);
		enabled = value;
	}

	private void handleSelection() {
		final Dictionary<String, Object> props = getRSProperties();
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					if (enabled) {
						monitor.beginTask("Exporting service manager...", 100);
						monitor.worked(10);
						Activator.getDefault().registerServiceManager(props);
						monitor.worked(50);
						monitor.setTaskName("Exporting RSA manager...");
						Activator.getDefault().registerRSAManager(props);
						monitor.worked(40);
						enabled = false;
						changed = true;
					} else {
						monitor.beginTask("Unexporting service manager...", 100);
						Activator.getDefault().unregisterServiceManager();
						monitor.worked(50);
						monitor.setTaskName("Unexporting RSA manager...");
						Activator.getDefault().unregisterRSAManager();
						monitor.worked(50);
						enabled = true;
						changed = true;
					}
				} catch (InterruptedException e) {
					throw e;
				} catch (Throwable e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			new LaunchServicesProgressMonitorDialog().run(true, true, runnable);
		} catch (InvocationTargetException | InterruptedException e) {
			Activator a = Activator.getDefault();
			if (a != null) {
				a.getLog().log(new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
						"Exception registering remoe managment services", e));
				a.unregisterServiceManager();
				a.unregisterServiceManager();
				this.enabled = true;
			}
		}
		setEnabled(enabled);
	}

	@Override
	public boolean performOk() {
		if (!changed)
			if (MessageDialog.openQuestion(getShell(), "No export",
					"You've not yet exported the management services.  Would you like to?"))
				handleSelection();
		return super.performOk();
	}
}
