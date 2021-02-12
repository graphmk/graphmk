// 
// Decompiled by Procyon v0.5.36
// 

package com.heftdb.mkcore;

import com.heftdb.mkcore.interfaces.Graph;
import com.heftdb.mkcore.interfaces.Vertex;
import com.heftdb.mkcore.mkelement.MkVertex;
import com.heftdb.utils.Utils;
import java.io.File;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.tinkerpop.blueprints.GraphQuery;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import java.util.List;

import com.tinkerpop.blueprints.util.ExceptionFactory;
import java.util.HashMap;
import com.tinkerpop.blueprints.Features;
import java.util.Map;
import java.util.ArrayList;
import java.io.Serializable;

public class MkGraph implements Graph, Serializable
{
    private static final long serialVersionUID = 1L;
    ArrayList<Vertex> vertices;
    public Map<String, Integer> index;
    public ArrayList<ArrayList<Vertex>> inVertices;
    public ArrayList<ArrayList<Vertex>> outVertices;
    private static final Features FEATURES;
    private static final Features PERSISTENT_FEATURES;
    private String directory;
    protected long numOfEdges;
    
    @Override
    public Map<String, Integer> getIndex() {
        return this.index;
    }
    
    public void setIndex(final Map<String, Integer> index) {
        this.index = index;
    }
    
    public long getNumOfEdges() {
        return this.numOfEdges;
    }
    
    public MkGraph() {
        this.vertices = new ArrayList<Vertex>();
        this.index = new HashMap<String, Integer>();
        this.inVertices = new ArrayList<ArrayList<Vertex>>();
        this.outVertices = new ArrayList<ArrayList<Vertex>>();
    }
    
    @Override
    public Features getFeatures() {
        if (null == this.directory) {
            return MkGraph.FEATURES;
        }
        return MkGraph.PERSISTENT_FEATURES;
    }
    
    @Override
    public Vertex addVertex(final Object id) {
        if (this.index.containsKey(id)) {
            throw ExceptionFactory.vertexWithIdAlreadyExists(id);
        }
        final Vertex v = new MkVertex(id, this);
        this.vertices.add(v);
        this.index.put(id.toString(), this.vertices.size() - 1);
        this.inVertices.add(new ArrayList<Vertex>());
        this.outVertices.add(new ArrayList<Vertex>());
        return v;
    }
    
    public Vertex addCopyOfVertex(final Vertex v) {
        final MkVertex copy = new MkVertex(v.getId(), this);
        for (final String o : v.getPropertyKeys()) {
            copy.setProperty(o, v.getProperty(o));
        }
        this.vertices.add(copy);
        this.index.put(v.toString(), this.vertices.size() - 1);
        this.inVertices.add(new ArrayList<Vertex>());
        this.outVertices.add(new ArrayList<Vertex>());
        return copy;
    }
    
    @Override
    public Vertex getVertex(final Object id) {
        if (this.index.get(id.toString()) != null) {
            return this.vertices.get(this.index.get(id.toString()));
        }
        return null;
    }
    
    @Override
    public void removeVertex(final Vertex vertex) {
        throw new UnsupportedOperationException("Deletion is too expensive");
    }
    
    @Override
    public List<Vertex> getVertices() {
        return this.vertices;
    }
    
    public ArrayList<ArrayList<Vertex>> getOutVertices() {
        return this.outVertices;
    }
    
    @Override
    public Iterable<Vertex> getVertices(final String key, final Object value) {
        throw new UnsupportedOperationException("Get Specific set of vertices not supported");
    }
    
    @Override
    public Edge addEdge(final Object id, final Vertex outVertex, final Vertex inVertex, final String label) {
        ++this.numOfEdges;
        if (!this.index.containsKey(outVertex.getId())) {
            this.addVertex(outVertex.getId());
        }
        if (!this.index.containsKey(inVertex.getId())) {
            this.addVertex(inVertex.getId());
        }
        final Integer outIndx = this.index.get(outVertex.getId());
        final Integer inIndx = this.index.get(inVertex.getId());
        this.inVertices.get(inIndx).add(outVertex);
        this.outVertices.get(outIndx).add(inVertex);
        return null;
    }
    
    public boolean existsPhysicalEdge(final Vertex v, final Vertex w) {
        return this.outVertices.get(this.index.get(v.toString())).contains(w) || this.inVertices.get(this.index.get(v.toString())).contains(w);
    }
    
