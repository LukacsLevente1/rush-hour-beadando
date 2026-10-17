package hu.egyetem.rushhour.ui;

import hu.egyetem.rushhour.model.*;
import hu.egyetem.rushhour.persistence.*;
import hu.egyetem.rushhour.service.GameService;
import java.nio.file.*;
import java.util.*;

public final class Main {
    private static final Scanner IN=new Scanner(System.in);
    private static final ScoreRepository SCORES=new ScoreRepository();
    private static final GameService SERVICE=new GameService();
    private Main(){}
    public static void main(String[] args){
        System.out.println("=== Rush Hour NxM ===");
        Board board=defaultBoard();
        if(args.length>0)try{board=LevelFile.load(Path.of(args[0]));System.out.println("Pálya betöltve: "+args[0]);}catch(Exception e){System.out.println("A pálya nem tölthető be, az alapértelmezett pálya indul: "+e.getMessage());}
        System.out.print("Játékos neve: ");String name=IN.nextLine().trim();if(name.isEmpty())name="Játékos";
        Game game=new Game(name,board);printHelp();
        while(true){
            System.out.print("> ");String line=IN.nextLine().trim();if(line.isEmpty())continue;
            String[] parts=line.split("\\s+",2);String command=parts[0].toLowerCase(Locale.ROOT);String arg=parts.length>1?parts[1].trim():"";
            try{
                switch(command){
                    case "quit","exit","kilep"->{System.out.println("Viszlát!");return;}
                    case "help","segit"->printHelp();
                    case "board","tabla","allapot"->System.out.print(game.getBoard().render());
                    case "move","lep"->{
                        Move move=Move.parse(arg);
                        if(!game.play(move)){System.out.println("Érvénytelen lépés: akadályba ütközik, kilóg, vagy nem létező jármű.");continue;}
                        System.out.print(game.getBoard().render());
                        if(game.getBoard().isFinished()){finish(game);return;}
                        Optional<Move> ai=SERVICE.chooseAiMove(game.getBoard());
                        if(ai.isPresent()){Move m=ai.get();game.getBoard().move(m);System.out.println("Gép lépése: "+m.vehicleId()+(m.distance()>0?"+":"")+m.distance());System.out.print(game.getBoard().render());}
                        else System.out.println("A gép nem talál szabályos lépést.");
                        if(game.getBoard().isFinished()){finish(game);return;}
                    }
                    case "load","betolt"->{board=LevelFile.load(Path.of(arg));game=new Game(name,board);System.out.println("Pálya betöltve.");System.out.print(board.render());}
                    case "save","ment"->{LevelFile.save(game.getBoard(),Path.of(arg));System.out.println("Állás elmentve.");}
                    case "scores","ranglista"->{List<String> scores=SCORES.highScores();if(scores.isEmpty())System.out.println("Még nincs győztes eredmény.");else scores.forEach(System.out::println);}
                    case "start","kezdes"->{System.out.print(game.getBoard().render());}
                    default->System.out.println("Ismeretlen parancs. Írd be: help");
                }
            }catch(Exception e){System.out.println("Hiba: "+e.getMessage());}
        }
    }
    private static void finish(Game game){boolean won=game.getBoard().isHumanWon();System.out.println(won?"Gratulálok, nyertél!":"A gép nyert.");System.out.println("Lépések száma: "+game.getMoves());SCORES.save(game.getHumanName(),game.getMoves(),won);System.out.println("Az eredmény mentve az adatbázisba.");}
    private static void printHelp(){System.out.println("Parancsok: board | move A+2 | load palya.json | save allas.json | scores | help | quit");System.out.println("A járművet betűvel, a mozgást + vagy - előjellel és mezőszámmal add meg.");}
    private static Board defaultBoard(){
        List<Vehicle> v=List.of(
            new Vehicle("H",2,1,2,Orientation.HORIZONTAL,true,false),
            new Vehicle("B",4,1,2,Orientation.HORIZONTAL,false,true),
            new Vehicle("A",0,0,2,Orientation.VERTICAL,false,false),
            new Vehicle("C",0,3,3,Orientation.VERTICAL,false,false),
            new Vehicle("D",0,4,2,Orientation.VERTICAL,false,false),
            new Vehicle("E",3,0,2,Orientation.VERTICAL,false,false),
            new Vehicle("F",3,3,2,Orientation.HORIZONTAL,false,false),
            new Vehicle("G",5,4,2,Orientation.HORIZONTAL,false,false)
        );return new Board(6,6,v);
    }
}
