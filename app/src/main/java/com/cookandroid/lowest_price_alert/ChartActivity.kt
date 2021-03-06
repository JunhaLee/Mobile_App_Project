package com.cookandroid.lowest_price_alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.cookandroid.lowest_price_alert.board.BoardActivity
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.github.mikephil.charting.components.YAxis as YAxis

class ChartActivity : AppCompatActivity() {
    lateinit var lineChart: LineChart
    lateinit var linkBtn: Button
    val chartData = ArrayList<ChartData>() // Line Chart에 그리기 위한 데이터를 담을 ArrayList

    val channel_name: String = "CHANNEL_1"
    val CHANNEL_ID: String = "MY_CH"
    val notificationId: Int = 1002

    val now_user = "nhUKsEBop5beTg2c4jT4vZtYj842"  // 현재 user 하드 코딩
//    var now_product = "15253217" // 현재 물품 하드 코딩
//    var now_product_no = "fQIjkqqMKEQQVbrbcM3v"
    lateinit var now_product: String
    lateinit var now_product_no: String
    lateinit var scroll_chart : ScrollView
    lateinit var bottom_btn : ImageButton


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_info)
        title = "최저가 알리미"


        val firebaseDatabase = FirebaseDatabase.getInstance() // 실시간 데이터 db
        val firestoredb = FirebaseFirestore.getInstance() // firestore db


        if (intent.hasExtra("product_no") && intent.hasExtra("product_code")){
            now_product = intent.getStringExtra("product_no").toString()
            now_product_no = intent.getStringExtra("product_code").toString()
            Log.d(ContentValues.TAG, "no, code 둘다 있음!!!! $now_product $now_product_no")
        }

        scroll_chart = findViewById(R.id.ScrollView1)
        bottom_btn = findViewById(R.id.bottom_btn)
        bottom_btn.setOnClickListener {
            scroll_chart.scrollTo(0,0)
        }

        //네비게이션 바
        lateinit var board_Btn: ImageButton
        lateinit var home_Btn : ImageButton
        lateinit var zzim_Btn : ImageButton
        lateinit var search_Btn : ImageButton
        lateinit var mypage_Btn: ImageButton

        board_Btn = findViewById(R.id.board_Btn)
        mypage_Btn = findViewById(R.id.mypage_Btn)
        home_Btn = findViewById(R.id.home_Btn)
        zzim_Btn = findViewById(R.id.zzim_Btn)
        search_Btn = findViewById(R.id.search_Btn)

        board_Btn.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            startActivity(intent)
        }
        mypage_Btn.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
        home_Btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        zzim_Btn.setOnClickListener {
            val intent = Intent(this, ZzimActivity::class.java)
            startActivity(intent)
        }
        search_Btn.setOnClickListener {
            val intent = Intent(this, SearchViewActivity::class.java)
            startActivity(intent)
        }



        var imageview = findViewById<ImageView>(R.id.imageview) // 제품 사진 ImageView
        var product_name_textView = findViewById<TextView>(R.id.nametext) // 제품명 TextView
        var most_cheap_textview = findViewById<TextView>(R.id.most_cheap) // 최저가 TextView
        var avg_textview = findViewById<TextView>(R.id.average) // 평균가 TextView
        var now_textview = findViewById<TextView>(R.id.now_price) // 현재가 TextView
        var most_expensive_textview = findViewById<TextView>(R.id.most_expensive) // 최고가 TextView
        var button_zzim = findViewById<Button>(R.id.zzim_button) // 찜하기 버튼
        var back_btn = findViewById<ImageButton>(R.id.back_button) // 뒤로가기
        var btn7 = findViewById<Button>(R.id.btn7)
        var btn10 = findViewById<Button>(R.id.btn10)
        var btn15 = findViewById<Button>(R.id.btn15)
        var days_for_month = arrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31) // 매달 날짜 수
        var is_zzim = -1 // 해당 물품이 찜 목록에 존재하는지 ( -1 : 찜 목록에 없음, num : 찜 목록의 num 번째 index에 해당 물품이 존재)
        linkBtn = findViewById(R.id.link_Btn)

        btn15.setBackgroundColor(Color.parseColor("#e9ecef"))

        back_btn.setOnClickListener{
            onBackPressed()
        }

        firestoredb.collection("user").document(now_user).get().addOnSuccessListener { result ->

            val wish_list = result["wish_list"] as ArrayList<String>
            for (i: Int in 0..wish_list.size-1) { // 이미 찜 목록에 해당 물품이 존재하는지 확인
                if (wish_list.get(i).equals(now_product)) {
                    button_zzim.text = "★"
                    is_zzim = i
                    break
                }
            }
        }

        button_zzim.setOnClickListener {

            if (is_zzim == -1) { // 찜 목록에 들어있지 않다면
                //firestoredb.collection("user").document(now_user).update("test", "22")
                firestoredb.collection("user").document(now_user).get()
                    .addOnSuccessListener { result ->

                        val wish_list = result["wish_list"] as ArrayList<String>
                        wish_list.add(now_product)// 찜 목록에 추가

                        firestoredb.collection("user").document(now_user)
                            .update("wish_list", wish_list)
                        button_zzim.text = "★"
                        is_zzim = wish_list.size - 1

                    }
            } else { // 찜 목록에 들어있다면
                firestoredb.collection("user").document(now_user).get()
                    .addOnSuccessListener { result ->

                        val wish_list = result["wish_list"] as ArrayList<String>
                        wish_list.removeAt(is_zzim) // 찜 목록에서 제거

                        firestoredb.collection("user").document(now_user)
                            .update("wish_list", wish_list)
                        button_zzim.text = "☆"
                        is_zzim = -1

                    }
            }
        }

        firestoredb.collection("product_list").document(now_product_no) //firestore db로부터 제품 읽어오기
            .get()
            .addOnSuccessListener { result -> // firestore에서 해당 물품 가져오기
                val product_no = result["no"].toString()
                val StartDate = result["start_date"].toString()
                var imageURL = result["image_url"].toString()
                val name = result["name"].toString()

                var start_date_info =
                    StartDate.split("-") // 시작 날짜 정보 가공 (나중에 업그레이드 해야함)

                var start_year = start_date_info[0].toInt()
                var start_month = start_date_info[1].toInt()
                var start_date = start_date_info[2].toInt()

                var max_cost = 0 // 최고가 검색을 위한 변수
                var min_cost = 1000000000 // 최저가 검색을 위한 변수
                var avg_cost = 0 // 평균가 검색을 위한 변수
                var total_cost = 0 // 평균가 검색을 위해 모든 가격에 대한 합산을 위한 변수
                var now_price = 0 // 현재가격

                var change_flag = 0
                val path = "product_list/" + now_product.toString() // 실시간 db에 접근하기 위한 경로. 현재는 하드코딩.
                val myRef: DatabaseReference = firebaseDatabase.getReference(path) // 실시간 db에 접근
                var name_substring = ""

                if(name.length > 25){
                    name_substring = name.substring(0,25)
                }
                else{
                    name_substring = name.toString()
                }
                val intent = Intent(this, ChartActivity::class.java).apply { // 알림 클릭시 실행할 Activity 지정
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                intent.putExtra("product_code", now_product_no.toString())
                intent.putExtra("product_no", now_product.toString())
                val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
                var mLargeIconForNoti = BitmapFactory.decodeResource(getResources(),R.drawable.brring_square)
                var builder = NotificationCompat.Builder(this, CHANNEL_ID) // 푸쉬 알람 기능
                    .setSmallIcon(R.drawable.brring_icon)
                    .setContentTitle("BRRING 최저가 알림") // 푸쉬 알람에 띄울 큰 문장
                    .setContentText(name_substring) // 푸쉬 알람에 띄울 작은 문장
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setLargeIcon(mLargeIconForNoti)
                    .setContentIntent(pendingIntent) // 알림 클릭 시 Activity 실행

                product_name_textView.text =
                    name // 제품명 textView에 띄우기

                imageURL = "http://" + imageURL
                Glide.with(this).load(imageURL)
                    .into(imageview) //이미지 url로 사진 불러오기

                linkBtn.setOnClickListener { // 외부 링크로 연결
                    var url_for_link = "http://prod.danawa.com/info/?pcode=" + now_product
                    var intent = Intent(Intent.ACTION_VIEW, Uri.parse(url_for_link))
                    startActivity(intent)
                }

                myRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val snapshot_info = snapshot.child("price")
                        var now_date = start_date
                        var now_month = start_month
                        var now_year = start_year
                        var count_record = 0 // 평균가 검색을 위해 검색한 가격의 수에 대한 변수

                        chartData.clear()

                        for (item in snapshot_info.children) {
                            count_record++;
                            val today_date: String
                            if (now_date >= 10) // 날짜 정보
                                today_date =
                                    now_year.toString() + "-" + now_month.toString() + "-" + now_date.toString()
                            else
                                today_date =
                                    now_year.toString() + "-" + now_month.toString() + "-0" + now_date.toString()

                            var price_info = item.value.toString()  //  실시간 db로 부터 가져오는 가격 정보

                            max_cost = Math.max(max_cost, price_info.toInt()) // 최고가 갱신
                            if (min_cost > price_info.toInt()) {
                                min_cost =
                                    Math.min(min_cost, price_info.toInt()) // 최저가 갱신
                            }
                            total_cost += price_info.toInt() // 가격 합산 갱신
                            avg_cost = total_cost / count_record // 평균가 갱신
                            now_price = price_info.toInt()

                            most_cheap_textview.text =
                                "최저가   " + min_cost.toString() + "원" // textview에 최저가 띄우기
                            most_expensive_textview.text =
                                "최고가   " + max_cost.toString() + "원"// textview에 최고가 띄우기
                            avg_textview.text =
                                "평균가   " + avg_cost.toString() + "원"// textview에 평균가 띄우기
                            now_textview.text =
                                "현재가   " + now_price.toString() + "원" // textview에 현재가 띄위기

                            addChartItem(
                                today_date,
                                price_info.toDouble()
                            ) // Chart를 그리기 위해 가격 및 날짜 정보 추가

                            if (now_date >= days_for_month[now_month-1]) { // 날짜 업데이트 (하루 추가)
                                now_date = 1
                                if (now_month >= 12) {
                                    now_year++
                                    now_month = 1
                                } else {
                                    now_month += 1
                                }
                            } else {
                                now_date++
                            }
                        }
                        LineChartGraph(chartData, "price", 15) // 그래프 그리기
                        change_flag = 1


                        // 며칠 간의 가격정보 보여줄지 선택
                        btn7.setOnClickListener {
                            btn7.setBackgroundColor(Color.parseColor("#e9ecef"))
                            btn10.setBackgroundColor(Color.parseColor("#00ff0000"))
                            btn15.setBackgroundColor(Color.parseColor("#00ff0000"))
                            LineChartGraph(chartData, "price",5)
                        }
                        btn10.setOnClickListener {
                            btn7.setBackgroundColor(Color.parseColor("#00ff0000"))
                            btn10.setBackgroundColor(Color.parseColor("#e9ecef"))
                            btn15.setBackgroundColor(Color.parseColor("#00ff0000"))
                            LineChartGraph(chartData, "price",7)
                        }

                        btn15.setOnClickListener {
                            btn7.setBackgroundColor(Color.parseColor("#00ff0000"))
                            btn10.setBackgroundColor(Color.parseColor("#00ff0000"))
                            btn15.setBackgroundColor(Color.parseColor("#e9ecef"))
                            LineChartGraph(chartData, "price",10)
                        }

                    }

                    override fun onCancelled(error: DatabaseError) { // 실시간 db 접근을 실패하면
                        println("Failed to read value.")
                    }
                })


            }
    }


    data class UserData(
        var email: String = "",
        var wish_list: ArrayList<String> = ArrayList()
    )

    data class ChartData(
        var dateData: String = "",
        var priceData: Double = 0.0
    )

    private fun addChartItem(date: String, price: Double) { // 차트에 담을 정보 추가
        val item = ChartData()
        item.priceData = price // 가격
        item.dateData = date // 날짜
        chartData.add(item)
    }



    private fun LineChartGraph(chartItem: ArrayList<ChartData>, displayname: String, days : Int) { // 날짜 제한이 있을 경우 그래프
        lineChart = findViewById(R.id.lineChart)
        lineChart.setDescription("");
        lineChart.getAxisRight().setDrawLabels(false) // 우측 기준선 삭제
        lineChart.getAxisLeft().setDrawLabels(false) // 좌측 기준선 삭제

        val entries = ArrayList<Entry>()
        if(chartItem.size < days) { // 지정 날짜보다 데이터가 적으면 있는 데이터 모두 띄우기
            for (i in chartItem.indices) {
                entries.add(Entry(chartItem[i].priceData.toFloat(), i)) //  그래프 그리기 위해서 가격 정보 추가
            }
        } else{ // 지정 날짜보다 데이터가 많으면 해당 날짜 만큼 데이터 띄우기
            for (i: Int in chartItem.size - days.. chartItem.size-1){ //  날짜 제한이 있을 경우
                entries.add(Entry(chartItem[i].priceData.toFloat(), i - (chartItem.size - days))) //  그래프 그리기 위해서 가격 정보 추가
            }
        }

        val depenses = LineDataSet(entries, displayname)
        depenses.axisDependency = YAxis.AxisDependency.LEFT
        depenses.valueTextSize = 12f // 값 폰트 지정하여 크게 보이게 하기
        depenses.setColor(Color.parseColor("#7C72D3"))

        depenses.setDrawCircles(true)
        depenses.setDrawCircleHole(true)
        depenses.setCircleColor(Color.parseColor("#7C72D3"))
        depenses.setCircleColorHole(Color.parseColor("#7C72D3"))
        //depenses.setCircleColor(Color.parseColor("#800000"))
        //depenses.setDrawCubic(true); //선 둥글게 만들기
        depenses.setDrawFilled(true) //그래프 밑부분 색칠
        depenses.setLineWidth(3f) // 그래프 선 굵기
        depenses.setFillColor(ContextCompat.getColor(this,R.color.purple_200)) // 배경색




        val labels = ArrayList<String>()
        if(chartItem.size < days) {
            for (i in chartItem.indices) {
                var date = chartItem[i].dateData.split('-')
                var real_date = date[1] + "-" + date[2]

                labels.add(real_date) // 그래프 그리기 위해서 날짜 정보 추가
            }
        } else{
            for (i: Int in chartItem.size - days.. chartItem.size-1){
                var date = chartItem[i].dateData.split('-')
                var real_date = date[1] + "-" + date[2]

                labels.add(real_date) // 그래프 그리기 위해서 날짜 정보 추가
            }
        }

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(depenses as ILineDataSet)
        val data = LineData(labels, dataSets) // 라이브러리 v3.x 사용하면 에러 발생함

        lineChart.data = data
        lineChart.animateXY(1000, 1000);
        lineChart.getAxisLeft().setDrawGridLines(false)
        lineChart.getXAxis().setDrawGridLines(false) // 세로선 삭제
        lineChart.invalidate()
    }


    private fun createNotificationChannel( // 푸쉬 알림 생성
        builder: NotificationCompat.Builder,
        notificationId: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = "notification for lowest price"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, channel_name, importance).apply {
                description = descriptionText
            }

            channel.lightColor = Color.BLUE
            //channel.enableVibration(true)
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            notificationManager.notify(notificationId, builder.build())
        } else {
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, builder.build())
        }
    }
}
