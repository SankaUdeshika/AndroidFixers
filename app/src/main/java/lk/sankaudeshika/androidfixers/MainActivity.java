package lk.sankaudeshika.androidfixers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //        Logo Spring Animation
        ImageView logoImageView = findViewById(R.id.imageView);
        SpringAnimation springAnimation = new SpringAnimation(logoImageView, DynamicAnimation.TRANSLATION_Y);

        SpringForce springForce = new SpringForce();
        springForce.setStiffness(SpringForce.STIFFNESS_VERY_LOW);
        springForce.setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
        springForce.setFinalPosition(600f);

        springAnimation.setSpring(springForce);
        springAnimation.start();



//        Loading PreLoader Page
        ProgressBar progressBar = findViewById(R.id.progressBar);

        new Thread(new Runnable() {


            int x=0;
            @Override
            public void run() {
                for( int i = 0;i<100; i++){
                    x = i;
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setProgress(x,true);
                        }
                    });
                }
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        }).start();
    }
}