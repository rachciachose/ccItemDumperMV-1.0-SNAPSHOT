// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.client.model.geojson;

import com.mongodb.annotations.Immutable;

@Immutable
public abstract class CoordinateReferenceSystem
{
    public abstract CoordinateReferenceSystemType getType();
}
