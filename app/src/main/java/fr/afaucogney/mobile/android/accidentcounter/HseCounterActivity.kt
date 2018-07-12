package fr.afaucogney.mobile.android.accidentcounter

import android.app.DatePickerDialog
import android.app.admin.DevicePolicyManager
import android.arch.lifecycle.Observer
import android.content.ComponentName
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import butterknife.OnClick
import com.afollestad.materialdialogs.MaterialDialog
import com.github.pwittchen.prefser.library.rx2.Prefser
import fr.afaucogney.mobile.android.accidentcounter.common.archi.base.BaseActivity
import fr.afaucogney.mobile.android.accidentcounter.domain.AddNewAccidentUseCase
import fr.afaucogney.mobile.android.accidentcounter.feature.AccidentCounterContract
import fr.afaucogney.mobile.android.accidentcounter.feature.AccidentCounterViewModel
import kotlinx.android.synthetic.main.activity_kiosk_hse.*
import org.joda.time.DateTime
import org.joda.time.LocalDate
import toothpick.config.Module
import javax.inject.Inject


//import java.sql.Date


class HseCounterActivity : BaseActivity(), AccidentCounterContract.ViewCapabilities {

    override val layoutResourceId = R.layout.activity_kiosk_hse

    override fun initViewModelObservations() {
        viewModel.observeDaysRecord().observe(this,
                Observer {
                    showDaysRecord(it ?: "N?A")
                })

        viewModel.observeLatestAccident().observe(this,
                Observer {
                    showLatestAccidentDate(it ?: "N?A")
                })

        viewModel.observeDaysSinceLatestAccident().observe(this,
                Observer {
                    showCurrentDayCountWithoutAccident(it ?: "N?A")
                })
    }
    /*
        1. Set Device owner and lock task/pin screen
        2. Set as home intent
        3. Disable power off button/ give a way to turn off the device - not possible
        4. Disable volume botton if required
        5. stop screen to turn off, or lock
     */

    override val injectionModule: Module
        get() = object : Module() {
            init {
                bind(AccidentCounterViewModel::class.java).toInstance(getViewModel(AccidentCounterViewModel::class.java))
            }
        }

    @Inject
    lateinit var viewModel: AccidentCounterViewModel


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

    @Inject
    lateinit var addNewAccidentUseCase: AddNewAccidentUseCase

    private lateinit var newAccidentDatelistener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
//        val scope = Toothpick.openScopes(application, this)
//        scope.installModules(SmoothieActivityModule(this))
        super.onCreate(savedInstanceState)
//        Toothpick.inject(this, scope)

        /* Set the app into full screen mode */
//        window.decorView.systemUiVisibility = flags

        /* Following code allow the app packages to lock task in true kiosk mode */
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


        newAccidentDatelistener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            viewModel.addNewAccident(DateTime(year, monthOfYear + 1, dayOfMonth, 12, 0, 0, 0))
        }

    }


    @OnClick(R.id.tv_current)
    fun onNewAccidentDate() {
        if (!isLocked) {
            showNewAccidentDateDialog()
        }
    }

    @OnClick(R.id.tv_record)
    fun onRecordListClick() {
        if (!isLocked) {
            showAccidentListDialog(viewModel.observeAccidents().value ?: listOf("void"))
        }
    }

    @OnClick(R.id.iv_clear_prefs)
    fun onCLearClick() {
        if (!isLocked) {
            Prefser(applicationContext).clear()
        }
    }

    @OnClick(R.id.iv_lockButton)
    fun onLockButtonClick() {
        if (!isLocked) {
            switchToKioskMode()
        } else {
            releaseKioskMode()
        }
    }


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
        menuInflater.inflate(R.menu.menu_lock_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }


    override fun showNewAccidentDateDialog() {
        DateTime.now().run {
            val t = DatePickerDialog(this@HseCounterActivity, newAccidentDatelistener, this.year, this.monthOfYear - 1, this.dayOfMonth)
            t.datePicker.maxDate = this.plusDays(1).millis
            t.show()
        }
    }

    override fun showCurrentDayCountWithoutAccident(dayCount: String) {
        tv_current.text = dayCount
    }

    override fun showDaysRecord(dayCount: String) {
        tv_record.text = dayCount

    }

    override fun showLatestAccidentDate(accident: String) {
        tv_latestAccident.text = "depuis le  $accident"
    }

    override fun showAccidentListDialog(accidents: List<String>) {
        MaterialDialog.Builder(this)
                .title("Les accidents")
                .items(accidents)
                .itemsCallbackSingleChoice(-1, MaterialDialog.ListCallbackSingleChoice { dialog, view, which, text ->
                    /**
                     * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                     * returning false here won't allow the newly selected radio button to actually be selected.
                     */
                    /**
                     * If you use alwaysCallSingleChoiceCallback(), which is discussed below,
                     * returning false here won't allow the newly selected radio button to actually be selected.
                     */
                    true
                })
                .negativeText("SUPPRIMER")
                .positiveText("FERMER")
                .show()
    }

    override fun switchToKioskMode() {
        startLockTask()
        isLocked = true
    }

    override fun releaseKioskMode() {
        stopLockTask()
        isLocked = false
    }

    override fun goToLockPatternRecord() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun goToUnlockPattern() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeApp() {
        finish()
    }

}