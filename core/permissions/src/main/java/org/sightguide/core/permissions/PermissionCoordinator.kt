package org.sightguide.core.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Coordinates permission checks and accessible spoken rationales.
 */
class PermissionCoordinator(private val context: Context) {

    fun isPermissionGranted(permission: SightGuidePermission): Boolean {
        if (permission.androidPermission.isEmpty()) return true
        return ContextCompat.checkSelfPermission(
            context,
            permission.androidPermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getMissingEssentialPermissions(): List<SightGuidePermission> {
        val list = mutableListOf<SightGuidePermission>()
        if (!isPermissionGranted(SightGuidePermission.LOCATION)) list.add(SightGuidePermission.LOCATION)
        if (!isPermissionGranted(SightGuidePermission.CAMERA)) list.add(SightGuidePermission.CAMERA)
        if (!isPermissionGranted(SightGuidePermission.MICROPHONE)) list.add(SightGuidePermission.MICROPHONE)
        return list
    }
}
