// 
// Decompiled by Procyon v0.5.36
// 

package com.graphmk.graphcore;

import com.graphmk.mkcore.interfaces.Graph;
import java.util.List;
import com.graphmk.mkcore.GraphOptMk;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class PyGenerateGraph
{
    static final Logger logger;
    
    public static void main(final String[] args) throws Exception {
        final String query = args[0];
        final String format = args[1];
        final String fileName = args[2] + "." + args[1];
        final String host = args[3];
        final String port = args[4];
        final String dbname = args[5];
        final String user = args[6];
        final String pass = args[7];
        final GraphGenerator ggen = new GraphGenerator("localhost", port, dbname, user, pass);
        List<Graph> l = null;
        try {
            l = ggen.generateGraph(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        final GraphOptMk g = (GraphOptMk) l.get(0);
        System.out.println(g);
        if (format.equalsIgnoreCase("gml")) {
            long startTime = System.currentTimeMillis();
            g.expand(true);
            long endTime = System.currentTimeMillis();
            PyGenerateGraph.logger.info("Expansion finished in :" + (endTime - startTime));
            startTime = System.currentTimeMillis();
            g.toGML(fileName, "id");
            endTime = System.currentTimeMillis();
            System.out.println("GRAPH: " + g);
            PyGenerateGraph.logger.info("Serialization took: " + (endTime - startTime));
        }
        else if (format.equalsIgnoreCase("json")) {
            long startTime = System.currentTimeMillis();
            g.expand(true);
            long endTime = System.currentTimeMillis();
            PyGenerateGraph.logger.info("Expansion finished in :" + (endTime - startTime));
            startTime = System.currentTimeMillis();
            g.toJSON(fileName, "id", false);
            endTime = System.currentTimeMillis();
            System.out.println("GRAPH: " + g);
            PyGenerateGraph.logger.info("Serialization took: " + (endTime - startTime));
        }
    }
    
    static {
        logger = Logger.getLogger(PyGenerateGraph.class);
    }
}
