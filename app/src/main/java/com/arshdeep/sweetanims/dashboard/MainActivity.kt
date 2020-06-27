package com.arshdeep.sweetanims.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.arshdeep.sweetanims.AppConstants
import com.arshdeep.sweetanims.R
import com.arshdeep.sweetanims.custom_view.DividerItemDecoration
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.inmobi.sdk.InMobiSdk
import io.branch.referral.Branch
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), InstallStateUpdatedListener {

    private val TAG = "UpdateStatus"

    private lateinit var appUpdateInf: AppUpdateInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Branch.getAutoInstance(applicationContext)
        setContentView(R.layout.activity_main)
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(this)
        init()

        Handler().postDelayed({
            initDeepLinkSdks()
        }, 5000)

        initInMobi()
    }

    private fun initInMobi() {
        val consent = JSONObject()
        try { // Provide correct consent value to sdk which is obtained by User
            consent.put(InMobiSdk.IM_GDPR_CONSENT_AVAILABLE, true)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        InMobiSdk.setLogLevel(InMobiSdk.LogLevel.DEBUG)
        InMobiSdk.init(this, "0868ad2513734eab9642ac4ac01dd459", consent) { error ->
            if (error == null) {
                Log.d(TAG, "InMobi SDK Initialization Success")
            } else {
                Log.e(TAG, "InMobi SDK Initialization failed: " + error.message)
            }
        }
    }

    private fun initDeepLinkSdks() {
        if (intent.data == null && intent.extras == null) {
            Log.e("data", "intent.data is null")
        } else {
            if (intent.data != null)
            Log.e("data", intent.data.toString())
            if (intent.extras != null)
            Log.e("data", intent.extras.toString())
        }
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener { pendingDynamicLinkData ->
            if (pendingDynamicLinkData != null) {
                Toast.makeText(this@MainActivity, pendingDynamicLinkData.link.getQueryParameter("name"), Toast.LENGTH_SHORT).show()
                Log.e("firebase", pendingDynamicLinkData.link.getQueryParameter("name"))
            } else {
                Log.e("firebase", "data is null")
            }
        }.addOnFailureListener {
            Log.e("firebase", "failure")
        }

        // Branch init
        Branch.getInstance().initSession({ referringParams, error ->
            if (error == null) {
                Log.e("BRANCH SDK", referringParams.toString())
                Toast.makeText(this@MainActivity, referringParams.toString(), Toast.LENGTH_SHORT).show()
                // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
                // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
            } else {
                Log.e("BRANCH SDK", error.message)
            }
        }, this.intent.data, this)
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager
                .appUpdateInfo
                .addOnSuccessListener {
                    appUpdateInf = it
                    Log.d(TAG, "InstallStatus = ${it.installStatus()}")
                    Log.d(TAG, "updateAvailability = ${it.updateAvailability()}")
                    if (it.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate()
                    } else if (it.updateAvailability()
                            == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                        // If an in-app update is already running, resume the update.
                        appUpdateManager.startUpdateFlowForResult(
                                it,
                                AppUpdateType.IMMEDIATE,
                                this@MainActivity,
                                AppConstants.REQUEST_CODE_IMMIDIATE_UPDATE)
                    }
                }.addOnCompleteListener {
                    Log.d(TAG, "completeListener")
                }.addOnFailureListener {
                    Log.d(TAG, "failureListener ${it.localizedMessage}")
                }
    }

    private fun init() {
        val animationList = resources.getStringArray(R.array.animation_list)
        var list = mutableListOf<String>()
        animationList.forEach { list.add(it) }
        recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recycler_view.adapter = AnimationListAdapter(this, list)
        recycler_view.addItemDecoration(DividerItemDecoration(this, R.drawable.divider))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_update_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.flexible -> doUpdate(AppUpdateType.FLEXIBLE)
            R.id.immediate -> doUpdate(AppUpdateType.IMMEDIATE)
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "resultCode = $resultCode")
        if (requestCode === AppConstants.REQUEST_CODE_IMMIDIATE_UPDATE) {
            if (resultCode !== Activity.RESULT_OK) {
                Log.d(TAG, "Update flow failed! Result code: $resultCode")
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }

    private lateinit var appUpdateManager: AppUpdateManager

    private fun doUpdate(appUpdateType: Int) {

        val requestCode = if (appUpdateType == AppUpdateType.IMMEDIATE) AppConstants.REQUEST_CODE_IMMIDIATE_UPDATE else AppConstants.REQUEST_CODE_FLEXIBLE_UPDATE

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            appUpdateInf = appUpdateInfo
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(appUpdateType)) {
                Log.d(TAG, "Update is available")
                appUpdateManager.registerListener(this@MainActivity)
                appUpdateManager.startUpdateFlowForResult(
                        // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,
                        // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                        appUpdateType,
                        // The current activity making the update request.
                        this,
                        // Include a request code to later monitor this update request.
                        requestCode)
            }
        }.addOnCompleteListener {
            Log.d(TAG, "completeListener")
        }.addOnFailureListener {
            Log.d(TAG, "failureListener ${it.localizedMessage}")
        }

    }

    private fun popupSnackbarForCompleteUpdate() {
        val snackbar = Snackbar.make(
                findViewById<View>(R.id.parent_layout),
                "An update has just been downloaded.",
                Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("RESTART") { view -> appUpdateManager.completeUpdate() }
        snackbar.setActionTextColor(
                resources.getColor(R.color.colorAccent))
        snackbar.show()
    }

    override fun onStateUpdate(state: InstallState?) {
        Log.d(TAG, "installStatus = ${state?.installStatus()}")
        Log.d(TAG, "installErrorCode = ${state?.installErrorCode()}")
        if (state?.installStatus() == InstallStatus.DOWNLOADED) {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            popupSnackbarForCompleteUpdate()
        }
    }
}
