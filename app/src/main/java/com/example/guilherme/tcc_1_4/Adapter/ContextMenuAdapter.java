package com.example.guilherme.tcc_1_4.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guilherme.tcc_1_4.Model.ContextMenuItem;
import com.example.guilherme.tcc_1_4.R;

import java.util.List;

public class ContextMenuAdapter extends BaseAdapter{

    private Context mContext;
    private List<ContextMenuItem> mList;
    private LayoutInflater mLayoutInflater;
    private int extraPadding;

    public ContextMenuAdapter(Context context, List<ContextMenuItem> lista){
        this.mContext = context;
        this.mList = lista;
        mLayoutInflater = LayoutInflater.from(context);

        float scale = context.getResources().getDisplayMetrics().density;
        extraPadding = (int) (8 * scale + 0.5);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;

        if(convertView == null){
            convertView = mLayoutInflater.inflate(R.layout.context_item_menu, parent, false);
            mViewHolder = new ViewHolder();
            convertView.setTag(mViewHolder);

            mViewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            mViewHolder.tvLabel = (TextView) convertView.findViewById(R.id.tv_label);
            mViewHolder.vwDivider = convertView.findViewById(R.id.vw_divider);
         }else{
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.ivIcon.setImageResource(mList.get(position).getIcon());
        mViewHolder.tvLabel.setText(mList.get(position).getLabel());

        //BACKGROUND
        if(position == 0){
            ((ViewGroup) convertView).getChildAt(0).setBackgroundResource(R.drawable.context_menu_top_background);
        }else if(position == mList.size() - 1){
            ((ViewGroup) convertView).getChildAt(0).setBackgroundResource(R.drawable.context_menu_bottom_background);
        }else{
            ((ViewGroup) convertView).getChildAt(0).setBackgroundResource(R.drawable.context_menu_middle_background);
        }

        //LINE
        mViewHolder.vwDivider.setVisibility(position == mList.size()-2 ? View.VISIBLE : View.GONE);

        //EXTRA PADDING
        ((ViewGroup) convertView).getChildAt(0).setPadding(0,
                position == 0 || position == mList.size()-1 ? extraPadding : 0,
                0,
                position == mList.size() -1 ? extraPadding : 0);

        return convertView;
    }

    public static class ViewHolder{
        private ImageView ivIcon;
        private TextView tvLabel;
        private View vwDivider;
    }
}
