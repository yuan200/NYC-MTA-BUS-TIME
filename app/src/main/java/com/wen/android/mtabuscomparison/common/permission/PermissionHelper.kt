package com.wen.android.mtabuscomparison.common.permission

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wen.android.mtabuscomparison.common.Observable
import dagger.hilt.android.scopes.ActivityScoped
import java.util.*
import javax.inject.Inject

@ActivityScoped
class PermissionHelper
@Inject constructor(private val activity: Activity) :
    Observable<PermissionHelper.Listener> {

    interface Listener {
        fun onRequestPermissionsResult(requestCode: Int, result: PermissionsResult)
        fun onPermissionsRequestCancelled(requestCode: Int)
    }

    class PermissionsResult(
        val granted: List<MyPermission>,
        val denied: List<MyPermission>,
        val deniedDoNotAskAgain: List<MyPermission>
    )

    private val listeners: MutableSet<Listener> = mutableSetOf()

    fun hasPermission(permission: MyPermission): Boolean =
        ContextCompat.checkSelfPermission(
            activity,
            permission.androidPermission
        ) == PackageManager.PERMISSION_GRANTED

    fun hasAllPermissions(permissions: Array<MyPermission>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission.androidPermission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun requestPermission(permission: MyPermission, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permission.androidPermission),
            requestCode
        )
    }

    fun requestAllPermissions(permissions: Array<MyPermission>, requestCode: Int) {
        val androidPermissions = arrayOfNulls<String>(permissions.size)
        for (i in permissions.indices) {
            androidPermissions[i] = permissions[i].androidPermission
        }
        ActivityCompat.requestPermissions(activity, androidPermissions, requestCode)
    }

    fun onRequestPermissionResult(
        requestCode: Int,
        androidPermissions: Array<String>,
        grantResults: IntArray
    ) {
        if (androidPermissions.isEmpty() || grantResults.isEmpty()) {
            notifyPermissionsRequestCancelled(requestCode);
        }

        val grantedPermissions: MutableList<MyPermission> = LinkedList()
        val deniedPermissions: MutableList<MyPermission> = LinkedList()
        val deniedAndDoNotAskAgainPermissions: MutableList<MyPermission> = LinkedList()

        var androidPermission: String
        var permission: MyPermission

        for (i in androidPermissions.indices) {
            androidPermission = androidPermissions[i]
            permission = MyPermission.fromAndroidPermission(androidPermission)
            if (grantResults[i] === PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(permission)
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    androidPermission
                )
            ) {
                deniedPermissions.add(permission)
            } else {
                deniedAndDoNotAskAgainPermissions.add(permission)
            }
        }
        val result = PermissionsResult(
            grantedPermissions,
            deniedPermissions,
            deniedAndDoNotAskAgainPermissions
        )
        notifyPermissionsResult(requestCode, result)
    }

    private fun notifyPermissionsResult(requestCode: Int, permissionsResult: PermissionsResult) {
        for (listener in listeners) {
            listener.onRequestPermissionsResult(requestCode, permissionsResult)
        }
    }

    private fun notifyPermissionsRequestCancelled(requestCode: Int) {
        for (listener in listeners) {
            listener.onPermissionsRequestCancelled(requestCode)
        }
    }

    override fun registerListener(listener: Listener) {
        listeners.add(listener)
    }

    override fun unregisterListener(listener: Listener) {
        listeners.remove(listener)
    }

}