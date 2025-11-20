package websocket.messages;

import java.util.Objects;

public class NotificationMessage extends ServerMessage {
    String msg;

    public NotificationMessage(String message) {
        super(ServerMessageType.ERROR);
        msg = message;
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "msg='" + msg + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        NotificationMessage that = (NotificationMessage) o;
        return Objects.equals(msg, that.msg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), msg);
    }
}
