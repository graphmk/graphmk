// 
// Decompiled by Procyon v0.5.36
// 

package com.heftdb.graphmk;

import com.heftdb.conn.PostgresConn;
import com.heftdb.conn.SQLiteConn;
import com.heftdb.mkcore.GraphOptMk;
import com.heftdb.mkcore.MkGraph;
import com.heftdb.mkcore.interfaces.Graph;
import com.heftdb.mkcore.interfaces.Vertex;
import com.heftdb.mkcore.GraphFactory;
import com.heftdb.parser.BailErrorStrategy;
import com.heftdb.parser.DatalogBaseListener;
import com.heftdb.parser.DatalogLexer;
import com.heftdb.parser.DatalogParser;
import com.heftdb.vertexCentric.DegreeExecutor;
import com.heftdb.vertexCentric.LCCExecutor;
import com.heftdb.vertexCentric.PageRankExecutor;
import com.heftdb.vertexCentric.VertexCentric;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.sql.*;
import java.util.*;

public class GraphGenerator
{
    static final Logger logger;
    private static Connection connection;
    protected static HashMap<String, List<String>> dlogVariables;
    protected static HashMap<Integer, Relation> forLoopVariables;
    private static List<String> groupByProps;
    protected static boolean egoQuery;
    public static GraphFactory factory;
    
    public GraphGenerator() {
        GraphGenerator.connection = new PostgresConn().getConnection();
    }
    
    public GraphGenerator(final String hostname, final String port, final String database, final String username, final String password) throws SQLException {
        try {
            final PostgresConn pc = new PostgresConn(hostname, port, database, username, password);
            GraphGenerator.connection = pc.getConnection();
        }
        catch (Exception e) {
            System.out.println("Trying to connect to SQLite Database...");
            final SQLiteConn pc2 = new SQLiteConn(database);
            GraphGenerator.connection = pc2.getConnection();
        }
    }
    
