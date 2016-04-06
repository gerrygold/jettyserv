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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;

public class ExampleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String user = "anonymous";

        java.security.cert.X509Certificate[] certs
                = (java.security.cert.X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");

        // Need certificates.
        if (certs != null && certs.length > 0 && certs[0] != null) {

            user = ((certs[0].getSubjectDN() == null) ? "clientcert" : certs[0].getSubjectDN().getName()  + ":" + certs[0].getSerialNumber().toString());
        } else {
            if (resp != null) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "A client certificate is required for accessing this web application but the server's listener is not configured for mutual authentication (or the client did not provide a certificate).");
            }
        }

        resp.setStatus(HttpStatus.OK_200);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        resp.getWriter().println("EmbeddedJetty [" + user + "]- " + sdfDate.format(new Date()));
    }
}
