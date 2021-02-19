// 
// Decompiled by Procyon v0.5.36
// 

package io.github.graphmk.mkcore;

import java.io.FileWriter;

import io.github.graphmk.fpgrowth.FPGrowth;
import io.github.graphmk.fpgrowth.FPTree;
import io.github.graphmk.iterators.ConcatList;
import io.github.graphmk.iterators.OTFListNoBMP;
import io.github.graphmk.mkcore.interfaces.Vertex;
import io.github.graphmk.mkcore.mkelement.MkVertex;
import io.github.graphmk.utils.DegreeComparator;
import io.github.graphmk.utils.Utils;
import io.github.graphmk.utils.VirtualDegreeComparator;
import io.github.graphmk.vertexCentric.DegreeExecutor;
import io.github.graphmk.vertexCentric.VertexCentric;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

import com.tinkerpop.blueprints.util.io.graphson.GraphSONWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.Random;
import java.util.NoSuchElementException;
import java.util.Collections;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.tinkerpop.blueprints.Edge;

import java.util.HashSet;
import java.util.List;
import java.util.BitSet;
import com.tinkerpop.blueprints.Direction;
import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import org.apache.log4j.Logger;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.paukov.combinatorics.CombinatoricsFactory;

import java.io.Serializable;

public class GraphOptMk extends MkGraph implements Serializable
{
    private static final long serialVersionUID = 1L;
    static final Logger logger;
    protected Map<String, Integer> virtualIndex;
    public ArrayList<ArrayList<Vertex>> inVertices;
    private boolean isExpanded;
    public static Set<String> theSetToIgnore;
    public static String virtIdentifier;
    public static String virtArtIdentifier;
    private Representation rep;
    
    public GraphOptMk() {
        this.virtualIndex = new HashMap<String, Integer>();
        this.inVertices = new ArrayList<ArrayList<Vertex>>();
        this.vertices = new ArrayList<Vertex>();
        this.index = new HashMap<String, Integer>();
        this.outVertices = new ArrayList<ArrayList<Vertex>>();
        this.rep = Representation.CDUP;
    }
    
    public GraphOptMk(final Representation r) {
        this();
    }
    
    private void initSingleBMP() {
        for (final Vertex v : this.vertices) {
            if (v instanceof VertexOptMk) {
                final VertexOptMk cv = (VertexOptMk)v;
                final List<? extends Vertex> l = cv.getPhysicalNeighbors(Direction.IN);
                cv.setBitMap(new BitSet(l.size()));
                for (int i = 0; i < l.size(); ++i) {
                    cv.getBitMap().set(this.index.get(((Vertex)l.get(i)).toString()));
                }
            }
        }
    }
    
    @Override
    public List<Vertex> getVertices() {
        final List<Vertex> theVertices = new ArrayList<Vertex>();
        for (final Vertex v : this.vertices) {
            if (!(v instanceof VertexOptMk)) {
                theVertices.add(v);
            }
        }
        return theVertices;
    }
    
    public List<Vertex> getAllVertices() {
        return this.vertices;
    }
    
    public void rebuildIndex() {
        this.index.clear();
        for (int i = 0; i < this.vertices.size(); ++i) {
            final String v = this.vertices.get(i).toString();
            this.index.put(v, i);
        }
    }
    
    public void removeLogicalEdge(final Vertex outV, final Vertex inV) {
        final MkVertex gOut = (MkVertex)outV;
        switch (this.rep) {
            case CDUP: {
                if (this.existsPhysicalEdgeEff(outV, inV)) {
                    this.removeEdgeEff(outV.toString(), inV.toString());
                    return;
                }
                final Set<String> union = new HashSet<String>();
                final Set<String> stillConnected = new HashSet<String>();
                final Set<String> toRemSet = new HashSet<String>();
                for (final Vertex r : new OTFListNoBMP(gOut)) {
                    union.add(r.toString());
                }
                for (final Vertex v : gOut.getPhysicalNeighbors(Direction.OUT)) {
                    if (v instanceof VertexOptMk) {
                        for (final Vertex w : ((VertexOptMk)v).getPhysicalNeighbors(Direction.IN)) {
                            if (w.equals(inV)) {
                                toRemSet.add(v.toString());
                                break;
                            }
                        }
                    }
                }
                for (final String s : toRemSet) {
                    this.removeEdgeEff(outV.toString(), s.toString());
                }
                for (final Vertex r : new OTFListNoBMP(gOut)) {
                    stillConnected.add(r.toString());
                }
                for (final String s : union) {
                    if (!stillConnected.contains(s) && !s.equals(inV.toString())) {
                        this.addEdge(null, outV, this.getVertex(s), null);
                    }
                }
            }
            case DEDUP1: {
                if (this.existsPhysicalEdge(outV, inV)) {
                    this.removeEdge(outV.toString(), inV.toString());
                    return;
                }
                final Set<String> union = new HashSet<String>();
                final Set<String> stillConnected = new HashSet<String>();
                for (final Vertex r : new ConcatList(gOut)) {
                    union.add(r.toString());
                }
                boolean found = false;
                Vertex toRem = null;
                for (final Vertex v2 : gOut.getPhysicalNeighbors(Direction.OUT)) {
                    if (v2 instanceof VertexOptMk) {
                        for (final Vertex w2 : v2.getPhysicalNeighbors(Direction.IN)) {
                            if (w2.equals(inV)) {
                                toRem = v2;
                                found = true;
                                break;
                            }
                        }
                    }
                    if (found) {
                        break;
                    }
                }
                this.removeEdge(outV.toString(), toRem.toString());
                for (final Vertex r2 : new ConcatList(gOut)) {
                    stillConnected.add(r2.toString());
                }
                for (final String s2 : union) {
                    if (!stillConnected.contains(s2) && !s2.equals(inV.toString())) {
                        this.addEdge(null, outV, this.getVertex(s2), null);
                    }
                }
            }
            case BMP1: {
                for (final Vertex v3 : gOut.getPhysicalNeighbors(Direction.OUT)) {
                    final VertexOptMk cv = (VertexOptMk)v3;
                    final Map<String, Integer> indx = cv.getBMPIndex();
                    if (indx.containsKey(inV.toString()) && cv.getBitmaps().get(indx.get(outV.toString())).get(indx.get(inV.toString()))) {
                        final Integer idxIn = indx.get(inV.toString());
                        final Integer idxOut = indx.get(outV.toString());
                        cv.getBitmaps().get(indx.get(outV.toString())).set(idxIn, false);
                        cv.getBitmaps().get(indx.get(inV.toString())).set(idxOut, false);
                    }
                }
            }
            case BMP2: {
                for (final Vertex v3 : gOut.getPhysicalNeighbors(Direction.OUT)) {
                    final VertexOptMk cv = (VertexOptMk)v3;
                    final Map<String, Integer> indx = cv.getBMPIndex();
                    if (indx.containsKey(inV.toString()) && cv.getBitmaps().get(indx.get(outV.toString())).get(indx.get(inV.toString()))) {
                        final Integer idxIn = indx.get(inV.toString());
                        final Integer idxOut = indx.get(outV.toString());
                        cv.getBitmaps().get(indx.get(outV.toString())).set(idxIn, false);
                        cv.getBitmaps().get(indx.get(inV.toString())).set(idxOut, false);
                    }
                }
            }
            case EXP: {
                this.removeEdgeEff(outV.toString(), inV.toString());
            }
            default: {}
        }
    }
    
    @Override
    public void removeVertex(final Vertex v) {
        final MkVertex gv = (MkVertex)v;
        if (this.rep.equals(Representation.BMP1) || this.rep.equals(Representation.BMP2)) {
            for (final Vertex w : gv.getPhysicalNeighbors(Direction.OUT)) {
                if (w instanceof VertexOptMk) {
                    final VertexOptMk gw = (VertexOptMk)w;
                    final int idx = gw.getBMPIndex().get(v.toString());
                    gw.getBMPIndex().remove(v.toString());
                    gw.rebuildBMPIndex3();
                }
            }
        }
        List<? extends Vertex> l = gv.getPhysicalNeighbors(Direction.OUT);
        int i = 0;
        while (i < l.size()) {
            final Vertex w2 = (Vertex)l.get(i);
            this.removeEdge(gv.toString(), w2.toString());
        }
        l = gv.getPhysicalNeighbors(Direction.IN);
        i = 0;
        while (i < l.size()) {
            final Vertex w2 = (Vertex)l.get(i);
            this.removeEdge(w2.toString(), gv.toString());
        }
        this.removeDisconnectedVertex(this.index.get(v.toString()));
        this.rebuildIndex();
    }
    
    @Override
    public Iterable<Edge> getEdges() {
        return super.getEdges();
    }
    
    @Override
    public Vertex addVertex(final Object id) {
        if (this.index.containsKey(id)) {
            throw ExceptionFactory.vertexWithIdAlreadyExists(id);
        }
        Vertex v = null;
        if (id.toString().startsWith(GraphOptMk.virtIdentifier)) {
            v = new VertexOptMk(id, this);
            this.virtualIndex.put(id.toString(), 0);
        }
        else {
            v = new MkVertex(id, this);
        }
        this.vertices.add(v);
        this.index.put(id.toString(), this.vertices.size() - 1);
        this.inVertices.add(new ArrayList<Vertex>());
        this.outVertices.add(new ArrayList<Vertex>());
        return v;
    }
    
