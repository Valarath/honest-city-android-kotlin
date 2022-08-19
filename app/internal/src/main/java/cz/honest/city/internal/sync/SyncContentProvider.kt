package cz.honest.city.internal.sync

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

class SyncContentProvider : ContentProvider() {
    /*
     * Always return true, indicating that the
     * provider loaded correctly.
     */
    override fun onCreate(): Boolean = true

    override fun query(
        p0: Uri,
        p1: Array<out String>?,
        p2: String?,
        p3: Array<out String>?,
        p4: String?
    ): Cursor? = null

    /*
     * Return no type for MIME type
     */
    override fun getType(uri: Uri): String? = null

    override fun insert(p0: Uri, p1: ContentValues?): Uri? =null

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?): Int =0

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?): Int =0

}