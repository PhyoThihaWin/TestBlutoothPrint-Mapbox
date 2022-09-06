package com.example.testbluetoothprint

import android.Manifest
import android.R.attr.bitmap
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anggastudio.printama.Printama
import id.zelory.cekrek.config.CanvasSize
import id.zelory.cekrek.extension.cekrekToBitmap


class MainActivity : AppCompatActivity() {

    var connectedPrinter: BluetoothDevice?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        findViewById<Button>(R.id.btnPrint).setOnClickListener {
            //---time to request permission
            if (checkMyPermission(this)) {
                Log.i("mypermission", "already granted")


                if (connectedPrinter == null) {
                    Printama.showPrinterList(this,R.color.purple_700){

                    }


                    connectedPrinter = Printama.with(this).connectedPrinter
                }

                if (connectedPrinter == null) {
                    toast("Printer not connected!")
                    return@setOnClickListener
                }



                Printama.with(this).connect { printama: Printama ->
                    printama.printImage(getBitmapFormView())
                    printama.printLine()
                    printama.close()
                }


            } else requestMyPermission(this)


        }


        findViewById<ImageView>(R.id.img).setImageBitmap(getBitmapFormView())

    }



    private fun getBitmapFormView() =
        getPrintView().cekrekToBitmap{
            canvasConfig.width = CanvasSize.Specific(1280) // set canvas size to 1280 px
        }






    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE ->
                if (grantResults.size > 1) {
                    val accept1 = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val accept2 = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    val accept3 = grantResults[2] == PackageManager.PERMISSION_GRANTED
                    val accept4 = grantResults[3] == PackageManager.PERMISSION_GRANTED
                    if (accept1 && accept2 && accept3 && accept4)
                        Log.i("mypermission", "granted")
                    else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                                showMessageOKCancel(getString(R.string.need_permission),
//                                    DialogInterface.OnClickListener { dialog, which ->
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                            requestPermissions(ConstantValue.perms, ConstantValue.PERMISSION_REQUEST_CODE)
//                                        }
//                                    })
                                return
                            }
                        }
                    }
                }
        }
    }


    //---PERMISSION REQUEST FUNCTION---//
    val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
    } else {
        arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
    }
    var PERMISSION_REQUEST_CODE = 200

    fun checkMyPermission(context: Context): Boolean {
        val result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH)
        val result2 =
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val result3 =
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
            val result4 =
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
            result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED
        } else {
            result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestMyPermission(activity: Activity) {
        ActivityCompat.requestPermissions(activity, perms, PERMISSION_REQUEST_CODE)
    }



    private fun getPrintView(): View {

        val foods = mutableListOf<Food>()
        foods.add(Food(3,"ကြာဇံထုပ်", 2000))
        foods.add(Food(3,"ခေါက်ဆွဲထုပ်", 2000))
        foods.add(Food(3,"အုန်းဆီ", 2000))
        foods.add(Food(3,"မြေပဲ", 2000))
        foods.add(Food(3,"ကြာဇံထုပ်", 2000))

        findViewById<TextView>(R.id.tvCustomerName).text = "PTHW"
        findViewById<TextView>(R.id.tvOrderTime).text = "12:00"
        findViewById<TextView>(R.id.tvResturantNamae).text = "Wholesale"
        findViewById<TextView>(R.id.tvTotal).text = "2000"
        findViewById<TextView>(R.id.tvBillTotalPrice).text = "2200"
        findViewById<TextView>(R.id.tvDeliveryCharge).text = "200"
        findViewById<RecyclerView>(R.id.recFood).apply {
            adapter = AdapterFoodlistPrint(foods)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        val v = findViewById<View>(R.id.reciepeLaoyut)
        return v
    }

}