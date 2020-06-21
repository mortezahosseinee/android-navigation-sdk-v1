package ir.map.navigationsdk;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ir.map.navigationsdk.model.Direction;

public class DirectionsAdapter extends RecyclerView.Adapter<DirectionsAdapter.ViewHolder> {

    private List<Direction> mDirections;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Typeface mTypeface;

    DirectionsAdapter(Context context, List<Direction> mDirections) {
        this.mInflater = LayoutInflater.from(context);
        this.mDirections = mDirections;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.direction_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Direction direction = mDirections.get(position);

        holder.nameTxv.setText(direction.getName());

        if (direction.getTo().isEmpty() || isLastDirections(position))
            holder.orderTxv.setText(direction.getOrder() + ".");
        else
            holder.orderTxv.setText(direction.getOrder().replace(".", "") + " و وارد " + direction.getTo() + " شوید.");
    }

    @Override
    public int getItemCount() {
        return mDirections.size();
    }

    public void setDirections(ArrayList<Direction> mDirections) {
        this.mDirections = mDirections;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTxv;
        TextView orderTxv;

        ViewHolder(View itemView) {
            super(itemView);

            nameTxv = itemView.findViewById(R.id.name_txv);
            orderTxv = itemView.findViewById(R.id.order_txv);

            nameTxv.setTypeface(mTypeface);
            orderTxv.setTypeface(mTypeface);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setTypeface(Typeface mTypeface) {
        this.mTypeface = mTypeface;
    }

    private boolean isLastDirections(int position) {
        return  (position == getItemCount() - 1 || position == getItemCount() - 2);
    }
}
