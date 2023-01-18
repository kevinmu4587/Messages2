package kevin.android.texts.Ending;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kevin.android.texts.GameManager;
import kevin.android.texts.R;
import kevin.android.texts.Utils;

public class EndingAdapter extends RecyclerView.Adapter<EndingAdapter.EndingViewHolder> {
    @NonNull
    @Override
    public EndingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EndingAdapter.EndingViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ending_guide, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EndingViewHolder holder, int position) {
        Ending current = GameManager.allEndingsList.get(position);

        holder.title.setText(current.getTitle());
        if (GameManager.endingsFound[position] == 0) {
            holder.description.setText("???");
            holder.guide.setText("Unlock by: ???");
        } else {
            holder.description.setText(Utils.replaceName(current.getDescription()));
            holder.guide.setText("Unlock by: " + Utils.replaceName(current.getGuide()));
        }
    }

    @Override
    public int getItemCount() {
        return GameManager.allEndingsList.size();
    }

    class EndingViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        private TextView guide;

        public EndingViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.ending_title);
            description = itemView.findViewById(R.id.ending_description);
            guide = itemView.findViewById(R.id.ending_unlock_guide);
        }
    }
}
