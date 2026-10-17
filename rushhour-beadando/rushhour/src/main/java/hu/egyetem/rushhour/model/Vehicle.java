package hu.egyetem.rushhour.model;

import java.util.Objects;

public final class Vehicle {
    private final String id;
    private final int length;
    private final Orientation orientation;
    private final boolean humanTarget;
    private final boolean aiTarget;
    private int row;
    private int col;

    public Vehicle(String id, int row, int col, int length, Orientation orientation, boolean humanTarget, boolean aiTarget) {
        if (id == null || id.isBlank() || id.length() != 1) throw new IllegalArgumentException("A jármű azonosítója egy karakter legyen.");
        if (row < 0 || col < 0 || length < 2) throw new IllegalArgumentException("Érvénytelen járműpozíció vagy hossz.");
        if (humanTarget && aiTarget) throw new IllegalArgumentException("Egy jármű nem lehet mindkét céljármű.");
        this.id=id; this.row=row; this.col=col; this.length=length; this.orientation=Objects.requireNonNull(orientation);
        this.humanTarget=humanTarget; this.aiTarget=aiTarget;
    }
    public String getId(){return id;} public int getRow(){return row;} public int getCol(){return col;}
    public int getLength(){return length;} public Orientation getOrientation(){return orientation;}
    public boolean isHumanTarget(){return humanTarget;} public boolean isAiTarget(){return aiTarget;}
    public void shift(int distance){ if(orientation==Orientation.HORIZONTAL) col+=distance; else row+=distance; }
    public Vehicle copy(){return new Vehicle(id,row,col,length,orientation,humanTarget,aiTarget);}
}