    public void removeEdgeEff(final String sourceId, final String destId) {
        GraphOptMk.logger.trace(" EFF trying to remove edge: " + sourceId + "-> " + destId);
        final int sindx = this.index.get(sourceId);
        final int dindx = this.index.get(destId);
        final int s1 = Collections.binarySearch(this.outVertices.get(sindx), this.vertices.get(dindx));
        final int s2 = Collections.binarySearch(this.inVertices.get(sindx), this.vertices.get(dindx));
        final int d1 = Collections.binarySearch(this.outVertices.get(dindx), this.vertices.get(sindx));
        final int d2 = Collections.binarySearch(this.inVertices.get(dindx), this.vertices.get(sindx));
        if (s1 < 0 && s2 < 0) {
            GraphOptMk.logger.debug("STOpping....");
            throw new NoSuchElementException("Called on an unsorted list");
        }
        boolean r1 = false;
        boolean r2 = false;
        if (s1 >= 0) {
            this.outVertices.get(sindx).remove(s1);
            r1 = true;
        }
        if (s2 >= 0) {
            this.inVertices.get(sindx).remove(s2);
            r2 = true;
        }
        if (d1 >= 0) {
            this.outVertices.get(dindx).remove(d1);
            r1 = true;
        }
        if (d2 >= 0) {
            this.inVertices.get(dindx).remove(d2);
            r2 = true;
        }
        if (r1 || r2) {
            --this.numOfEdges;
        }
        GraphOptMk.logger.trace("EFF Removed Edge : " + sourceId + "->" + destId);
    }
    
    @Override
    public void removeEdge(final String sourceId, final String destId) {
        GraphOptMk.logger.trace("trying to remove edge: " + sourceId + "-> " + destId);
        final int sindx = this.index.get(sourceId);
        final int dindx = this.index.get(destId);
        final boolean s1 = this.outVertices.get(sindx).remove(this.vertices.get(dindx));
        final boolean s2 = this.inVertices.get(sindx).remove(this.vertices.get(dindx));
        final boolean d1 = this.outVertices.get(dindx).remove(this.vertices.get(sindx));
        final boolean d2 = this.inVertices.get(dindx).remove(this.vertices.get(sindx));
        if (s1 || s2 || d1 || d2) {
            --this.numOfEdges;
        }
        GraphOptMk.logger.trace("Removed Edge : " + sourceId + "->" + destId);
    }
    
    public boolean existsPhysicalEdgeEff(final Vertex v, final Vertex w) {
        GraphOptMk.logger.trace("Looking for edge: " + v + "-> " + w);
        final int out = Collections.binarySearch(this.outVertices.get(this.index.get(v.toString())), w);
        final int in = Collections.binarySearch(this.inVertices.get(this.index.get(v.toString())), w);
        return out >= 0 || in >= 0;
    }
    
    @Override
    public boolean existsPhysicalEdge(final Vertex v, final Vertex w) {
        return this.outVertices.get(this.index.get(v.toString())).contains(w) || this.inVertices.get(this.index.get(v.toString())).contains(w);
    }
    
    public boolean existsEdge(final Vertex v, final Vertex w) {
        final MkVertex gv = (MkVertex)v;
        switch (this.rep) {
            case EXP: {
                return this.existsPhysicalEdgeEff(v, w);
            }
            case CDUP: {
                for (final Vertex cv : new ConcatList((MkVertex)v)) {
                    if (cv.equals(w)) {
                        return true;
                    }
                }
                return false;
            }
            case DEDUP1: {
                for (final Vertex cv : new ConcatList((MkVertex)v)) {
                    if (cv.equals(w)) {
                        return true;
                    }
                }
                return false;
            }
            case BMP1: {
                for (final Vertex z : gv.getPhysicalNeighbors(Direction.OUT)) {
                    final VertexOptMk cz = (VertexOptMk)z;
                    if (cz.getBMPIndex().containsKey(w.toString())) {
                        return true;
                    }
                }
                return false;
            }
            case BMP2: {
                for (final Vertex z : gv.getPhysicalNeighbors(Direction.OUT)) {
                    final VertexOptMk cz = (VertexOptMk)z;
                    if (cz.getBMPIndex().containsKey(w.toString())) {
                        return true;
                    }
                }
                return false;
            }
            default: {
                return false;
            }
        }
    }
    
    public void addLogicalEdge(final Vertex outVertex, final Vertex inVertex) {
        final MkVertex gout = (MkVertex)outVertex;
        final MkVertex gin = (MkVertex)inVertex;
        switch (this.rep) {
            case CDUP: {
                this.addEdge(null, outVertex, inVertex, null);
            }
            case DEDUP1: {
                this.addEdge(null, outVertex, inVertex, null);
            }
            case BMP1: {
                boolean found = false;
                for (final Vertex z : gout.getPhysicalNeighbors(Direction.OUT)) {
                    final VertexOptMk cz = (VertexOptMk)z;
                    final int indxOfVout = cz.getBMPIndex().get(outVertex.toString());
                    if (cz.getBMPIndex().containsKey(inVertex.toString())) {
                        found = true;
                        final int indxOfVin = cz.getBMPIndex().get(inVertex.toString());
                        if (cz.getBitmaps().get(indxOfVout).get(indxOfVin)) {
                            continue;
                        }
                        cz.getBitmaps().get(indxOfVout).flip(indxOfVin);
                        cz.getBitmaps().get(indxOfVin).flip(indxOfVout);
                    }
                }
                if (!found) {
                    final List<? extends Vertex> inVNodes = gin.getPhysicalNeighbors(Direction.OUT);
                    final VertexOptMk cout = (VertexOptMk)inVNodes.get(new Random().nextInt(inVNodes.size()));
                    this.addEdge(null, outVertex, cout, null);
                    final int indxOfVout2 = cout.getBMPIndex().size();
                    cout.getBMPIndex().put(outVertex.toString(), cout.getBitmaps().size());
                    cout.getBitmaps().add(new BitSet(cout.getBitmaps().size()));
                    final int indxOfVin2 = cout.getBMPIndex().get(inVertex.toString());
                    cout.getBitmaps().get(indxOfVin2).set(indxOfVout2);
                    cout.getBitmaps().get(indxOfVout2).set(indxOfVin2);
                }
            }
            case BMP2: {
                boolean found = false;
                for (final Vertex z : gout.getPhysicalNeighbors(Direction.OUT)) {
                    final VertexOptMk cz = (VertexOptMk)z;
                    final int indxOfVout = cz.getBMPIndex().get(outVertex.toString());
                    if (cz.getBMPIndex().containsKey(inVertex.toString())) {
                        found = true;
                        final int indxOfVin = cz.getBMPIndex().get(inVertex.toString());
                        if (cz.getBitmaps().get(indxOfVout).get(indxOfVin)) {
                            continue;
                        }
                        cz.getBitmaps().get(indxOfVout).flip(indxOfVin);
                        cz.getBitmaps().get(indxOfVin).flip(indxOfVout);
                    }
                }
                if (!found) {
                    final List<? extends Vertex> inVNodes = gin.getPhysicalNeighbors(Direction.OUT);
                    final VertexOptMk cout = (VertexOptMk)inVNodes.get(new Random().nextInt(inVNodes.size()));
                    this.addEdge(null, outVertex, cout, null);
                    final int indxOfVout2 = cout.getBMPIndex().size();
                    cout.getBMPIndex().put(outVertex.toString(), cout.getBitmaps().size());
                    cout.getBitmaps().add(new BitSet(cout.getBitmaps().size()));
                    final int indxOfVin2 = cout.getBMPIndex().get(inVertex.toString());
                    cout.getBitmaps().get(indxOfVin2).set(indxOfVout2);
                    cout.getBitmaps().get(indxOfVout2).set(indxOfVin2);
                }
            }
            case EXP: {
                this.addEdge(null, outVertex, inVertex, null);
            }
            default: {}
        }
    }
    
    @Override
    public Edge addEdge(final Object id, final Vertex outVertex, final Vertex inVertex, final String label) {
        if (!this.isExpanded && inVertex instanceof VertexOptMk) {
            this.virtualIndex.put(inVertex.toString(), this.virtualIndex.get(inVertex.toString()) + 1);
        }
        ++this.numOfEdges;
        if (outVertex == null || inVertex == null) {
            System.out.println("Tried to add: " + outVertex + "->" + inVertex);
        }
        final Integer outIndx = this.index.get(outVertex.toString());
        final Integer inIndx = this.index.get(inVertex.toString());
        this.inVertices.get(inIndx).add(outVertex);
        this.outVertices.get(outIndx).add(inVertex);
        GraphOptMk.logger.trace("Added Edge : " + outVertex + "->" + inVertex);
        return null;
    }
    
    public void sortIndex() {
        this.virtualIndex = sortByValues(this.virtualIndex, true);
    }
    
    public void deduplicate1(final Ordering o) {
        int numOfIntersections = 0;
        int numOfDeduplications = 0;
        int numOfIterations = 0;
        final Set<String> processed = new HashSet<String>();
        final List<String> srtd = this.orderVNodes(o);
        for (final String s : srtd) {
            GraphOptMk.logger.trace("For " + s);
            ++numOfIterations;
            final Set<String> union = this.getVNodeUnion(s);
            GraphOptMk.logger.trace(s + "'s relevant nodes: " + union);
            GraphOptMk.logger.trace("Processed set: " + processed);
            for (final String p : union) {
                if (!p.equals(s) && processed.contains(p)) {
                    ++numOfIntersections;
                    final List<String> inter = intersect(this.inVertices.get(this.index.get(s)), this.inVertices.get(this.index.get(p)));
                    GraphOptMk.logger.trace(s + " intsct " + p + ": " + inter);
                    if (inter.size() <= 1) {
                        continue;
                    }
                    ++numOfDeduplications;
                    this.resolveDuplicates1(s, p, inter);
                }
            }
            GraphOptMk.logger.trace("Done with " + s);
            processed.add(s);
        }
        this.rep = Representation.DEDUP1;
        this.finalize();
    }
    
