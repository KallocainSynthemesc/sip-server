package com.avinvivo.sip.server.processor.enums;

import com.avinvivo.sip.server.processor.ResponseMessageProcessor;
import java.util.function.BiConsumer;

import javax.sip.ResponseEvent;
import javax.sip.message.Response;

public enum SipResponseType {
	OK(Response.OK, Response.ACCEPTED,ResponseMessageProcessor::processOk),
	ERROR(Response.BAD_REQUEST,Response.SESSION_NOT_ACCEPTABLE,ResponseMessageProcessor::processError);

	public final BiConsumer<ResponseMessageProcessor, ResponseEvent> compute;
	private final Range range;

	private Range toRange() {
		return this.range;
	}

	SipResponseType(int low, int high, BiConsumer<ResponseMessageProcessor, ResponseEvent> compute) {
		this.compute = compute;
		this.range = new Range(low, high);
	}

	public static SipResponseType fromCode(int id) {
		for (SipResponseType type : values()) {
			if (type.toRange().contains(id)) {
				return type;
			}
		}
		return null;
	}
	
	private class Range
	{
	    private final int low;
	    private final int high;

	    public Range(final int low, final int high){
	        this.low = low;
	        this.high = high;
	    }

	    public boolean contains(final int number){
	        return (number >= this.low && number <= this.high);
	    }
	}
}
