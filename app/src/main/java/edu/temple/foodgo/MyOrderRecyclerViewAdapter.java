package edu.temple.foodgo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import edu.temple.foodgo.OrderFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;


public class MyOrderRecyclerViewAdapter extends RecyclerView.Adapter<MyOrderRecyclerViewAdapter.ViewHolder>{

    private final ArrayList mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyOrderRecyclerViewAdapter(ArrayList items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final int pos = position;
        holder.mItem = (OrderItem)mValues.get(position);
       // holder.mIdView.setText(mValues.get(position).id);
        //holder.mCostView.setText(mValues.get(position).content);
        Picasso.get().load(holder.mItem.getImageURL()).resize(128, 128).into(holder.mImageView);
        holder.mNameView.setText(holder.mItem.getName());
        holder.mCostView.setText(holder.mItem.getPrice());
        holder.removeButton.setText("Remove Item");
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRemoveButton(pos);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mNameView;
        public final TextView mCostView;
        public final Button removeButton;
        public OrderItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.item_image);
            mNameView = (TextView) view.findViewById(R.id.item_name);
            mCostView = (TextView) view.findViewById(R.id.item_cost);
            removeButton = (Button) view.findViewById(R.id.remove_button);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCostView.getText() + "'";
        }
    }
}
