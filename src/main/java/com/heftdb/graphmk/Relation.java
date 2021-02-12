// 
// Decompiled by Procyon v0.5.36
// 

package com.heftdb.graphmk;

import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class Relation
{
    private String name;
    private List<String> attribAliases;
    private List<String> originalAtts;
    private String alias;
    private Map<String, String> attribTypes;
    private Map<String, String> foreignKeys;
    private List<String> primaryKeys;
    private Map<Integer, String> values;
    
    public String getAlias() {
        return this.alias;
    }
    
    public void setAlias(final String alias) {
        this.alias = alias;
    }
    
    public Relation() {
        this.name = "";
        this.originalAtts = new ArrayList<String>();
        this.attribAliases = new ArrayList<String>();
        this.attribTypes = new HashMap<String, String>();
        this.primaryKeys = new ArrayList<String>();
        this.foreignKeys = new HashMap<String, String>();
        this.values = new HashMap<Integer, String>();
    }
    
    public Relation(final String n, final String tableAlias) {
        this.name = n;
        this.alias = tableAlias;
        this.attribTypes = new LinkedHashMap<String, String>();
        this.attribAliases = new ArrayList<String>();
        this.primaryKeys = new ArrayList<String>();
        this.foreignKeys = new HashMap<String, String>();
        this.values = new HashMap<Integer, String>();
    }
    
    public List<String> getAttribAliases() {
        return this.attribAliases;
    }
    
    public void setAttribAliases(final List<String> attribs) {
        this.attribAliases = attribs;
    }
    
    public void setAttribTypes(final Map<String, String> types) {
        this.attribTypes = types;
    }
    
    public Map<String, String> getAttribTypes() {
        return this.attribTypes;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String schematoString() {
        String toReturn = this.name + " : ";
        toReturn = toReturn + "AttTypes: " + this.attribTypes.toString() + "\n";
        toReturn = toReturn + "FKs: " + this.foreignKeys + "\n";
        toReturn = toReturn + "PKs: " + this.primaryKeys + "\n";
        return toReturn;
    }
    
    @Override
    public String toString() {
        return this.name + "(" + this.alias + ")" + ":" + this.originalAtts.toString();
    }
    
    public List<String> getOriginalAtts() {
        return this.originalAtts;
    }
    
    public void setOriginalAtts(final List<String> originalAtts) {
        this.originalAtts = originalAtts;
    }
    
    public String getAdditionalPredicates(final boolean aliases) {
        String preds = "";
        for (final Integer s : this.values.keySet()) {
            if (aliases) {
                preds = preds + this.alias + ".";
            }
            preds = preds + this.originalAtts.get(s) + this.values.get(s);
            preds += " and ";
        }
        if (!preds.equals("")) {
            preds = preds.substring(0, preds.length() - 4);
        }
        return preds;
    }
    
    public static String getJoinCondition(final List<Relation> relsToJoin) {
        String joinCond = "";
        for (int j = 0; j < relsToJoin.size(); ++j) {
            final Relation left = relsToJoin.get(j);
            for (int k = j + 1; k < relsToJoin.size(); ++k) {
                final Relation right = relsToJoin.get(k);
                int attIndexRight = -1;
                int attIndexLeft = -1;
                for (final String a : left.getAttribAliases()) {
                    attIndexRight = right.getAttribAliases().indexOf(a);
                    attIndexLeft = left.getAttribAliases().indexOf(a);
                    if (attIndexRight != -1 && !a.equals("_")) {
                        joinCond = joinCond + left.getAlias() + "." + left.getOriginalAtts().get(attIndexLeft) + "=" + right.getAlias() + "." + right.getOriginalAtts().get(attIndexRight);
                        joinCond += " and ";
                    }
                }
            }
        }
        if (joinCond.endsWith(" and ")) {
            joinCond = joinCond.substring(0, joinCond.length() - 4);
        }
        return joinCond;
    }
    
    public Map<String, String> getForeignKeys() {
        return this.foreignKeys;
    }
    
    public void setForeignKeys(final Map<String, String> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }
    
    public List<String> getPrimaryKeys() {
        return this.primaryKeys;
    }
    
    public void setPrimaryKeys(final List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }
    
    public Map<Integer, String> getValues() {
        return this.values;
    }
}
