package com.example.guilherme.tcc_1_4.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.guilherme.tcc_1_4.Controller.InformationActivity;
import com.example.guilherme.tcc_1_4.Controller.MapActivity;
import com.example.guilherme.tcc_1_4.Extra.ImageHelper;
import com.example.guilherme.tcc_1_4.Interfaces.RecylerViewOnClickListenerHack;
import com.example.guilherme.tcc_1_4.Model.ContextMenuItem;
import com.example.guilherme.tcc_1_4.Model.Robo;
import com.example.guilherme.tcc_1_4.R;

import java.util.ArrayList;
import java.util.List;

public class RoboAdapter extends RecyclerView.Adapter<RoboAdapter.MyViewHolder>{

    private Context mContext;
    private List<Robo> mList;
    private LayoutInflater mLayoutInflater;
    private RecylerViewOnClickListenerHack mRecylerViewOnClickListenerHack;
    private float scale;
    private int width;
    private int height;
    private int roundPixels;

    public RoboAdapter(Context context, List<Robo> lista){
        mContext = context;
        mList = lista;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        scale = mContext.getResources().getDisplayMetrics().density;
        width = mContext.getResources().getDisplayMetrics().widthPixels - (int)(14 * scale + 0.5f);
        height = (width/16)*9;

        roundPixels = (int)(2 * scale + 0.5f);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //chamado quando tem necessidade de criar uma nova view
        Log.i("LOG", "onCreateViewHolder");
        View v = mLayoutInflater.inflate(R.layout.item_robo_card, parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        //chamado toda hora.. vinculando dados
        Log.i("LOG", "onBindViewHolder");
        myViewHolder.ivRobo.setImageResource(mList.get(position).getPhoto());
        myViewHolder.tvName.setText(mList.get(position).getNomeRobo());
        if(mList.get(position).getTipoRobo() == 1){
            myViewHolder.tvType.setText((String)"Mestre");
        }else{
            myViewHolder.tvType.setText((String)"Escravo");
        }

        //DESCOBRI VERSAO CARD VIEW
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            myViewHolder.ivRobo.setImageResource(mList.get(position).getPhoto());
        }else{
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), mList.get(position).getPhoto());
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
            bitmap = ImageHelper.getRoundedCornerBitmap(mContext, bitmap, 15, width, height, false, false, true, true);
            myViewHolder.ivRobo.setImageBitmap(bitmap);
        }

        //EFEITO
        try{
            YoYo.with(Techniques.Tada)
                    .duration(700)
                    .playOn(myViewHolder.itemView);
        }catch (Exception e){}
    }

    @Override
    public int getItemCount() {
        //tamanho do conjunto
        return mList.size();
    }

    public void removeListItem(int position){
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public void addListItem(Robo r,int position){
        mList.add(r);
        notifyItemInserted(position);
    }

    public void setmRecylerViewOnClickListenerHack(RecylerViewOnClickListenerHack mRecylerViewOnClickListenerHack) {
        this.mRecylerViewOnClickListenerHack = mRecylerViewOnClickListenerHack;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView ivRobo;
        public TextView tvName;
        public TextView tvType;
        public ImageView ivContextMenu;

        public MyViewHolder(View itemView) {
            super(itemView);
            ivRobo = (ImageView) itemView.findViewById(R.id.iv_robo);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvType = (TextView) itemView.findViewById(R.id.tv_type);
            ivContextMenu = (ImageView) itemView.findViewById(R.id.iv_context_menu);

            if(ivContextMenu != null){
                ivContextMenu.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {

            //CRIO A LISTA DE ELEMENTOS QUE SERÃO UTILIZADOS
            List<ContextMenuItem> itens = new ArrayList<ContextMenuItem>();
            itens.add(new ContextMenuItem(R.drawable.information, "Info"));

            ContextMenuAdapter mContextMenuAdapter = new ContextMenuAdapter(mContext, itens);

            ListPopupWindow listPopupWindow = new ListPopupWindow(mContext);
            listPopupWindow.setAdapter(mContextMenuAdapter);
            listPopupWindow.setAnchorView(ivContextMenu);
            listPopupWindow.setWidth((int) (240 * scale + 0.5f));
            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(mContext, InformationActivity.class);

                    Bundle params = new Bundle();
                    int posList = getAdapterPosition();
                    params.putInt("positionList", posList);
                    intent.putExtras(params);

                    mContext.startActivity(intent);

                }
            });

            listPopupWindow.setModal(true); //da o click fora para fechar
            listPopupWindow.getBackground().setAlpha(0);
            listPopupWindow.show();
        }
    }

}
