package websocket.commands;

import chess.ChessPosition;

import java.util.Objects;

public class ShowMovesCommand extends UserGameCommand{

    ChessPosition position;

    public ShowMovesCommand(UserGameCommand.CommandType commandType, String authToken, Integer gameID, ChessPosition position) {
        super(commandType, authToken, gameID);
        this.position = position;
    }

    public ChessPosition getPosition() { return position; }

    @Override
    public String toString() {
        return "ShowMovesCommand{" +
                "position=" + position +
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
        ShowMovesCommand that = (ShowMovesCommand) o;
        return Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), position);
    }
}
