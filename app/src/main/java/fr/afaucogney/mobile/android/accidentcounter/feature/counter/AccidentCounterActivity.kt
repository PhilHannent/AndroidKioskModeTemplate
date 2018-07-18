package fr.afaucogney.mobile.android.accidentcounter.feature.counter

import android.app.ActivityManager
import android.app.DatePickerDialog
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.app.admin.SystemUpdatePolicy
import android.arch.lifecycle.Observer
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.BatteryManager
import android.os.Bundle
import android.os.UserManager
import android.provider.Settings
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import butterknife.OnClick
import com.afollestad.materialdialogs.MaterialDialog
import fr.afaucogney.mobile.android.accidentcounter.R
import fr.afaucogney.mobile.android.accidentcounter.common.archi.base.BaseActivity
import fr.afaucogney.mobile.android.accidentcounter.common.archi.base.BaseDialogFragment
import fr.afaucogney.mobile.android.accidentcounter.data.LockPatternDialogEvent
import fr.afaucogney.mobile.android.accidentcounter.feature.lock.CheckLockViewPatternFragment
import fr.afaucogney.mobile.android.accidentcounter.feature.lock.NewPatternLockViewFragment
import kotlinx.android.synthetic.main.activity_kiosk_hse.*
import org.joda.time.DateTime
import toothpick.config.Module
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class AccidentCounterActivity : BaseActivity(), AccidentCounterContract.ViewCapabilities {

    override val layoutResourceId = R.layout.activity_kiosk_hse

    override fun initViewModelObservations() {

        newAccidentDatelistener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            viewModel.addNewAccident(DateTime(year, monthOfYear + 1, dayOfMonth, 12, 0, 0, 0))
        }

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

        viewModel.observeLockPatternDialog().observe(this, Observer {
            when (it) {
                LockPatternDialogEvent.SHOWRECORD -> goToLockPatternRecordDialog()
                LockPatternDialogEvent.SHOWCHECK -> goToUnlockPatternDialog()
                LockPatternDialogEvent.LOCK -> goToKioskMode()
                LockPatternDialogEvent.UNLOCK -> goToNotKioskMode()
            }
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


//    private val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
////            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//            or View.SYSTEM_UI_FLAG_FULLSCREEN
////            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//            )


    var isLocked = false
//    var counter = 0

    private lateinit var newAccidentDatelistener: DatePickerDialog.OnDateSetListener

    @Inject
    lateinit var mDevicePolicyManager: DevicePolicyManager

    @Inject
    lateinit var mPackageManager: PackageManager

    var mAdminComponentName: ComponentName? = null

//    cyManager? = null
//    var mPackageManager: PackageManager? = null


//    mPackageManager = packageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAdminComponentName = ComponentName(applicationContext, MyAdmin::class.java)

//        mDevicePolicyManager = (DevicePolicyManager) getSystemService (Context.DEVICE_POLICY_SERVICE)
//        mPackageManager = packageManager
        setDefaultCosuPolicies(true)

//        /* Set the app into full screen mode */
////        window.decorView.systemUiVisibility = flags
//
//        /* Following code allow the app packages to lock task in true kiosk mode */
////        // get policy manager
////        val myDevicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
////        // get this app package name
////        val mDPM = ComponentName(this, MyAdmin::class.java)
////        //startLockTask();
////        if (myDevicePolicyManager.isDeviceOwnerApp(this.packageName)) {
////            // get this app package name
////            val packages = arrayOf(this.packageName)
////            // mDPM is the admin package, and allow the specified packages to lock task
////            //myDevicePolicyManager.setLockTaskPackages(mDPM, packages);
////            startLockTask()
////        } else {
////            Toast.makeText(applicationContext, "Not owner", Toast.LENGTH_LONG).show()
////        }
//
////        setVolumMax()
//
//
    }

    @OnClick(R.id.iv_clearPatternButton)
    fun onClearSymbolClick() {
        if (!isLocked) {
            showClearPatternDialog()
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
            showAccidentListDialog(viewModel.observeAccidents().value ?: listOf(-1L))
        }
    }

    @OnClick(R.id.iv_lockButton)
    fun onLockButtonClick() {
        if (!isLocked) {
            viewModel.tryToGoToKioskMode()
        } else {
            goToUnlockPatternDialog()
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


    override fun showNewAccidentDateDialog() {
        DateTime.now().run {
            val t = DatePickerDialog(this@AccidentCounterActivity, newAccidentDatelistener, this.year, this.monthOfYear - 1, this.dayOfMonth)
            t.datePicker.maxDate = this.millis
            t.show()
        }
    }

    override fun showCurrentDayCountWithoutAccident(dayCount: String) {
        if (dayCount.equals("NA")) {
            tv_current.text = "-"
        } else {
            tv_current.text = dayCount
        }

    }

    override fun showDaysRecord(dayCount: String) {
        if (dayCount.equals("NA")) {
            tv_record.text = "-"
        } else {
            tv_record.text = dayCount
        }
    }

    override fun showLatestAccidentDate(accident: String) {
        if (accident.equals("NA")) {
            tv_latestAccident.text = "pas encore d'accident"
        } else {
            tv_latestAccident.text = "depuis le  $accident"
        }
    }

    override fun showAccidentListDialog(accidents: List<Long>) {
        MaterialDialog.Builder(this)
                .title("Accidents depuis le " + SimpleDateFormat("dd MM yyyy", Locale.FRANCE).format(DateTime.now().millis))
                .items(accidents.map { SimpleDateFormat("dd MM yyyy", Locale.FRANCE).format(it) })
                .autoDismiss(false)
                .alwaysCallSingleChoiceCallback()
                .itemsCallbackSingleChoice(-1, { _, _, _, _ -> true })
                .negativeText("SUPPRIMER")
                .onNegative { dialog, _ ->
                    viewModel.removeAccident(accidents[dialog.selectedIndex])
                    dialog.dismiss()
                }
                .neutralText("RESET")
                .onNeutral { dialog, _ ->
                    viewModel.clearAccidents()
                    dialog.dismiss()
                }
                .positiveText("FERMER")
                .onPositive { dialog, _ -> dialog.dismiss() }
                .show()
    }

    private fun showClearPatternDialog() {
        MaterialDialog.Builder(this)
                .title("Etes vous certain de vouloir éffacer votre symbol de securité")
                .autoDismiss(false)
                .negativeText("ANNULER")
                .onNegative { dialog, _ ->
                    dialog.dismiss()
                }
                .positiveText("CONFIRMER")
                .onPositive { dialog, _ ->
                    viewModel.clearLockPattern()
                    dialog.dismiss()
                }
                .show()
    }


    override fun goToKioskMode() {
        if (!isLocked) {
            startLockTask()
            iv_clearPatternButton.visibility = View.GONE
            isLocked = true
            iv_lockButton.setImageResource(R.drawable.unlock_drawable)
        }
    }

    override fun goToNotKioskMode() {
        if (isLocked) {
            stopLockTask()
            iv_clearPatternButton.visibility = View.VISIBLE
            isLocked = false
            iv_lockButton.setImageResource(R.drawable.lock_drawable)
        }
    }

    override fun goToLockPatternRecordDialog() {
        clearDialog()
        NewPatternLockViewFragment.newInstance().show(supportFragmentManager)
    }

    override fun goToUnlockPatternDialog() {
        clearDialog()
        CheckLockViewPatternFragment.newInstance().show(supportFragmentManager)
    }

    private fun clearDialog() {
        val dialog = supportFragmentManager.findFragmentByTag(BaseDialogFragment.DIALOG)
        if (dialog != null && dialog is BaseDialogFragment) {
            dialog.dismiss()
        }
    }

    override fun closeApp() {
        clearDialog()
        finish()
    }

    inner class MyAdmin : DeviceAdminReceiver() {
    }

    fun fds() {
        // start lock task mode if it's not already active
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // ActivityManager.getLockTaskModeState api is not available in pre-M.
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            if (!am.isInLockTaskMode) {
//                startLockTask()
//            }
//        } else {
        if (am.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE) {
            startLockTask()
        }
//        }
    }

    //    var mAdminComponentName: ComponentName? = null
////    var mDevicePolicyManager: DevicePolicyManager? = null
//    var mPackageManager: PackageManager? = null
    val Battery_PLUGGED_ANY = Integer.toString(BatteryManager.BATTERY_PLUGGED_AC
            or BatteryManager.BATTERY_PLUGGED_USB
            or BatteryManager.BATTERY_PLUGGED_WIRELESS)

    val DONT_STAY_ON = "0"


    private fun setDefaultCosuPolicies(active: Boolean) {
        // set user restrictions
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active)
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active)
        setUserRestriction(UserManager.DISALLOW_ADD_USER, active)
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active)
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active)

        // disable keyguard and status bar
        mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, active)
        mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, active)

        // enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active)

        // set System Update policy

        if (active) {
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    SystemUpdatePolicy.createWindowedInstallPolicy(60, 120))
        } else {
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName, null)
        }

        // set this Activity as a lock task package

        mDevicePolicyManager.setLockTaskPackages(mAdminComponentName,
                if (active) arrayOf(packageName) else arrayOf<String>())

        val intentFilter = IntentFilter(Intent.ACTION_MAIN)
        intentFilter.addCategory(Intent.CATEGORY_HOME)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)

        if (active) {
            // set Cosu activity as home intent receiver so that it is started
            // on reboot
            mDevicePolicyManager.addPersistentPreferredActivity(
                    mAdminComponentName, intentFilter, ComponentName(
                    packageName, AccidentCounterActivity::class.java.name))
        } else {
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(
                    mAdminComponentName, packageName)
        }
    }

    private fun setUserRestriction(restriction: String, disallow: Boolean) {
        if (disallow) {
            mDevicePolicyManager.addUserRestriction(mAdminComponentName,
                    restriction)
        } else {
            mDevicePolicyManager.clearUserRestriction(mAdminComponentName,
                    restriction)
        }
    }

    private fun enableStayOnWhilePluggedIn(enabled: Boolean) {
        if (enabled) {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
                    Battery_PLUGGED_ANY)
        } else {
            mDevicePolicyManager.setGlobalSetting(
                    mAdminComponentName,
                    Settings.Global.STAY_ON_WHILE_PLUGGED_IN, DONT_STAY_ON)
        }
    }

}