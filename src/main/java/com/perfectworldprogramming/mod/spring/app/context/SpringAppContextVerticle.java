package com.perfectworldprogramming.mod.spring.app.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.vertx.java.core.Future;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;
import org.vertx.java.platform.Verticle;

import java.util.ArrayList;
import java.util.List;

/**
 * Verticle that will create an ApplicationContext and assign it to a static member.
 *
 * You should only deploy one instance of this Verticle in your application, or one instance of this Module in your application
 * because you only want one ApplicationContext to be created. And even if you do try to deploy more than one instance
 * there will still be only one ApplicationContext created. There is no need or ability to make more than one instance.
 *
 * User: Mark Spritzler
 */
@Deprecated
public class SpringAppContextVerticle extends Verticle {

    private Logger logger = LoggerFactory.getLogger(SpringAppContextVerticle.class);

    private JsonObject config;

    static ApplicationContext applicationContext;

    private void createApplicationContext() {
        logger.debug("Staring to create the ApplicationContext");
        config = getContainer().config();
        String configType = config.getString("configType");
        if (configType == null) {
            throw new IllegalArgumentException("configType is a mandatory configuration that must be set");
        }
        if (ConfigType.XML.getValue().equals(configType)) {
            createXMLBasedApplicationContext();
        } else if (ConfigType.JAVA_CONFIG.getValue().equals(configType)) {
            createJavaConfigBasedApplicationContext();
        } else {
            throw new IllegalArgumentException("illegal configTye: " + configType +
            " must be xml or class");
        }
    }

    private void createXMLBasedApplicationContext() {
        JsonArray jsonArrayOfXmlFiles = config.getValue("configFiles");
        if (jsonArrayOfXmlFiles == null) {
            throw new IllegalArgumentException("xml based context requires configFiles configuration property to be set");
        }
        String[] xmlFiles= new String[jsonArrayOfXmlFiles.size()];
        try {
            // You are really going to make me do this, because JsonArray doesn't have a nice method to convert the type
            for (int i = 0; i< jsonArrayOfXmlFiles.size(); i++) {
                xmlFiles[i] = jsonArrayOfXmlFiles.get(i);
            }
            if (xmlFiles[0] == null) {
                throw new IllegalArgumentException("xml based context requires configFiles configuration property to be set with an array of Strings");
            }
        } catch (ClassCastException notStringsException) {
            throw new IllegalArgumentException("xml based context requires configFiles configuration property to be set with an array of String type only");
        }

        logger.debug("Creating an ApplicationContext with xml configuration");
        applicationContext = new ClassPathXmlApplicationContext(xmlFiles);
        logger.info("Application Context has been created");
    }

    private void createJavaConfigBasedApplicationContext() {
        JsonArray jsonArrayOfClassStrings = config.getValue("configClasses");
        if (jsonArrayOfClassStrings == null) {
            throw new IllegalArgumentException("java config based context requires configClasses configuration property to be set");
        }
        String[] classes = new String[jsonArrayOfClassStrings.size()];
        try {
            // You are really going to make me do this, because JsonArray doesn't have a nice method to convert the type
            for (int i = 0; i< jsonArrayOfClassStrings.size(); i++) {
                classes[i] = jsonArrayOfClassStrings.get(i);
            }
        } catch (ClassCastException notStringsException) {
            throw new IllegalArgumentException("java config based context requires configFiles configuration property to be set with an array of String type only");
        }
        if (classes.length > 0) {
            List<Class> clazzes = new ArrayList<>();
            for (String stringClass : classes) {
                try {
                    Class clazz = Class.forName(stringClass);
                    clazzes.add(clazz);
                } catch (ClassNotFoundException cnfe) {
                    throw new IllegalArgumentException("Invalid class: " + stringClass +
                    ". This must be the fully qualified class of a Spring @Configuration class");
                }
            }
            logger.debug("Creating an ApplicationContext with Java Config classes configuration");
            applicationContext = new AnnotationConfigApplicationContext((Class[])clazzes.toArray());
            logger.info("Application Context has been created");
        }
    }

    @Override
    public void start(Future<Void> startedResult) {
        super.start(startedResult);
        logger = container.logger();
        config = container.config();
        // If someone tries to deploy this more than once, this will make sure we still only create one ApplicationContext instance
        if (applicationContext == null) {
            createApplicationContext();
            startedResult.setResult(null);
        } else {
            startedResult.setResult(null);
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
