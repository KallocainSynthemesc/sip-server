/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avinvivo.sip.server.processor.state;

import com.avinvivo.sip.server.exception.SubscriptionHeaderException;
import java.text.ParseException;
import java.util.function.BiConsumer;
import javax.sip.SipException;

/**
 *
 * @author Kilian
 */
@FunctionalInterface
public interface StateBiConsumer<T, U> extends BiConsumer<T, U> {

    @Override
    default void accept(T t, U u) {
        try {
            acceptThrows(t, u);
        } catch (final SipException | SubscriptionHeaderException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    void acceptThrows(T t, U u) throws SipException, SubscriptionHeaderException, ParseException;
}
