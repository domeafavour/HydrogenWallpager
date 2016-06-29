package cn.zhj.hydrogenwallpager.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.zhj.hydrogenwallpager.R;
import cn.zhj.hydrogenwallpager.databinding.ItemColorBinding;
import cn.zhj.hydrogenwallpager.model.ColorModel;

/**
 * Created by not_n on 2016/6/27.
 */
public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {

    private List<ColorModel> colorModels;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    public ColorAdapter(List<ColorModel> colorModels, Context context) {
        this.colorModels = colorModels;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = inflater.inflate(R.layout.item_color, null);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ColorModel model = colorModels.get(position);
        holder.colorBinding.setColorModel(model);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return colorModels == null ? 0 : colorModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemColorBinding colorBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            colorBinding = ItemColorBinding.bind(itemView);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
