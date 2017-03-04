package ua.arina.task4.models;

import java.util.ArrayList;

/**
 * Created by Arina on 04.03.2017.
 */

public class ItemModel {

    private String mName;


    public ItemModel(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }


    private static int lastItemId = 0;

    public static ArrayList<ItemModel> createContactsList(int numContacts) {
        ArrayList<ItemModel> contacts = new ArrayList<ItemModel>();

        for (int i = 1; i <= numContacts; i++) {
            contacts.add(new ItemModel("Photo " + ++lastItemId));
        }

        return contacts;
    }
}
