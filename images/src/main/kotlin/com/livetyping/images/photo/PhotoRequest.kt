package com.livetyping.images.photo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.livetyping.images.ImageRequest
import com.livetyping.images.photo.filecreator.FileCreator
import java.io.*


abstract class PhotoRequest(
    chooserText: String? = null,
    private val fileCreator: FileCreator,
    private val maxSize: Int? = null
) : ImageRequest<File>(chooserText) {

    internal lateinit var mCurrentPhotoPath: Uri
    internal lateinit var providerAuthority: String
    internal var paths: Int? = null
    override val requestCode = 9467


    override fun request(attachedObject: Activity) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(attachedObject.packageManager)?.also {
                val imageFile: File? = try {
                    fileCreator.getImageFile(attachedObject, paths!!)
                } catch (ex: IOException) {
                    //TODO add work on exception
                    null
                }
                imageFile?.let {
                    it.deleteOnExit()
                    mCurrentPhotoPath = FileProvider.getUriForFile(
                        attachedObject,
                        providerAuthority,
                        it
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoPath)
                    startIntentConsideringChooserText(intent, attachedObject)
                }
            }
        }
    }

    override fun activityResult(attachedObject: Activity, data: Intent?) {
        val contentResolver = attachedObject.application.contentResolver
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, mCurrentPhotoPath)
        val rotationInputStream = contentResolver.openInputStream(mCurrentPhotoPath)
        //TODO check for null
        val angle = getRotationAngle(rotationInputStream)
        rotationInputStream?.close()
        val imageFile = fileCreator.getImageFile(attachedObject, paths!!)
        val quality = if (maxSize != null) calculateQuality(imageFile, maxSize) else 100
        imageFile.delete()
        imageFile.createNewFile()
        val out = FileOutputStream(imageFile)
        val orientationMatrix = Matrix()
        orientationMatrix.postRotate(angle.toFloat())
        val picture = Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            orientationMatrix,
            false
        )
        picture.compress(Bitmap.CompressFormat.JPEG, quality, out)
        picture.recycle()
        out.close()
        resultFunction.invoke(imageFile)
    }

    private fun calculateQuality(file: File, maxSize: Int): Int {
        var compressQuality = 100
        if (file.length() > maxSize) {
            var streamLength = maxSize
            val bmpStream = ByteArrayOutputStream()
            while (streamLength >= maxSize) {
                bmpStream.use {
                    it.flush()
                    it.reset()
                }
                compressQuality -= 8
                val bitmap =
                    BitmapFactory.decodeFile(file.absolutePath, BitmapFactory.Options())
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                streamLength = bmpStream.toByteArray().size
            }
            return compressQuality
        }
        return compressQuality
    }
}