    @Override
    public Edge getEdge(final Object id) {
        throw new UnsupportedOperationException("Edges don't have ids");
    }
    
    @Override
    public void removeEdge(final Edge edge) {
        this.removeEdge(edge.getVertex(Direction.OUT).getId().toString(), edge.getVertex(Direction.IN).getId().toString());
    }
    
    public void removeEdge(final String sourceId, final String destId) {
        final int sindx = this.index.get(sourceId);
        final int dindx = this.index.get(destId);
        this.outVertices.get(sindx).remove(this.vertices.get(dindx));
        this.inVertices.get(dindx).remove(this.vertices.get(sindx));
        --this.numOfEdges;
    }
    
    @Override
    public Iterable<Edge> getEdges(final String key, final Object value) {
        throw new UnsupportedOperationException("Specific Edge Set iteration not supported!");
    }
    
    @Override
    public void shutdown() {
    }
    
    @Override
    public String toString() {
        final GremlinPipeline pipe = new GremlinPipeline();
        final long numOfVertices = pipe.start(this.getVertices()).count();
        return "gengrpah [vertices:" + numOfVertices + " edges:" + this.numOfEdges + "]";
    }
    
    public static <K extends Comparable, V extends Comparable> Map<K, V> sortByValues(final Map<K, V> map, final boolean desc) {
        final List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        final Map<K, V> sortedMap = new LinkedHashMap<K, V>();
        if (desc) {
            Collections.reverse(entries);
        }
        for (final Map.Entry<K, V> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
    
    public boolean alreadyConnected(final String idToRem, final String vid, final boolean onlyDirectly) {
        if (!onlyDirectly) {
            for (final Vertex v : this.outVertices.get(this.index.get(idToRem))) {
                if (v instanceof VertexOptMk && this.inVertices.get(this.index.get(v.getId())).contains(this.vertices.get(this.index.get(vid)))) {
                    return true;
                }
            }
        }
        return this.outVertices.get(this.index.get(idToRem)).contains(this.vertices.get(this.index.get(vid))) || this.inVertices.get(this.index.get(idToRem)).contains(this.vertices.get(this.index.get(vid)));
    }
    
    public int bfs(final String rootId) {
        Vertex curr = this.getVertex(rootId);
        final BitSet b = new BitSet();
        final List<Vertex> queue = new LinkedList<Vertex>();
        b.set(this.index.get(curr.toString()));
        boolean flag = false;
        int distance = 0;
        boolean firstTime = true;
        while (queue.size() >= 0) {
            for (final Vertex v : curr.getVertices(Direction.BOTH, new String[0])) {
                final int indx = this.index.get(v.toString());
                if (!b.get(indx)) {
                    flag = true;
                    b.set(indx);
                    queue.add(v);
                }
            }
            if (firstTime) {
                ++distance;
                queue.add(null);
                firstTime = false;
            }
            if (queue.size() <= 0) {
                break;
            }
            curr = queue.remove(0);
            if (queue.size() > 0 && curr == null) {
                ++distance;
                curr = queue.remove(0);
                if (flag) {
                    queue.add(null);
                }
                flag = false;
            }
            if (queue.size() == 0) {
                break;
            }
        }
        return distance;
    }
    
    public void eccentricity() {
        final int numProcs = Runtime.getRuntime().availableProcessors();
        final ExecutorService executor = Executors.newFixedThreadPool(numProcs);
        System.out.println("Num procs " + numProcs);
        for (final Vertex v : this.getVertices()) {
            final Runnable worker = new Runnable() {
                @Override
                public void run() {
                    final int bf = MkGraph.this.bfs(v.toString());
                    v.setProperty("eccentricity", bf);
                }
            };
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {}
        System.out.println("Finished all threads");
    }
    
    @Override
    public Iterable<Edge> getEdges() {
        return null;
    }
    
    @Override
    public GraphQuery query() {
        return null;
    }
    
    public void toJSON(final String path, final String labelPar, final boolean color) {
        final JSONObject json = new JSONObject();
        final JSONArray edgesArr = new JSONArray();
        final JSONArray nodesArr = new JSONArray();
        int x = 0;
        int y = 0;
        int eId = 0;
        json.put("edges", edgesArr);
        json.put("nodes", nodesArr);
        final BitSet checked = new BitSet();
        for (final Vertex v : this.getVertices()) {
            JSONObject js = new JSONObject();
            js.put("x", x--);
            js.put("y", y++);
            js.put("label", v.toString());
            if (color) {
                js.put("color", "rgb(90,90,90)");
            }
            js.put("id", v.toString());
            js.put("size", 100);
            nodesArr.add(js);
            for (final Vertex w : v.getVertices(Direction.BOTH, new String[0])) {
                if (!checked.get(this.index.get(w.toString()))) {
                    js = new JSONObject();
                    js.put("id", Integer.toString(eId++));
                    js.put("source", v.toString());
                    js.put("target", w.toString());
                    edgesArr.add(js);
                }
            }
            checked.set(this.index.get(v.toString()));
        }
        Utils.flushToFile(new File(path), json.toString());
    }
    
    public static GraphOptMk testGraph5() {
        final GraphFactory factory = new GraphFactory();
        final GraphOptMk g = (GraphOptMk)factory.create("Condensed");
        final Vertex G = g.addVertex("G");
        final Vertex F = g.addVertex("F");
        final Vertex H = g.addVertex("H");
        final Vertex A = g.addVertex("A");
        final Vertex B = g.addVertex("B");
        final Vertex C = g.addVertex("C");
        final Vertex D = g.addVertex("D");
        final Vertex E = g.addVertex("E");
        final Vertex I = g.addVertex("I");
        final Vertex J = g.addVertex("J");
        g.addEdge(null, G, A, "");
        g.addEdge(null, F, A, "");
        g.addEdge(null, H, G, "");
        g.addEdge(null, A, H, "");
        g.addEdge(null, F, G, "");
        g.addEdge(null, F, H, "");
        g.addEdge(null, B, H, "");
        g.addEdge(null, A, B, "");
        g.addEdge(null, B, C, "");
        g.addEdge(null, C, D, "");
        g.addEdge(null, D, E, "");
        g.addEdge(null, C, H, "");
        g.addEdge(null, B, D, "");
        g.addEdge(null, H, D, "");
        g.addEdge(null, G, B, "");
        g.addEdge(null, G, C, "");
        return g;
    }
    
    static {
        FEATURES = new Features();
        MkGraph.FEATURES.supportsDuplicateEdges = true;
        MkGraph.FEATURES.supportsSelfLoops = true;
        MkGraph.FEATURES.supportsSerializableObjectProperty = true;
        MkGraph.FEATURES.supportsBooleanProperty = true;
        MkGraph.FEATURES.supportsDoubleProperty = true;
        MkGraph.FEATURES.supportsFloatProperty = true;
        MkGraph.FEATURES.supportsIntegerProperty = true;
        MkGraph.FEATURES.supportsPrimitiveArrayProperty = true;
        MkGraph.FEATURES.supportsUniformListProperty = true;
        MkGraph.FEATURES.supportsMixedListProperty = true;
        MkGraph.FEATURES.supportsLongProperty = true;
        MkGraph.FEATURES.supportsMapProperty = true;
        MkGraph.FEATURES.supportsStringProperty = true;
        MkGraph.FEATURES.ignoresSuppliedIds = false;
        MkGraph.FEATURES.isPersistent = false;
        MkGraph.FEATURES.isWrapper = false;
        MkGraph.FEATURES.supportsIndices = false;
        MkGraph.FEATURES.supportsKeyIndices = false;
        MkGraph.FEATURES.supportsVertexKeyIndex = true;
        MkGraph.FEATURES.supportsEdgeKeyIndex = false;
        MkGraph.FEATURES.supportsVertexIndex = true;
        MkGraph.FEATURES.supportsEdgeIndex = false;
        MkGraph.FEATURES.supportsTransactions = false;
        MkGraph.FEATURES.supportsVertexIteration = true;
        MkGraph.FEATURES.supportsEdgeIteration = false;
        MkGraph.FEATURES.supportsEdgeRetrieval = false;
        MkGraph.FEATURES.supportsVertexProperties = true;
        MkGraph.FEATURES.supportsEdgeProperties = false;
        MkGraph.FEATURES.supportsThreadedTransactions = false;
        MkGraph.FEATURES.supportsThreadIsolatedTransactions = false;
        PERSISTENT_FEATURES = MkGraph.FEATURES.copyFeatures();
        MkGraph.PERSISTENT_FEATURES.isPersistent = true;
    }
    
    public enum FileType
    {
        GML, 
        GRAPHML, 
        GRAPHSON;
    }
}
