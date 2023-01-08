package kevin.android.texts.Ending;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kevin.android.texts.R;

public class EndingAdapter extends RecyclerView.Adapter<EndingAdapter.EndingViewHolder> {
    Ending e = new Ending("Ending A", "Ending A is the true ending", "NPC1/NPC2");
    private List<Ending> endings = Arrays.asList(e);

    @NonNull
    @Override
    public EndingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EndingAdapter.EndingViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ending_guide, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EndingViewHolder holder, int position) {
        Ending current = endings.get(position);

        holder.title.setText(current.getTitle());
        holder.description.setText(current.getDescription());
        holder.guide.setText(current.getGuide());
    }

    @Override
    public int getItemCount() {
        return endings.size();
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
