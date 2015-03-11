/*******************************************************************************
 * Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Composent, Inc. - initial API and implementation
 ******************************************************************************/
package org.eclipse.ecf.mgmt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SerializationUtil {

	public static boolean isSerializable(Object o) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new ByteArrayOutputStream());
			oos.writeObject(o);
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (oos != null)
					oos.close();
			} catch (IOException e) {
				// do nothing
			}
		}
	}

	public static Throwable checkForSerializable(Throwable t) {
		if (isSerializable(t))
			return t;
		else {
			String message = t.getMessage();
			if (t instanceof RuntimeException)
				return new RuntimeException(message);
			else if (t instanceof Exception)
				return new Exception(message);
			return new Throwable(message);
		}
	}

}
