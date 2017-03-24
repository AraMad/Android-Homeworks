package ua.arina.task4.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.arina.task4.R;
import ua.arina.task4.activitys.MainActivity;
import ua.arina.task4.interfaces.ClickListener;
import ua.arina.task4.models.ItemModel;

/**
 * Created by Arina on 04.03.2017
 */

public class ItemsAdapter extends
        RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private List<ItemModel> items;
    private ClickListener clicklistener = null;

    public ItemsAdapter(ArrayList<ItemModel> photoItems) {
        items = photoItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_layout_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try {
            Bitmap preview;
            if ((preview = items.get(position).getIcon()) == null) {
                holder.progressBar.setVisibility(View.VISIBLE);
            } else {
                holder.iconImageView.setImageBitmap(preview);
                holder.progressBar.setVisibility(View.INVISIBLE);
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void setClickListener(MainActivity clicklistener) {
        this.clicklistener = clicklistener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView iconImageView;
        public ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            iconImageView = (ImageView) itemView.findViewById(R.id.icon);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View v) {
            if (clicklistener != null) {
                clicklistener.itemClicked(v, getAdapterPosition());
            }
        }
    }
}
