package com.perfectworldprogramming.mod.spring.app.context.test.integration;

import com.perfectworldprogramming.mod.spring.app.context.SpringAppContextVerticle;
import org.springframework.context.ApplicationContext;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;

/**
 * User: Mark Spritzler
 * Date: 8/6/13
 * Time: 2:43 PM
 */
public class StaticMemberVerticle extends Verticle {

  @Override
  public void start() {
      vertx.eventBus().registerHandler("test", new Handler<Message>() {
          @Override
          public void handle(Message event) {
              System.out.println("I received an event: " + event.body().toString());
              ApplicationContext context = SpringAppContextVerticle.getApplicationContext();
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
           }
      });
  }
}
