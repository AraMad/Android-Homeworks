package ua.arina.task5.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ua.arina.task5.R;
import ua.arina.task5.models.ItemModel;

/**
 * Created by Arina on 06.04.2017
 */

public class RecyclerViewItemAdapter extends
            RecyclerView.Adapter<RecyclerViewItemAdapter.ViewHolder> {

        private List<ItemModel> citys;

        public RecyclerViewItemAdapter(ArrayList<ItemModel> photoItems) {
            this.citys = photoItems;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_view_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return citys.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{


            public ViewHolder(View itemView) {
                super(itemView);
            }

            /*@Override
            public void onClick(View v) {
                if (clicklistener != null) {
                    clicklistener.itemClicked(v, getAdapterPosition());
                }
            }*/
        }
}