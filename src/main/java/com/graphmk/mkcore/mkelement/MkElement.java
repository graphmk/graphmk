// 
// Decompiled by Procyon v0.5.36
// 

package com.graphmk.mkcore.mkelement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import com.tinkerpop.blueprints.Element;

public class MkElement implements Element
{
    protected Map<String, Object> properties;
    protected Object id;
    
    public MkElement() {
    }
    
    public MkElement(final Object id) {
        this.id = id;
    }
    
    @Override
    public <T> T getProperty(final String key) {
        if (this.properties != null) {
            return (T)this.properties.get(key);
        }
        return null;
    }
    
    @Override
    public Set<String> getPropertyKeys() {
        if (this.properties != null) {
            return this.properties.keySet();
        }
        return new HashSet<String>();
    }
    
    @Override
    public void setProperty(final String key, final Object value) {
        if (this.properties == null) {
            this.properties = new HashMap<String, Object>();
        }
        this.properties.put(key, value);
    }
    
    @Override
    public <T> T removeProperty(final String key) {
        if (this.properties != null) {
            final T prop = (T)this.properties.get(key);
            this.properties.remove(key);
            return prop;
        }
        return null;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Removing is too expensive!");
    }
    
    @Override
    public Object getId() {
        return this.id;
    }
}
