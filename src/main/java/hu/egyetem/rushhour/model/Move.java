package hu.egyetem.rushhour.model;
public record Move(String vehicleId, int distance) {
    public Move { if (vehicleId == null || vehicleId.isBlank() || distance == 0) throw new IllegalArgumentException("A lépéshez járműazonosító és nem nulla távolság kell."); }
    public static Move parse(String input) {
        String s=input.trim().replace(" ", "");
        if(s.length()<2) throw new IllegalArgumentException("Formátum: A+2 vagy B-1.");
        int signIndex=(s.charAt(1)=='+'||s.charAt(1)=='-')?1:-1;
        if(signIndex<0) throw new IllegalArgumentException("Formátum: A+2 vagy B-1.");
        int n=Integer.parseInt(s.substring(2));
        return new Move(s.substring(0,1), signIndex==1 && s.charAt(1)=='-' ? -n : n);
    }
}
