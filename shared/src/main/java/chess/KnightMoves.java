package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class KnightMoves implements ChessMoveCalculator{
    ChessPiece piece;
    private Collection<ChessMove> attacks = new ArrayList<>();

    public KnightMoves(ChessPiece newPiece) {
        piece = newPiece;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        Collection<ChessPosition> possiblePositions = new ArrayList<>(Arrays.asList(
                new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()-1),
                new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()+1),
                new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()-1),
                new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()+1),
                new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-2),
                new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+2),
                new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-2),
                new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+2)
            )
        );

        getValidMovesFor(possiblePositions, possibleMoves,attacks, board, myPosition);


        return possibleMoves;
    }

    @Override
    public Collection<ChessMove> attackMoves(ChessBoard board, ChessPosition myPosition) {
        pieceMoves(board, myPosition);
        return attacks;
    }
}
