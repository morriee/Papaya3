package com.example.papaya;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;



public class RcvAdapter extends RecyclerView.Adapter<RcvAdapter.ViewHolder> {

    private Activity activity;
    private List<Memo> dataList;
    private Realm realm;

    public RcvAdapter(Activity activity, List<Memo> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvContent;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.mTitleTextView);
            tvContent = itemView.findViewById(R.id.mContentTextView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    removeMemo(dataList.get(getAdapterPosition()).getTitle(),dataList.get(getAdapterPosition()).getContent());
                    removeItemView(getAdapterPosition());
                    return false;
                }
            });
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Memo data = dataList.get(position);
        holder.tvTitle.setText(data.getTitle());
        holder.tvContent.setText(data.getContent());
    }

    private void removeItemView(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataList.size());
    }

    // 데이터 삭제
    private void removeMemo(String title,String content) {
        realm = Realm.getDefaultInstance();
        final RealmResults<Memo> results1 = realm.where(Memo.class).equalTo("title",title).findAll();


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results1.deleteFromRealm(0);

            }
        });
    }
}