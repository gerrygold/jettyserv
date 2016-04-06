/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldco.jettyserv;

/**
 *
 * @author gerry
 */
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
//import org.eclipse.jetty.webapp.WebAppContext;

//import com.javacodegeeks.snippets.enterprise.embeddedjetty.servlet.ExampleServlet;
public class EmbeddedJettyMain {

    private static final int HTTPS_PORT = 8443;
    private static final int HTTP_PORT = 8080;
    // private static final String KEYSTORE_PATH = EmbeddedJettyMain.class.getResource("keystore").toExternalForm();
    private static final String KEYSTORE_PATH = EmbeddedJettyMain.class.getResource("keystore256").toExternalForm();

    private static final String KEYSTORE_MANAGER_PASSWORD = "secret";
    private static final String KEYSTORE_PASSWORD = "secret";
    private static final String KEYSTORE_TYPE = "jks";
    private static final String TRUSTSTORE_PATH = EmbeddedJettyMain.class.getResource("truststore").toExternalForm();
    private static final String TRUSTSTORE_PASSWORD = "secret";

    public enum ServerMode {

        http, https
    };

    public static void main(String[] args) throws Exception {

        ServerMode mode = (args.length == 0 ? ServerMode.http : ServerMode.https);

        startServer(mode);
    }

    private static void startServer(ServerMode mode) throws Exception {
        Server server = new Server();
        HttpConfiguration http = new HttpConfiguration();
        if (mode == ServerMode.http) {
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(HTTP_PORT);
            server.setConnectors(new Connector[]{connector});

        } else {
            // HTTP Configuration

            http.addCustomizer(new SecureRequestCustomizer());

            // Configuration for HTTPS redirect
            http.setSecurePort(HTTPS_PORT);
            http.setSecureScheme("https");

            // HTTPS configuration
            HttpConfiguration https = new HttpConfiguration();
            https.addCustomizer(new SecureRequestCustomizer());

            // Configuring SSL
            SslContextFactory sslContextFactory = new SslContextFactory();

            // Defining keystore path and passwords
            sslContextFactory.setKeyStorePath(KEYSTORE_PATH);
            sslContextFactory.setKeyStorePassword(KEYSTORE_PASSWORD);
            sslContextFactory.setKeyManagerPassword(KEYSTORE_MANAGER_PASSWORD);
            sslContextFactory.setKeyStoreType("jks");

            sslContextFactory.setTrustAll(false);
            sslContextFactory.setNeedClientAuth(true);

            sslContextFactory.setTrustStorePath(TRUSTSTORE_PATH);
            sslContextFactory.setTrustStorePassword(TRUSTSTORE_PASSWORD);
            sslContextFactory.setTrustStoreType("jks");
            // Configuring the connector
            ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https));
            sslConnector.setPort(HTTPS_PORT);

            // Setting HTTP and HTTPS connectors
            // server.setConnectors(new Connector[]{connector, sslConnector});
            server.setConnectors(new Connector[]{sslConnector});

        }

        ServletContextHandler handler = new ServletContextHandler(server, "/example");
        handler.addServlet(ExampleServlet.class, "/");

        // Starting the Server
        server.start();
        server.join();
    }
}
