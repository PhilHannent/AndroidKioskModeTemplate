package fr.afaucogney.mobile.android.accidentcounter;

//
//public class KioskActivity extends Activity {
//    /*
//        1. Set Device owner and lock task/pin screen
//        2. Set as home intent
//        3. Disable power off button/ give a way to turn off the device - not possible
//        4. Disable volume botton if required
//        5. stop screen to turn off, or lock
//     */
//
//    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//            | View.SYSTEM_UI_FLAG_FULLSCREEN
//            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        /* Set the app into full screen mode */
//        getWindow().getDecorView().setSystemUiVisibility(flags);
//
//        /* Following code allow the app packages to lock task in true kiosk mode */
//        setContentView(wenchao.kiosk.R.layout.activity_lock_activity);
//        // get policy manager
//        DevicePolicyManager myDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
//        // get this app package name
//        ComponentName mDPM = new ComponentName(this, MyAdmin.class);
//        //startLockTask();
//        if (myDevicePolicyManager.isDeviceOwnerApp(this.getPackageName())) {
//            // get this app package name
//            String[] packages = {this.getPackageName()};
//            // mDPM is the admin package, and allow the specified packages to lock task
//            //myDevicePolicyManager.setLockTaskPackages(mDPM, packages);
//            startLockTask();
//        } else {
//            Toast.makeText(getApplicationContext(), "Not owner", Toast.LENGTH_LONG).show();
//        }
//
//        setVolumMax();
//
//        Button lock_btn = (Button) findViewById(wenchao.kiosk.R.id.lock_button);
//        Button unlock_btn = (Button) findViewById(wenchao.kiosk.R.id.unlock_button);
//
//        lock_btn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                startLockTask();
//                return false;
//            }
//        });
//
//        unlock_btn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                stopLockTask();
//                return false;
//            }
//        });
//
//        Button b1 = (Button) findViewById(R.id.button);
//        final TextView t1 = findViewById(R.id.textView);
//        Button b2 = (Button) findViewById(R.id.button2);
//        Button b3 = (Button) findViewById(R.id.button3);
//
//        b1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new MaterialDialog.Builder(KioskActivity.this)
//                        .title("Compteur")
//                        //         .inputRangeRes(2, 20, R.color.material_red_500
//                        .input(null, null, new MaterialDialog.InputCallback() {
//                            @Override
//                            public void onInput(MaterialDialog dialog, CharSequence input) {
//                                t1.setText("Nombre de jours sans accident = " + input);
//                            }
//                        }).show();
//            }
//        });
//
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            Toast.makeText(this, "Volume button is disabled", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//            Toast.makeText(this, "Volume button is disabled", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
//
//    private void setVolumMax() {
//        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        am.setStreamVolume(
//                AudioManager.STREAM_SYSTEM,
//                am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM),
//                0);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(wenchao.kiosk.R.menu.menu_lock_activity, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
//        if (id == wenchao.kiosk.R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//}