package com.idamobile.platform.telegram.bot.api.dto;

import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ResponseParametrizedType implements ParameterizedType {

    private Type argType;

    public ResponseParametrizedType(Class<?> arg) {
        argType = TypeUtils.wrap(arg).getType();
    }

    public ResponseParametrizedType(Type argType) {
        this.argType = argType;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[] {argType};
    }

    @Override
    public Type getRawType() {
        return TypeUtils.wrap(Response.class).getType();
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
