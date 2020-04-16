// 
// Decompiled by Procyon v0.5.30
// 

package com.mongodb.operation;

import org.bson.BsonValue;
import com.mongodb.connection.ServerType;
import com.mongodb.connection.ConnectionDescription;
import com.mongodb.MongoCommandException;
import com.mongodb.binding.AsyncWriteBinding;
import com.mongodb.connection.AsyncConnection;
import com.mongodb.binding.AsyncConnectionSource;
import com.mongodb.internal.async.ErrorHandlingResultCallback;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.binding.AsyncReadBinding;
import com.mongodb.assertions.Assertions;
import com.mongodb.ReadPreference;
import org.bson.FieldNameValidator;
import com.mongodb.internal.validator.NoOpFieldNameValidator;
import com.mongodb.binding.WriteBinding;
import com.mongodb.connection.Connection;
import com.mongodb.binding.ConnectionSource;
import com.mongodb.Function;
import org.bson.codecs.Decoder;
import org.bson.codecs.BsonDocumentCodec;
import org.bson.BsonDocument;
import com.mongodb.binding.ReadBinding;

final class CommandOperationHelper
{
    static BsonDocument executeWrappedCommandProtocol(final ReadBinding binding, final String database, final BsonDocument command) {
        return executeWrappedCommandProtocol(binding, database, command, (Decoder<BsonDocument>)new BsonDocumentCodec());
    }
    
    static <T> T executeWrappedCommandProtocol(final ReadBinding binding, final String database, final BsonDocument command, final Function<BsonDocument, T> transformer) {
        return executeWrappedCommandProtocol(binding, database, command, new BsonDocumentCodec(), transformer);
    }
    
