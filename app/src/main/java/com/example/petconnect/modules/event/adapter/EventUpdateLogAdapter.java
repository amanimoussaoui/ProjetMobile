package com.example.petconnect.modules.event.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petconnect.R;
import com.example.petconnect.modules.event.model.EventUpdateLog;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventUpdateLogAdapter extends RecyclerView.Adapter<EventUpdateLogAdapter.UpdateLogViewHolder> {
    private List<EventUpdateLog> logs;

    public EventUpdateLogAdapter(List<EventUpdateLog> logs) {
        this.logs = logs;
    }

    @NonNull
    @Override
    public UpdateLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_update_log, parent, false);
        return new UpdateLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateLogViewHolder holder, int position) {
        EventUpdateLog log = logs.get(position);
        holder.bind(log);
    }

    @Override
    public int getItemCount() {
        return logs != null ? logs.size() : 0;
    }

    public static class UpdateLogViewHolder extends RecyclerView.ViewHolder {
        private TextView changeTypeText;
        private TextView changeTimeText;
        private TextView organizerNameText;
        private TextView oldValueText;
        private TextView newValueText;

        public UpdateLogViewHolder(@NonNull View itemView) {
            super(itemView);
            changeTypeText = itemView.findViewById(R.id.textViewChangeType);
            changeTimeText = itemView.findViewById(R.id.textViewChangeTime);
            organizerNameText = itemView.findViewById(R.id.textViewOrganizerName);
            oldValueText = itemView.findViewById(R.id.textViewOldValue);
            newValueText = itemView.findViewById(R.id.textViewNewValue);
        }

        public void bind(EventUpdateLog log) {
            changeTypeText.setText(log.getChangeType());
            changeTimeText.setText(getTimeAgo(log.getChangedAt()));
            organizerNameText.setText("Par: " + (log.getOrganizerName() != null ? log.getOrganizerName() : "Admin"));
            
            String oldVal = log.getOldValue() != null ? log.getOldValue() : "Non défini";
            String newVal = log.getNewValue() != null ? log.getNewValue() : "Non défini";
            
            oldValueText.setText(oldVal.length() > 50 ? oldVal.substring(0, 47) + "..." : oldVal);
            newValueText.setText(newVal.length() > 50 ? newVal.substring(0, 47) + "..." : newVal);
        }

        private String getTimeAgo(Date date) {
            if (date == null) return "Date inconnue";
            
            long seconds = (System.currentTimeMillis() - date.getTime()) / 1000;
            
            if (seconds < 60) return "À l'instant";
            long minutes = seconds / 60;
            if (minutes < 60) return "Il y a " + minutes + "m";
            
            long hours = minutes / 60;
            if (hours < 24) return "Il y a " + hours + "h";
            
            long days = hours / 24;
            return "Il y a " + days + "j";
        }
    }
}