    private Set<String> getVNodeUnion(final String vNodeId) {
        final Set<String> toRet = new HashSet<String>();
        final MkVertex vNode = (MkVertex)this.getVertex(vNodeId);
        for (final Vertex v : vNode.getPhysicalNeighbors(Direction.IN)) {
            final MkVertex gv = (MkVertex)v;
            for (final Vertex w : gv.getPhysicalNeighbors(Direction.OUT)) {
                if (w instanceof VertexOptMk) {
                    toRet.add(w.toString());
                }
            }
        }
        return toRet;
    }
    
    private void resolveDuplicates1(final String id1, final String id2, final List<String> inter) {
        final List<Vertex> v1 = this.inVertices.get(this.index.get(id1));
        final List<Vertex> v2 = this.inVertices.get(this.index.get(id2));
        final int degree1 = v1.size();
        final int degree2 = v2.size();
        final Set<String> union = new HashSet<String>();
        while (inter.size() > 1) {
            final String idToRem = inter.get(0);
            String minVNode;
            if (degree1 > degree2) {
                minVNode = id2;
            }
            else {
                minVNode = id1;
            }
            GraphOptMk.logger.trace("Choosing: " + minVNode);
            this.removeEdge(idToRem, minVNode);
            for (final Vertex xy : this.outVertices.get(this.index.get(idToRem))) {
                if (xy instanceof VertexOptMk) {
                    for (final Vertex g : this.inVertices.get(this.index.get(xy.getId().toString()))) {
                        union.add(g.getId().toString());
                    }
                }
                else {
                    union.add(xy.getId().toString());
                }
            }
            for (final Vertex v3 : this.inVertices.get(this.index.get(minVNode))) {
                if (!union.contains(v3.getId().toString())) {
                    this.addEdge("", this.vertices.get(this.index.get(idToRem)), this.vertices.get(this.index.get(v3.toString())), "");
                    union.add(v3.getId().toString());
                }
            }
            inter.remove(0);
            union.clear();
        }
    }
    
    public List<Vertex> getSortedRealNodes(final Ordering o) throws InterruptedException {
        final VertexCentric v = new VertexCentric(this);
        final long start = System.currentTimeMillis();
        v.run(new DegreeExecutor());
        final long end = System.currentTimeMillis();
        System.out.println("Degree took " + (end - start));
        final List<Vertex> srted = new ArrayList<Vertex>();
        for (final Vertex w : this.getVertices()) {
            srted.add(w);
        }
        Comparator<Vertex> comp = null;
        if (o.equals(Ordering.INCR)) {
            comp = new DegreeComparator(true);
        }
        else if (o.equals(Ordering.DECR)) {
            comp = new DegreeComparator(false);
        }
        else if (o.equals(Ordering.RAND)) {
            Collections.shuffle(srted);
        }
        if (comp != null) {
            Collections.sort(srted, comp);
        }
        return srted;
    }
    
