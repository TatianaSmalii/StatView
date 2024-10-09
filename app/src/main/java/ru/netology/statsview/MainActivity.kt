package ru.netology.statsview

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.netology.statsview.ui.StatsView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val view = findViewById<StatsView>(R.id.statsView)
        view.data = listOf(
            0.25F,
            0.25F,
            0.25F,
            0.25F,
        )
        val textView = findViewById<TextView>(R.id.label)
//        view.startAnimation(
//
//            AnimationUtils.loadAnimation(this, R.anim.animation).apply {
//                setAnimationListener( object : Animation.AnimationListener{
//                    override fun onAnimationStart(animation: Animation?) {
//                        textView.text = "Start"
//                    }
//
//                    override fun onAnimationEnd(animation: Animation?) {
//                        textView.text = "End"
//                    }
//
//                    override fun onAnimationRepeat(animation: Animation?) {
//                        textView.text = "Repeat"
//                    }
//
//                })
//            }
//        )
    }
}