    static <T> T executeWrappedCommandProtocol(final ReadBinding binding, final String database, final BsonDocument command, final Decoder<T> decoder) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_0         /* binding */
        //     1: aload_1         /* database */
        //     2: aload_2         /* command */
        //     3: aload_3         /* decoder */
        //     4: new             Lcom/mongodb/operation/OperationHelper$IdentityTransformer;
        //     7: dup            
        //     8: invokespecial   com/mongodb/operation/OperationHelper$IdentityTransformer.<init>:()V
        //    11: invokestatic    com/mongodb/operation/CommandOperationHelper.executeWrappedCommandProtocol:(Lcom/mongodb/binding/ReadBinding;Ljava/lang/String;Lorg/bson/BsonDocument;Lorg/bson/codecs/Decoder;Lcom/mongodb/Function;)Ljava/lang/Object;
        //    14: areturn        
        //    Signature:
        //  <T:Ljava/lang/Object;>(Lcom/mongodb/binding/ReadBinding;Ljava/lang/String;Lorg/bson/BsonDocument;Lorg/bson/codecs/Decoder<TT;>;)TT;
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------------
        //  0      15      0     binding   Lcom/mongodb/binding/ReadBinding;
        //  0      15      1     database  Ljava/lang/String;
        //  0      15      2     command   Lorg/bson/BsonDocument;
        //  0      15      3     decoder   Lorg/bson/codecs/Decoder;
        //    LocalVariableTypeTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ------------------------------
        //  0      15      3     decoder  Lorg/bson/codecs/Decoder<TT;>;
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MetadataHelper$AdaptFailure
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2234)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2156)
        //     at com.strobel.assembler.metadata.GenericParameter.accept(GenericParameter.java:85)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2190)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2167)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2180)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2156)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper.adapt(MetadataHelper.java:1271)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:932)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2685)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:778)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1656)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:655)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:365)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:317)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:238)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:138)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static <D, T> T executeWrappedCommandProtocol(final ReadBinding binding, final String database, final BsonDocument command, final Decoder<D> decoder, final Function<D, T> transformer) {
        final ConnectionSource source = binding.getReadConnectionSource();
        try {
            return transformer.apply(executeWrappedCommandProtocol(database, command, decoder, source, binding.getReadPreference()));
        }
        finally {
            source.release();
        }
    }
    
    static <T> T executeWrappedCommandProtocol(final ReadBinding binding, final String database, final BsonDocument command, final Connection connection, final Function<BsonDocument, T> transformer) {
        return executeWrappedCommandProtocol(binding, database, command, new BsonDocumentCodec(), connection, transformer);
    }
    
    static <T> T executeWrappedCommandProtocol(final ReadBinding binding, final String database, final BsonDocument command, final Decoder<BsonDocument> decoder, final Connection connection, final Function<BsonDocument, T> transformer) {
        return executeWrappedCommandProtocol(database, command, decoder, connection, binding.getReadPreference(), transformer);
    }
    
    static BsonDocument executeWrappedCommandProtocol(final WriteBinding binding, final String database, final BsonDocument command) {
        return executeWrappedCommandProtocol(binding, database, command, (Function<BsonDocument, BsonDocument>)new OperationHelper.IdentityTransformer<BsonDocument>());
    }
    
    static <T> T executeWrappedCommandProtocol(final WriteBinding binding, final String database, final BsonDocument command, final Decoder<T> decoder) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_0         /* binding */
        //     1: aload_1         /* database */
        //     2: aload_2         /* command */
        //     3: aload_3         /* decoder */
        //     4: new             Lcom/mongodb/operation/OperationHelper$IdentityTransformer;
        //     7: dup            
        //     8: invokespecial   com/mongodb/operation/OperationHelper$IdentityTransformer.<init>:()V
        //    11: invokestatic    com/mongodb/operation/CommandOperationHelper.executeWrappedCommandProtocol:(Lcom/mongodb/binding/WriteBinding;Ljava/lang/String;Lorg/bson/BsonDocument;Lorg/bson/codecs/Decoder;Lcom/mongodb/Function;)Ljava/lang/Object;
        //    14: areturn        
        //    Signature:
        //  <T:Ljava/lang/Object;>(Lcom/mongodb/binding/WriteBinding;Ljava/lang/String;Lorg/bson/BsonDocument;Lorg/bson/codecs/Decoder<TT;>;)TT;
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ----------------------------------
        //  0      15      0     binding   Lcom/mongodb/binding/WriteBinding;
        //  0      15      1     database  Ljava/lang/String;
        //  0      15      2     command   Lorg/bson/BsonDocument;
        //  0      15      3     decoder   Lorg/bson/codecs/Decoder;
        //    LocalVariableTypeTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ------------------------------
        //  0      15      3     decoder  Lorg/bson/codecs/Decoder<TT;>;
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MetadataHelper$AdaptFailure
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2234)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2156)
        //     at com.strobel.assembler.metadata.GenericParameter.accept(GenericParameter.java:85)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2190)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2167)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2180)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2156)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper.adapt(MetadataHelper.java:1271)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:932)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2685)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:778)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1656)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:655)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:365)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:317)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:238)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:138)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static <T> T executeWrappedCommandProtocol(final WriteBinding binding, final String database, final BsonDocument command, final Function<BsonDocument, T> transformer) {
        return executeWrappedCommandProtocol(binding, database, command, new BsonDocumentCodec(), transformer);
    }
    
    static <D, T> T executeWrappedCommandProtocol(final WriteBinding binding, final String database, final BsonDocument command, final Decoder<D> decoder, final Function<D, T> transformer) {
        return executeWrappedCommandProtocol(binding, database, command, new NoOpFieldNameValidator(), decoder, transformer);
    }
    
    static <D, T> T executeWrappedCommandProtocol(final WriteBinding binding, final String database, final BsonDocument command, final FieldNameValidator fieldNameValidator, final Decoder<D> decoder, final Function<D, T> transformer) {
        final ConnectionSource source = binding.getWriteConnectionSource();
        try {
            return transformer.apply(executeWrappedCommandProtocol(database, command, fieldNameValidator, decoder, source, ReadPreference.primary()));
        }
        finally {
            source.release();
        }
    }
    
    static BsonDocument executeWrappedCommandProtocol(final WriteBinding binding, final String database, final BsonDocument command, final Connection connection) {
        Assertions.notNull("binding", binding);
        return executeWrappedCommandProtocol(database, command, (Decoder<BsonDocument>)new BsonDocumentCodec(), connection, ReadPreference.primary());
    }
    
    private static <T> T executeWrappedCommandProtocol(final String database, final BsonDocument command, final Decoder<T> decoder, final ConnectionSource source, final ReadPreference readPreference) {
        return executeWrappedCommandProtocol(database, command, new NoOpFieldNameValidator(), decoder, source, readPreference);
    }
    
    private static <T> T executeWrappedCommandProtocol(final String database, final BsonDocument command, final FieldNameValidator fieldNameValidator, final Decoder<T> decoder, final ConnectionSource source, final ReadPreference readPreference) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload           source
        //     2: invokeinterface com/mongodb/binding/ConnectionSource.getConnection:()Lcom/mongodb/connection/Connection;
        //     7: astore          connection
        //     9: aload_0         /* database */
        //    10: aload_1         /* command */
        //    11: aload_2         /* fieldNameValidator */
        //    12: aload_3         /* decoder */
        //    13: aload           connection
        //    15: aload           readPreference
        //    17: new             Lcom/mongodb/operation/OperationHelper$IdentityTransformer;
        //    20: dup            
        //    21: invokespecial   com/mongodb/operation/OperationHelper$IdentityTransformer.<init>:()V
        //    24: invokestatic    com/mongodb/operation/CommandOperationHelper.executeWrappedCommandProtocol:(Ljava/lang/String;Lorg/bson/BsonDocument;Lorg/bson/FieldNameValidator;Lorg/bson/codecs/Decoder;Lcom/mongodb/connection/Connection;Lcom/mongodb/ReadPreference;Lcom/mongodb/Function;)Ljava/lang/Object;
        //    27: astore          7
        //    29: aload           connection
        //    31: invokeinterface com/mongodb/connection/Connection.release:()V
        //    36: aload           7
        //    38: areturn        
        //    39: astore          8
        //    41: aload           connection
        //    43: invokeinterface com/mongodb/connection/Connection.release:()V
        //    48: aload           8
        //    50: athrow         
        //    Signature:
        //  <T:Ljava/lang/Object;>(Ljava/lang/String;Lorg/bson/BsonDocument;Lorg/bson/FieldNameValidator;Lorg/bson/codecs/Decoder<TT;>;Lcom/mongodb/binding/ConnectionSource;Lcom/mongodb/ReadPreference;)TT;
        //    LocalVariableTable:
        //  Start  Length  Slot  Name                Signature
        //  -----  ------  ----  ------------------  --------------------------------------
        //  0      51      0     database            Ljava/lang/String;
        //  0      51      1     command             Lorg/bson/BsonDocument;
        //  0      51      2     fieldNameValidator  Lorg/bson/FieldNameValidator;
        //  0      51      3     decoder             Lorg/bson/codecs/Decoder;
        //  0      51      4     source              Lcom/mongodb/binding/ConnectionSource;
        //  0      51      5     readPreference      Lcom/mongodb/ReadPreference;
        //  9      42      6     connection          Lcom/mongodb/connection/Connection;
        //    LocalVariableTypeTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ------------------------------
        //  0      51      3     decoder  Lorg/bson/codecs/Decoder<TT;>;
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  9      29     39     51     Any
        //  39     41     39     51     Any
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MetadataHelper$AdaptFailure
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2234)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2156)
        //     at com.strobel.assembler.metadata.GenericParameter.accept(GenericParameter.java:85)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2190)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2167)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2180)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2156)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper.adapt(MetadataHelper.java:1271)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:932)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2685)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:770)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:881)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.invalidateDependentExpressions(TypeAnalysis.java:759)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1011)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:778)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1656)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:655)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:365)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:317)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:238)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:138)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static <T> T executeWrappedCommandProtocol(final String database, final BsonDocument command, final Decoder<T> decoder, final Connection connection, final ReadPreference readPreference) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_0         /* database */
        //     1: aload_1         /* command */
        //     2: new             Lcom/mongodb/internal/validator/NoOpFieldNameValidator;
        //     5: dup            
        //     6: invokespecial   com/mongodb/internal/validator/NoOpFieldNameValidator.<init>:()V
        //     9: aload_2         /* decoder */
        //    10: aload_3         /* connection */
        //    11: aload           readPreference
        //    13: new             Lcom/mongodb/operation/OperationHelper$IdentityTransformer;
        //    16: dup            
        //    17: invokespecial   com/mongodb/operation/OperationHelper$IdentityTransformer.<init>:()V
        //    20: invokestatic    com/mongodb/operation/CommandOperationHelper.executeWrappedCommandProtocol:(Ljava/lang/String;Lorg/bson/BsonDocument;Lorg/bson/FieldNameValidator;Lorg/bson/codecs/Decoder;Lcom/mongodb/connection/Connection;Lcom/mongodb/ReadPreference;Lcom/mongodb/Function;)Ljava/lang/Object;
        //    23: areturn        
        //    Signature:
        //  <T:Ljava/lang/Object;>(Ljava/lang/String;Lorg/bson/BsonDocument;Lorg/bson/codecs/Decoder<TT;>;Lcom/mongodb/connection/Connection;Lcom/mongodb/ReadPreference;)TT;
        //    LocalVariableTable:
        //  Start  Length  Slot  Name            Signature
        //  -----  ------  ----  --------------  -----------------------------------
        //  0      24      0     database        Ljava/lang/String;
        //  0      24      1     command         Lorg/bson/BsonDocument;
        //  0      24      2     decoder         Lorg/bson/codecs/Decoder;
        //  0      24      3     connection      Lcom/mongodb/connection/Connection;
        //  0      24      4     readPreference  Lcom/mongodb/ReadPreference;
        //    LocalVariableTypeTable:
        //  Start  Length  Slot  Name     Signature
        //  -----  ------  ----  -------  ------------------------------
        //  0      24      2     decoder  Lorg/bson/codecs/Decoder<TT;>;
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MetadataHelper$AdaptFailure
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2234)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2156)
        //     at com.strobel.assembler.metadata.GenericParameter.accept(GenericParameter.java:85)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2190)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2167)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2180)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2156)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper.adapt(MetadataHelper.java:1271)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:932)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2685)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:778)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1656)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:655)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:365)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:317)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:238)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:138)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    private static <D, T> T executeWrappedCommandProtocol(final String database, final BsonDocument command, final Decoder<D> decoder, final Connection connection, final ReadPreference readPreference, final Function<D, T> transformer) {
        return executeWrappedCommandProtocol(database, command, new NoOpFieldNameValidator(), decoder, connection, readPreference, transformer);
    }
    
    private static <D, T> T executeWrappedCommandProtocol(final String database, final BsonDocument command, final FieldNameValidator fieldNameValidator, final Decoder<D> decoder, final Connection connection, final ReadPreference readPreference, final Function<D, T> transformer) {
        return transformer.apply(connection.command(database, wrapCommand(command, readPreference, connection.getDescription()), readPreference.isSlaveOk(), fieldNameValidator, decoder));
    }
    
    static void executeWrappedCommandProtocolAsync(final AsyncReadBinding binding, final String database, final BsonDocument command, final SingleResultCallback<BsonDocument> callback) {
        executeWrappedCommandProtocolAsync(binding, database, command, new BsonDocumentCodec(), callback);
    }
    
    static <T> void executeWrappedCommandProtocolAsync(final AsyncReadBinding binding, final String database, final BsonDocument command, final Decoder<T> decoder, final SingleResultCallback<T> callback) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_0         /* binding */
        //     1: aload_1         /* database */
        //     2: aload_2         /* command */
        //     3: aload_3         /* decoder */
        //     4: new             Lcom/mongodb/operation/OperationHelper$IdentityTransformer;
        //     7: dup            
        //     8: invokespecial   com/mongodb/operation/OperationHelper$IdentityTransformer.<init>:()V
        //    11: aload           callback
        //    13: invokestatic    com/mongodb/operation/CommandOperationHelper.executeWrappedCommandProtocolAsync:(Lcom/mongodb/binding/AsyncReadBinding;Ljava/lang/String;Lorg/bson/BsonDocument;Lorg/bson/codecs/Decoder;Lcom/mongodb/Function;Lcom/mongodb/async/SingleResultCallback;)V
        //    16: return         
        //    Signature:
        //  <T:Ljava/lang/Object;>(Lcom/mongodb/binding/AsyncReadBinding;Ljava/lang/String;Lorg/bson/BsonDocument;Lorg/bson/codecs/Decoder<TT;>;Lcom/mongodb/async/SingleResultCallback<TT;>;)V
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ----------------------------------------
        //  0      17      0     binding   Lcom/mongodb/binding/AsyncReadBinding;
        //  0      17      1     database  Ljava/lang/String;
        //  0      17      2     command   Lorg/bson/BsonDocument;
        //  0      17      3     decoder   Lorg/bson/codecs/Decoder;
        //  0      17      4     callback  Lcom/mongodb/async/SingleResultCallback;
        //    LocalVariableTypeTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------------------------
        //  0      17      3     decoder   Lorg/bson/codecs/Decoder<TT;>;
        //  0      17      4     callback  Lcom/mongodb/async/SingleResultCallback<TT;>;
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MetadataHelper$AdaptFailure
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2234)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2156)
        //     at com.strobel.assembler.metadata.GenericParameter.accept(GenericParameter.java:85)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2190)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2167)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2180)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2156)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper.adapt(MetadataHelper.java:1271)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:932)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2685)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:655)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:365)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:317)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:238)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:138)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static <T> void executeWrappedCommandProtocolAsync(final AsyncReadBinding binding, final String database, final BsonDocument command, final Function<BsonDocument, T> transformer, final SingleResultCallback<T> callback) {
        executeWrappedCommandProtocolAsync(binding, database, command, new BsonDocumentCodec(), transformer, callback);
    }
    
    static <D, T> void executeWrappedCommandProtocolAsync(final AsyncReadBinding binding, final String database, final BsonDocument command, final Decoder<D> decoder, final Function<D, T> transformer, final SingleResultCallback<T> callback) {
        binding.getReadConnectionSource(new CommandProtocolExecutingCallback<Object, Object>(database, command, new NoOpFieldNameValidator(), decoder, binding.getReadPreference(), transformer, ErrorHandlingResultCallback.errorHandlingCallback(callback)));
    }
    
    static <T> void executeWrappedCommandProtocolAsync(final AsyncReadBinding binding, final String database, final BsonDocument command, final Decoder<BsonDocument> decoder, final AsyncConnection connection, final Function<BsonDocument, T> transformer, final SingleResultCallback<T> callback) {
        Assertions.notNull("binding", binding);
        executeWrappedCommandProtocolAsync(database, command, decoder, connection, binding.getReadPreference(), transformer, callback);
    }
    
    static void executeWrappedCommandProtocolAsync(final AsyncWriteBinding binding, final String database, final BsonDocument command, final SingleResultCallback<BsonDocument> callback) {
        executeWrappedCommandProtocolAsync(binding, database, command, new BsonDocumentCodec(), callback);
    }
    
    static <T> void executeWrappedCommandProtocolAsync(final AsyncWriteBinding binding, final String database, final BsonDocument command, final Decoder<T> decoder, final SingleResultCallback<T> callback) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     0: aload_0         /* binding */
        //     1: aload_1         /* database */
        //     2: aload_2         /* command */
        //     3: aload_3         /* decoder */
        //     4: new             Lcom/mongodb/operation/OperationHelper$IdentityTransformer;
        //     7: dup            
        //     8: invokespecial   com/mongodb/operation/OperationHelper$IdentityTransformer.<init>:()V
        //    11: aload           callback
        //    13: invokestatic    com/mongodb/operation/CommandOperationHelper.executeWrappedCommandProtocolAsync:(Lcom/mongodb/binding/AsyncWriteBinding;Ljava/lang/String;Lorg/bson/BsonDocument;Lorg/bson/codecs/Decoder;Lcom/mongodb/Function;Lcom/mongodb/async/SingleResultCallback;)V
        //    16: return         
        //    Signature:
        //  <T:Ljava/lang/Object;>(Lcom/mongodb/binding/AsyncWriteBinding;Ljava/lang/String;Lorg/bson/BsonDocument;Lorg/bson/codecs/Decoder<TT;>;Lcom/mongodb/async/SingleResultCallback<TT;>;)V
        //    LocalVariableTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ----------------------------------------
        //  0      17      0     binding   Lcom/mongodb/binding/AsyncWriteBinding;
        //  0      17      1     database  Ljava/lang/String;
        //  0      17      2     command   Lorg/bson/BsonDocument;
        //  0      17      3     decoder   Lorg/bson/codecs/Decoder;
        //  0      17      4     callback  Lcom/mongodb/async/SingleResultCallback;
        //    LocalVariableTypeTable:
        //  Start  Length  Slot  Name      Signature
        //  -----  ------  ----  --------  ---------------------------------------------
        //  0      17      3     decoder   Lorg/bson/codecs/Decoder<TT;>;
        //  0      17      4     callback  Lcom/mongodb/async/SingleResultCallback<TT;>;
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MetadataHelper$AdaptFailure
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2234)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitGenericParameter(MetadataHelper.java:2156)
        //     at com.strobel.assembler.metadata.GenericParameter.accept(GenericParameter.java:85)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2190)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.adaptRecursive(MetadataHelper.java:2167)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2180)
        //     at com.strobel.assembler.metadata.MetadataHelper$Adapter.visitParameterizedType(MetadataHelper.java:2156)
        //     at com.strobel.assembler.metadata.ParameterizedType.accept(ParameterizedType.java:103)
        //     at com.strobel.assembler.metadata.DefaultTypeVisitor.visit(DefaultTypeVisitor.java:25)
        //     at com.strobel.assembler.metadata.MetadataHelper.adapt(MetadataHelper.java:1271)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:932)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferCall(TypeAnalysis.java:2685)
        //     at com.strobel.decompiler.ast.TypeAnalysis.doInferTypeForExpression(TypeAnalysis.java:1029)
        //     at com.strobel.decompiler.ast.TypeAnalysis.inferTypeForExpression(TypeAnalysis.java:803)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:672)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:655)
        //     at com.strobel.decompiler.ast.TypeAnalysis.runInference(TypeAnalysis.java:365)
        //     at com.strobel.decompiler.ast.TypeAnalysis.run(TypeAnalysis.java:96)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:109)
        //     at com.strobel.decompiler.ast.AstOptimizer.optimize(AstOptimizer.java:42)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:214)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:757)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:655)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:532)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:499)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:141)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:130)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:105)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:317)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:238)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:138)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    static <T> void executeWrappedCommandProtocolAsync(final AsyncWriteBinding binding, final String database, final BsonDocument command, final Function<BsonDocument, T> transformer, final SingleResultCallback<T> callback) {
        executeWrappedCommandProtocolAsync(binding, database, command, new BsonDocumentCodec(), transformer, callback);
    }
    
    static <D, T> void executeWrappedCommandProtocolAsync(final AsyncWriteBinding binding, final String database, final BsonDocument command, final Decoder<D> decoder, final Function<D, T> transformer, final SingleResultCallback<T> callback) {
        executeWrappedCommandProtocolAsync(binding, database, command, new NoOpFieldNameValidator(), decoder, transformer, callback);
    }
    
    static <D, T> void executeWrappedCommandProtocolAsync(final AsyncWriteBinding binding, final String database, final BsonDocument command, final FieldNameValidator fieldNameValidator, final Decoder<D> decoder, final Function<D, T> transformer, final SingleResultCallback<T> callback) {
        binding.getWriteConnectionSource(new CommandProtocolExecutingCallback<Object, Object>(database, command, fieldNameValidator, decoder, ReadPreference.primary(), transformer, ErrorHandlingResultCallback.errorHandlingCallback(callback)));
    }
    
    static void executeWrappedCommandProtocolAsync(final AsyncWriteBinding binding, final String database, final BsonDocument command, final AsyncConnection connection, final SingleResultCallback<BsonDocument> callback) {
        executeWrappedCommandProtocolAsync(binding, database, command, connection, new OperationHelper.IdentityTransformer<BsonDocument>(), callback);
    }
    
    static <T> void executeWrappedCommandProtocolAsync(final AsyncWriteBinding binding, final String database, final BsonDocument command, final AsyncConnection connection, final Function<BsonDocument, T> transformer, final SingleResultCallback<T> callback) {
        Assertions.notNull("binding", binding);
        executeWrappedCommandProtocolAsync(database, command, new BsonDocumentCodec(), connection, ReadPreference.primary(), transformer, callback);
    }
    
    private static <D, T> void executeWrappedCommandProtocolAsync(final String database, final BsonDocument command, final Decoder<D> decoder, final AsyncConnection connection, final ReadPreference readPreference, final Function<D, T> transformer, final SingleResultCallback<T> callback) {
        connection.commandAsync(database, wrapCommand(command, readPreference, connection.getDescription()), readPreference.isSlaveOk(), new NoOpFieldNameValidator(), decoder, new SingleResultCallback<D>() {
            @Override
            public void onResult(final D result, final Throwable t) {
                if (t != null) {
                    callback.onResult(null, t);
                }
                else {
                    try {
                        final T transformedResult = transformer.apply(result);
                        callback.onResult(transformedResult, null);
                    }
                    catch (Exception e) {
                        callback.onResult(null, e);
                    }
                }
            }
        });
    }
    
    static void rethrowIfNotNamespaceError(final MongoCommandException e) {
        rethrowIfNotNamespaceError(e, (Object)null);
    }
    
    static <T> T rethrowIfNotNamespaceError(final MongoCommandException e, final T defaultValue) {
        if (!isNamespaceError(e)) {
            throw e;
        }
        return defaultValue;
    }
    
    static boolean isNamespaceError(final Throwable t) {
        if (t instanceof MongoCommandException) {
            final MongoCommandException e = (MongoCommandException)t;
            return e.getErrorMessage().contains("ns not found") || e.getErrorCode() == 26;
        }
        return false;
    }
    
    static BsonDocument wrapCommand(final BsonDocument command, final ReadPreference readPreference, final ConnectionDescription connectionDescription) {
        if (connectionDescription.getServerType() == ServerType.SHARD_ROUTER && !readPreference.equals(ReadPreference.primary())) {
            return new BsonDocument("$query", command).append("$readPreference", readPreference.toDocument());
        }
        return command;
    }
    
    private static class CommandProtocolExecutingCallback<D, R> implements SingleResultCallback<AsyncConnectionSource>
    {
        private final String database;
        private final BsonDocument command;
        private final Decoder<D> decoder;
        private final ReadPreference readPreference;
        private final FieldNameValidator fieldNameValidator;
        private final Function<D, R> transformer;
        private final SingleResultCallback<R> callback;
        
        public CommandProtocolExecutingCallback(final String database, final BsonDocument command, final FieldNameValidator fieldNameValidator, final Decoder<D> decoder, final ReadPreference readPreference, final Function<D, R> transformer, final SingleResultCallback<R> callback) {
            this.database = database;
            this.command = command;
            this.fieldNameValidator = fieldNameValidator;
            this.decoder = decoder;
            this.readPreference = readPreference;
            this.transformer = transformer;
            this.callback = callback;
        }
        
        @Override
        public void onResult(final AsyncConnectionSource source, final Throwable t) {
            if (t != null) {
                this.callback.onResult(null, t);
            }
            else {
                source.getConnection(new SingleResultCallback<AsyncConnection>() {
                    @Override
                    public void onResult(final AsyncConnection connection, final Throwable t) {
                        if (t != null) {
                            CommandProtocolExecutingCallback.this.callback.onResult(null, t);
                        }
                        else {
                            final SingleResultCallback<R> wrappedCallback = OperationHelper.releasingCallback(CommandProtocolExecutingCallback.this.callback, source, connection);
                            connection.commandAsync(CommandProtocolExecutingCallback.this.database, CommandOperationHelper.wrapCommand(CommandProtocolExecutingCallback.this.command, CommandProtocolExecutingCallback.this.readPreference, connection.getDescription()), CommandProtocolExecutingCallback.this.readPreference.isSlaveOk(), CommandProtocolExecutingCallback.this.fieldNameValidator, CommandProtocolExecutingCallback.this.decoder, (SingleResultCallback<Object>)new SingleResultCallback<D>() {
                                @Override
                                public void onResult(final D response, final Throwable t) {
                                    if (t != null) {
                                        wrappedCallback.onResult(null, t);
                                    }
                                    else {
                                        wrappedCallback.onResult(CommandProtocolExecutingCallback.this.transformer.apply(response), null);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}
