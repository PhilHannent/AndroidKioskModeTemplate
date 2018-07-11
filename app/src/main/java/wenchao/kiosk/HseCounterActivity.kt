package wenchao.kiosk

//import android.icu.util.Calendar
//import android.icu.util.ULocale
import android.app.DatePickerDialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import kotlinx.android.synthetic.main.activity_kiosk_hse.*
import org.joda.time.DateTime
import org.joda.time.LocalDate
import java.text.SimpleDateFormat

//import java.sql.Date


class HseCounterActivity : AppCompatActivity() {

    /*
        1. Set Device owner and lock task/pin screen
        2. Set as home intent
        3. Disable power off button/ give a way to turn off the device - not possible
        4. Disable volume botton if required
        5. stop screen to turn off, or lock
     */

    private val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
//            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )


    var isLocked = false
    var counter = 0
    var record = 0
    var latestAccident: LocalDate = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* Set the app into full screen mode */
//        window.decorView.systemUiVisibility = flags

        /* Following code allow the app packages to lock task in true kiosk mode */
        setContentView(R.layout.activity_kiosk_hse)
        // get policy manager
        val myDevicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        // get this app package name
        val mDPM = ComponentName(this, MyAdmin::class.java)
        //startLockTask();
        if (myDevicePolicyManager.isDeviceOwnerApp(this.packageName)) {
            // get this app package name
            val packages = arrayOf(this.packageName)
            // mDPM is the admin package, and allow the specified packages to lock task
            //myDevicePolicyManager.setLockTaskPackages(mDPM, packages);
            startLockTask()
        } else {
            Toast.makeText(applicationContext, "Not owner", Toast.LENGTH_LONG).show()
        }

//        setVolumMax()

        tv_record.text = counter.toString()

        iv_lockButton.setOnClickListener {
            isLocked = when (isLocked) {
                true -> {
                    stopLockTask()
                    false
                }
                false -> {
                    startLockTask()
                    true
                }
            }
        }

        var listener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            //            // TODO Auto-generated method stub
            latestAccident = DateTime(year, monthOfYear, dayOfMonth, 12, 0, 0, 0).toLocalDate()
            tv_current.text = DateTime.now().minus(latestAccident.toDate().time).dayOfYear.toString()
            tv_latestAccident.text = "depuis le " + SimpleDateFormat("dd/MM/yyyy").parse(latestAccident.toDate().toString())
        }

        tv_current.setOnClickListener {
            if (!isLocked) {
                DatePickerDialog(this, listener, latestAccident.year, latestAccident.monthOfYear, latestAccident.dayOfMonth)

                        .show()

            }
        }


        tv_record.setOnClickListener {
            if (!isLocked) {
                MaterialDialog
                        .Builder(this@HseCounterActivity)
                        .title("Record")
                        .input(null, null) { _, input ->
                            record = input.toString().toInt()
                            tv_record.text = record.toString()

                        }.show()
            }
        }
    }
//
//        val myCalendar = Calendar.getInstance(ULocale.FRANCE)
//
//        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//            // TODO Auto-generated method stub
//            myCalendar.set(Calendar.YEAR, year)
//            myCalendar.set(Calendar.MONTH, monthOfYear)
//            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
////            updateLabel()
//        }
//
//        tv_current.setOnClickListener {
//            // TODO Auto-generated method stub
//            DatePickerDialog(this@HseCounterActivity, date, myCalendar
//                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                    myCalendar.get(Calendar.DAY_OF_MONTH))
//                    .show()
//        }


//        val lock_btn = findViewById<View>(wenchao.kiosk.R.id.lock_button) as Button
//        val unlock_btn = findViewById<View>(wenchao.kiosk.R.id.unlock_button) as Button
//
//        lock_btn.setOnTouchListener { v, event ->
//            startLockTask()
//            false
//        }
//
//        unlock_btn.setOnTouchListener { v, event ->
//            stopLockTask()
//            false
//        }
//
//        val b1 = findViewById<View>(R.id.button) as Button
//        val t1 = findViewById<TextView>(R.id.textView)
//        val b2 = findViewById<View>(R.id.button2) as Button
//        val b3 = findViewById<View>(R.id.button3) as Button
//
//        b1.setOnClickListener {
//            MaterialDialog.Builder(this@KioskActivity)
//                    .title("Compteur")
//                    //         .inputRangeRes(2, 20, R.color.material_red_500
//                    .input(null, null) { dialog, input -> t1.text = "Nombre de jours sans accident = $input" }.show()
//        }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Toast.makeText(this, "Volume button is disabled", Toast.LENGTH_SHORT).show()
            return true
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Toast.makeText(this, "Volume button is disabled", Toast.LENGTH_SHORT).show()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun setVolumMax() {
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.setStreamVolume(
                AudioManager.STREAM_SYSTEM,
                am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM),
                0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(wenchao.kiosk.R.menu.menu_lock_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return if (id == wenchao.kiosk.R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

}