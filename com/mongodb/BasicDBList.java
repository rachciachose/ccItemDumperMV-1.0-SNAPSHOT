// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb;

import com.mongodb.util.JSON;
import org.bson.types.BasicBSONList;

public class BasicDBList extends BasicBSONList implements DBObject
{
    private static final long serialVersionUID = -4415279469780082174L;
    private boolean _isPartialObject;
    
    @Override
    public String toString() {
        return JSON.serialize(this);
    }
    
    @Override
    public boolean isPartialObject() {
        return this._isPartialObject;
    }
    
    @Override
    public void markAsPartialObject() {
        this._isPartialObject = true;
    }
    
    public Object copy() {
        final BasicDBList newobj = new BasicDBList();
        for (int i = 0; i < this.size(); ++i) {
            Object val = this.get(i);
            if (val instanceof BasicDBObject) {
                val = ((BasicDBObject)val).copy();
            }
            else if (val instanceof BasicDBList) {
                val = ((BasicDBList)val).copy();
            }
            newobj.add(val);
        }
        return newobj;
    }
}
