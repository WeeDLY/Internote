package no.hiof.internote.internote.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import no.hiof.internote.internote.R;
import no.hiof.internote.internote.model.NoteOverview;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.NoteViewHolder> {

    private List<NoteOverview> noteList;
    private LayoutInflater inflater;

    public NoteRecyclerAdapter(Context context, List<NoteOverview> noteList) {
        inflater = LayoutInflater.from(context);
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View itemView = inflater.inflate(R.layout.layout_listitem, parent, false);

        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder viewHolder, int position) {
        NoteOverview noteToDisplay = noteList.get(position);
        viewHolder.textView_title.setText(NoteOverview.get);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        //ImageView imageView_thumbnail;
        TextView textView_title, textView_lastEdited;
        private int position;


        public NoteViewHolder(View itemView) {
            super(itemView);

            //imageView_thumbnail = itemView.findViewById(R.id.imageView_thumbnail);
            textView_title = itemView.findViewById(R.id.textView_listItem_title);
            textView_lastEdited = itemView.findViewById(R.id.textView_listItem_lastEdited);

            itemView.setOnClickListener(this);
        }
    }
}
