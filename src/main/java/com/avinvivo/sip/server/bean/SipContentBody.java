package com.avinvivo.sip.server.bean;

import fr.c2lr.j2eeBase.annotations.FrameworkStrategy;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "SIP_MESSAGES")
public class SipContentBody {

    @Id //id would be better to be username but at the same time username is a string.
    @GeneratedValue(strategy = IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Long id = 0L;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "username", nullable = false, length = 256)
    private String username;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1028)
    @Column(nullable = false, length = 1028)
    private String message;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(nullable = false, length = 1028)
    private String eventName;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(nullable = false, length = 1028)
    private String eventId;

	@Basic(optional = false)
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @FrameworkStrategy(mergeable = false, populatable = false)
    private Date dateCreation;

    SipContentBody() {
        // default constructor for hibernate
    }

    SipContentBody(final String uri, final String message, final String eventName, final String eventId) {
        this.username = uri;
        this.message = message;
        this.eventId = eventId;
        this.eventName = eventName;
		this.dateCreation = new Date();
    }

    public static SipContentBody newInstance(final String username, final String message, final String eventName,
            final String eventId) {
        return new SipContentBody(username, message, eventName, eventId);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventId() {
        return eventId;
    }

	public Date getDateCreation() {
		return dateCreation;
	}

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if ( ! (o instanceof SipContentBody)) {
            return false;
        }
        SipContentBody sip = (SipContentBody) o;
        return sip.getId().equals(this.id);
    }
}
