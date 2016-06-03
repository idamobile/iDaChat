package com.idamobile.platform.chatbot.fn;

@FunctionalInterface
public interface XBiFunction<T, U, R, X extends Throwable> {

    R apply(T t, U u) throws X;

}
