package hu.egyetem.rushhour.model;

public final class Game {
    private final String humanName; private final Board board; private int moves;
    public Game(String humanName, Board board){if(humanName==null||humanName.isBlank())throw new IllegalArgumentException("A játékos neve nem lehet üres.");this.humanName=humanName.trim();this.board=board;}
    public String getHumanName(){return humanName;} public Board getBoard(){return board;} public int getMoves(){return moves;}
    public boolean play(Move move){boolean ok=board.move(move);if(ok)moves++;return ok;}
}
