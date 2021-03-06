package com.ddplay.yuntech.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ddplay.yuntech.Common.CommonData
import com.ddplay.yuntech.Common.CommonFunction
import com.ddplay.yuntech.Database.SQLite
import com.ddplay.yuntech.Dialog.DialogLoading
import com.ddplay.yuntech.Dialog.DialogWarn2
import com.ddplay.yuntech.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.max
import kotlin.math.min


class HomeFragment : Fragment() {
    private lateinit var dbrw: SQLiteDatabase
    private var MyView: View ?= null
    private val loading = DialogLoading()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // define view
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        findView(view)
        MyView = view
        dbrw = SQLite(view.context).writableDatabase
        checkWeb()
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCamera.setOnClickListener {
            camera(view)
        }
        btnGallery.setOnClickListener {
            gallery()
        }
        btnUpload.setOnClickListener {
            upload()
            imgPhoto.setImageResource(R.drawable.icon_yuntech)
            btnUpload.isEnabled = false
        }
        btnCheck.setOnClickListener {
            checkWeb()
        }
    }
    /*-------------------------------------------------------------------*/
    // Upload parameter
    private var uploadName: String = ""
    private var uploadLat: String = ""
    private var uploadLng: String = ""
    private var uploadDate: String = ""
    private var uploadImg: ByteArray? = null
    /*-------------------------------------------------------------------*/
    private lateinit var imgPhoto: ImageView
    private lateinit var btnCamera: Button
    private lateinit var btnUpload: Button
    private lateinit var btnGallery: Button
    private lateinit var listName: ListView
    private lateinit var btnCheck: ImageView
    // Define UI Views
    private fun findView(view: View) {
        imgPhoto = view.findViewById(R.id.img_photo)
        btnCamera = view.findViewById(R.id.btn_camera)
        btnUpload = view.findViewById(R.id.btn_upload)
        btnGallery = view.findViewById(R.id.btn_gallery)
        listName = view.findViewById(R.id.list_name)
        btnCheck = view.findViewById(R.id.btn_check)
    }
    /*-------------------------------------------------------------------*/
    private fun dialog(msg: String) {
        activity?.runOnUiThread {
            val warnDialog = DialogWarn2(MyView!!.context)
            // ?????? message????????????
            val message = msg
            // ????????????
            warnDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // ??????????????????dialog
            warnDialog
                .setMessage(message) // ??????setMessage()?????????dialog?????????
                .positive(object : DialogWarn2.IOnConfirmListener {
                    override fun positive(dialog: DialogWarn2?) {
                        warnDialog.dismiss()
                    }
                })
                .close(object : DialogWarn2.IOnCloseListener{
                    override fun close(dialog: DialogWarn2?) {
                        warnDialog.dismiss()
                    }
                })
                .show()
        }
    }
    /*-------------------------------------------------------------------*/
    // check web online?
    private fun checkWeb() {
        loading.show(MyView!!.context, "??????????????????...")
        val req = Request.Builder()
            .url(CommonData().url)
            .build()
        OkHttpClient().newCall(req).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                activity!!.runOnUiThread {
                    loading.dialog.dismiss()
                    dialog("?????????????????????!!!")
                    btnCheck.setImageResource(R.drawable.img_off)
                }
            }
            override fun onResponse(call: Call, response: Response) {
                activity!!.runOnUiThread {
                    loading.dialog.dismiss()
                    dialog("?????????????????????!!!")
                    btnCheck.setImageResource(R.drawable.img_on)
                }
            }
        })
    }
    /*-------------------------------------------------------------------*/
    // Initialize Photo Path
    private var currentPhotoPath: String = ""
    // Camera function
    private var cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setPic()
            }
        }
    private fun camera(view: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val photoFile: File? = try {
                createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    view.context,
                    "com.ddplay.yuntech.fileprovider",
                    it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                cameraLauncher.launch(intent)
            }
        }catch (e: ActivityNotFoundException) {
            dialog("????????????!!!")
        }
    }
    // ??????????????????????????????????????????
    @SuppressLint("NewApi")
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.TAIWAN).format(Date())
        val storageDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "YUNTECH_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }
    // ??????????????????ImageView
    @SuppressLint("NewApi")
    private fun setPic() {
        // ????????????????????????
        MediaScannerConnection.scanFile(view?.context,
            arrayOf(currentPhotoPath),
            null) { _, _ -> }
        // ????????????imageView?????????
        val targetW: Int = imgPhoto.width
        val targetH: Int = imgPhoto.height
        //BitmapFactory.Options()??????????????????
        val bmOptions = BitmapFactory.Options().apply {
            // ??????????????????bitmap?????????????????????????????????????????????????????????????????????????????????????????????
            inJustDecodeBounds = true
            //??????????????????
            val photoW: Int = outWidth
            val photoH: Int = outHeight
            // ??????????????????????????????1??????????????????????????????
            val scaleFactor: Int = max(1, min(photoW / targetW, photoH / targetH))
            // ????????????????????????
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
            inMutable = true
        }
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)?.also { tempBitmap ->
            val bitmap = CommonFunction().scaleDown(tempBitmap)
            imgPhoto.setImageBitmap(bitmap)
            name = ""
            position = 1
            loading.show(MyView!!.context, "????????????...")
            lifecycleScope.launch(Dispatchers.Default) {
                bitmap?.let {
                    runObjectDetection(it)
                }
            }
        }
    }
    /*-------------------------------------------------------------------*/
    // Gallery function
    // ???????????? onActivityResult
    @SuppressLint("NewApi")
    private var galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                currentPhotoPath = getPathFromUri(uri)
                val tempBitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
                val bitmap = CommonFunction().scaleDown(tempBitmap)
                imgPhoto.setImageBitmap(bitmap)
                name = ""
                position = 1
                loading.show(MyView!!.context, "????????????...")
                lifecycleScope.launch(Dispatchers.Default) {
                    bitmap?.let {
                        runObjectDetection(it)
                    }
                }
            }
        }
    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*" //????????????
        galleryLauncher.launch(intent)
    }
    // ??? Uri ???????????????
    private fun getPathFromUri(uri: Uri): String {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = activity?.contentResolver?.query(uri, projection, null, null, null)
        val columnIndex: Int? = cursor?.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        cursor?.moveToFirst()
        val result: String = columnIndex?.let { cursor.getString(it) } ?: ""
        cursor?.close()
        return result
    }
    /*-------------------------------------------------------------------*/
    private lateinit var name: String
    private lateinit var tempChar: String
    private var position: Int = 1
    // Object Detection
    @RequiresApi(Build.VERSION_CODES.O)
    private fun runObjectDetection(bitmap: Bitmap) {
        val items = ArrayList<String>()
        // Step 1: ??????????????????bitmap ??? tensor???
        val image = TensorImage.fromBitmap(bitmap)
        // Step 2: ??????????????????
        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setMaxResults(10) // 10????????????
            .setScoreThreshold(0.8f) // 80%????????????
            .build()
        val detector = ObjectDetector.createFromFileAndOptions(
            MyView?.context,
            "EfficientDet_Lite4.tflite",
            options
        )
        // Step 3: ????????????????????????
        val results = detector.detect(image)
        // Step 4: ????????????
        val resultToDisplay = results.map {
            // ???????????????????????????????????????
            val category = it.categories.first()
            // ???ListView??????
            if (category.label in CommonData().map)
                tempChar = CommonData().map[category.label].toString()
            if (!(name.contains(tempChar))) name = "$name???$tempChar"
            val text = "${position}. ?????????${tempChar}, ?????????${category.score.times(100).toInt()}%"
            items.add(text)
            position ++
            CommonData.DetectionResult(it.boundingBox, text)
        }
        if (name != "") name = name.drop(1)
        // Step 5: ????????????
        val imgWithResult = drawDetectionResult(bitmap, resultToDisplay)
        activity?.runOnUiThread {
            imgPhoto.setImageBitmap(imgWithResult)
            listName.adapter = ArrayAdapter(MyView!!.context, android.R.layout.simple_list_item_1, items)
            if (name != "") {
                // Storage data
                val blob = ByteArrayOutputStream()
                imgWithResult.compress(CompressFormat.PNG, 0 /* Ignored for PNGs */, blob)
                val bitmapdata = blob.toByteArray()
                uploadLat  = CommonFunction.MyLocation(MyView!!.context).findLat()
                uploadLng = CommonFunction.MyLocation(MyView!!.context).findLng()
                dbrw.execSQL("insert into history(Variety, Category_img, Time, Lat, Lng)" +
                        " values(?,?,?,?,?)", arrayOf(
                    name,
                    bitmapdata,
                    CommonFunction().getTime(),
                    uploadLat,
                    uploadLng))
                uploadName = name
                uploadImg = bitmapdata
                btnUpload.isEnabled = true
            }
            loading.dialog.dismiss()
        }
    }
    private fun drawDetectionResult(
        bitmap: Bitmap,
        detectionResults: List<CommonData.DetectionResult>,
    ): Bitmap {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val pen = Paint()

        detectionResults.forEach{
            // ??????bounding box
            pen.color = Color.GREEN
            pen.strokeWidth = 8F
            pen.style = Paint.Style.STROKE
            val box = it.boundingBox
            canvas.drawRect(box, pen)
            val tagSize = Rect(0,0,0,0)
            // ??????
            pen.style = Paint.Style.FILL_AND_STROKE
            pen.color = Color.RED
            pen.strokeWidth = 2F
            pen.textSize = 96F
            pen.getTextBounds(it.text, 0, it.text.length, tagSize)
            val fontSize: Float = pen.textSize * box.width() / tagSize.width()
            // ????????????????????????????????????bounding box???
            if (fontSize < pen.textSize) pen.textSize = fontSize

            var margin = (box.width() - tagSize.width()) / 2.0F
            if (margin < 0F) margin = 0F
            canvas.drawText(
                it.text,
                box.left + margin,
                box.top + tagSize.height().times(1F),
                pen
            )
        }
        return outputBitmap
    }
    /*-------------------------------------------------------------------*/
    // Upload
    @RequiresApi(Build.VERSION_CODES.O)
    private fun upload() {
        loading.show(MyView!!.context, "?????????...")
        val currentDateTime = LocalDateTime.now()
        uploadDate = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val requestBody = FormBody.Builder()
            .add("name", uploadName)
            .add("longitude", uploadLng)
            .add("latitude", uploadLat)
            .add("date", uploadDate)
            .build()
        val request = Request.Builder()
            .addHeader("Content-Type", "application/json")
            .url(CommonData().detailUrl)
            .post(requestBody)
            .build()
        if (uploadName != "") {
            OkHttpClient().newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                        loading.dialog.dismiss()
                        Looper.prepare()
                        dialog("????????????")
                        Looper.loop()
                    }
                    override fun onResponse(call: Call, response: Response) {
                        uploadName = ""
                        activity?.runOnUiThread {
                            val prefix = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", Locale.TAIWAN).format(Date())
                            val suffix = ".jpg"
                            val imageName = "$prefix$suffix"
                            val body = MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart(
                                    "imageFile",
                                    imageName,
                                    RequestBody.create("image/jpeg".toMediaTypeOrNull(), uploadImg!!)
                                )
                                .build()
                            val req = Request.Builder()
                                .url(CommonData().imageUrl)
                                .post(body)
                                .build()
                            OkHttpClient().newCall(req)
                                .enqueue(object : Callback {
                                    override fun onFailure(call: Call, e: IOException) {
                                        loading.dialog.dismiss()
                                        e.printStackTrace()
                                        Looper.prepare()
                                        dialog("????????????")
                                        Looper.loop()
                                    }
                                    override fun onResponse(call: Call, response: Response) {
                                        loading.dialog.dismiss()
                                        Looper.prepare()
                                        dialog("????????????")
                                        Looper.loop()
                                    }
                                })
                        }
                    }
                })
        }else {
            loading.dialog.dismiss()
            dialog("????????????!!!")
        }
    }
}