package org.example;

import org.example.model.Item;
import org.example.model.CsvLoader;
import org.example.model.Backpack;

import java.io.IOException;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {
        String path = "problem plecakowy dane CSV tabulatory.csv";

        List<Item> items = CsvLoader.loadItems(path);
        Backpack backpack = new Backpack(6404180,items);

        System.out.println("Wczytano przedmiotów: " + items.size());
        for (int i = 0; i < items.size(); i++) {
            System.out.println(items.get(i).getName() + " " + items.get(i).getWeight() + " " + items.get(i).getValue());
        }


    }
}