package mad.devkai.aladinfortuneslots;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpinnerAdapter extends RecyclerView.Adapter<SpinnerAdapter.ItemViewHolder> {
    private int[] slot;
    private Context context;
    private Mechanics gameLogic;

    public SpinnerAdapter(Context context, int[] slot, Mechanics gameLogic) {
        this.context = context;
        this.slot = slot;
        this.gameLogic = gameLogic;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.spin_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        int i = position < slot.length ? position : position % slot.length;
        switch (slot[i]) {
            case 1:
                holder.pic.setImageResource(R.drawable.aladin);
                break;
            case 2:
                holder.pic.setImageResource(R.drawable.jamine);
                break;
            case 3:
                holder.pic.setImageResource(R.drawable.mop);
                break;
            case 4:
                holder.pic.setImageResource(R.drawable.genie);
                break;
            case 5:
                holder.pic.setImageResource(R.drawable.box);
                break;
            case 6:
                holder.pic.setImageResource(R.drawable.bonus);
                handleBonusLogic();
                break;
            case 7:
                holder.pic.setImageResource(R.drawable.magic);
                break;
        }
    }

    private void handleBonusLogic() {
        gameLogic.applyFreeSpins(3);
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;

        public ItemViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.spinner_item);
        }
    }
}
