package chess;

import java.util.Collection;

public interface ChessMoveCalculator {


    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
    Collection<ChessMove> attackMoves(ChessBoard board, ChessPosition myPosition);

    default boolean getValidMovesWhile(Collection<ChessMove> possibleMoves,
                                       Collection<ChessMove> attacks,
                                       ChessBoard board, ChessPosition myPosition, int curRow, int curCol) {
        boolean validSpace = true;
        ChessPiece piece = board.getPiece(myPosition);

        if ((curRow < 1 || curRow > 8) || (curCol < 1 || curCol > 8)) {
            return false;
        }
        ChessPosition curSpace = new ChessPosition(curRow, curCol);
        if (board.isSpaceEmpty(curSpace)) {
            possibleMoves.add(new ChessMove(myPosition, curSpace, null));
        } else if (board.getPiece(curSpace).getTeamColor() != piece.getTeamColor()){
            possibleMoves.add(new ChessMove(myPosition, curSpace, null));
            attacks.add(new ChessMove(myPosition, curSpace, null));
            validSpace = false;
        } else { validSpace = false; }

        return validSpace;
    }

    default void getValidMovesFor(Collection<ChessPosition> possiblePositions,
                              Collection<ChessMove> possibleMoves,
                              Collection<ChessMove> attacks,
                              ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        for (ChessPosition pos : possiblePositions) {
            if (pos.getRow() > 8 || pos.getRow() < 1) {
                continue;
            }
            if (pos.getColumn() > 8 || pos.getColumn() < 1) {
                continue;
            }
            ChessPiece tempPiece = board.getPiece(pos);
            if (board.isSpaceEmpty(pos)) {
                possibleMoves.add(new ChessMove(myPosition, pos, null));
            }
            else if(tempPiece.getTeamColor() != piece.getTeamColor()) {
                possibleMoves.add(new ChessMove(myPosition, pos, null));
                attacks.add(new ChessMove(myPosition, pos, null));

            }
        }
    }

}
