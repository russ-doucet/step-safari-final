package com.su.mystepcounter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.su.mystepcounter.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null

    // variable gives the running status
    private var running = false

    // variable counts total steps
    private var totalSteps = 0f

    private lateinit var binding : ActivityMainBinding

    //Code for navigation bar

    val ACTIVITY_RECOGNITION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //check if permission isn't already granted, request the permission
        if (isPermissionGranted()) {
            requestPermission()
        }

        //initializing sensorManager instance
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Setting up the progress bar to correspond with the user's inputted step goal
        binding.setGoalButton.setOnClickListener{
            var stepGoal = binding.inputtedGoal.getText().toString().toInt()
            binding.progressBar.max = stepGoal
            binding.progressBar.progress = 0
            Toast.makeText(this, "Step Goal Successfully Set!", Toast.LENGTH_SHORT).show()
        }

        // Test button to test that progress bar incrementign is working:
        binding.incrementTest.setOnClickListener(View.OnClickListener {
            val progress: Int = binding.progressBar.getProgress()
            val newProgress = progress + 1

            // Ensure progress doesn't exceed maximum value
            if (newProgress <= binding.progressBar.getMax()) {
                binding.progressBar.setProgress(newProgress)
            }
        })


        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.achievement -> {
                    // Navigate to AchievementsActivity
                    startActivity(Intent(this, AchievementsActivity::class.java))
                    true
                }
                R.id.settings -> {
                    // Navigate to Settings Activity
                    startActivity(Intent(this, Settings::class.java))
                    true
                }
                else -> false
            }
        }
//        binding.bottomNav.setOnItemSelectedListener{ item ->
//            Toast.makeText(this, "Achievements Clicked", Toast.LENGTH_SHORT).show()
//            onOptionsItemSelected(item)
//        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == R.id.home){
            return true
        }
        else if (id == R.id.achievement) {
            val intent = Intent(this, AchievementsActivity::class.java)
            this.startActivity(intent)
           Toast.makeText(this, "Achievements Clicked", Toast.LENGTH_SHORT).show()
           return true
        }
        else if (id == R.id.settings) {
            Toast.makeText(this, "Achievements Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Settings::class.java)
            this.startActivity(intent)
            return true
        }
        return false
    }

    // Function for incrementing the progress bar
    private fun incrementProgress(value: Int){
        val progress: Int = binding.progressBar.getProgress()
        val newProgress = progress + value

        // Ensure progress doesn't exceed maximum value
        if (newProgress <= binding.progressBar.getMax()) {
            binding.progressBar.setProgress(newProgress)
        }
        else{
            Toast.makeText(this, "You have reached your step goal! Time to set a new One!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {

        super.onResume()
        running = true

        // TYPE_STEP_COUNTER:  A constant describing a step counter sensor
        // Returns the number of steps taken by the user since the last reboot while activated
        // This sensor requires permission android.permission.ACTIVITY_RECOGNITION.
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            // show toast message, if there is no sensor in the device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // register listener with sensorManager
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        running = false
        // unregister listener
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        // get textview by its id
        val tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)

        if (running) {

            //get the number of steps taken by the user.
            totalSteps = event!!.values[0]

            val currentSteps = totalSteps.toInt()

            // set current steps in textview
            tv_stepsTaken.text = ("$currentSteps")
            binding.progressBar.progress = currentSteps
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        println("onAccuracyChanged: Sensor: $sensor; accuracy: $accuracy")
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_RECOGNITION_REQUEST_CODE
            )
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) != PackageManager.PERMISSION_GRANTED

    }

    //handle requested permission result(allow or deny)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ACTIVITY_RECOGNITION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }
            }
        }
    }
}