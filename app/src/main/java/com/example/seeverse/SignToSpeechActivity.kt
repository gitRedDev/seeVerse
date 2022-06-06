package com.example.seeverse

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.seeverse.databinding.ActivitySignToSpeechBinding
import com.example.seeverse.ml.SignToSpeech
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


typealias LumaListener = (luma: String) -> Unit


class SignToSpeechActivity : AppCompatActivity() {
    private lateinit var context: Context;
    private lateinit var viewBinding: ActivitySignToSpeechBinding

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var takeCaptureOn: Boolean = false

    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySignToSpeechBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { capture() }

        context = this;
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun capture() {
        takeCaptureOn = true
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, SignAnalyzer {luma ->
                        runOnUiThread {
                            val previous = viewBinding.detectedText.text
                            viewBinding.detectedText.text = "%s%s".format(previous,luma)}
                        }
                    )
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview,imageAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "seeVerse"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
        private val ALPHABETS = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
    }

    private inner class SignAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        fun Bitmap.toGrayscale(): Bitmap? {
            val width: Int
            val height: Int
            height = this.height
            width = this.width
            val bmGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val c = Canvas(bmGrayscale)
            val paint = Paint()
            val cm = ColorMatrix()
            cm.setSaturation(0f)
            val f = ColorMatrixColorFilter(cm)
            paint.colorFilter = f
            c.drawBitmap(this, 0.toFloat(), 0.toFloat(), paint)
            return bmGrayscale
        }



        override fun analyze(image: ImageProxy) {
            runOnUiThread {
                if(takeCaptureOn){
                    val image_width = 32
                    val image_heigth = 32
                    val bm = viewBinding.viewFinder.bitmap!!
                    val bm_32 = Bitmap.createScaledBitmap(bm, image_width, image_heigth, true)
                        .toGrayscale()
                    viewBinding.image?.setImageBitmap(bm_32)
                    try {
                        val model = SignToSpeech.newInstance(this@SignToSpeechActivity)

                        // Creates inputs for reference.
                        val inputFeature0 =
                            TensorBuffer.createFixedSize(intArrayOf(1, 32, 32, 3), DataType.FLOAT32)
                        val tensorImage = TensorImage(DataType.FLOAT32)
                        tensorImage.load(bm_32)
                        val byteBuffer = tensorImage.buffer
                        inputFeature0.loadBuffer(byteBuffer)

                        // Runs model inference and gets result.
                        val outputs = model.process(inputFeature0)
                        val prediction = outputs.outputFeature0AsTensorBuffer.floatArray
                        val max = prediction.toList().maxOrNull()
                        val predictedLetterIndex = prediction.indexOfFirst { it == max }
                        val predictedLetter = ALPHABETS[predictedLetterIndex]

                        // Releases model resources if no longer used.
                        model.close()

                        listener(predictedLetter)
                        takeCaptureOn = false
                    }
                    catch (e: IOException) {
                        // TODO Handle the exception
                    }
                }

                image.close()
            }}


}
}