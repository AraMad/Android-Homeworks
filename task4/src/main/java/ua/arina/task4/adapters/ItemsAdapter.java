package ua.arina.task4.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ua.arina.task4.R;
import ua.arina.task4.models.ItemModel;

/**
 * Created by Arina on 04.03.2017.
 */

public class ItemsAdapter extends
        RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private List<ItemModel> items;
    private Context context;

    // Pass in the contact array into the constructor
    public ItemsAdapter(Context context, List<ItemModel> contacts) {
        items = contacts;
        this.context = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View itemView = inflater.inflate(R.layout.relative_layout_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ItemModel item = items.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(item.getName());
        Button button = holder.messageButton;
        button.setText("Show");

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Button messageButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.photo_name);
            messageButton = (Button) itemView.findViewById(R.id.show_button);
        }
    }
}
