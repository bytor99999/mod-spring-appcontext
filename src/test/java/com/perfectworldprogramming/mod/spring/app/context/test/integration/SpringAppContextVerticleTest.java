package com.perfectworldprogramming.mod.spring.app.context.test.integration;

import com.perfectworldprogramming.mod.spring.app.context.ConfigType;
import com.perfectworldprogramming.mod.spring.app.context.SpringAppContextVerticle;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import static org.junit.Assert.*;

/*
 * Copyright 2013 Perfect World Programming, LLC.
 *
 * Perfect World Programming licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * @author <a href="http://www.perfectworldprogramming.com">Mark Spritzler</a>
 */
public class SpringAppContextVerticleTest extends TestVerticle {
    DeployComplete testerAfterDeploy = new DeployComplete();

    @Test
    public void testVerticleWithXmlFiles() {
        initialize();

        JsonObject configFiles = new JsonObject();
        JsonArray xmlFilesArray = new JsonArray();
        xmlFilesArray.add("spring/test-application-config.xml");
        xmlFilesArray.add("spring/another-config.xml");
        configFiles.putArray("configFiles", xmlFilesArray);
        configFiles.putString("configType", ConfigType.XML.getValue());

        // It is required to wait till the SpringAppContextVerticle deploys before doing anything else
        container.deployVerticle(SpringAppContextVerticle.class.getName(), configFiles, testerAfterDeploy);
    }

    @Test
    public void testWithConfigClasses() {
        initialize();

        JsonObject configClasses = new JsonObject();
        JsonArray configClassesArray = new JsonArray();
        configClassesArray.add("TestJavaConfiguration");
        configClassesArray.add("AnotherJavaConfiguration");
        configClasses.putArray("configClasses", configClassesArray);
        configClasses.putString("configType", ConfigType.JAVA_CONFIG.getValue());

        // It is required to wait till the SpringAppContextVerticle deploys before doing anything else
        container.deployVerticle(SpringAppContextVerticle.class.getName(), configClasses, testerAfterDeploy);
    }

    private class DeployComplete implements Handler<AsyncResult<String>> {
        @Override
        public void handle(AsyncResult<String> event) {
            ApplicationContext context = SpringAppContextVerticle.getApplicationContext();
            assertNotNull(context);
            String[] beanNames = context.getBeanDefinitionNames();
            for (String name: beanNames) {
                System.out.println(name);
            }

            String helloWorld = context.getBean("helloWorld", String.class);
            String goodbyeWorld = context.getBean("goodbyeWorld", String.class);

            System.out.println(helloWorld);
            System.out.println(goodbyeWorld);
            assertEquals("We should have said Hello", "Hello World", helloWorld);
            assertEquals("We should have said GoodBye", "GoodBye Cruel World", goodbyeWorld);

            VertxAssert.testComplete();
        }
    }

}
