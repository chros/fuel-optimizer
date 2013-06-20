/*
 *  Licensed to GraphHopper and Peter Karich under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for 
 *  additional information regarding copyright ownership.
 * 
 *  GraphHopper licenses this file to you under the Apache License, 
 *  Version 2.0 (the "License"); you may not use this file except in 
 *  compliance with the License. You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.http;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import java.util.HashMap;
import java.util.Map;

/**
 * Replacement of web.xml
 *
 * http://code.google.com/p/google-guice/wiki/ServletModule
 *
 * @author Peter Karich, pkarich@pannous.info
 */
public class GuiceServletConfig extends GuiceServletContextListener {

    @Override protected Injector getInjector() {
        return Guice.createInjector(createDefaultModule(), createServletModule());
    }

    protected Module createDefaultModule() {
        return new DefaultModule();
    }

    protected Module createServletModule() {
        return new ServletModule() {
            @Override protected void configureServlets() {                
                Map<String, String> params = new HashMap<String, String>();
                params.put("mimeTypes", "text/html,"
                        + "text/plain,"
                        + "text/xml,"
                        + "application/xhtml+xml,"
                        + "text/css,"
                        + "application/json,"
                        + "application/javascript,"
                        + "image/svg+xml");

                filter("/*").through(MyGZIPHook.class, params);
                bind(MyGZIPHook.class).in(Singleton.class);
                
                serve("/api*").with(GraphHopperServlet.class);
                bind(GraphHopperServlet.class).in(Singleton.class);                
            }
        };
    }
}
