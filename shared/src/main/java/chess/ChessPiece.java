package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        Collection<ChessMove> possibleMoves = List.of();
        if (piece.getPieceType() == PieceType.BISHOP) {
            BishopMoves bishopMoves = new BishopMoves(piece);
            possibleMoves = bishopMoves.pieceMoves(board, myPosition);
        }
        else if (piece.getPieceType() == PieceType.KING) {
            KingMoves kingMoves = new KingMoves(piece);
            possibleMoves = kingMoves.pieceMoves(board, myPosition);
        }
        else if (piece.getPieceType() == PieceType.QUEEN) {
            QueenMoves queenMoves = new QueenMoves(piece);
            possibleMoves = queenMoves.pieceMoves(board, myPosition);
        }
        else if (piece.getPieceType() == PieceType.ROOK) {
            RookMoves rookMoves = new RookMoves(piece);
            possibleMoves = rookMoves.pieceMoves(board, myPosition);
        }
        else if (piece.getPieceType() == PieceType.KNIGHT) {
            KnightMoves knightMoves = new KnightMoves(piece);
            possibleMoves = knightMoves.pieceMoves(board, myPosition);
        }
        else if (piece.getPieceType() == PieceType.PAWN) {
            PawnMoves pawnMoves = new PawnMoves(piece);
            possibleMoves = pawnMoves.pieceMoves(board, myPosition);
        }
        return possibleMoves;
    }

    public Collection<ChessMove> attackMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> attacks = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);

        switch (piece.getPieceType()) {
            case KING -> {
                KingMoves kingMoves = new KingMoves(piece);
                attacks = kingMoves.attackMoves(board, myPosition);
            }
            case PAWN -> {
                PawnMoves pawnMoves = new PawnMoves(piece);
                attacks = pawnMoves.attackMoves(board, myPosition);
            }
            case ROOK -> {
                RookMoves rookMoves = new RookMoves(piece);
                attacks = rookMoves.attackMoves(board, myPosition);
            }
            case QUEEN -> {
                QueenMoves queenMoves = new QueenMoves(piece);
                attacks = queenMoves.attackMoves(board, myPosition);
            }
            case BISHOP -> {
                BishopMoves bishopMoves = new BishopMoves(piece);
                attacks = bishopMoves.attackMoves(board, myPosition);
            }
            case KNIGHT -> {
                KnightMoves knightMoves = new KnightMoves(piece);
                attacks = knightMoves.attackMoves(board, myPosition);
            }
            default -> throw new RuntimeException("NO PIECE TYPE THAT MATCHES");
        }
        return attacks;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
