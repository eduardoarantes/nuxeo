/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.runtime;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.ComponentManager;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.Extension;
import org.nuxeo.runtime.test.NXRuntimeTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class ComponentDeploymentTest extends NXRuntimeTestCase {

    @Test
    public void testContributions() throws Exception {
        deployContrib("org.nuxeo.runtime.test.tests", "MyComp1.xml");
        deployContrib("org.nuxeo.runtime.test.tests", "MyComp2.xml");
        try {
            RuntimeService runtime = Framework.getRuntime();
            ComponentManager mgr = runtime.getComponentManager();
            assertTrue(mgr.size() > 0);

            ComponentInstance co = runtime.getComponentInstance("service:my.comp1");
            assertNotNull(co);
            assertEquals(co.getName(), new ComponentName("service:my.comp1"));

            co = runtime.getComponentInstance("service:my.comp2");
            assertNotNull(co);
            assertEquals(co.getName(), new ComponentName("service:my.comp2"));

            mgr.unregister(new ComponentName("service:my.comp2"));
            co = runtime.getComponentInstance("service:my.comp2");
            assertNull(co);
            co = runtime.getComponentInstance("service:my.comp1");
            assertNotNull(co);
        } finally {
            undeployContrib("org.nuxeo.runtime.test.tests", "MyComp2.xml");
            undeployContrib("org.nuxeo.runtime.test.tests", "MyComp1.xml");
        }
    }

    @Test
    public void testStartupStatus() throws Exception {
        deployContrib("org.nuxeo.runtime.test.tests", "CompA.xml");
        deployContrib("org.nuxeo.runtime.test.tests", "CompB.xml");
        try {
            RuntimeService runtime = Framework.getRuntime();
            ComponentManager mgr = runtime.getComponentManager();
            assertTrue(mgr.size() > 0);
            System.err.println(mgr.getRegistrations());
            // check pending registrations
            Map<ComponentName, Set<ComponentName>> pending = mgr.getPendingRegistrations();
            assertEquals(1, pending.size());
            assertTrue(pending.containsKey(new ComponentName("CompA")));
            assertEquals("[service:CompC]", pending.get(new ComponentName("CompA")).toString());
            // check missing registrations
            Map<ComponentName, Set<Extension>> missing = mgr.getMissingRegistrations();
            assertEquals(1, missing.size());
            assertTrue(missing.containsKey(new ComponentName("CompB")));
            assertEquals(
                    "[ExtensionImpl {target: service:my.comp3, point:xp, contributor:RegistrationInfo: service:CompB}, "
                            + "ExtensionImpl {target: service:my.comp4, point:xp, contributor:RegistrationInfo: service:CompB}]",
                    missing.get(new ComponentName("CompB")).toString());
            StringBuilder builder = new StringBuilder();
            assertFalse(runtime.getStatusMessage(builder));
            assertEquals(
                    "======================================================================\n"
                            + "= Component Loading Status: Pending: 1 / Missing: 1 / Unstarted: 0 / Total: 10\n"
                            + "  * service:CompA requires [service:CompC]\n"
                            + "  * service:CompB references missing [target=my.comp3;point=xp, target=my.comp4;point=xp]\n"
                            + "======================================================================",
                    builder.toString());
        } finally {
            undeployContrib("org.nuxeo.runtime.test.tests", "CompB.xml");
            undeployContrib("org.nuxeo.runtime.test.tests", "CompA.xml");
        }
    }

}
