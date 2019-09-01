package cn.funnymc.occupations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Occupations {
    public static List<String> getList(){
        return new ArrayList<>(Arrays.asList(
            "UnemployedMan",
            "PetroleumMan",
            "WildKing",
            "King"
        ));
    }
    public static List<String> getNames(){
        return new ArrayList<>(Arrays.asList(
            "失业人",
            "石油人",
            "蛮王",
            "国王"
        ));
    }
}