    public void deduplicate2(final Ordering o) {
        int numOfIntersections = 0;
        int numOfDeduplications = 0;
        int numOfIterations = 0;
        if (!this.isExpanded) {
            final Set<Vertex> toProcess = new HashSet<Vertex>();
            List<Vertex> srtd = this.vertices;
            try {
                srtd = this.getSortedRealNodes(o);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (final Vertex v : srtd) {
                if (!(v instanceof VertexOptMk)) {
                    ++numOfIterations;
                    final List<Vertex> vNodes = new ArrayList<Vertex>(this.outVertices.get(this.index.get(v.getId().toString())));
                    for (int i = 0; i < vNodes.size(); ++i) {
                        final Vertex w = vNodes.get(i);
                        if (w instanceof VertexOptMk) {
                            for (final Vertex p : toProcess) {
                                ++numOfIntersections;
                                final List<String> inter = intersect(this.inVertices.get(this.index.get(w.getId().toString())), this.inVertices.get(this.index.get(p.getId().toString())));
                                if (inter.size() > 1) {
                                    ++numOfDeduplications;
                                    this.resolveDuplicates2(w.getId().toString(), p.getId().toString(), inter);
                                }
                            }
                            toProcess.add(w);
                        }
                    }
                    toProcess.clear();
                }
            }
        }
        this.rep = Representation.DEDUP1;
    }
    
    private void resolveDuplicates2(final String id1, final String id2, final List<String> inter) {
        final List<Vertex> v1 = this.inVertices.get(this.index.get(id1));
        final List<Vertex> v2 = this.inVertices.get(this.index.get(id2));
        final BitSet union = new BitSet();
        while (inter.size() > 1) {
            final String idToRem = inter.get(0);
            final int degree1 = v1.size();
            final int degree2 = v2.size();
            String minVNode;
            if (degree1 > degree2) {
                minVNode = id2;
            }
            else {
                minVNode = id1;
            }
            this.removeEdge(idToRem, minVNode);
            final int minVindx = this.index.get(minVNode);
            for (final Vertex xy : this.outVertices.get(this.index.get(idToRem))) {
                if (xy instanceof VertexOptMk) {
                    for (final Vertex g : this.inVertices.get(this.index.get(xy.toString()))) {
                        union.set(this.index.get(g.toString()));
                    }
                }
                else {
                    union.set(this.index.get(xy.toString()));
                }
            }
            for (final Vertex v3 : this.inVertices.get(minVindx)) {
                if (!union.get(this.index.get(v3.toString()))) {
                    this.addEdge("", this.vertices.get(this.index.get(idToRem)), this.vertices.get(this.index.get(v3.getId().toString())), "");
                    union.set(this.index.get(v3.toString()));
                }
            }
            inter.remove(0);
            union.clear();
        }
    }
    
    public void dedupRealNFirst(final Ordering o) {
        final Set<String> vprime = new HashSet<String>();
        final Set<String> vdoubleprime = new HashSet<String>();
        final Set<String> X = new HashSet<String>();
        final Set<String> vDirectEdges = new HashSet<String>();
        final Set<String> vExpandedEdges = new HashSet<String>();
        List<Vertex> srtd = this.vertices;
        if (!o.equals(Ordering.RAND)) {
            try {
                srtd = this.getSortedRealNodes(o);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (final Vertex v : srtd) {
            if (!(v instanceof VertexOptMk)) {
                final MkVertex gv = (MkVertex)v;
                for (final Vertex y : gv.getPhysicalNeighbors(Direction.OUT)) {
                    if (y instanceof VertexOptMk) {
                        vdoubleprime.add(y.toString());
                    }
                }
                for (final Vertex rn : new OTFListNoBMP(gv)) {
                    vDirectEdges.add(rn.toString());
                    vExpandedEdges.add(rn.toString());
                }
                final Map<String, Set<String>> neighborEdgeList = this.generateNeighborList(vExpandedEdges);
                while (vdoubleprime.size() > 0) {
                    final Vertex maxBenefitVNode = this.getMaxBenefit(gv, vprime, vdoubleprime, vDirectEdges, X, neighborEdgeList);
                    if (maxBenefitVNode == null) {
                        for (final String vn : vdoubleprime) {
                            this.removeEdge(v.toString(), vn);
                        }
                        break;
                    }
                    final Set<String> V = new HashSet<String>();
                    for (final Vertex z : ((MkVertex)this.getVertex(maxBenefitVNode.toString())).getPhysicalNeighbors(Direction.IN)) {
                        V.add(z.toString());
                        vDirectEdges.remove(z.toString());
                        this.removeEdge(v.toString(), z.toString());
                        this.removeEdge(z.toString(), v.toString());
                        vprime.add(maxBenefitVNode.toString());
                    }
                    vdoubleprime.remove(maxBenefitVNode.toString());
                    final Set<String> VCapX = intersectSets(V, X);
                    for (final String d : V) {
                        if (!VCapX.contains(d) && !d.equals(v.toString())) {
                            for (final String p : VCapX) {
                                final Set<String> pNeighbors = neighborEdgeList.get(p);
                                final Set<String> dNeighbors = neighborEdgeList.get(d);
                                if (!pNeighbors.contains(d)) {
                                    final Set<String> inter = intersectVNodeSets(pNeighbors, dNeighbors, maxBenefitVNode.toString());
                                    if (inter.size() != 0) {
                                        continue;
                                    }
                                    final Vertex outV = this.getVertex(p);
                                    final Vertex inV = this.getVertex(d);
                                    if (!this.existsPhysicalEdge(outV, inV)) {
                                        this.addEdge(null, outV, inV, null);
                                    }
                                    neighborEdgeList.get(p).add(d);
                                    neighborEdgeList.get(d).add(p);
                                }
                            }
                        }
                    }
                    final ICombinatoricsVector<String> initialVector = CombinatoricsFactory.createVector((Collection<? extends String>)VCapX);
                    final Generator<String> gen = CombinatoricsFactory.createSimpleCombinationGenerator(initialVector, 2);
                    for (final ICombinatoricsVector<String> combination : gen) {
                        final String s = combination.getValue(0);
                        final String k = combination.getValue(1);
                        if (!s.equals(k)) {
                            final Set<String> sNeighbors = neighborEdgeList.get(s);
                            final Set<String> kNeighbors = neighborEdgeList.get(k);
                            if (sNeighbors.contains(k)) {
                                continue;
                            }
                            final Set<String> inter2 = intersectVNodeSets(sNeighbors, kNeighbors, maxBenefitVNode.toString());
                            if (inter2.size() != 0) {
                                continue;
                            }
                            final Vertex outV2 = this.getVertex(s);
                            final Vertex inV2 = this.getVertex(k);
                            if (!this.existsPhysicalEdge(outV2, inV2)) {
                                this.addEdge(null, outV2, inV2, null);
                            }
                            neighborEdgeList.get(s).add(k);
                            neighborEdgeList.get(k).add(s);
                        }
                    }
                    for (final String s2 : V) {
                        if (!s2.equals(v.toString())) {
                            X.add(s2);
                        }
                    }
                    for (final String s2 : VCapX) {
                        neighborEdgeList.get(s2).remove(maxBenefitVNode.toString());
                        this.removeEdge(s2, maxBenefitVNode.toString());
                    }
                }
                for (final String s3 : vDirectEdges) {
                    if (!this.existsPhysicalEdge(v, this.getVertex(s3))) {
                        this.addEdge(null, v, this.getVertex(s3), null);
                    }
                }
                vprime.clear();
                vdoubleprime.clear();
                X.clear();
                vDirectEdges.clear();
                vExpandedEdges.clear();
            }
        }
        this.rep = Representation.DEDUP1;
    }
    
    private Map<String, Set<String>> generateNeighborList(final Set<String> neighbors) {
        final Map<String, Set<String>> nList = new HashMap<String, Set<String>>();
        for (final String s : neighbors) {
            final MkVertex gv = (MkVertex)this.getVertex(s);
            for (final Vertex v : gv.getPhysicalNeighbors(Direction.OUT)) {
                if (!nList.containsKey(s)) {
                    nList.put(s, new HashSet<String>());
                }
                nList.get(s).add(v.toString());
            }
            for (final Vertex v : gv.getPhysicalNeighbors(Direction.IN)) {
                if (!nList.containsKey(s)) {
                    nList.put(s, new HashSet<String>());
                }
                nList.get(s).add(v.toString());
            }
        }
        return nList;
    }
    
    private Vertex getMaxBenefit(final MkVertex v, final Set<String> vprime, final Set<String> vdoubleprime, final Set<String> vDirectEdges, final Set<String> X, final Map<String, Set<String>> neighborLists) {
        String maxBenefitV = null;
        int maxBenefit = 0;
        final Set<String> toRemove = new HashSet<String>();
        for (final String w : vdoubleprime) {
            int b = 0;
            final Set<String> V = new HashSet<String>();
            for (final Vertex z : ((MkVertex)this.getVertex(w)).getPhysicalNeighbors(Direction.IN)) {
                V.add(z.toString());
                if (vDirectEdges.contains(z.toString())) {
                    ++b;
                }
            }
            final Set<String> VCapX = intersectSets(V, X);
            b += VCapX.size();
            if (b > maxBenefit) {
                maxBenefit = b;
                maxBenefitV = w;
            }
            if (b <= 0) {
                toRemove.add(w);
            }
        }
        for (final String tr : toRemove) {
            this.removeEdge(v.toString(), tr);
            vdoubleprime.remove(tr);
        }
        if (maxBenefit > 0) {
            return this.getVertex(maxBenefitV);
        }
        return null;
    }
    
    public List<String> orderVNodes(final Ordering o) {
        final List<String> sorted = new ArrayList<String>(this.virtualIndex.keySet());
        Comparator<String> comp = null;
        if (o.equals(Ordering.INCR)) {
            comp = new VirtualDegreeComparator(this.virtualIndex, true);
        }
        else if (o.equals(Ordering.DECR)) {
            comp = new VirtualDegreeComparator(this.virtualIndex, false);
        }
        else if (o.equals(Ordering.RAND)) {
            Collections.shuffle(sorted);
        }
        if (comp != null) {
            Collections.sort(sorted, comp);
        }
        return sorted;
    }
    
    public void dedupVirtualNFirst(final Ordering o) {
        final Set<String> processed = new HashSet<String>();
        final List<String> srtd = this.orderVNodes(o);
        for (final String s : srtd) {
            GraphOptMk.logger.trace("Deduplicating Virtual Node: " + s);
            final Set<String> union = this.getVNodeUnion(s);
            GraphOptMk.logger.trace("Might have duplicates with: " + union);
            this.resolveDuplication(s, union, processed);
        }
        this.rep = Representation.DEDUP1;
        this.finalize();
    }
    
    private void resolveDuplication(final String vId, final Set<String> union, final Set<String> processed) {
        for (final String s : union) {
            GraphOptMk.logger.trace("For: " + s);
            if (!s.equals(vId) && processed.contains(s)) {
                final List<String> Ci = intersect(this.inVertices.get(this.index.get(s)), this.inVertices.get(this.index.get(vId)));
                GraphOptMk.logger.trace(vId + " intsct " + s + ":" + Ci);
                GraphOptMk.logger.trace("deduplicating real nodes in the intersection...");
                for (final String rn : Ci) {
                    final Set<String> toAddDirectEdges = new HashSet<String>();
                    GraphOptMk.logger.trace(" Calculating max Benefit/cost For :" + rn);
                    final String toRemFrom = this.maxBenCostRatio(rn, vId, s, toAddDirectEdges, union, processed);
                    this.removeEdge(rn, toRemFrom);
                    for (final String c : toAddDirectEdges) {
                        final Vertex outV = this.getVertex(rn);
                        final Vertex inV = this.getVertex(c);
                        if (!this.existsPhysicalEdge(outV, inV)) {
                            this.addEdge(null, outV, inV, null);
                        }
                    }
                }
            }
        }
        GraphOptMk.logger.trace("Done with " + vId);
        processed.add(vId);
    }
    
    private String maxBenCostRatio(final String rn, final String vId, final String s, final Set<String> toAddDirectEdges, final Set<String> union, final Set<String> processed) {
        final Set<String> unionVId = new HashSet<String>();
        final Set<String> dirEdgesVId = new HashSet<String>();
        final Set<String> unionS = new HashSet<String>();
        final Set<String> dirEdgesS = new HashSet<String>();
        int costvId = 1;
        int costS = 1;
        int benefitV = 1;
        final int benefitS = 1;
        final MkVertex gv = (MkVertex)this.getVertex(rn);
        for (final Vertex w : gv.getPhysicalNeighbors(Direction.OUT)) {
            if (w instanceof VertexOptMk) {
                if (w.toString().equals(vId)) {
                    continue;
                }
                for (final Vertex v : ((VertexOptMk)w).getPhysicalNeighbors(Direction.IN)) {
                    unionVId.add(v.toString());
                }
            }
            else {
                unionVId.add(w.toString());
                unionS.add(w.toString());
            }
        }
        for (final Vertex w : gv.getPhysicalNeighbors(Direction.IN)) {
            unionVId.add(w.toString());
            unionS.add(w.toString());
        }
        final MkVertex gvId = (MkVertex)this.getVertex(vId);
        for (final Vertex v2 : gvId.getPhysicalNeighbors(Direction.IN)) {
            if (!unionVId.contains(v2.toString())) {
                dirEdgesVId.add(v2.toString());
                ++costvId;
            }
        }
        for (final String u : union) {
            if (processed.contains(u)) {
                final Vertex gB = this.getVertex(u);
                if (!gB.getPhysicalNeighbors(Direction.IN).contains(this.getVertex(rn))) {
                    continue;
                }
                ++benefitV;
            }
        }
        for (final Vertex w2 : gv.getPhysicalNeighbors(Direction.OUT)) {
            if (w2 instanceof VertexOptMk && !w2.toString().equals(s)) {
                for (final Vertex v3 : ((VertexOptMk)w2).getPhysicalNeighbors(Direction.IN)) {
                    unionS.add(v3.toString());
                }
            }
        }
        final MkVertex gS = (MkVertex)this.getVertex(s);
        for (final Vertex v : gS.getPhysicalNeighbors(Direction.IN)) {
            if (!unionS.contains(v.toString())) {
                dirEdgesS.add(v.toString());
                ++costS;
            }
        }
        GraphOptMk.logger.trace("Benefit of removing " + vId + ":" + benefitV);
        GraphOptMk.logger.trace("Cost of removing " + vId + ":" + costvId);
        GraphOptMk.logger.trace("Benefit of removing " + s + ": " + benefitS);
        GraphOptMk.logger.trace("Cost of removing " + s + ": " + costS);
        final float benefitToCVId = benefitV / (float)costvId;
        final float benefitToCS = benefitS / (float)costS;
        GraphOptMk.logger.trace("BCRatio for " + vId + ": " + benefitToCVId);
        GraphOptMk.logger.trace("BCRatio for " + s + ": " + benefitToCS);
        if (benefitToCVId > benefitToCS) {
            toAddDirectEdges.addAll(dirEdgesVId);
            GraphOptMk.logger.trace("Choosing " + vId);
            return vId;
        }
        toAddDirectEdges.addAll(dirEdgesS);
        GraphOptMk.logger.trace("Choosing " + s);
        return s;
    }
    
    private static <T> Set<T> intersectSets(final Set<T> a, final Set<T> b) {
        final Set<T> toRet = new HashSet<T>();
        for (final T t : a) {
            if (b.contains(t)) {
                toRet.add(t);
            }
        }
        return toRet;
    }
    
    private static Set<String> intersectVNodeSets(final Set<String> a, final Set<String> b, final String maxBenefitV) {
        final Set<String> toRet = new HashSet<String>();
        for (final String t : a) {
            if (b.contains(t) && t.startsWith("V") && !t.equals(maxBenefitV)) {
                toRet.add(t);
            }
        }
        return toRet;
    }
    
    public void deduplicate3() {
        int numOfIntersections = 0;
        int numOfDeduplications = 0;
        int numOfIterations = 0;
        final int toRem = 0;
        if (!this.isExpanded) {
            final Set<Vertex> toProcess = new HashSet<Vertex>();
            final Map<List<String>, Integer> toDedup = new HashMap<List<String>, Integer>();
            for (int j = 0; j < this.vertices.size(); ++j) {
                final Vertex v = this.vertices.get(j);
                if (!(v instanceof VertexOptMk)) {
                    ++numOfIterations;
                    final List<Vertex> vNodes = new ArrayList<Vertex>(this.outVertices.get(this.index.get(v.getId().toString())));
                    for (int i = 0; i < vNodes.size(); ++i) {
                        final Vertex w = vNodes.get(i);
                        if (w instanceof VertexOptMk) {
                            for (final Vertex p : toProcess) {
                                ++numOfIntersections;
                                final List<String> inter = intersect(this.inVertices.get(this.index.get(w.getId().toString())), this.inVertices.get(this.index.get(p.getId().toString())));
                                if (inter.size() > 1) {
                                    ++numOfDeduplications;
                                    this.resolveDuplicates3(w.getId().toString(), p.getId().toString(), inter, toRem);
                                }
                            }
                            toProcess.add(w);
                        }
                    }
                    toProcess.clear();
                }
            }
        }
        System.out.println("Number of intersections : " + numOfIntersections);
        System.out.println("Number of Dedups : " + numOfDeduplications);
        System.out.println("Num of Iterations: " + numOfIterations);
        System.out.println("Virtual Nodes to Remove: " + toRem);
        System.out.println("Num of vertices at the end: " + this.vertices.size());
    }
    
    private void resolveDuplicates3(final String id1, final String id2, final List<String> inter, int toRem) {
        System.out.println("Resolving duplicates between :" + this.inVertices.get(this.index.get(id1)) + " AND " + this.inVertices.get(this.index.get(id2)) + " inter: " + inter);
        final Set<String> virtToConnect = new HashSet<String>();
        if (!id1.startsWith(GraphOptMk.virtArtIdentifier) && !id2.startsWith(GraphOptMk.virtArtIdentifier)) {
            final String artifId = GraphOptMk.virtArtIdentifier + id1 + id2;
            this.addVertex(artifId);
            final Vertex artifNode = this.vertices.get(this.index.get(artifId));
            for (final String rn : inter) {
                this.removeEdge(rn, id1);
                this.removeEdge(rn, id2);
                this.addEdge("", this.vertices.get(this.index.get(rn)), artifNode, null);
            }
            this.addEdge("", this.vertices.get(this.index.get(id1)), artifNode, null);
            this.addEdge("", this.vertices.get(this.index.get(id2)), artifNode, null);
            if (this.inVertices.get(this.index.get(id1.toString())).size() == 0) {
                ++toRem;
            }
            if (this.inVertices.get(this.index.get(id2.toString())).size() == 0) {
                ++toRem;
            }
            for (final Vertex v : this.outVertices.get(this.index.get(id1))) {
                if (v instanceof VertexOptMk && !v.equals(artifNode)) {
                    this.addEdge("", v, artifNode, null);
                }
            }
            for (final Vertex v : this.outVertices.get(this.index.get(id2))) {
                if (v instanceof VertexOptMk && !v.equals(artifNode)) {
                    this.addEdge("", v, artifNode, null);
                }
            }
            for (final Vertex v : this.inVertices.get(this.index.get(id1))) {
                if (v instanceof VertexOptMk && !v.equals(artifNode)) {
                    this.addEdge("", v, artifNode, null);
                }
            }
            for (final Vertex v : this.inVertices.get(this.index.get(id2))) {
                if (v instanceof VertexOptMk && !v.equals(artifNode)) {
                    this.addEdge("", v, artifNode, null);
                }
            }
        }
        else if (id1.startsWith(GraphOptMk.virtArtIdentifier) && !id2.startsWith(GraphOptMk.virtArtIdentifier)) {
            for (final String rn2 : inter) {
                this.removeEdge(rn2, id2);
            }
            this.addEdge("", this.vertices.get(this.index.get(id1)), this.vertices.get(this.index.get(id2)), null);
        }
        else if (!id1.startsWith(GraphOptMk.virtArtIdentifier) && id2.startsWith(GraphOptMk.virtArtIdentifier)) {
            for (final String rn2 : inter) {
                this.removeEdge(rn2, id1);
            }
            this.addEdge("", this.vertices.get(this.index.get(id2)), this.vertices.get(this.index.get(id1)), null);
        }
        else {
            System.out.println("ART WITH ART...." + inter);
            final String artifId = "VIRTART" + id1 + id2;
            this.addVertex(artifId);
            final Vertex artifNode = this.vertices.get(this.index.get(artifId));
            for (final String rn : inter) {
                this.removeEdge(rn, id1);
                this.removeEdge(rn, id2);
                this.addEdge("", this.vertices.get(this.index.get(rn)), artifNode, null);
            }
        }
    }
    
    public void deduplicateFP(final int fpTreeMin) throws InterruptedException {
        int mins = fpTreeMin;
        final FPGrowth<Vertex> fpg = new FPGrowth<Vertex>();
        long startTime = System.currentTimeMillis();
        fpg.CollectAndSort(this, mins);
        long endTime = System.currentTimeMillis();
        System.out.println("Collect n' Sort: " + (endTime - startTime));
        startTime = System.currentTimeMillis();
        fpg.generateTree();
        endTime = System.currentTimeMillis();
        System.out.println("Generating tree: " + (endTime - startTime));
        mins = 500;
        final int size = 2;
        int i = 1;
        while (mins >= 2) {
            for (i = this.deduplicate4(fpg, mins, size); i > 0; i = this.deduplicate4(fpg, mins, size)) {}
            mins -= mins / 2;
            System.err.println("MINS :" + mins);
            System.out.println(this);
        }
        startTime = System.currentTimeMillis();
        this.deduplicate2(Ordering.RAND);
        endTime = System.currentTimeMillis();
        System.out.print("Final Dedup Took : ");
        System.out.println(endTime - startTime);
    }
    
    public int deduplicate4(final FPGrowth<Vertex> fpg, final int minsupp, final int size) {
        final Comparator<FPTree.Pattern<Vertex>> ptrnSorter = new Comparator<FPTree.Pattern<Vertex>>() {
            @Override
            public int compare(final FPTree.Pattern<Vertex> o1, final FPTree.Pattern<Vertex> o2) {
                return o2.getCount() - o1.getCount();
            }
        };
        long startTime = System.currentTimeMillis();
        final List<FPTree.Pattern<Vertex>> ptrns = this.getFreqPatterns(fpg, size, minsupp);
        Collections.sort(ptrns, ptrnSorter);
        final Set<String> set = new HashSet<String>();
        final List<FPTree.Pattern<Vertex>> toProcess = new ArrayList<FPTree.Pattern<Vertex>>();
        boolean flag = false;
        System.out.println("All the  patterns : " + ptrns);
        for (final FPTree.Pattern<Vertex> p : ptrns) {
            flag = false;
            for (final Vertex v : p.getP()) {
                if (GraphOptMk.theSetToIgnore.contains(v.toString())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                toProcess.add(p);
                for (final Vertex v : p.getP()) {
                    set.add(v.toString());
                    GraphOptMk.theSetToIgnore.add(v.toString());
                }
                flag = false;
            }
        }
        List<Vertex> vNodes = null;
        for (final FPTree.Pattern<Vertex> p2 : toProcess) {
            final Vertex newVirt = this.addVertex(GraphOptMk.virtArtIdentifier + p2.getP().toString());
            final Map<String, List<Vertex>> commonVNodes = new HashMap<String, List<Vertex>>();
            for (final Vertex v2 : p2.getP()) {
                vNodes = new ArrayList<Vertex>(this.outVertices.get(this.index.get(v2.toString())));
                commonVNodes.put(v2.toString(), vNodes);
                final List<Vertex> outVs = vNodes;
                for (int i = 0; i < outVs.size(); ++i) {
                    final Vertex z = outVs.get(i);
                    if (z instanceof VertexOptMk) {
                        for (final Vertex w : this.outVertices.get(this.index.get(z.toString()))) {
                            if (!w.toString().startsWith(GraphOptMk.virtArtIdentifier) || !w.toString().equals(newVirt.toString())) {}
                        }
                    }
                }
                this.addEdge("", v2, newVirt, "");
            }
            final List<String> theLists = new ArrayList<String>(commonVNodes.keySet());
            final List<String> intersection = intersect2(commonVNodes.get(theLists.get(0)), commonVNodes.get(theLists.get(1)));
            System.out.println("the intersection : " + intersection);
            System.out.println("Common VNodes " + commonVNodes);
            for (final String v3 : commonVNodes.keySet()) {
                for (final String w2 : intersection) {
                    this.removeEdge(v3.toString(), w2.toString());
                }
            }
            for (final String v3 : commonVNodes.keySet()) {
                final List<Vertex> vN = commonVNodes.get(v3);
                for (int j = 0; j < vN.size(); ++j) {
                    final Vertex w = vN.get(j);
                    if (w instanceof VertexOptMk) {
                        if (intersection.contains(w.toString()) && this.inVertices.get(this.index.get(w.toString())).size() > 0) {
                            this.addEdge("", w, newVirt, null);
                            for (final Vertex q : ((MkVertex)w).getPhysicalNeighbors(Direction.OUT)) {
                                System.err.println("Q is " + q);
                                if (!newVirt.toString().equals(q.toString())) {
                                    this.addEdge("", q, newVirt, null);
                                }
                            }
                        }
                    }
                    else {
                        System.err.println("ITS NOT ONLY VIRTUAL NODES  " + vN);
                        System.exit(0);
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time it took to process " + (endTime - startTime));
        startTime = System.currentTimeMillis();
        for (final String s : set) {
            FPTree.Node<Vertex> travel = fpg.getFptree().getHeaderTable().get(s);
            while (travel != null) {
                travel.getParent().getChildren().addAll(travel.getChildren());
                for (final FPTree.Node<Vertex> n : travel.getChildren()) {
                    n.setParent(travel.getParent());
                }
                travel.getChildren().clear();
                travel.getParent().removeChild(travel);
                final FPTree.Node<Vertex> prevTravel = travel;
                travel = travel.getNodeLink();
                prevTravel.setNodeLink(null);
            }
            fpg.getFptree().getHeaderTable().remove(s);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Time tree deletion took " + (endTime - startTime));
        return toProcess.size();
    }
    
    public void deleteBMPs() {
        for (final Vertex v : this.vertices) {
            if (v instanceof VertexOptMk) {
                final VertexOptMk cv = (VertexOptMk)v;
                cv.getBitmaps().clear();
                cv.getBMPIndex().clear();
            }
        }
    }
    
    public void initializeBMPs() {
        for (final Vertex v : this.vertices) {
            if (v instanceof VertexOptMk) {
                final VertexOptMk cv = (VertexOptMk)v;
                final List<? extends Vertex> l = cv.getPhysicalNeighbors(Direction.IN);
                cv.initBitMaps(l.size());
                for (int i = 0; i < l.size(); ++i) {
                    final BitSet a = new BitSet(l.size());
                    cv.getBMPIndex().put(((Vertex)l.get(i)).toString(), i);
                    cv.getBitmaps().add(a);
                }
            }
        }
    }
    
    public void finalize() {
        GraphOptMk.logger.info("Finalizing Condensed Graph Construction...");
        this.sortVertexLists();
    }
    
    public void deduplicateBMP() {
        this.initializeBMPs();
        if (!this.isExpanded) {
            final Set<String> seen = new HashSet<String>();
            for (final Vertex v : this.vertices) {
                if (!(v instanceof VertexOptMk)) {
                    final List<Vertex> vNodes = new ArrayList<Vertex>(((MkVertex)v).getPhysicalNeighbors(Direction.OUT));
                    for (int i = 0; i < vNodes.size(); ++i) {
                        final Vertex x = vNodes.get(i);
                        if (x instanceof VertexOptMk) {
                            final VertexOptMk w = (VertexOptMk)x;
                            final List<? extends Vertex> vNeighbors = w.getPhysicalNeighbors(Direction.IN);
                            for (int j = 0; j < vNeighbors.size(); ++j) {
                                final Vertex z = (Vertex)vNeighbors.get(j);
                                final String s = z.toString();
                                if (!seen.contains(s)) {
                                    w.setBitAtIndexHashM(j, v);
                                    seen.add(s);
                                }
                            }
                        }
                    }
                    seen.clear();
                }
            }
        }
        this.rep = Representation.BMP1;
    }
    
    public void deduplicateBMP2() {
        this.initializeBMPs();
        if (!this.isExpanded) {
            final Set<String> seen = new HashSet<String>();
            for (final Vertex v : this.vertices) {
                if (!(v instanceof VertexOptMk)) {
                    final List<Vertex> vNodes = new ArrayList<Vertex>(((MkVertex)v).getPhysicalNeighbors(Direction.OUT));
                    final Set<String> setcover = this.greedySetCover(v.toString());
                    for (int i = 0; i < vNodes.size(); ++i) {
                        final Vertex x = vNodes.get(i);
                        if (x instanceof VertexOptMk) {
                            final VertexOptMk w = (VertexOptMk)x;
                            if (setcover.contains(x.toString())) {
                                final List<? extends Vertex> vNeighbors = w.getPhysicalNeighbors(Direction.IN);
                                for (int j = 0; j < vNeighbors.size(); ++j) {
                                    final Vertex z = (Vertex)vNeighbors.get(j);
                                    final String s = z.toString();
                                    if (!seen.contains(s)) {
                                        w.setBitAtIndexHashM(j, v);
                                        seen.add(s);
                                    }
                                }
                            }
                            else {
                                w.removeBitMapFor(v);
                                this.getOutVertices().get(this.index.get(v.toString())).remove(w);
                            }
                        }
                    }
                    seen.clear();
                }
            }
            for (final Vertex v : this.getAllVertices()) {
                if (v instanceof VertexOptMk) {
                    final VertexOptMk w2 = (VertexOptMk)v;
                    w2.rebuildBMPIndex2();
                }
            }
        }
    }
    
    public void deduplicateBMP3() {
        this.initializeBMPs();
        if (!this.isExpanded) {
            final Set<String> seen = new HashSet<String>();
            for (final Vertex v : this.vertices) {
                if (!(v instanceof VertexOptMk)) {
                    final List<Vertex> vNodes = new ArrayList<Vertex>(((MkVertex)v).getPhysicalNeighbors(Direction.OUT));
                    final Set<String> setcover = this.greedySetCover(v.toString());
                    for (int i = 0; i < vNodes.size(); ++i) {
                        final Vertex x = vNodes.get(i);
                        if (x instanceof VertexOptMk) {
                            boolean removalEvent = false;
                            final VertexOptMk w = (VertexOptMk)x;
                            if (setcover.contains(x.toString())) {
                                final List<? extends Vertex> vNeighbors = w.getPhysicalNeighbors(Direction.IN);
                                for (int j = 0; j < vNeighbors.size(); ++j) {
                                    final Vertex z = (Vertex)vNeighbors.get(j);
                                    final String s = z.toString();
                                    if (!seen.contains(s)) {
                                        w.setBitAtIndexHashM(j, v);
                                        seen.add(s);
                                    }
                                }
                            }
                            else {
                                final Integer vIdx = w.getBMPIndex().get(v.toString());
                                if (vIdx != null) {
                                    boolean chosen = false;
                                    for (final String s : w.getBMPIndex().keySet()) {
                                        final BitSet b = w.getBitmaps().get(w.getBMPIndex().get(s));
                                        if (b.get(vIdx)) {
                                            chosen = true;
                                            break;
                                        }
                                    }
                                    if (!chosen) {
                                        w.removeBitMapFor(v);
                                        this.removeEdge(v.toString(), w.toString());
                                        removalEvent = true;
                                    }
                                }
                            }
                            if (removalEvent) {
                                w.rebuildBMPIndex3();
                            }
                        }
                    }
                    seen.clear();
                }
            }
            for (final Vertex v : this.getAllVertices()) {
                if (v instanceof VertexOptMk) {
                    final VertexOptMk w2 = (VertexOptMk)v;
                    w2.rebuildBMPIndex3();
                }
            }
        }
        this.rep = Representation.BMP2;
    }
    
    public int expand(final boolean explode) {
        GraphOptMk.logger.info("Expanding Condensed Graph...");
        this.rep = Representation.EXP;
        if (explode) {
            this.isExpanded = true;
        }
        final BitSet edges = new BitSet();
        final Set<String> processed = new HashSet<String>();
        final List<String> arlist = new ArrayList<String>();
        final List<Vertex> toAdd = new ArrayList<Vertex>();
        int numz = 0;
        int progress = 0;
        for (final Vertex v : this.vertices) {
            if (++progress % 1000 == 0) {
                GraphOptMk.logger.info("expanded: " + progress + ", vertices");
            }
            if (!(v instanceof VertexOptMk)) {
                for (final Vertex w : this.outVertices.get(this.index.get(v.getId().toString()))) {
                    if (w instanceof VertexOptMk) {
                        arlist.add(w.getId().toString());
                        for (final Vertex z : this.inVertices.get(this.index.get(w.getId().toString()))) {
                            int indx = this.index.get(z.toString());
                            if (!processed.contains(z.toString()) && !edges.get(indx)) {
                                edges.set(indx);
                                if (explode) {
                                    toAdd.add(z);
                                }
                                ++numz;
                            }
                            else {
                                for (final Vertex k : this.inVertices.get(this.index.get(z.toString()))) {
                                    if (!(k instanceof VertexOptMk)) {
                                        indx = this.index.get(k.toString());
                                        if (processed.contains(k.getId().toString()) || edges.get(indx)) {
                                            continue;
                                        }
                                        edges.set(indx);
                                        if (explode) {
                                            toAdd.add(k);
                                        }
                                        ++numz;
                                    }
                                }
                            }
                        }
                    }
                    else {
                        ++numz;
                    }
                }
                if (explode) {
                    for (final String s : arlist) {
                        this.removeEdge(v.toString(), s);
                    }
                    for (final Vertex p : toAdd) {
                        if (!v.toString().equals(p.toString()) && !this.existsPhysicalEdge(v, p)) {
                            this.addEdge(null, v, p, null);
                        }
                    }
                }
            }
            processed.add(v.getId().toString());
            edges.clear();
            toAdd.clear();
            arlist.clear();
        }
        GraphOptMk.logger.info("Cleaning up condensed vertices and edges...");
        if (explode) {
            for (final Vertex v : this.vertices) {
                if (v instanceof VertexOptMk) {
                    final List<Vertex> inVs = new ArrayList<Vertex>(this.inVertices.get(this.index.get(v.toString())));
                    for (final Vertex z2 : inVs) {
                        this.removeEdge(z2.toString(), v.toString());
                    }
                    final List<Vertex> outVs = new ArrayList<Vertex>(this.outVertices.get(this.index.get(v.toString())));
                    for (final Vertex w2 : outVs) {
                        this.removeEdge(v.toString(), w2.toString());
                    }
                }
            }
            for (int j = this.vertices.size() - 1; j >= 0; --j) {
                final Vertex v = this.vertices.get(j);
                if (v instanceof VertexOptMk) {
                    this.removeDisconnectedVertex(j);
                }
            }
            this.rebuildIndex();
        }
        this.finalize();
        return numz;
    }
    
    public int expand2(final boolean explode) {
        GraphOptMk.logger.info("Expanding Condensed Graph...");
        this.rep = Representation.EXP;
        if (explode) {
            this.isExpanded = true;
        }
        final BitSet processed = new BitSet();
        final List<String> arlist = new ArrayList<String>();
        final List<Vertex> toAdd = new ArrayList<Vertex>();
        final int numz = 0;
        for (Vertex v : this.vertices) {}
        return numz;
    }
    
    public int countEdges() {
        final BitSet edges = new BitSet();
        final Set<String> processed = new HashSet<String>();
        int numz = 0;
        for (final Vertex v : this.vertices) {
            if (!(v instanceof VertexOptMk)) {
                for (final Vertex w : this.outVertices.get(this.index.get(v.getId().toString()))) {
                    if (w instanceof VertexOptMk) {
                        for (final Vertex z : this.inVertices.get(this.index.get(w.getId().toString()))) {
                            if (!(z instanceof VertexOptMk) && !z.equals(v) && !processed.contains(z.toString()) && !edges.get(this.index.get(z.toString()))) {
                                edges.set(this.index.get(z.toString()));
                                ++numz;
                            }
                        }
                    }
                    else {
                        ++numz;
                    }
                }
            }
            processed.add(v.getId().toString());
            edges.clear();
        }
        return numz;
    }
    
    public void explode() {
        this.setExpanded(true);
        final List<String> arlist = new ArrayList<String>();
        for (final String s : this.virtualIndex.keySet()) {
            for (final Vertex v : this.inVertices.get(this.index.get(s))) {
                arlist.add(v.getId().toString());
                for (final Vertex w : this.inVertices.get(this.index.get(s))) {
                    if (!this.alreadyConnected(w.getId().toString(), v.getId().toString(), true)) {
                        this.addEdge(null, v, w, "");
                    }
                }
            }
            for (final String v2 : arlist) {
                this.removeEdge(v2, s);
            }
            arlist.clear();
        }
    }
    
    public boolean isExpanded() {
        return this.isExpanded;
    }
    
    private void setExpanded(final boolean isExpanded) {
        this.isExpanded = isExpanded;
    }
    
    public void sortVertexLists() {
        GraphOptMk.logger.info("Sorting Vertex Lists...");
        for (final ArrayList<Vertex> l : this.inVertices) {
            if (l.size() > 0) {
                Collections.sort(l);
            }
        }
        for (final ArrayList<Vertex> l : this.outVertices) {
            if (l.size() > 0) {
                Collections.sort(l);
            }
        }
    }
    
    public void removeDisconnectedVertex(final int indxToRem) {
        final String id = this.vertices.get(indxToRem).toString();
        this.vertices.remove(indxToRem);
        this.index.remove(id);
        this.inVertices.remove(indxToRem);
        this.outVertices.remove(indxToRem);
    }
    
    public void removeDisconnectedVertex(final Vertex v) {
        final int indxToRem = this.vertices.indexOf(v);
        this.vertices.remove(indxToRem);
        this.inVertices.remove(indxToRem);
        this.outVertices.remove(indxToRem);
    }
    
    public static List<String> intersect(final List<Vertex> A, final List<Vertex> B) {
        int i_a = 0;
        int i_b = 0;
        final int s_a = A.size();
        final int s_b = B.size();
        final List<String> C = new ArrayList<String>();
        while (i_a < s_a && i_b < s_b) {
            if (A.get(i_a).compareTo(B.get(i_b)) < 0) {
                ++i_a;
            }
            else if (B.get(i_b).compareTo(A.get(i_a)) < 0) {
                ++i_b;
            }
            else {
                C.add(A.get(i_a).getId().toString());
                ++i_a;
                ++i_b;
            }
        }
        return C;
    }
    
    public static List<String> intersect2(final List<Vertex> list, final List<Vertex> list2) {
        final Set<String> forA = new HashSet<String>();
        final Set<String> forB = new HashSet<String>();
        final List<String> toRet = new ArrayList<String>();
        for (final Vertex v : list) {
            forA.add(v.getId().toString());
        }
        for (final Vertex w : list2) {
            forB.add(w.getId().toString());
        }
        for (final String s : forA) {
            if (forB.contains(s)) {
                toRet.add(s);
            }
        }
        return toRet;
    }
    
    public static List<String> intersectVertexAndStringLists(final List<String> A, final List<Vertex> B) {
        final Set<String> forB = new HashSet<String>();
        final Set<String> forA = new HashSet<String>();
        final List<String> toRet = new ArrayList<String>();
        for (final String s : A) {
            forA.add(s);
        }
        for (final Vertex v : B) {
            forB.add(v.getId().toString());
        }
        for (final String s : forA) {
            if (forB.contains(s)) {
                toRet.add(s);
            }
        }
        return toRet;
    }
    
    public static List<String> intersectGenV(final List<Vertex> list, final List<Vertex> list2) {
        final Set<String> forA = new HashSet<String>();
        final Set<String> forB = new HashSet<String>();
        final List<String> toRet = new ArrayList<String>();
        for (final Vertex v : list) {
            forA.add(v.getId().toString());
        }
        for (final Vertex w : list2) {
            forB.add(w.getId().toString());
        }
        for (final String s : forA) {
            if (forB.contains(s)) {
                toRet.add(s);
            }
        }
        return toRet;
    }
    
    public int countWithDups() {
        int numz = 0;
        if (!this.isExpanded) {
            for (final String s : this.virtualIndex.keySet()) {
                final int cliqueSize = this.inVertices.get(this.index.get(s)).size();
                numz += cliqueSize * (cliqueSize - 1) / 2;
            }
        }
        return numz;
    }
    
    public Map<String, Integer> getVirtualIndex() {
        return this.virtualIndex;
    }
    
    public ArrayList<ArrayList<Vertex>> getInVertices() {
        return this.inVertices;
    }
    
    @Override
    public String toString() {
        final int numOfVertices = this.vertices.size();
        return this.rep + " [vertices:" + numOfVertices + " edges:" + this.numOfEdges + "]";
    }
    
    public List<FPTree.Pattern<Vertex>> findFreqPatterns(final int pSize, final int minSupp) {
        final FPGrowth<Vertex> fpg = new FPGrowth<Vertex>();
        long startTime = 0L;
        long endTime = 0L;
        startTime = System.currentTimeMillis();
        fpg.CollectAndSort(this, minSupp);
        endTime = System.currentTimeMillis();
        System.out.println("Collect n' Sort: " + (endTime - startTime));
        startTime = System.currentTimeMillis();
        fpg.generateTree();
        endTime = System.currentTimeMillis();
        System.out.println("Generating tree: " + (endTime - startTime));
        final FPTree<Vertex> tree = fpg.getFptree();
        startTime = System.currentTimeMillis();
        final List<FPTree.Pattern<Vertex>> ptrns = fpg.FreqPGrowth(tree, null, minSupp, pSize);
        endTime = System.currentTimeMillis();
        System.out.println("Generating FPs: " + (endTime - startTime));
        final int numOfPatterns = ptrns.size();
        System.out.println(numOfPatterns + " frequent patterns found!");
        return ptrns;
    }
    
    public List<FPTree.Pattern<Vertex>> getFreqPatterns(final FPGrowth<Vertex> fpg, final int pSize, final int minSupp) {
        long startTime = 0L;
        long endTime = 0L;
        final FPTree<Vertex> tree = fpg.getFptree();
        startTime = System.currentTimeMillis();
        final List<FPTree.Pattern<Vertex>> ptrns = fpg.FreqPGrowth(tree, null, minSupp, pSize);
        endTime = System.currentTimeMillis();
        System.out.println("Generating FPs: " + (endTime - startTime));
        final int numOfPatterns = ptrns.size();
        System.out.println(numOfPatterns + " frequent patterns found!");
        return ptrns;
    }
    
    public void serialize(final String fileName) throws IOException {
        final File file = new File(fileName + ".json");
        try (final FileOutputStream fop = new FileOutputStream(file)) {
            final long startTime = System.currentTimeMillis();
            GraphSONWriter.outputGraph((com.tinkerpop.blueprints.Graph)this, fop);
            final long endTime = System.currentTimeMillis();
            System.out.println("For GraphSON: " + (endTime - startTime));
        }
    }
    
    public void removeBadVirtualNodes() {
        final Set<String> vNodesToRemove = new HashSet<String>();
        for (final String v : this.virtualIndex.keySet()) {
            final List<Vertex> inVs = this.inVertices.get(this.index.get(v));
            if (inVs.size() <= 3) {
                vNodesToRemove.add(v);
                for (int i = 0; i < inVs.size(); ++i) {
                    final Vertex w = inVs.get(i);
                    for (final Vertex z : inVs) {
                        if (!z.toString().equals(w.toString()) && !this.isIndirectlyConnected(w, z) && !this.isIndirectlyConnected(z, w)) {
                            this.addEdge(null, w, z, null);
                        }
                    }
                }
                int i = 0;
                while (i < inVs.size()) {
                    final Vertex w = inVs.get(i);
                    this.removeEdge(w.toString(), v);
                }
            }
        }
        for (int j = this.vertices.size() - 1; j >= 0; --j) {
            final Vertex v2 = this.vertices.get(j);
            if (vNodesToRemove.contains(v2.toString())) {
                this.removeDisconnectedVertex(j);
                this.virtualIndex.remove(v2);
            }
        }
        this.rebuildIndex();
    }
    
    private boolean isIndirectlyConnected(final Vertex w, final Vertex z) {
        for (final Vertex x : ((MkVertex)w).getPhysicalNeighbors(Direction.OUT)) {
            if (x instanceof VertexOptMk && this.inVertices.get(this.index.get(x.toString())).contains(z)) {
                return true;
            }
        }
        return false;
    }
    
    public long getSizeInBytes() throws UnsupportedEncodingException {
        long totalNumBytes = 0L;
        final String encoding = "UTF-8";
        for (final Vertex v : this.vertices) {
            if (!(v instanceof VertexOptMk)) {
                totalNumBytes += v.toString().getBytes(encoding).length;
                for (final String k : v.getPropertyKeys()) {
                    final Object o = v.getProperty(k);
                    if (o instanceof String) {
                        totalNumBytes += ((String)o).getBytes().length;
                    }
                    else if (o instanceof Integer) {
                        totalNumBytes += 4L;
                    }
                    else if (o instanceof Boolean) {
                        ++totalNumBytes;
                    }
                    else if (o instanceof Double) {
                        totalNumBytes += 8L;
                    }
                    else {
                        System.out.println(k + " is of type " + k.getClass());
                    }
                }
            }
            else {
                final VertexOptMk cv = (VertexOptMk)v;
                if (cv.getBitmaps() == null) {
                    continue;
                }
                for (int i = 0; i < cv.getBitmaps().size(); ++i) {
                    totalNumBytes += cv.getBitmaps().get(i).size() / 8;
                }
                for (final String s : cv.getBMPIndex().keySet()) {
                    totalNumBytes += 4L;
                    totalNumBytes += 4L;
                }
            }
        }
        for (final ArrayList<Vertex> al : this.inVertices) {
            for (final Vertex w : al) {
                totalNumBytes += 4L;
            }
        }
        for (final ArrayList<Vertex> al : this.outVertices) {
            for (final Vertex w : al) {
                totalNumBytes += 4L;
            }
        }
        for (final String s2 : this.index.keySet()) {
            totalNumBytes += 4L;
            totalNumBytes += 4L;
        }
        totalNumBytes += 8L;
        return totalNumBytes;
    }
    
    public Set<String> greedySetCover(final String id) {
        final Map<String, Set<String>> F = new HashMap<String, Set<String>>();
        final Map<String, List<String>> D = new HashMap<String, List<String>>();
        final Map<Integer, Set<String>> L = new HashMap<Integer, Set<String>>();
        final MkVertex a = (MkVertex)this.getVertex(id);
        for (final Vertex v : a.getPhysicalNeighbors(Direction.OUT)) {
            final Set<String> s = new HashSet<String>();
            for (final Vertex w : this.inVertices.get(this.getIndex().get(v.toString()))) {
                s.add(w.toString());
            }
            F.put(v.toString(), s);
        }
        for (final String s2 : F.keySet()) {
            for (final String v2 : F.get(s2)) {
                if (!D.containsKey(v2)) {
                    final List<String> l = new ArrayList<String>();
                    l.add(s2);
                    D.put(v2, l);
                }
                else {
                    D.get(v2).add(s2);
                }
            }
        }
        for (final String s2 : F.keySet()) {
            final int sz = F.get(s2).size();
            if (!L.containsKey(sz)) {
                final Set<String> set = new HashSet<String>();
                set.add(s2);
                L.put(sz, set);
            }
            else {
                L.get(sz).add(s2);
            }
        }
        final Set<String> E = new HashSet<String>();
        if (!F.isEmpty()) {
            for (int i = Collections.max((Collection<? extends Integer>)L.keySet()); i > 0; --i) {
                if (L.containsKey(i)) {
                    final Set<String> P = L.get(i);
                    while (P.size() > 0) {
                        final String x = P.iterator().next();
                        P.remove(x);
                        E.add(x);
                        for (final String v3 : F.get(x)) {
                            for (final String y : D.get(v3)) {
                                if (!y.equals(x)) {
                                    final Set<String> S2 = F.get(y);
                                    L.get(S2.size()).remove(y);
                                    S2.remove(v3);
                                    final int s2sz = S2.size();
                                    if (!L.containsKey(s2sz)) {
                                        L.put(s2sz, new HashSet<String>());
                                    }
                                    L.get(S2.size()).add(y);
                                }
                            }
                        }
                    }
                }
            }
        }
        return E;
    }
    
    public Representation getRep() {
        return this.rep;
    }
    
    public void setRep(final Representation rep) {
        this.rep = rep;
    }
    
    public Vertex getRandomRealNode(final int seed) {
        final Random r = new Random(seed);
        int rand = r.nextInt(this.vertices.size());
        Vertex toRet = this.vertices.get(rand);
        int numNeighbors = 0;
        for (final Vertex vr : ((MkVertex)toRet).getVertices(Direction.BOTH, new String[0])) {
            ++numNeighbors;
        }
        while (numNeighbors <= 2 || toRet instanceof VertexOptMk) {
            numNeighbors = 0;
            rand = r.nextInt(this.vertices.size());
            toRet = this.vertices.get(rand);
            for (final Vertex vr : ((MkVertex)toRet).getVertices(Direction.BOTH, new String[0])) {
                ++numNeighbors;
            }
        }
        return toRet;
    }
    
    public Vertex getRandomRealNeighbor(final Vertex v, final int seed) {
        final Random r = new Random(seed);
        String toRet = null;
        final List<String> chooseFrom = new ArrayList<String>();
        for (final Vertex vr : ((MkVertex)v).getVertices(Direction.BOTH, new String[0])) {
            chooseFrom.add(vr.toString());
        }
        toRet = chooseFrom.get(r.nextInt(chooseFrom.size()));
        return this.getVertex(toRet);
    }
    
    @Override
    public void toJSON(String path, final String labelPar, final boolean color) {
        if (path == null) {
            path = "debug_viz/data/graph.json";
        }
        final JSONObject json = new JSONObject();
        final JSONArray edgesArr = new JSONArray();
        final JSONArray nodesArr = new JSONArray();
        int x = 0;
        int y = 0;
        long eId = 0L;
        json.put("edges", edgesArr);
        json.put("nodes", nodesArr);
        final BitSet checked = new BitSet();
        for (final Vertex v : this.getAllVertices()) {
            JSONObject js = new JSONObject();
            js.put("x", x--);
            js.put("y", y++);
            if (v.getProperty(labelPar) == null) {
                js.put("label", v.toString());
            }
            else {
                js.put("label", v.getProperty(labelPar));
            }
            if (color) {
                if (v instanceof VertexOptMk) {
                    js.put("color", "rgb(255,0,0)");
                }
                else {
                    js.put("color", "rgb(90,90,90)");
                }
            }
            js.put("id", v.toString());
            if (v instanceof VertexOptMk) {
                js.put("size", 100);
            }
            else {
                js.put("size", 100);
            }
            nodesArr.add(js);
            for (final Vertex w : v.getPhysicalNeighbors(Direction.OUT)) {
                if (!checked.get(this.index.get(w.toString()))) {
                    js = new JSONObject();
                    js.put("id", Long.toString(eId++));
                    js.put("source", v.toString());
                    js.put("target", w.toString());
                    edgesArr.add(js);
                }
            }
            for (final Vertex w : v.getPhysicalNeighbors(Direction.IN)) {
                if (!checked.get(this.index.get(w.toString()))) {
                    js = new JSONObject();
                    js.put("id", Long.toString(eId++));
                    js.put("source", v.toString());
                    js.put("target", w.toString());
                    edgesArr.add(js);
                }
            }
            checked.set(this.index.get(v.toString()));
        }
        Utils.flushToFile(new File(path), json.toString());
        GraphOptMk.logger.info("Graph successfully Serialized to: " + path);
    }
    
    public void toGML(String path, final String labelPar) {
        GraphOptMk.logger.info("Writing to GML...");
        if (path == null) {
            path = "debug_viz/data/graph.gml";
        }
        final File f = new File(path);
        final BitSet checked = new BitSet();
        if (f.exists()) {
            f.delete();
        }
        try (final FileWriter writer = new FileWriter(f.getAbsoluteFile(), true)) {
            writer.append("graph [");
            for (final Vertex v : this.getAllVertices()) {
                writer.append(Utils.printGMLVertex(v));
            }
            for (final Vertex v : this.getAllVertices()) {
                for (final Vertex w : v.getPhysicalNeighbors(Direction.OUT)) {
                    if (!checked.get(this.index.get(w.toString()))) {
                        writer.append(Utils.printGMLEdge(v, w));
                    }
                }
                for (final Vertex w : v.getPhysicalNeighbors(Direction.IN)) {
                    if (!checked.get(this.index.get(w.toString()))) {
                        writer.append(Utils.printGMLEdge(w, v));
                    }
                }
                checked.set(this.index.get(v.toString()));
            }
            writer.append("]");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        GraphOptMk.logger.info("Graph successfully Serialized to: " + path);
    }
    
    public static GraphOptMk testGraph9() {
        final GraphFactory factory = new GraphFactory();
        final GraphOptMk g = (GraphOptMk)factory.create("Condensed");
        final Vertex v1 = g.addVertex("VIRT1");
        final Vertex v2 = g.addVertex("VIRT2");
        final Vertex G = g.addVertex("G");
        final Vertex H = g.addVertex("H");
        final Vertex A = g.addVertex("A");
        final Vertex B = g.addVertex("B");
        final Vertex C = g.addVertex("C");
        final Vertex u = g.addVertex("u");
        final Vertex E = g.addVertex("E");
        g.addEdge(null, A, v1, "");
        g.addEdge(null, B, v1, "");
        g.addEdge(null, C, v1, "");
        g.addEdge(null, H, v1, "");
        g.addEdge(null, u, v1, "");
        g.addEdge(null, A, v2, "");
        g.addEdge(null, H, v2, "");
        g.addEdge(null, u, v2, "");
        g.addEdge(null, G, v2, "");
        g.addEdge(null, E, v2, "");
        g.finalize();
        return g;
    }
    static {
        logger = Logger.getLogger(GraphOptMk.class);
        GraphOptMk.theSetToIgnore = new HashSet<String>();
        GraphOptMk.virtIdentifier = "V";
        GraphOptMk.virtArtIdentifier = "VIRTART";
    }
    
    public enum Representation
    {
        DEDUP1, 
        DEDUP2, 
        BMP1, 
        BMP2, 
        CDUP, 
        EXP;
    }
    
    public enum Ordering
    {
        RAND, 
        INCR, 
        DECR;
    }
}
