/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.container.glassfish.embedded_3_1.app;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MultiDeploymentsTestCase {

    @Deployment(order = 1, name = "normal")
    public static WebArchive generateDefaultDeployment() {
        return ShrinkWrap.create(WebArchive.class, "normal.war")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addClasses(MultiDeploymentsTestCase.class
                    ,NameProvider.class
                    ,NameProviderExtendedImpl.class);
    }

    @Deployment(order = 0, name = "extended")
    public static Archive generateExtendedDeployment() {
        Archive ejb = ShrinkWrap.create(JavaArchive.class, "extra_ejb.jar")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addClasses(MultiDeploymentsTestCase.class
                    ,NameProvider.class
                    ,NameProviderExtendedImpl.class);
        return ShrinkWrap.create(EnterpriseArchive.class, "extra.ear").addAsLibraries(ejb);
    }
    
    @Test
    @OperateOnDeployment("normal")
    public void shouldInjectNormal(NameProvider bean) {
        Assert.assertNotNull(
                "Verify that the Bean has been injected",
                bean);

        Assert.assertEquals(NameProviderNormalImpl.NAME, bean.getName());
    }
    
    @Test
    @OperateOnDeployment("extended")
    public void shouldInjectExtended(NameProvider bean) {
        Assert.assertNotNull(
                "Verify that the Bean has been injected",
                bean);

        Assert.assertEquals(NameProviderExtendedImpl.NAME, bean.getName());
    }

}
