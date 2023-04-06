package uz.jalil.viewpagerindicator

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val vp = findViewById<ViewPager2>(R.id.vp_slide)
        val adapter = PagerAdapter(arrayListOf("", "", "","",""), supportFragmentManager, lifecycle)
        vp.adapter = adapter

        val dots = findViewById<DotsIndicator>(R.id.dotIndicator)

        dots.setViewPager2(vp)

        vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                Log.d("TTT", "$position")
                Log.d("TTT", "$positionOffset")
                Log.d("TTT", "$positionOffsetPixels")

                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        })
    }
}