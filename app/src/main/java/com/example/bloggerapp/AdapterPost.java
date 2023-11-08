package com.example.bloggerapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.HolderPost>{

    private Context context;
    private ArrayList<ModelPost> postArrayList;

    public AdapterPost(Context context, ArrayList<ModelPost> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;
    }

    @NonNull
    @Override
    public HolderPost onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_post, parent, false);
        return new HolderPost(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPost holder, int position) {
        ModelPost model = postArrayList.get(position);
        String authorName = model.getAuthorName();
        String content = model.getContent();
        String id = model.getId();
        String published = model.getPublished();
        String selfLink = model.getSelfLink();
        String title = model.getTitle();
        String updated = model.getUpdated();
        String url = model.getUrl();

        Document document = Jsoup.parse(content);
        try {
            Elements elements = document.select("img");
            String image = elements.get(0).attr("src");
            Picasso.get().load(image).placeholder(R.drawable.ic_image_black).into(holder.imageTv);
        }catch (Exception e){
            holder.imageTv.setImageResource(R.drawable.ic_image_black);
        }

        String gmtDate = published;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy K:mm a");
        String formattedDate = "";
        try{
            Date date = dateFormat.parse(gmtDate);
            formattedDate = dateFormat2.format(date);
        }catch (Exception e){
            formattedDate = published;
            e.printStackTrace();
        }

        holder.titleTv.setText(title);
        holder.descriptionTv.setText(document.text());
        holder.publishInfoTv.setText("By " + authorName + " "+ formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class HolderPost extends RecyclerView.ViewHolder{
        ImageButton moreBtn;
        TextView titleTv, publishInfoTv, descriptionTv;
        ImageView imageTv;

        public HolderPost(@NonNull View itemView){
            super(itemView);
            //init UI View
            moreBtn = itemView.findViewById(R.id.moreBtn);
            titleTv = itemView.findViewById(R.id.titleTv);
            publishInfoTv = itemView.findViewById(R.id.publishInfoTv);
            imageTv = itemView.findViewById(R.id.imageTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
        }
    }
}
