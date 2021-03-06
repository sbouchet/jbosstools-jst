/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jst.web.ui.test;

import org.jboss.tools.jst.web.ui.internal.preferences.LibSetPreferencePage;
import org.jboss.tools.jst.web.ui.internal.preferences.LibrarySetsPreferencePage;
import org.jboss.tools.tests.PreferencePageTest;
import org.junit.Test;

/**
 * @author eskimo
 *
 */
public class JstWebUiPreferencesPagesTest extends PreferencePageTest {
	
	@Test
	public void testLibrarySetsPreferencePage() {
		doDefaultTest(LibrarySetsPreferencePage.ID,LibrarySetsPreferencePage.class);
	}
}
