package com.waynezhang.test.service;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by don on 1/14/15.
 */
class TestTypeAdapter implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> tokenType) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, tokenType);

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public T read(JsonReader in) throws IOException {
                // 部分接口后台返回的JSON对象带双引号，因此如果后台返回的是字符串类型，客户端需要对象类型，这里做特殊处理转换
                if (in.peek() == JsonToken.STRING && tokenType.getRawType() != String.class) {
                    String json = in.nextString();
                    if (json.startsWith("{") && json.endsWith("}") || json.startsWith("[") && json.endsWith("]")) {
                        Type retType = tokenType.getType();
                        if (retType instanceof ParameterizedType) {
                            ParameterizedType callbackType = (ParameterizedType) retType;
                            retType = $Gson$Types.newParameterizedTypeWithOwner(
                                    callbackType.getOwnerType(), callbackType.getRawType(),
                                    callbackType.getActualTypeArguments());
                        }
                        return (T) gson.fromJson(json, retType);
                    } else {
                        return null;
                    }
                }
                T t = delegate.read(in);
                if (List.class.isAssignableFrom(tokenType.getRawType()) && t == null) {
                    return (T) new ArrayList();
                }
                return t;
            }
        };
    }
}