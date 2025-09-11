package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves implements ChessMoveCalculator{
    ChessPiece piece;
    public KnightMoves(ChessPiece newPiece) {
        piece = newPiece;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        ChessPosition[] possiblePositions = {
                new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()-1),
                new ChessPosition(myPosition.getRow()+2, myPosition.getColumn()+1),
                new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()-1),
                new ChessPosition(myPosition.getRow()-2, myPosition.getColumn()+1),
                new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-2),
                new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+2),
                new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-2),
                new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+2)
        };

        for (ChessPosition pos : possiblePositions) {
            if (pos.getRow() > 8 || pos.getRow() < 1) { continue; }
            if (pos.getColumn() > 8 || pos.getColumn() < 1) { continue; }
            ChessPiece tempPiece = board.getPiece(pos);
            if (board.isSpaceEmpty(pos) || (tempPiece.getTeamColor() != piece.getTeamColor())) {
                possibleMoves.add(new ChessMove(myPosition, pos, piece.getPieceType()));
            }
        }

        return possibleMoves;
    }
}
