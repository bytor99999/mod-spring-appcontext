README.MD

#The Mod-Spring-AppContext module

This module creates a Spring ApplicationContext so that you can use the Spring Framework in your Vert.x applications.

<pre>
*note
This module does not instantiate any Vert.x objects and making them beans. If you want your Vert.x objects to be
Spring beans, then I recommend Pidster's Spring modules, or the architectural route of using
Embedded Vert.x Platform into your Spring application.
</pre>

In order to use this module, the module's classes will also need to be in the classpath of any module you want to use
this in. That way you will have access to the SpringAppContextVerticle class in your code to retrieve the ApplicationContext
within your module.

This module must be deployed before your other modules, in order for the ApplicationContext to complete instantiation
of its beans before other modules can use them. This means because Vert.x deploys modules Asynchronously, you will need
to first deploy this module, then in the callback of deploy for this module, then deploy all your other modules.

<pre>
*note
Vert.x does have versions of deploy that is not asynchronous, however, you will tend to use the asynchronous ones to
get your Vert.x application started up faster.
</pre>

This is a requirement in order to run the code that creates the ApplicationContext done first.

Here is an example of how to deploy this module correctly. Please note, this is code from the integration test
included with this module. Typically, you will have the configFiles information in a json config file of your
own module that is using this module.

Here are the properties for the config.json file
<pre>
"configType":"xml"|"class"
</pre>
configType is mandatory, it tells this module what type of ApplicationContext to create. If it is set to "xml"
then it will use a ClassPathXmlApplicationContext. So it will expect a "configFiles" attribute also in your config.json.
If it is set to "class" then it will use an AnnotationConfigApplicationContext. So it will expect a "configClasses"
attribute also in your config.json. With configType you specify one or the other, and require one of "configFiles" or
"configClasses" but never both.
<pre>
"configFiles" : {"config1.xml", "config2.xml", ...}
</pre>
configFiles is required if configType (above) is set to "xml". This is an array of Spring xml configuration file(s)
as Strings that will be passed to the constructor of the ClassPathXmlApplicationContext that gets created
<pre>
"configClasses" : {"com.company.configuration.MyConfigClass", "com.company.configuration.OtherConfigClass"}
</pre>
<i>configClasses is required if configType (above) is set to "class". This is an array of Spring Java @Configuration
fully qualified classes as Strings (no ".class") that will be passed to the constructor of the
AnnotationConfigApplicationContext that gets created.

```Java
JsonObject configFiles = new JsonObject();
JsonArray xmlFilesArray = new JsonArray();
xmlFilesArray.add("spring/test-application-config.xml");
xmlFilesArray.add("spring/another-config.xml");
configFiles.putArray("configFiles", xmlFilesArray);
configFiles.putString("configType", ConfigType.XML.getValue());

container.deployVerticle(SpringAppContextVerticle.class.getName(), configFiles, new DeployComplete());


private class DeployComplete implements Handler<AsyncResult<String>> {
    @Override
    public void handle(AsyncResult<String> event) {
        ApplicationContext context = SpringAppContextVerticle.getApplicationContext();
    }
}
```

Also typically, the DeployComplete handle() code would be where you then deploy your own Verticle and
Modules. This code would be in an Application Manager class like those discussed on the Vert.x documentation
(http://vertx.io/core_manual_java.html#using-a-verticle-to-co-ordinate-loading-of-an-application).

Make sure you also "include" this module in your module's mod.json like
```
{
  "includes":"io.vertx~mod-spring-appcontext~1.0.0-B1"
}
```
And if you need more Spring jars than Core Spring then you must create another module that
only contains those other Spring Jars and use includes in your module to load these jars into a parent
classloader, so that the Spring jars are available to all modules, Verticles in your module.
Yes, it is a bit convoluted to add jar files with classes that need to be available through your Vert.x application
but that is how Vert.x works. Please see the Vert.x Modules documentation that better explains how this all works.
Because, it confuses me too. But it does work.

also add "includes" for those modules that have the Spring jar

For you convenience, I have put in the gradle.properties versions of Spring Data and Spring Integration as
recommendations of versions of those projects to use with the version of Spring this module currently uses.