    public List<Graph> generateGraph(String query) throws SQLException, RuntimeException {
        final long startTime = System.currentTimeMillis();
        List<Graph> gList = new ArrayList<Graph>();
        Graph graph = null;
        if (GraphGenerator.connection != null) {
            query = query.replace("\n", "").replace("\r", "");
            final String[] lines = query.split("(?<=[.\n\r])");
            String SQLQuery = "";
            graph = GraphGenerator.factory.create("Condensed");
            for (final String l : lines) {
                try {
                    GraphGenerator.logger.info("Processing: " + l);
                    SQLQuery = toSQL(l);
                }
                catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                }
                if (l.trim().startsWith("Nodes")) {
                    try {
                        GraphGenerator.logger.info("Executing Nodes SQL :" + SQLQuery);
                        generateNodes(graph, SQLQuery);
                    }
                    catch (SQLException e2) {
                        e2.printStackTrace();
                    }
                }
                else if (l.trim().startsWith("Edges")) {
                    try {
                        GraphGenerator.logger.debug("Executing Edges SQL :" + SQLQuery);
                        gList = generateEdges(graph, SQLQuery);
                    }
                    catch (SQLException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            graph.shutdown();
            final long endTime = System.currentTimeMillis();
            final long duration = endTime - startTime;
            GraphGenerator.logger.info("Total Extraction Time : " + duration);
            GraphGenerator.egoQuery = false;
            return gList;
        }
        throw new SQLException("Failed to make connection!");
    }
    
    private static String decideCondensed(final String sql, final boolean tempTable, final TranslatorListener tListener, final long threshold) {
        try {
            final Statement st = preparePostGStatement();
            final ResultSet rs = st.executeQuery("explain (format json) " + sql);
            if (rs.next()) {
                final String json_plan = rs.getString(1);
                final JSONObject root = (JSONObject) ((JSONArray) JSONValue.parse(json_plan)).get(0);
                final String resultRows = root.get("Plan.Plan Rows").toString();
                final long estimate = Long.parseLong(resultRows);
                GraphGenerator.logger.info("Estimated Self-Join #Rows: " + estimate);
                final List<JSONObject> joins = interpretJoins(root);
                String joinAttribute = null;
                String selfJRel = null;
                String condensedQuery = "";
                if (estimate <= threshold) {
                    st.close();
                    return sql;
                }
                for (final JSONObject o : joins) {
                    if (o.get("inner") != null && o.get("inner").toString().equals(o.get("outer").toString())) {
                        selfJRel = o.get("outer").toString();
                        joinAttribute = removeAlias(o.get("condition").toString().split("=")[0]).trim();
                    }
                }
                condensedQuery += "select ";
                Relation selfJRelation = null;
                for (final Relation r : tListener.getRelsToJoin()) {
                    if (r.getName().equalsIgnoreCase(selfJRel)) {
                        selfJRelation = r;
                    }
                }
                if (selfJRelation == null) {
                    st.close();
                    GraphGenerator.logger.info("Query too complex -- Not using condensed representation");
                    return sql;
                }
                for (int i = 0; i < tListener.getPropsAlias().size(); ++i) {
                    if (selfJRelation.getAttribAliases().get(i).toLowerCase().startsWith("id")) {
                        condensedQuery += selfJRelation.getOriginalAtts().get(i);
                        condensedQuery += ",";
                    }
                }
                condensedQuery += joinAttribute;
                condensedQuery = condensedQuery + " from " + selfJRel;
                final String preds = selfJRelation.getAdditionalPredicates(false);
                if (!preds.equals("")) {
                    condensedQuery = condensedQuery + " where " + preds + ";";
                }
                else {
                    condensedQuery += ";";
                }
                final String afterRem = removeFromOriginal(selfJRelation, joinAttribute, sql + ";");
                condensedQuery = condensedQuery + "|" + afterRem;
                return condensedQuery;
            }
            else {
                st.close();
            }
        }
        catch (SQLException e) {
            System.out.println("exception :" + e);
        }
        return null;
    }

    private static List<JSONObject> interpretJoins(final JSONObject root) {
        final JSONObject currNode = (JSONObject) root.get("Plan");
        return traverse(currNode, new ArrayList<JSONObject>());
    }
    private static List<JSONObject> traverse(JSONObject currNode, final List<JSONObject> joins) {
        final JSONArray plns = (JSONArray) currNode.get("Plans");
        if (plns != null) {
            for (int i = 0; i < plns.size(); ++i) {
                final JSONObject node = (JSONObject) plns.get(i);
                final String nodeType = currNode.get("Node Type").toString();
                JSONObject lastJoin = null;
                if (nodeType.contains("Join") || nodeType.contains("Nested")) {
                    joins.add(new JSONObject());
                    lastJoin = joins.get(joins.size() - 1);
                    lastJoin.put("estimate", currNode.get("Plan Rows").toString());
                    final String kindOfJoin = nodeType.substring(0, nodeType.length() - 5);
                    lastJoin.put("joinType", kindOfJoin);
                    if (currNode.get(kindOfJoin + " Cond") != null) {
                        lastJoin.put("condition", currNode.get(kindOfJoin + " Cond").toString().replaceAll("[()]", ""));
                    }
                }
                if (node.containsKey("Relation Name")) {
                    lastJoin = joins.get(joins.size() - 1);
                    if (lastJoin.containsKey("outer")) {
                        lastJoin.put("inner", node.get("Relation Name"));
                        lastJoin.put("inner-alias", node.get("Alias"));
                    }
                    else {
                        lastJoin.put("outer", node.get("Relation Name"));
                        lastJoin.put("outer-alias", node.get("Alias"));
                    }
                    if (lastJoin.get("condition") == null && node.containsKey("Index Cond")) {
                        final String s = node.get("Index Cond").toString().replaceAll("[()]", "");
                        lastJoin.put("condition", node.get("Alias").toString() + "." + s);
                    }
                }
                currNode = node;
                traverse(currNode, joins);
            }
        }
        return joins;
    }


    private static String removeAlias(final String string) {
        if (string.contains(".")) {
            return string.split("\\.")[1];
        }
        return string;
    }
    
    private static String getInBetween(final String str, final String a, final String b) {
        return str.substring(str.indexOf(a) + a.length(), str.indexOf(b)).trim();
    }
    
    private static String removeFromOriginal(final Relation selfJRelation, final String joinAttribute, String a) {
        final String rel = selfJRelation.getName();
        final String[] rels = getInBetween(a, "from", "where").split(",");
        String alias1 = null;
        String alias2 = null;
        boolean replaced = false;
        for (final String s : rels) {
            if (s.contains(rel)) {
                if (alias1 == null) {
                    alias1 = s.substring(s.length() - 1);
                }
                else {
                    alias2 = s.substring(s.length() - 1);
                }
                if (a.substring(a.indexOf(s), a.indexOf(s) + s.length() + 1).endsWith(",")) {
                    if (!replaced) {
                        a = a.replace(s + ",", "");
                        replaced = true;
                    }
                }
                else if (!replaced) {
                    a = a.replace(s, "");
                    replaced = true;
                }
            }
        }
        final String whereC = getInBetween(a, "where", ";");
        final String[] conditions = whereC.trim().split(" ");
        boolean someExists = false;
        for (int i = 0; i < conditions.length; ++i) {
            String toRep = conditions[i];
            if (conditions[i].contains(alias1) && conditions[i].contains(alias2)) {
                if (i != conditions.length - 1) {
                    toRep += " and";
                }
                a = a.replace(toRep, "");
            }
            else if (conditions[i].contains(alias1) || conditions[i].contains(alias2)) {
                if (conditions[i].contains("'") || conditions[i].contains("\"") || conditions[i].contains(">") || conditions[i].contains("<") || conditions[i].contains("<=") || conditions[i].contains(">=")) {
                    if (i != conditions.length - 1) {
                        toRep += " and";
                    }
                    a = a.replace(toRep, "");
                }
                else {
                    someExists = true;
                }
            }
        }
        final String selection = a.substring(a.indexOf("distinct") + "distinct".length() + 1, a.indexOf("from")).trim();
        final String[] s2 = selection.split(",");
        String attToRem = "";
        for (int j = 0; j < s2.length; ++j) {
            if (s2[j].contains(alias1)) {
                attToRem = s2[j];
            }
            if (j != s2.length - j) {
                attToRem += ",";
            }
            a = a.replace(attToRem, "");
            attToRem = "";
        }
        if (someExists) {
            a = a.replaceAll(" " + alias1 + " ", " " + alias2);
            a = a.replaceAll(alias1 + "\\.", alias2 + "\\.");
        }
        else {
            a = " ";
        }
        return a;
    }
    
    private static String getXHopNeighborhood(final int hops, final String sql, final List<String> propsAlias) {
        GraphGenerator.logger.info("Getting " + hops + " hop neighborhood");
        String taggedQuery = "with edges as (" + sql + ") ";
        String select = "";
        String from = "";
        String where = "";
        final String sourceId = propsAlias.get(0);
        final String destId = propsAlias.get(1);
        if (hops > 0) {
            select = select + ", result as ( select distinct e1." + sourceId + " as e1u, e1." + destId + " as e1v," + sourceId + " as tag ";
            from += "from edges e1 ";
            where = where + "where e1." + sourceId + " in ";
            final List<String> distinctIDs = null;
            where = where + "(select distinct " + propsAlias.get(0) + " from edges)";
            taggedQuery = taggedQuery + select + from + where + " union ";
            select = "select distinct e2." + propsAlias.get(0) + ", e2." + propsAlias.get(1) + "," + "e1." + propsAlias.get(0) + " as tag ";
            from = "from edges e1,edges e2 ";
            where = where + " and e1." + destId + "=e2." + sourceId + ")";
            final String finalQ = " select e1u,e1v,array_agg(tag) from result group by e1u,e1v";
            taggedQuery = taggedQuery + select + from + where + finalQ;
            GraphGenerator.logger.info("Executing: " + taggedQuery);
        }
        return taggedQuery;
    }
    
    public static String toSQL(final String datalog) {
        final ANTLRInputStream input = new ANTLRInputStream(datalog);
        final DatalogLexer lexer = new DatalogLexer(input);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final DatalogParser parser = new DatalogParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());
        final ParseTree tree = parser.datalog();
        final ParseTreeWalker walker = new ParseTreeWalker();
        final TranslatorListener translator = new TranslatorListener(parser);
        walker.walk(translator, tree);
        return translator.getResult();
    }
    
    private static String nextChar(final String curr) {
        final int charValue = curr.charAt(0);
        return String.valueOf((char)(charValue + 1));
    }
    
    private static List<String> strToList(String s, final Relation r) {
        final List<String> attsList = new ArrayList<String>();
        s = s.replaceAll("[//(//)]", "");
        for (String l : s.split(",")) {
            String val = null;
            if (l.contains("=")) {
                val = l.substring(l.indexOf("=") + 1, l.length());
                l = l.substring(0, l.indexOf("="));
            }
            attsList.add(l.trim());
            if (r != null && val != null) {
                r.getValues().put(attsList.size() - 1, val);
            }
        }
        return attsList;
    }
    
    private static List<String> fetchAttsFromSchema(final String rName) throws SQLException {
        final Statement st = preparePostGStatement();
        final ResultSet rs = st.executeQuery("select * from " + rName + " limit(1)");
        try {
            final ResultSetMetaData rsMetaData = rs.getMetaData();
            final int colCount = rsMetaData.getColumnCount();
            final List<String> atts = new LinkedList<String>();
            for (int i = 1; i < colCount + 1; ++i) {
                atts.add(rsMetaData.getColumnName(i));
            }
            return atts;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            st.close();
            rs.close();
        }
        return null;
    }
    
    private static String getSelection(final List<String> aliasProps, final List<Relation> relsToJoin) {
        String selection = "";
        final Set<String> set = new HashSet<String>();
        for (final String a : aliasProps) {
            for (final Relation r : relsToJoin) {
                if (r.getAttribAliases().contains(a) && !set.contains(a)) {
                    set.add(a);
                    final String s = r.getAlias() + "." + r.getOriginalAtts().get(r.getAttribAliases().indexOf(a));
                    GraphGenerator.groupByProps.add(s);
                    selection = selection + s + " as " + a + ",";
                }
            }
        }
        return selection.substring(0, selection.length() - 1) + " ";
    }
    
    private static Statement preparePostGStatement() throws SQLException {
        Statement st = null;
        GraphGenerator.connection.setAutoCommit(false);
        st = GraphGenerator.connection.createStatement();
        st.setFetchSize(0);
        return st;
    }
    
    private static void generateNodes(final Graph graph, final String sqlQuery) throws SQLException {
        final Statement st = preparePostGStatement();
        final ResultSet rs = st.executeQuery(sqlQuery);
        int rowCount = 0;
        while (rs.next()) {
            ++rowCount;
            final ResultSetMetaData rsMetaData = rs.getMetaData();
            final String vId = rs.getString(1);
            Vertex v = null;
            if (graph.getVertex(vId) == null) {
                v = graph.addVertex(vId);
                for (int j = 1; j <= rsMetaData.getColumnCount(); ++j) {
                    String colId = rsMetaData.getColumnName(j);
                    if (colId.equals("id")) {
                        colId += "v";
                    }
                    if (j > 1) {
                        v.setProperty(colId, rs.getString(j));
                    }
                }
            }
        }
        GraphGenerator.logger.info("# Rows in Nodes: " + rowCount);
        st.close();
        rs.close();
    }
    
    private static List<Graph> generateEdges(final Graph graph, String sqlQuery) throws IllegalArgumentException, SQLException {
        boolean condensed = false;
        final String[] queries = sqlQuery.split("\\|");
        sqlQuery = queries[0];
        if (queries.length > 1) {
            condensed = true;
            GraphGenerator.logger.info("Choosing to Use Condensed Representation...");
        }
        final Statement st = preparePostGStatement();
        final ResultSet rs = st.executeQuery(sqlQuery);
        List<Graph> gList = new ArrayList<Graph>();
        final HashMap<String, Graph> graphs = new HashMap<String, Graph>();
        int rowCount = 0;
        while (rs.next()) {
            ++rowCount;
            final String outId = rs.getString(1);
            final String inId = rs.getString(2);
            Vertex outV = null;
            Vertex inV = null;
            if (GraphGenerator.egoQuery) {
                final Array a = rs.getArray(3);
                final Object[] array;
                final Object[] arr = array = (Object[])a.getArray();
                for (final Object s : array) {
                    final String egoId = s.toString();
                    if (graph.getVertex(egoId) != null) {
                        if (graph.getVertex(inId) == null) {
                            break;
                        }
                        if (graph.getVertex(inId) == null) {
                            break;
                        }
                        MkGraph g;
                        if (graphs.keySet().contains(egoId)) {
                            g = (MkGraph) graphs.get(egoId);
                        }
                        else {
                            graphs.put(egoId, GraphGenerator.factory.create("AdjacencyCSR"));
                            g = (MkGraph) graphs.get(egoId);
                        }
                        if (g.getVertex(outId) == null) {
                            final Vertex fromNodesQuery = graph.getVertex(rs.getString(1));
                            outV = g.addCopyOfVertex(fromNodesQuery);
                            if (outV.toString().equals(egoId)) {
                                outV.setProperty("ego", "true");
                            }
                        }
                        if (g.getVertex(inId) == null) {
                            final Vertex fromNodesQuery = graph.getVertex(rs.getString(2));
                            inV = g.addCopyOfVertex(fromNodesQuery);
                            if (inV.toString().equals(egoId)) {
                                inV.setProperty("ego", "true");
                            }
                        }
                        outV = g.getVertex(outId);
                        inV = g.getVertex(inId);
                        if (!g.existsPhysicalEdge(outV, inV)) {
                            g.addEdge(null, outV, inV, "");
                        }
                    }
                }
            }
            else if (condensed) {
                final String virtualNodeId = GraphOptMk.virtIdentifier + inId;
                Vertex vNode = null;
                if (graph.getVertex(virtualNodeId) == null) {
                    vNode = graph.addVertex(virtualNodeId);
                }
                else {
                    vNode = graph.getVertex(virtualNodeId);
                }
                if (graph.getVertex(outId) == null) {
                    outV = graph.addVertex(outId);
                }
                else {
                    outV = graph.getVertex(outId);
                }
                if (((GraphOptMk)graph).existsPhysicalEdge(outV, vNode)) {
                    continue;
                }
                graph.addEdge(null, outV, vNode, "");
            }
            else {
                outV = graph.getVertex(outId);
                inV = graph.getVertex(inId);
                if (outV == null || inV == null || ((MkGraph)graph).existsPhysicalEdge(outV, inV) || outV.equals(inV)) {
                    continue;
                }
                graph.addEdge(null, outV, inV, "");
            }
        }
        st.close();
        rs.close();
        if (!GraphGenerator.egoQuery) {
            gList.add(graph);
        }
        else {
            gList = new ArrayList<Graph>(graphs.values());
        }
        for (final Graph g2 : gList) {
            if (!GraphGenerator.egoQuery) {
                ((GraphOptMk)g2).finalize();
            }
        }
        return gList;
    }
    
    public static void runPageRank(final Graph g) {
        System.out.println("Running Pagerank");
        final VertexCentric v = new VertexCentric(g);
        try {
            final long start = System.currentTimeMillis();
            v.run(new PageRankExecutor());
            final long end = System.currentTimeMillis();
            System.out.println("PageRank took " + (end - start));
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void runClusterinCoefficient(final Graph g) {
        System.out.println("Running Clustering Coefficient");
        final VertexCentric v = new VertexCentric(g);
        try {
            final long start = System.currentTimeMillis();
            v.run(new LCCExecutor());
            final long end = System.currentTimeMillis();
            System.out.println("LCC took " + (end - start));
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void runNodeDegree(final Graph g) {
        final VertexCentric v = new VertexCentric(g);
        try {
            final long start = System.currentTimeMillis();
            v.run(new DegreeExecutor());
            final long end = System.currentTimeMillis();
            System.out.println("Degree took " + (end - start));
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Graph> getRandomSample(final Graph g, final int numOfSamples, final int maxDistance, final int maxNumOfVertices) {
        final List<Graph> samples = new ArrayList<Graph>();
        final Random random = new Random();
        Vertex startVertex = null;
        int randNum = 0;
        final int range = ((ArrayList)g.getVertices()).size();
        for (int i = 0; i < numOfSamples; ++i) {
            final MkGraph sample = (MkGraph)GraphGenerator.factory.create("AdjacencyCSR");
            randNum = random.nextInt(range);
            startVertex = (Vertex) ((ArrayList)g.getVertices()).get(randNum);
            System.out.println("Random Start vertex " + startVertex.getId());
            sample.addCopyOfVertex(startVertex);
            walkGraph(sample, g, startVertex, 0, maxDistance, maxNumOfVertices);
            samples.add(sample);
        }
        return samples;
    }
    
    private static void walkGraph(final MkGraph sample, final Graph g, final Vertex v, final int curDistance, final int maxDistance, final int maxNumOfVertices) {
        final Vertex current = sample.getVertex(v.getId());
        if (current.getProperty("visited") != null) {
            return;
        }
        final GremlinPipeline<Iterable<?>, Object> pipe = new GremlinPipeline<Iterable<?>, Object>();
        long numOfVertices = pipe.start(sample.getVertices()).count();
        if (curDistance <= maxDistance) {
            Vertex next = null;
            final Random r = new Random();
            for (final Vertex e : v.getVertices(Direction.OUT, new String[0])) {
                Vertex inVertex = null;
                if (!v.getId().equals(e.getId())) {
                    inVertex = e;
                    if (sample.getVertex(inVertex.getId()) == null) {
                        next = sample.addCopyOfVertex(inVertex);
                    }
                    else {
                        next = sample.getVertex(inVertex.getId());
                    }
                    if (!sample.existsPhysicalEdge(current, next)) {
                        sample.addEdge("", current, next, "");
                    }
                    next = g.getVertex(next.getId());
                    numOfVertices = pipe.start(sample.getVertices()).count();
                    if (numOfVertices >= maxNumOfVertices) {
                        return;
                    }
                    current.setProperty("visited", true);
                    walkGraph(sample, g, next, curDistance + 1, maxDistance, maxNumOfVertices);
                }
            }
        }
    }
    
    public static List<Graph> getXHopNeighborhoodSample(final Graph g, final int numOfSamples, final int hops, final int maxEdges, final String specificId) {
        final List<Graph> samples = new ArrayList<Graph>();
        final Random random = new Random();
        final GremlinPipeline pipe = new GremlinPipeline();
        final int numOfVertices = (int)pipe.start(g.getVertices()).count();
        Vertex v2 = null;
        for (int i = 0; i < numOfSamples; ++i) {
            final MkGraph sample = (MkGraph)GraphGenerator.factory.create("AdjacencyCSR");
            if (specificId == null) {
                v2 = getRandomVertex(g);
            }
            else {
                v2 = g.getVertex(specificId);
            }
            sample.addCopyOfVertex(v2);
            sample.getVertex(v2).setProperty("ego", "true");
            getNeighbors(sample, g, v2, 0, hops, maxEdges);
            samples.add(sample);
        }
        return samples;
    }
    
    private static Vertex getRandomVertex(final Graph g) {
        final Random random = new Random();
        final int randNum = random.nextInt(g.getVertices().size());
        return g.getVertices().get(randNum);
    }
    
    private static void getNeighbors(final MkGraph sample, final Graph g, final Vertex v, final int hop, final int totalHops, final int maxNumEdges) {
        if (hop <= totalHops) {
            Vertex next = null;
            final Vertex current = sample.getVertex(v);
            for (final Vertex e : v.getVertices(Direction.BOTH, new String[0])) {
                if (sample.getNumOfEdges() >= maxNumEdges) {
                    break;
                }
                final Vertex inVertex = e;
                if (sample.getVertex(inVertex) == null) {
                    next = sample.addCopyOfVertex(inVertex);
                }
                else {
                    next = sample.getVertex(inVertex.getId());
                }
                if (sample.existsPhysicalEdge(current, next)) {
                    continue;
                }
                sample.addEdge(null, current, next, "");
            }
            for (final Vertex e : v.getVertices(Direction.BOTH, new String[0])) {
                next = g.getVertex(e);
                getNeighbors(sample, g, next, hop + 1, totalHops, maxNumEdges);
            }
        }
    }
    
    public Map<String, Relation> getSchema() throws SQLException {
        final DatabaseMetaData meta = GraphGenerator.connection.getMetaData();
        final ResultSet metadata = meta.getTables(null, null, "%", null);
        final Map<String, Relation> schema = new HashMap<String, Relation>();
        while (metadata.next()) {
            final String tableName = metadata.getString(3);
            final String type = metadata.getString(4);
            if (type != null && type.equals("TABLE")) {
                final Relation r = new Relation();
                r.setName(tableName);
                final ResultSet columns = meta.getColumns(null, null, tableName, null);
                while (columns.next()) {
                    r.getOriginalAtts().add(columns.getString(4));
                    r.getAttribTypes().put(columns.getString(4), columns.getString(6));
                }
                final ResultSet primaryKeys = meta.getPrimaryKeys(null, null, tableName);
                while (primaryKeys.next()) {
                    r.getPrimaryKeys().add(primaryKeys.getString(4));
                }
                final ResultSet importedKeys = meta.getImportedKeys(null, null, tableName);
                while (importedKeys.next()) {
                    r.getForeignKeys().put(importedKeys.getString(8), importedKeys.getString(3) + "." + importedKeys.getString(4));
                }
                schema.put(tableName, r);
            }
        }
        return schema;
    }
    
    public static void calculateStats(final Graph g) {
        System.out.println("Calculating PageRank...");
        runPageRank(g);
        System.out.println("Calculating Clustering Coefficient...");
        runClusterinCoefficient(g);
        System.out.println("Calculating Node Degrees...");
        runNodeDegree(g);
    }
    
    public static List<Graph> generateTestCondensed(final String sqlQuery) throws SQLException {
        final Statement st = preparePostGStatement();
        final ResultSet rs = st.executeQuery(sqlQuery);
        final boolean exists = false;
        final List<Graph> gList = new ArrayList<Graph>();
        final Graph graph = GraphGenerator.factory.create("Condensed");
        int rowCount = 0;
        while (rs.next()) {
            ++rowCount;
            final String outId = rs.getString(1);
            final String virtualNodeId = GraphOptMk.virtIdentifier + rs.getString(2);
            Vertex vNode = null;
            Vertex outV = null;
            if (graph.getVertex(virtualNodeId) == null) {
                vNode = graph.addVertex(virtualNodeId);
            }
            else {
                vNode = graph.getVertex(virtualNodeId);
            }
            if (graph.getVertex(outId) == null) {
                outV = graph.addVertex(outId);
            }
            else {
                outV = graph.getVertex(outId);
            }
            graph.addEdge(null, outV, vNode, "");
        }
        st.close();
        rs.close();
        gList.add(graph);
        System.out.println("Got " + gList.get(0));
        for (final Graph g : gList) {
            ((GraphOptMk)g).sortVertexLists();
        }
        return gList;
    }
    
    static {
        logger = Logger.getLogger(GraphGenerator.class);
        GraphGenerator.dlogVariables = new HashMap<String, List<String>>();
        GraphGenerator.forLoopVariables = new HashMap<Integer, Relation>();
        GraphGenerator.groupByProps = new ArrayList<String>();
        GraphGenerator.egoQuery = false;
        GraphGenerator.factory = new GraphFactory();
    }
    
    public static class TranslatorListener extends DatalogBaseListener
    {
        private String result;
        private DatalogParser parser;
        private List<String> propsAlias;
        private List<Relation> relsToJoin;
        private TokenStream tokens;
        private String tableAlias;
        private boolean NODES;
        private boolean EDGES;
        private Map<String, String> aggregates;
        
        public List<String> getPropsAlias() {
            return this.propsAlias;
        }
        
        public TranslatorListener(final DatalogParser parser) {
            this.propsAlias = null;
            this.tableAlias = "A";
            this.parser = parser;
            this.tokens = parser.getTokenStream();
            parser.setErrorHandler(new BailErrorStrategy());
            this.relsToJoin = new ArrayList<Relation>();
            this.aggregates = new HashMap<String, String>();
        }
        
        public String getResult() {
            return this.result;
        }
        
        @Override
        public void enterAgg_predicate(final DatalogParser.Agg_predicateContext ctx) {
            final String aggr = ctx.aggregate_expr().getText();
            final String condition = ctx.CONDITION().toString() + ctx.value().getText();
            this.aggregates.put(aggr, condition);
        }
        
        @Override
        public void enterLh_atom(final DatalogParser.Lh_atomContext ctx) {
            final String params = this.tokens.getText(ctx.parameters());
            this.propsAlias = strToList(params, null);
            if (ctx.NODES() != null) {
                this.NODES = true;
            }
            if (ctx.EDGES() != null) {
                this.EDGES = true;
            }
        }
        
        @Override
        public void enterRh_atom(final DatalogParser.Rh_atomContext ctx) {
            final String id = ctx.ID().toString();
            final Relation r = new Relation(id, this.tableAlias);
            this.relsToJoin.add(r);
            this.tableAlias = nextChar(this.tableAlias);
            final String params = this.tokens.getText(ctx.parameters());
            r.setAttribAliases(strToList(params, r));
            try {
                r.setOriginalAtts(fetchAttsFromSchema(r.getName()));
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public void exitStatement(final DatalogParser.StatementContext ctx) {
            for (final DatalogParser.PredicateContext s : ctx.predicate()) {
                final String attrib = s.ID().toString();
                final String value = s.CONDITION().toString() + s.value().getText();
                for (final Relation r : this.relsToJoin) {
                    if (r.getAttribAliases().contains(attrib)) {
                        r.getValues().put(r.getAttribAliases().indexOf(attrib), value);
                    }
                }
            }
        }
        
        @Override
        public void exitDatalog(final DatalogParser.DatalogContext ctx) {
            String select = "select distinct ";
            String from = "from ";
            String whereClause = "";
            String groupBy = "";
            GraphGenerator.logger.info("Ego Query: " + GraphGenerator.egoQuery);
            if (GraphGenerator.egoQuery) {
                for (final Relation r : this.relsToJoin) {
                    for (final String a : this.propsAlias) {
                        if (!a.equals("_")) {
                            GraphGenerator.forLoopVariables.put(this.propsAlias.indexOf(a), r);
                        }
                    }
                }
            }
            select += getSelection(this.propsAlias, this.relsToJoin);
            if (!this.aggregates.isEmpty()) {
                select = select + "," + getAggSelections(this.relsToJoin, this.aggregates);
            }
            for (final Relation table : this.relsToJoin) {
                from = from + table.getName() + " " + table.getAlias() + ",";
            }
            from = from.substring(0, from.length() - 1) + " ";
            if (this.relsToJoin.size() > 1 || GraphGenerator.dlogVariables.size() > 0) {
                final String joinCond = Relation.getJoinCondition(this.relsToJoin);
                if (!joinCond.equals("")) {
                    whereClause = whereClause + "where " + joinCond;
                }
                if (!this.aggregates.isEmpty()) {
                    groupBy += "group by ";
                    for (int i = 0; i < GraphGenerator.groupByProps.size(); ++i) {
                        final String p = GraphGenerator.groupByProps.get(i);
                        groupBy += p;
                        if (i != GraphGenerator.groupByProps.size() - 1) {
                            groupBy += ",";
                        }
                    }
                }
            }
            for (final Relation r : this.relsToJoin) {
                if (r.getValues().size() > 0) {
                    if (whereClause.equals("")) {
                        whereClause += " where ";
                    }
                    else {
                        whereClause += "and ";
                    }
                    whereClause += r.getAdditionalPredicates(true);
                }
            }
            String havingClause = "";
            if (!this.aggregates.isEmpty()) {
                havingClause += " having ";
                final Iterator<String> j = this.aggregates.keySet().iterator();
                while (j.hasNext()) {
                    final String aggClause = j.next();
                    havingClause = havingClause + aggClause + this.aggregates.get(aggClause);
                    if (j.hasNext()) {
                        havingClause += " and ";
                    }
                }
            }
            this.result = select + from + whereClause + groupBy.trim() + havingClause;
            if (this.EDGES && GraphGenerator.egoQuery) {
                this.result = this.result.substring(0, this.result.length() - 1);
                this.result = getXHopNeighborhood(1, this.result, this.propsAlias);
            }
            if (this.EDGES && !GraphGenerator.egoQuery) {
                System.out.println("res: " + this.result);
            }
            this.result += ";";
            GraphGenerator.groupByProps.clear();
            this.relsToJoin.clear();
            this.propsAlias.clear();
        }
        
        public static List<String> intersectStrLists(final List<String> list, final List<String> list2) {
            final Set<String> forA = new HashSet<String>();
            final Set<String> forB = new HashSet<String>();
            final List<String> toRet = new ArrayList<String>();
            for (final String v : list) {
                forA.add(v);
            }
            for (final String w : list2) {
                forB.add(w);
            }
            for (final String s : forA) {
                if (forB.contains(s)) {
                    toRet.add(s);
                }
            }
            return toRet;
        }
        
        private String getCondensedRep(final List<Relation> relsToJoin2, final List<String> propsAlias2) {
            Relation ID1Rel = null;
            Relation ID2Rel = null;
            for (final Relation r : relsToJoin2) {
                for (final String s : r.getAttribAliases()) {
                    if (s.equals("ID1")) {
                        ID1Rel = r;
                    }
                    else {
                        if (!s.equals("ID2")) {
                            continue;
                        }
                        ID2Rel = r;
                    }
                }
            }
            String ID1Select = "SELECT ";
            String ID1From = "FROM ";
            String ID1Where = "WHERE ";
            String ID2Select = "SELECT ";
            String ID2From = "FROM ";
            final String ID2Where = "WHERE ";
            final Map<String, List<String>> finalQueries = new HashMap<String, List<String>>();
            final List<String> id1 = new ArrayList<String>();
            id1.add(ID1Select);
            id1.add(ID1From);
            id1.add(ID1Where);
            final List<String> id2 = new ArrayList<String>();
            id2.add(ID2Select);
            id2.add(ID2From);
            id2.add(ID2Where);
            finalQueries.put("ID1", id1);
            finalQueries.put("ID2", id2);
            ID1Select = ID1Select + ID1Rel.getAlias() + "." + ID1Rel.getOriginalAtts().get(ID1Rel.getAttribAliases().indexOf("ID1"));
            ID1Select += ",";
            ID1From = ID1From + ID1Rel.getName() + " " + ID1Rel.getAlias();
            ID1From += ",";
            ID2Select = ID2Select + ID2Rel.getAlias() + "." + ID2Rel.getOriginalAtts().get(ID2Rel.getAttribAliases().indexOf("ID2"));
            ID2Select += ",";
            ID2From = ID2From + ID2Rel.getName() + " " + ID2Rel.getAlias();
            ID2From += ",";
            for (final Relation r2 : relsToJoin2) {
                if (r2.getName().equals(ID1Rel.getName())) {
                    continue;
                }
                final List<String> inter = intersectStrLists(ID1Rel.getAttribAliases(), r2.getAttribAliases());
                if (inter.size() <= 0) {
                    continue;
                }
                for (int i = 0; i < r2.getAttribAliases().size(); ++i) {
                    final String attrib = r2.getAttribAliases().get(i);
                    if (!inter.contains(attrib) && !attrib.equals("_")) {
                        ID1Select = ID1Select + r2.getAlias() + "." + r2.getOriginalAtts().get(i);
                        if (i != r2.getAttribAliases().size() - 1) {
                            ID1Select += ", ";
                        }
                    }
                }
                ID1From = ID1From + r2.getName() + " " + r2.getAlias();
                for (final String a : inter) {
                    final int attIndexLeft = ID1Rel.getAttribAliases().indexOf(a);
                    final int attIndexRight = r2.getAttribAliases().indexOf(a);
                    if (!ID1Where.endsWith("WHERE ")) {
                        ID1Where += "and ";
                    }
                    ID1Where = ID1Where + ID1Rel.getAlias() + "." + ID1Rel.getOriginalAtts().get(attIndexLeft) + "=" + r2.getAlias() + "." + r2.getOriginalAtts().get(attIndexRight) + " ";
                }
            }
            final String final1 = ID1Select + " " + ID1From + " " + ID1Where;
            final String with = "WITH selfJoinT as(" + final1 + ") select distinct * from selfJoinT; |";
            return with;
        }
        
        private static String getAggSelections(final List<Relation> relsToJoin, final Map<String, String> aggregates) {
            String agg = null;
            String prop = null;
            String toRet = "";
            for (final String s : aggregates.keySet()) {
                agg = s.split("\\(")[0];
                prop = s.split("\\(")[1].split("\\)")[0];
                for (final Relation r : relsToJoin) {
                    if (r.getAttribAliases().contains(prop)) {
                        final int indx = r.getAttribAliases().indexOf(prop);
                        prop = r.getAlias() + "." + r.getOriginalAtts().get(indx);
                    }
                }
                toRet = toRet + agg + "(" + prop + ")";
                aggregates.put(toRet, aggregates.get(s));
                aggregates.remove(s);
                toRet += ",";
            }
            return toRet.substring(0, toRet.length() - 1) + " ";
        }
        
        public List<Relation> getRelsToJoin() {
            return this.relsToJoin;
        }
        
        public void setRelsToJoin(final List<Relation> relsToJoin) {
            this.relsToJoin = relsToJoin;
        }
        
        @Override
        public void enterLoop_stmt(final DatalogParser.Loop_stmtContext ctx) {
            GraphGenerator.egoQuery = true;
        }
    }
}
