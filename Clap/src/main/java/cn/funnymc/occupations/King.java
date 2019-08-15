package cn.funnymc.occupations;

public class King extends UnemployedMan{

    public King(String name) {
        super(name);
        health=15;
    }

    @Override
    public String getOccupationName() {return "国王";}
}
