Setup
=====

In Metro application, add the dependencies:

    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>jul-to-slf4j</artifactId>
        <version>1.7.11</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.1.3</version>
    </dependency>

Make sure to enable `SLF4JBridgeHandler`. For example, install it via Servlet WebListener:

    @WebListener
    public class JavaUtilLoggingToSlf4BridgeInitializer implements ServletContextListener {

        @Override
        public void contextInitialized(ServletContextEvent sce) {
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
             SLF4JBridgeHandler.uninstall();
        }

    }

Configure logback
-----------------
*   Add `LevelChangePropagator` to configure JUL loggers and to improve performance of JUL->SLF4J translation
*   Configure `FileAppender` and use the *log4j* XML layout
*   Configure `HttpTransportPipe` with TRACE level (corresponds to JUL `FINER` level)

A sample `logback.xml`:

    <configuration>
        <contextListener class="ch.qos.logback.classic.jul.LevelChangePropagator">
            <resetJUL>true</resetJUL>
        </contextListener>
        <appender name="METRO_HTTP_TRACE_FILE" class="ch.qos.logback.core.FileAppender">
            <file>/var/log/metro/metro-soap-http.xml</file>
            <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
                <layout class="ch.qos.logback.classic.log4j.XMLLayout" />
            </encoder>
        </appender>

        <logger name="com.sun.xml.ws.transport.http.client.HttpTransportPipe" level="TRACE" additivity="false">
            <appender-ref ref="METRO_HTTP_TRACE_FILE" />
        </logger>
    </configuration>

Run
====
Run the application and load the logback XML output.

Notes
======
Log4j XML output file doesn't have the root element. However, MLT handles this situation correctly and
adds an artificial root element.

Icons
-----
Icons by FlatIcons.net