package hu.egyetem.rushhour.service;

import hu.egyetem.rushhour.model.*;
import java.util.*;

public final class GameService {
    private final Random random;
    public GameService(){this(new Random());} public GameService(Random random){this.random=Objects.requireNonNull(random);}
    public Optional<Move> chooseAiMove(Board board){
        List<Move> options=board.legalMoves().stream().filter(m->{Vehicle v=board.getVehicle(m.vehicleId());return v!=null&&!v.isHumanTarget();}).toList();
        if(options.isEmpty())return Optional.empty();return Optional.of(options.get(random.nextInt(options.size())));
    }
}
