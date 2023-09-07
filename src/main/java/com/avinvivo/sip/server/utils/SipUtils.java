package com.avinvivo.sip.server.utils;

import com.avinvivo.bimi.notification.Notification;
import com.avinvivo.sip.server.exception.InvalidSipMessageException;
import com.avinvivo.sip.server.exception.InvalidSipMessageException.ErrorCode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import static java.net.InetAddress.getByName;
import static java.net.InetAddress.getLocalHost;
import static java.net.NetworkInterface.getByInetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

public final class SipUtils {

    private static final Logger LOG = getLogger(SipUtils.class);
    public static final String PUBLISH_USER = "glassfishqueue";

    @SuppressWarnings("unchecked")
    public static Notification deserializeToNotification(byte[] arr) throws InvalidSipMessageException {
        ByteArrayInputStream bis = new ByteArrayInputStream(arr);
        ObjectInput in = null;
        Notification notification;
        try {
            in = new ObjectInputStream(bis);
            notification = (Notification) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            LOG.warn("Exception occured during deserialization." + ex.getMessage());
            throw new InvalidSipMessageException(ErrorCode.DESERIALIZATION_FAILURE);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                LOG.warn("Ignore exception: " + ex);
            }
        }
        return notification;
    }

    public static byte[] serializeNotification(Notification notification) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(notification);
        oos.flush();
        byte[] data = bos.toByteArray();
        return data;
    }

    public static boolean isLocalAddress(final String host) {

        InetAddress addr;
        try {
            if (getLocalId().equals(host)) {
                return true;
            }
            addr = getByName(host);
        } catch (UnknownHostException e1) {
            return false;
        }
        if (addr.isAnyLocalAddress() || addr.isLoopbackAddress()) {
            return true;
        }

        try {
            return getByInetAddress(addr) != null;
        } catch (SocketException e) {
            return false;
        }
    }

    //Not sure if it can return null. Documentation claims it does not.
    public static String getLocalId() throws UnknownHostException {
        return getLocalHost().getHostAddress();
    }
}
