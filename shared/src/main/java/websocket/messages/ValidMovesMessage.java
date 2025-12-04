package websocket.messages;

import chess.ChessGame;
import chess.ChessMove;

import java.util.Collection;
import java.util.Objects;

public class ValidMovesMessage extends ServerMessage {
    Collection<ChessMove> validMoves;
    ChessGame game;
    public ValidMovesMessage(Collection<ChessMove> moves, ChessGame game) {
        super(ServerMessageType.VALID_MOVES);
        this.validMoves = moves;
        this.game = game;
    }

    public Collection<ChessMove> getValidMoves() {
        return validMoves;
    }
    public ChessGame getGame() {
        return game;
    }


    @Override
    public String toString() {
        return "ValidMovesMessage{" +
                "moves=" + validMoves +
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
        ValidMovesMessage that = (ValidMovesMessage) o;
        return Objects.equals(validMoves, that.validMoves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), validMoves);
    }
}
