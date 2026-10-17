package hu.egyetem.rushhour.service;

import hu.egyetem.rushhour.model.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private Board board(){return new Board(6,6,List.of(
        new Vehicle("H",2,1,2,Orientation.HORIZONTAL,true,false),
        new Vehicle("B",4,1,2,Orientation.HORIZONTAL,false,true),
        new Vehicle("A",0,0,2,Orientation.VERTICAL,false,false)));}
    @Test void moveIntoEmptyCellIsAccepted(){Board b=board();assertTrue(b.move(new Move("A",1)));assertEquals(1,b.getVehicle("A").getRow());}
    @Test void unknownVehicleIsRejected(){assertFalse(board().move(new Move("Z",1)));}
    @Test void overlappingMoveIsRejected(){Board b=board();assertFalse(b.move(new Move("A",7)));assertEquals(0,b.getVehicle("A").getRow());}
    @Test void targetCanExitAndWin(){Board b=board();assertTrue(b.move(new Move("H",4)));assertTrue(b.isHumanWon());assertTrue(b.isFinished());}
    @Test void aiReturnsAMoveWhenAvailable(){assertTrue(new GameService(new Random(1)).chooseAiMove(board()).isPresent());}
    @Test void invalidDimensionsAreRejected(){assertThrows(IllegalArgumentException.class,()->new Board(0,6,List.of()));}
}
