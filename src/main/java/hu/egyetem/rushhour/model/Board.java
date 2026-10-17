package hu.egyetem.rushhour.model;

import java.util.*;

public final class Board {
    private final int rows, cols;
    private final LinkedHashMap<String, Vehicle> vehicles = new LinkedHashMap<>();
    private boolean humanWon, aiWon;

    public Board(int rows, int cols, Collection<Vehicle> initialVehicles) {
        if(rows<=0 || cols<=0) throw new IllegalArgumentException("A tábla méreteinek pozitívnak kell lenniük.");
        this.rows=rows; this.cols=cols;
        for(Vehicle v:initialVehicles) { if(vehicles.putIfAbsent(v.getId(),v.copy())!=null) throw new IllegalArgumentException("Ismétlődő járműazonosító: "+v.getId()); }
        if(vehicles.values().stream().noneMatch(Vehicle::isHumanTarget) || vehicles.values().stream().noneMatch(Vehicle::isAiTarget))
            throw new IllegalArgumentException("A pályán kell lennie H és B céljárműnek.");
        validateAll();
    }
    public int getRows(){return rows;} public int getCols(){return cols;}
    public Collection<Vehicle> getVehicles(){return Collections.unmodifiableCollection(vehicles.values());}
    public boolean isFinished(){return humanWon||aiWon;} public boolean isHumanWon(){return humanWon;} public boolean isAiWon(){return aiWon;}
    public Vehicle getVehicle(String id){return vehicles.get(id);}
    private List<int[]> cells(Vehicle v) {
        List<int[]> out=new ArrayList<>();
        for(int i=0;i<v.getLength();i++) out.add(new int[]{v.getRow()+(v.getOrientation()==Orientation.VERTICAL?i:0),v.getCol()+(v.getOrientation()==Orientation.HORIZONTAL?i:0)});
        return out;
    }
    private void validateAll(){
        Set<String> occupied=new HashSet<>();
        for(Vehicle v:vehicles.values()) for(int[] p:cells(v)) {
            if(p[0]<0||p[0]>=rows||p[1]<0||p[1]>=cols) throw new IllegalArgumentException("A jármű kilóg a tábláról: "+v.getId());
            if(!occupied.add(p[0]+":"+p[1])) throw new IllegalArgumentException("A járművek átfedik egymást.");
        }
    }
    public boolean move(Move move) {
        if(isFinished()) return false;
        Vehicle v=vehicles.get(move.vehicleId()); if(v==null) return false;
        int step=Integer.signum(move.distance());
        int moved=0;
        for(int i=0;i<Math.abs(move.distance());i++) {
            v.shift(step); moved++;
            boolean exits=(v.isHumanTarget() && v.getOrientation()==Orientation.HORIZONTAL && v.getCol()+v.getLength()>cols)
                    || (v.isAiTarget() && v.getOrientation()==Orientation.HORIZONTAL && v.getCol()<0);
            if(exits) {
                if(!isPathClearForExit(v)) { v.shift(-step*moved); return false; }
                vehicles.remove(v.getId()); if(v.isHumanTarget()) humanWon=true; else aiWon=true; return true;
            }
            if(!inBounds(v) || overlaps(v)) { v.shift(-step*moved); return false; }
        }
        return true;
    }
    private boolean inBounds(Vehicle v){for(int[] p:cells(v)) if(p[0]<0||p[0]>=rows||p[1]<0||p[1]>=cols)return false; return true;}
    private boolean overlaps(Vehicle moved){for(Vehicle other:vehicles.values()) if(other!=moved) for(int[] a:cells(moved)) for(int[] b:cells(other)) if(a[0]==b[0]&&a[1]==b[1])return true; return false;}
    private boolean isPathClearForExit(Vehicle target){
        int targetRow=target.getRow();
        for(Vehicle other:vehicles.values()) if(other!=target) for(int[] p:cells(other)) if(p[0]==targetRow) {
            if(target.isHumanTarget() && p[1]>=target.getCol()) return false;
            if(target.isAiTarget() && p[1]<target.getCol()+target.getLength()) return false;
        }
        return true;
    }
    public List<Move> legalMoves(){
        List<Move> result=new ArrayList<>();
        for(Vehicle v:vehicles.values()) for(int d:new int[]{-1,1}) for(int n=1;n<=Math.max(rows,cols);n++) {
            Board copy=copy(); if(copy.move(new Move(v.getId(),d*n))) result.add(new Move(v.getId(),d*n));
            else break;
            if(copy.isFinished()) break;
        }
        return result;
    }
    public Board copy(){Board b=new Board(rows,cols,vehicles.values()); b.humanWon=humanWon;b.aiWon=aiWon;return b;}
    public String render(){
        char[][] grid=new char[rows][cols]; for(char[] line:grid) Arrays.fill(line,'.');
        for(Vehicle v:vehicles.values()) for(int[] p:cells(v)) grid[p[0]][p[1]]=v.getId().charAt(0);
        StringBuilder s=new StringBuilder("   "); for(int c=0;c<cols;c++)s.append((char)('a'+c)).append(' '); s.append('\n');
        for(int r=0;r<rows;r++){s.append(String.format("%2d ",r+1));for(int c=0;c<cols;c++)s.append(grid[r][c]).append(' ');s.append('\n');} return s.toString();
    }
}
