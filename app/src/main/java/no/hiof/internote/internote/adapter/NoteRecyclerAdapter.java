package no.hiof.internote.internote.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import no.hiof.internote.internote.NoteTextActivity;
import no.hiof.internote.internote.R;
import no.hiof.internote.internote.model.NoteOverview;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.NoteViewHolder> {
    private List<NoteOverview> data;
    private LayoutInflater inflater;

    private View.OnClickListener clickListener;

    public void setOnItemClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public NoteRecyclerAdapter(Context context, List<NoteOverview> data) {
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_listitem, parent, false);
        NoteViewHolder holder = new NoteViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        NoteOverview current_Object = data.get(position);
        holder.setData(current_Object);

        if (clickListener != null) {
            holder.itemView.setOnClickListener(clickListener);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        TextView lastEdited;
        ImageView thumbnail;

        public NoteViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView_listItem_title);
            lastEdited = itemView.findViewById(R.id.textView_listItem_lastEdited);
            thumbnail = itemView.findViewById(R.id.imageView_thumbnail);
        }

        public void setData(NoteOverview current) {
            this.title.setText(current.getTitle());

            String imageUrl = current.getImageUrl();
            Date lastEditedDate = new Date(current.getLastEdited());
            this.lastEdited.setText(new SimpleDateFormat("HH:mm dd-MM-yyyy").format(lastEditedDate));

            if (imageUrl != null && !imageUrl.equals("")) {
                Glide.with(thumbnail.getContext())
                        .load(imageUrl)
                        .into(thumbnail);
            }
            else
                thumbnail.setImageResource(R.drawable.thumbnail_placeholder);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), NoteTextActivity.class);
            view.getContext().startActivity(intent);
        }
    }
}