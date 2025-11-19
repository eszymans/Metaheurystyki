package org.example.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {

    public static List<Item> loadItems(String path) throws IOException {
        List<Item> items = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;

            // pomijamy nagłówek
            br.readLine();

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // split na tab
                String[] parts = line.split("\t");

                if (parts.length < 4)
                    throw new IllegalArgumentException("Błędny format CSV: " + line);

                // kolumny:
                // 0 - Numer
                // 1 - Nazwa
                // 2 - Waga (kg)
                // 3 - Wartość (zł)

                String name = parts[1];
                String weightStr = parts[2].replace(" ", "");
                String valueStr  = parts[3].replace(" ", "");

                int weight = Integer.parseInt(weightStr);
                int value  = Integer.parseInt(valueStr);

                items.add(new Item(name, weight, value));
            }
        }

        return items;
    }
}
