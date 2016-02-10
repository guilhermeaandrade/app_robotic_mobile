package com.example.guilherme.tcc_1_4.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Explode;
import android.transition.Fade;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.guilherme.tcc_1_4.Adapter.RoboAdapter;
import com.example.guilherme.tcc_1_4.Controller.InformationActivity;
import com.example.guilherme.tcc_1_4.Controller.MainActivity;
import com.example.guilherme.tcc_1_4.Interfaces.RecylerViewOnClickListenerHack;
import com.example.guilherme.tcc_1_4.Model.Robo;
import com.example.guilherme.tcc_1_4.R;

import java.util.List;

public class RoboFragment extends Fragment implements RecylerViewOnClickListenerHack{

    private RecyclerView mRecyclerView;
    private List<Robo> mList;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.robo_fragment, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // -------------------------------------- STAGGEREDGRIDLAYOUTMANAGER -------------------------------------------------
                /*StaggeredGridLayoutManager mStaggeredGridLayoutManager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
                int[] aux = mStaggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(null);
                int max = -1;
                for(int i = 0; i < aux.length; i++){
                    max = aux[i] > max ? aux[i] : max;
                }

                RoboAdapter roboAdapter = (RoboAdapter) mRecyclerView.getAdapter();

                if (mList.size() == max + 1) {
                    List<Robo> listAux = ((MainActivity) getActivity()).getSetRoboList(10);

                    for (int i = 0; i < listAux.size(); i++) {
                        roboAdapter.addListItem(listAux.get(i), mList.size());
                    }
                }*/

                // ------------------------------- GRIDLAYOUT ---------------------------------------------------------
                /*GridLayoutManager gridLayoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
                RoboAdapter roboAdapter = (RoboAdapter) mRecyclerView.getAdapter();

                if (mList.size() == gridLayoutManager.findLastCompletelyVisibleItemPosition() + 1) {
                    List<Robo> listAux = ((MainActivity) getActivity()).getSetRoboList(10);

                    for (int i = 0; i < listAux.size(); i++) {
                        roboAdapter.addListItem(listAux.get(i), mList.size());
                    }
                }*/

                // -----------------------------LINEARLAYOUT -----------------------------------------------------
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                RoboAdapter roboAdapter = (RoboAdapter) mRecyclerView.getAdapter();

                /*if (mList.size() == linearLayoutManager.findLastCompletelyVisibleItemPosition() + 1) {
                    List<Robo> listAux = ((MainActivity) getActivity()).getSetRoboList(10);

                    for (int i = 0; i < listAux.size(); i++) {
                        roboAdapter.addListItem(listAux.get(i), mList.size());
                    }
                }*/
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity(), mRecyclerView, this));

        // ---------------------- LINEARLAYOUT ----------------------------------------------
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //linearLayoutManager.setReverseLayout(true); //limita o final -> acrescenta do inicio
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // ---------------------- GRIDLAYOUT -----------------------------------------------
        /*GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mGridLayoutManager);*/

        // --------------------- STAGGEREDGRIDLAYOUTMANAGER
        /*StaggeredGridLayoutManager mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);*/

        mList = ((MainActivity) getActivity()).getSetRoboList(4); //4 elementos
        RoboAdapter roboAdapter = new RoboAdapter(getActivity(), mList);
        //roboAdapter.setmRecylerViewOnClickListenerHack(this);
        mRecyclerView.setAdapter(roboAdapter);


        return view;
    }

    @Override
    public void onClickListener(View view, int position) {

        Intent intent = new Intent(this.getActivity(), InformationActivity.class);

        Bundle params = new Bundle();
        params.putInt("positionList", position);
        intent.putExtras(params);

        //TRANSITIONS
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), null);
            getActivity().startActivity(intent, options.toBundle());
        }else {
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onLongPressClickListener(View view, int position) {
        //Toast.makeText(getActivity(), "onLongPressClickListener", Toast.LENGTH_SHORT).show();

        //RoboAdapter roboAdapter = (RoboAdapter) mRecyclerView.getAdapter();
        //roboAdapter.removeListItem(position);
    }

    private static class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener{

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
