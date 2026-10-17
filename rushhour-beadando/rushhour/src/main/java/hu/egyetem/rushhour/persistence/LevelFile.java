package hu.egyetem.rushhour.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.egyetem.rushhour.model.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public final class LevelFile {
    private static final ObjectMapper MAPPER=new ObjectMapper();
    private LevelFile(){}
    public static Board load(Path path) throws IOException {
        Map<?,?> root=MAPPER.readValue(path.toFile(),Map.class);
        int rows=((Number)root.get("rows")).intValue(), cols=((Number)root.get("cols")).intValue();
        List<Vehicle> vehicles=new ArrayList<>();
        for(Object item:(List<?>)root.get("vehicles")){
            Map<?,?> v=(Map<?,?>)item;
            vehicles.add(new Vehicle((String)v.get("id"),((Number)v.get("row")).intValue(),((Number)v.get("col")).intValue(),((Number)v.get("length")).intValue(),Orientation.valueOf(((String)v.get("orientation")).toUpperCase(Locale.ROOT)),Boolean.TRUE.equals(v.get("humanTarget")),Boolean.TRUE.equals(v.get("aiTarget"))));
        }
        return new Board(rows,cols,vehicles);
    }
    public static void save(Board board,Path path) throws IOException {
        Map<String,Object> root=new LinkedHashMap<>();root.put("rows",board.getRows());root.put("cols",board.getCols());
        List<Map<String,Object>> list=new ArrayList<>();
        for(Vehicle v:board.getVehicles()){Map<String,Object> m=new LinkedHashMap<>();m.put("id",v.getId());m.put("row",v.getRow());m.put("col",v.getCol());m.put("length",v.getLength());m.put("orientation",v.getOrientation().name().toLowerCase(Locale.ROOT));m.put("humanTarget",v.isHumanTarget());m.put("aiTarget",v.isAiTarget());list.add(m);}
        root.put("vehicles",list);Files.writeString(path,MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(root));
    }
}
