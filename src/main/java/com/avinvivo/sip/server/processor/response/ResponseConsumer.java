package com.avinvivo.sip.server.processor.response;
import java.util.function.Consumer;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;

@FunctionalInterface
public interface ResponseConsumer<T> extends Consumer<T>{
	//Default method in case somebody wants to use my function as if it was a normal Consumer.
	//However use acceptThrows rather than accept.
    @Override
    default void accept(final T elem) {
        try {
            acceptThrows(elem);
        } catch (final SipException | InvalidArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    void acceptThrows(T elem) throws SipException, InvalidArgumentException;
}
