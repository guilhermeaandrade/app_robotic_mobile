package com.example.guilherme.tcc_1_4.Controller;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.guilherme.tcc_1_4.Adapter.RoboAdapter;
import com.example.guilherme.tcc_1_4.Interfaces.RecylerViewOnClickListenerHack;
import com.example.guilherme.tcc_1_4.Model.Robo;
import com.example.guilherme.tcc_1_4.R;

import java.util.ArrayList;
import java.util.List;

public class ComponentActivity extends AppCompatActivity implements RecylerViewOnClickListenerHack {

    private static final int[] idPhoto = new int[]{R.drawable.i1,R.drawable.i2, R.drawable.i3, R.drawable.i4};
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private List<Robo> mList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component_activity_layout);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Componentes");
        mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                RoboAdapter roboAdapter = (RoboAdapter) mRecyclerView.getAdapter();
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, mRecyclerView, this));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //linearLayoutManager.setReverseLayout(true); //limita o final -> acrescenta do inicio
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mList = getSetRoboList(2); //1 elementos
        RoboAdapter roboAdapter = new RoboAdapter(this, mList);
        mRecyclerView.setAdapter(roboAdapter);

    }

    public List<Robo> getSetRoboList(int quantidade){
        String[] names = new String[]{"Robo 1", "Robo 2", "Robo 3", "Robo 4"};
        int[] types = new int[]{1, 2, 2, 2};
        List<Robo> listAux = new ArrayList<Robo>();

        for (int i = 0; i < quantidade; i++) {
            Robo r = new Robo(names[i % names.length], types[i % types.length], idPhoto[i % idPhoto.length]);
            listAux.add(r);
        }
        return listAux;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }

        return true;
    }

    @Override
    public void onClickListener(View view, int position) {

    }

    @Override
    public void onLongPressClickListener(View view, int position) {

    }

    private class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener{

        private Context mContext;
        private GestureDetector mGestureDetector;
        private RecylerViewOnClickListenerHack mRecylerViewOnClickListenerHack;

        public RecyclerViewTouchListener(Context context, final RecyclerView recyclerView, RecylerViewOnClickListenerHack recylerViewOnClickListenerHack){
            this.mContext = context;
            this.mRecylerViewOnClickListenerHack = recylerViewOnClickListenerHack;

            this.mGestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener(){

                @Override
                public void onLongPress(MotionEvent e) {
                    super.onLongPress(e);

                    View cv = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if(cv != null && mRecylerViewOnClickListenerHack != null){
                        mRecylerViewOnClickListenerHack.onLongPressClickListener(cv,
                                recyclerView.getChildAdapterPosition(cv));
                    }
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    View cv = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    boolean callContextMenuStatus = false;
                    if(cv instanceof CardView){
                        float x = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getX();
                        float y;// = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getY();
                        float w = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getWidth();
                        float h = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getHeight();

                        Rect rect = new Rect();
                        ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getGlobalVisibleRect(rect);
                        y = rect.top; //Y ATUAL

                        if(e.getX() >= x && e.getX() <= w + x && e.getRawY() >= y && e.getRawY() <= h + y){
                            callContextMenuStatus = true;
                        }
                    }

                    if(cv != null && mRecylerViewOnClickListenerHack != null && !callContextMenuStatus){
                        mRecylerViewOnClickListenerHack.onClickListener(cv,
                                recyclerView.getChildAdapterPosition(cv));
                    }

                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            mGestureDetector.onTouchEvent(e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
    }
}
