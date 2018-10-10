package no.hiof.internote.internote.adapter;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import no.hiof.internote.internote.R;
import no.hiof.internote.internote.model.Note;
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
        viewHolder.setNoteOverview(noteToDisplay, position);
        viewHolder.setListeners();
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView_thumbnail;
        TextView textView_title, textView_lastEdited;


        public NoteViewHolder(View itemView) {
            super(itemView);

            imageView_thumbnail = itemView.findViewById(R.id.imageView_thumbnail);
            textView_title = itemView.findViewById(R.id.textView_listItem_title);
            textView_lastEdited = itemView.findViewById(R.id.textView_listItem_lastEdited);

            itemView.setOnClickListener((View.OnClickListener) this);
        }

        public void setNoteOverview(NoteOverview noteOverview, int position) {
            // Fills the views with the given data
            imageView_thumbnail.setImageResource(noteOverview.getImageUrl());
            textView_title.setText(noteOverview.getDescription());
            thumbnailImageView.setImageResource(landscape.getImageID());

            // Stores a reference to the data and position
            landscaped = landscape;
            this.position = position;
        }
    }
}
