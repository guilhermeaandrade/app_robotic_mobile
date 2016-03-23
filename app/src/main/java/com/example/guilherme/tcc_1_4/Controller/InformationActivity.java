package com.example.guilherme.tcc_1_4.Controller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guilherme.tcc_1_4.Model.Robo;
import com.example.guilherme.tcc_1_4.R;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.List;

public class InformationActivity extends AppCompatActivity {

    private static final int[] idPhoto = new int[]{R.drawable.i1,R.drawable.i2, R.drawable.i3, R.drawable.i4};
    private List<Robo> listRobo;
    private Robo robo;
    private Toolbar mToolbar;
    private Button btManualControl;
    private int idRobo;
    private Drawer.Result navigationDrawerLeft;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //TRANSITIONS
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Explode transGo = new Explode();
            transGo.setDuration(3000);

            Fade transBack = new Fade();
            transBack.setDuration(3000);

            getWindow().setEnterTransition(transGo);
            getWindow().setReturnTransition(transBack);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_activity_layout);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Informações");
        mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        listRobo = new ArrayList<Robo>();
        listRobo = getSetRoboList(1);

        ImageView ivRobo = (ImageView) findViewById(R.id.iv_robo);
        TextView tvNome = (TextView) findViewById(R.id.tv_nome);
        TextView tvTipo = (TextView) findViewById(R.id.tv_tipo);

        Intent intent = getIntent();
        Bundle params = intent.getExtras();

        if(params != null){
            idRobo = params.getInt("positionList");
        }

        ivRobo.setImageResource(listRobo.get(idRobo).getPhoto());
        tvNome.setText(listRobo.get(idRobo).getNomeRobo());
        if(listRobo.get(idRobo).getTipoRobo() == 1){
            tvTipo.setText("Mestre");
        }else{
            tvTipo.setText("Escravo");
        }


        btManualControl = (Button) findViewById(R.id.btMControl);
        btManualControl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ManualControlActivity.class);
                Bundle params = new Bundle();
                params.putInt("positionList", idRobo);
                intent.putExtras(params);

                startActivity(intent);
            }
        });

        navigationDrawerLeft = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(false)
                .withCloseOnClick(true)
                .withActionBarDrawerToggleAnimated(false)
                .withActionBarDrawerToggle(new ActionBarDrawerToggle(this, new DrawerLayout(this), R.string.drawer_open, R.string.drawer_close){
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        super.onDrawerSlide(drawerView, slideOffset);
                        navigationDrawerLeft.closeDrawer();
                        finish();
                    }
                })
                .build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        return true;
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
}
