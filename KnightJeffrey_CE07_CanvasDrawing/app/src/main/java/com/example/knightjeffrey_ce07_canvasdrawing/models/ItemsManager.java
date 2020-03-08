package com.example.knightjeffrey_ce07_canvasdrawing.models;


import android.graphics.Point;

import java.util.ArrayList;
import java.util.Random;

public class ItemsManager {

    private static final ItemsManager myItemManager = new ItemsManager();
    private ArrayList<Item> allItems;
    private ArrayList<Point> allItemPoints;
    private ArrayList<Item> myItems;
    private ArrayList<Point> myItemPoints;
    private ArrayList<Point> clickPoints;



    public static ItemsManager getInstance(){
        return myItemManager;
    }

    public  void populateAllItems(ArrayList<Item> _allItems){
        allItems = _allItems;
        myItems = new ArrayList<>();
        myItemPoints = new ArrayList<>();

    }

    public ArrayList<Item> getMyItems() {
        return myItems;
    }

    public ArrayList<Point> getMyItemPoints() {
        return myItemPoints;
    }

    public void setClickPoints(ArrayList<Point> clickPoints) {
        this.clickPoints = clickPoints;
    }

    public ArrayList<Point> getClickPoints() {
        return clickPoints;
    }


    public void generateItemLocations(int xMax, int yMax){
        allItemPoints = new ArrayList<>();

        int randomX;
        int randomY;
        for (int i = 0; i < allItems.size(); i++) {
            randomX = new Random().nextInt(xMax + 1);
            randomY = new Random().nextInt(yMax + 1);
            allItemPoints.add(new Point(randomX,randomY));

        }

    }

    public boolean checkMatch(Point point){

        boolean matched = false;
        for (Point p: allItemPoints) {

            int xRadius = (point.x + 20) - p.x;
            int yRadius = (point.y + 20) - p.y;

            int xRadius2 = (point.x + 10) - p.x;
            int yRadius2 = (point.y + 10) - p.y;

            if(xRadius < 0){xRadius = xRadius * -1;}
            if(yRadius < 0){yRadius = yRadius * -1;}
            if(xRadius2 < 0){xRadius2 = xRadius2 * -1;}
            if(yRadius2 < 0){yRadius2 = yRadius2 * -1;}

            if(p.x == point.x && p.y == point.y){
                if(!myItemPoints.contains(p)) {
                    int index = allItemPoints.indexOf(p);
                    myItems.add(allItems.get(index));
                    myItemPoints.add(p);
                    matched = true;
                }
            }
            else if(xRadius < 30 && yRadius < 30){
                if(!myItemPoints.contains(p)) {
                    int index = allItemPoints.indexOf(p);
                    myItems.add(allItems.get(index));
                    myItemPoints.add(p);
                    matched = true;
                }
            }
            else if(xRadius2 < 30 && yRadius2 < 30){
                if(!myItemPoints.contains(p)) {
                    int index = allItemPoints.indexOf(p);
                    myItems.add(allItems.get(index));
                    myItemPoints.add(p);
                    matched = true;
                }
            }

        }

        return matched;
    }

}
