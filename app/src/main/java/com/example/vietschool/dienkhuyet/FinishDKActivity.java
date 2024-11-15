package com.example.vietschool.dienkhuyet;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vietschool.MainActivity;
import com.example.vietschool.R;

public class FinishDKActivity extends AppCompatActivity {
    TextView txtcongrats,txtfinalqtrue,txtfinaltext, txtfinalScore;
    Button btnReturn;
    ImageView imgtrophy;
    final  String DATABASE_NAME = "HocNgonNgu.db";
    SQLiteDatabase database;
    int score;
    int questiontrue;
    int qcount;
    Animation smalltobig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_hoctap);

        smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig);
        Intent intent=getIntent();
        score=intent.getIntExtra("scoreDK",0);
        questiontrue = intent.getIntExtra("questiontrueDK",0);
        qcount = intent.getIntExtra("qcountDK",0);
        Anhxa();
        if(questiontrue==4){
            txtfinalqtrue.setText(questiontrue + " / " + qcount);
            txtcongrats.setText("Your final result: ");
            txtfinaltext.setText("Almost there!!");
            txtfinalScore.setText(" "+score);
        }
        if(questiontrue<4){
            txtfinalqtrue.setText(questiontrue +" / "+ qcount);
            txtcongrats.setText("Your final result: ");
            txtfinaltext.setText("Good luck next time !!");
            txtfinalScore.setText(" "+score);
        }
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FinishDKActivity.this, MainActivity.class));
            }
        });
    }
    public void Anhxa(){
        txtfinalScore=findViewById(R.id.tvPoints);
        txtcongrats=findViewById(R.id.txtcongrats);
        txtfinalqtrue=findViewById(R.id.txtquestiontrue);
        txtfinaltext=findViewById(R.id.txtfinaltext);
        btnReturn=findViewById(R.id.btnReturn);
        imgtrophy=findViewById(R.id.imgtrophy);
        imgtrophy.startAnimation(smalltobig);
    }
}