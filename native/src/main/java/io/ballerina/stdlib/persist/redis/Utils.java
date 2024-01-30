package io.ballerina.stdlib.persist.redis;

import io.ballerina.runtime.api.Environment;
import io.ballerina.runtime.api.PredefinedTypes;
import io.ballerina.runtime.api.creators.TypeCreator;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.types.Field;
import io.ballerina.runtime.api.types.MapType;
import io.ballerina.runtime.api.types.RecordType;
import io.ballerina.runtime.api.types.Type;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.utils.TypeUtils;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BError;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BObject;
import io.ballerina.runtime.api.values.BStream;
import io.ballerina.runtime.api.values.BString;
import io.ballerina.runtime.api.values.BTypedesc;

import java.util.Locale;
import java.util.Map;

import static io.ballerina.runtime.api.utils.StringUtils.fromString;
import static io.ballerina.stdlib.persist.redis.Constants.PERSIST_REDIS_STREAM;
import static io.ballerina.stdlib.persist.redis.ModuleUtils.getModule;


public class Utils {

    public static BString getEntityFromStreamMethod(Environment env) {
        String functionName = env.getFunctionName();
        String entity = functionName.substring(5, functionName.length() - 6).toLowerCase(Locale.ENGLISH);
        return fromString(entity);
    }
    
    public static BMap<BString, Object> getFieldTypes(RecordType recordType) {
        MapType stringMapType = TypeCreator.createMapType(PredefinedTypes.TYPE_STRING);
        BMap<BString, Object> typeMap = ValueCreator.createMapValue(stringMapType);
        Map<String, Field> fieldsMap = recordType.getFields();
        for (Field field : fieldsMap.values()) {

            Type type = field.getFieldType();
            String fieldName = field.getFieldName();
            typeMap.put(StringUtils.fromString(fieldName), StringUtils.fromString(type.getName()));
        }
        return typeMap;
    }

    private static BObject createPersistRedisStream(BStream redisStream, BTypedesc targetType, BMap<BString, Object> typeMap, BArray fields,
                                                  BArray includes, BArray typeDescriptions, BObject persistClient,
                                                  BError persistError) {
        return ValueCreator.createObjectValue(getModule(), PERSIST_REDIS_STREAM,
        redisStream, targetType, typeMap, fields, includes, typeDescriptions, persistClient, persistError);
    }

    private static BStream createPersistRedisStreamValue(BTypedesc targetType, BObject persistRedisStream) {
        RecordType streamConstraint =
                (RecordType) TypeUtils.getReferredType(targetType.getDescribingType());
        return ValueCreator.createStreamValue(
                TypeCreator.createStreamType(streamConstraint, PredefinedTypes.TYPE_NULL), persistRedisStream);
    }

    public static BStream createPersistRedisStreamValue(BStream redisStream, BTypedesc targetType, BArray fields,
                                                      BArray includes, BArray typeDescriptions, BObject persistClient,
                                                      BError persistError) {
        BObject persistRedisStream = createPersistRedisStream(redisStream, targetType, Utils.getFieldTypes((RecordType) targetType.getDescribingType()), fields, includes,
                typeDescriptions, persistClient, persistError);
        return createPersistRedisStreamValue(targetType, persistRedisStream);
    }
}
