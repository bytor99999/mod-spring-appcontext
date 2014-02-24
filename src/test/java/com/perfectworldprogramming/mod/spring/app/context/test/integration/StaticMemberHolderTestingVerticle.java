package com.perfectworldprogramming.mod.spring.app.context.test.integration;

import com.perfectworldprogramming.mod.spring.app.context.ConfigType;
import com.perfectworldprogramming.mod.spring.app.context.SpringApplicationContextHolder;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

import javax.swing.*;

/**
 * User: Mark Spritzler
 * Date: 8/6/13
 * Time: 2:43 PM
 */
public class StaticMemberHolderTestingVerticle extends TestVerticle {

  @Test
  public void test() {
      JsonObject configFiles = new JsonObject();
      JsonArray xmlFilesArray = new JsonArray();
      xmlFilesArray.add("spring/another-config.xml");
      xmlFilesArray.add("spring/test-application-config.xml");
      configFiles.putArray("configFiles", xmlFilesArray);
      configFiles.putString("configType", ConfigType.XML.getValue());
      SpringApplicationContextHolder.setVertx(vertx);
      SpringApplicationContextHolder.createApplicationContext(configFiles);

      vertx.eventBus().registerHandler("test", new Handler<Message>() {
          @Override
          public void handle(Message event) {
              System.out.println("I received an event: " + event.body().toString());
              ApplicationContext context = SpringApplicationContextHolder.getApplicationContext();
              VertxAssert.assertNotNull("Vertx not available from spring context", context.getBean("vertx"));
              VertxAssert.assertNotNull("Vertx not available from spring context", context.getBean(Vertx.class));
              VertxAssert.assertNotNull("EventBus not available from spring context", context.getBean("eventBus"));
              VertxAssert.assertNotNull("EventBus not available from spring context", context.getBean(EventBus.class));
              if (context == null) {
                  System.out.println("Doesn't work inside another project's Verticle. context is null");
              } else {
                  System.out.println("****************************************************");
                  System.out.println("Accessing the ApplicationContext in another Verticle/Module");
                  String[] beanNames = context.getBeanDefinitionNames();
                  for (String name: beanNames) {
                      System.out.println(name);
                  }
              }
              event.reply("Got your message, thanks");
              VertxAssert.testComplete();
          }
      });

      vertx.eventBus().send("test", "BooYah");
  }
}
