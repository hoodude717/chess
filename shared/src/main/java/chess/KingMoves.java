package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class KingMoves implements ChessMoveCalculator {
    ChessPiece piece;
    public KingMoves(ChessPiece newPiece) {
        piece = newPiece;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessPosition> possiblePositions = new ArrayList<>(Arrays.asList(
                new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()), //One in front
                new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1), //One in front level
                new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1), //One in front right
                new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1), //One in left
                new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1), //One in right
                new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()), //Back
                new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1), //Back left
                new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1) //Back right
            )
        );
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        getValidMovesFor(possiblePositions, possibleMoves, board, myPosition);

        return possibleMoves;
    }
}
