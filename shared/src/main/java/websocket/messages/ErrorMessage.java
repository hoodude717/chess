package websocket.messages;

import java.util.Objects;

public class ErrorMessage extends ServerMessage {
    String msg;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        msg = errorMessage;
    }

    @Override
    public String toString() {
        return "ErrorMessage{" +
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
        ErrorMessage that = (ErrorMessage) o;
        return Objects.equals(msg, that.msg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), msg);
    }
}
