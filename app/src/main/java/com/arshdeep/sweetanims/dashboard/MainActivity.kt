package com.arshdeep.sweetanims.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
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
import io.branch.referral.Branch
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), InstallStateUpdatedListener {

    private val TAG = "UpdateStatus"

    private lateinit var appUpdateInf: AppUpdateInfo

    private lateinit var referrerClient: InstallReferrerClient

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

        getInstallReferrerData()
    }

    private fun getInstallReferrerData() {
        referrerClient = InstallReferrerClient.newBuilder(this).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                Log.e(TAG, "onInstallReferrerSetupFinished : $responseCode")
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        val response = referrerClient.installReferrer
                        Log.d(TAG, response.installReferrer)
                        Log.d(TAG, response.referrerClickTimestampSeconds.toString())
                        Log.d(TAG, response.installBeginTimestampSeconds.toString())
//                        Log.d(TAG, response.googlePlayInstantParam.toString())
                        referrerClient.endConnection()
                    }
                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {
                    }
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {
                    }
                }
            }

            override fun onInstallReferrerServiceDisconnected() {
                Log.e(TAG, "onInstallReferrerServiceDisconnected")
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
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
        recycler_view.layoutManager = LinearLayoutManager(this)
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
