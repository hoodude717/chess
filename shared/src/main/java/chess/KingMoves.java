package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMoves implements ChessMoveCalculator {
    ChessPiece piece;
    public KingMoves(ChessPiece newPiece) {
        piece = newPiece;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPosition[] possiblePositions = {
                new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()), //One in front
                new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1), //One in front level
                new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1), //One in front right
                new ChessPosition(myPosition.getRow(), myPosition.getColumn()-1), //One in left
                new ChessPosition(myPosition.getRow(), myPosition.getColumn()+1), //One in right
                new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()), //Back
                new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1), //Back left
                new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1) //Back right
        };
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        for (ChessPosition pos : possiblePositions) {
            if (pos.getRow() > 8 || pos.getRow() < 1) { continue; }
            if (pos.getColumn() > 8 || pos.getColumn() < 1) { continue; }
            ChessPiece tempPiece = board.getPiece(pos);
            if (!board.isPiecePresent(pos) || (tempPiece.getTeamColor() != piece.getTeamColor())) {
                possibleMoves.add(new ChessMove(myPosition, pos, piece.getPieceType()));
            }
        }

        return possibleMoves;
    }
}
