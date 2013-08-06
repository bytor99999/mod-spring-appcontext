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
import org.vertx.testtools.TestVerticleInfo;

/**
 * User: Mark Spritzler
 * Date: 8/6/13
 * Time: 2:48 PM
 */
@TestVerticleInfo(includes = "bytor99999~mod-spring-appcontext~1.0.0-RC2")
public class TestSpringAppContextAsDeploy extends TestVerticle{

    ApplicationContext context;

    @Test
    public void testStaticAppContext() {
        initialize();

        JsonObject configFiles = new JsonObject();
        JsonArray xmlFilesArray = new JsonArray();
        xmlFilesArray.add("spring/testing-separate-module-config.xml");

        configFiles.putArray("configFiles", xmlFilesArray);
        configFiles.putString("configType", ConfigType.XML.getValue());

        container.deployVerticle("com.perfectworldprogramming.mod.spring.app.context.SpringAppContextVerticle",
            configFiles,
            new Handler<AsyncResult<String>>() {
                @Override
                public void handle(AsyncResult<String> event) {
                    context = SpringAppContextVerticle.getApplicationContext();
                    if (context == null) {
                        System.out.println("Doesn't work deploying as just a Verticle. context is null");
                    } else {
                        String[] beanNames = context.getBeanDefinitionNames();
                        for (String name: beanNames) {
                            System.out.println(name);
                        }
                        container.deployVerticle("com.perfectworldprogramming.mod.spring.app.context.test.integration.StaticMemberVerticle",
                            new Handler<AsyncResult<String>>() {
                                @Override
                                public void handle(AsyncResult<String> event) {
                                    vertx.eventBus().send("test", "Hello World");
                                }
                            }
                        );
                    }
                }
            }
        );

        container.deployModule("bytor99999~mod-spring-appcontext~1.0.0-RC2",
            configFiles,
            new Handler<AsyncResult<String>>() {
                @Override
                public void handle(AsyncResult<String> event) {
                    context = SpringAppContextVerticle.getApplicationContext();
                    if (context == null) {
                        System.out.println("Doesn't work deploying as Module. context is null");
                    } else {
                        String[] beanNames = context.getBeanDefinitionNames();
                        for (String name : beanNames) {
                            System.out.println(name);
                        }
                        container.deployVerticle("com.perfectworldprogramming.mod.spring.app.context.test.integration.StaticMemberVerticle",
                            new Handler<AsyncResult<String>>() {
                                @Override
                                public void handle(AsyncResult<String> event) {
                                  vertx.eventBus().send("test", "Hello World");
                                }
                            }
                        );
                    }
                }
            }
        );
    }
}
