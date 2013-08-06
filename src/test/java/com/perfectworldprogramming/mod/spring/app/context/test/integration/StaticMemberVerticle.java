package com.perfectworldprogramming.mod.spring.app.context.test.integration;

import com.perfectworldprogramming.mod.spring.app.context.SpringAppContextVerticle;
import org.springframework.context.ApplicationContext;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
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
      }
    });
  }
}
