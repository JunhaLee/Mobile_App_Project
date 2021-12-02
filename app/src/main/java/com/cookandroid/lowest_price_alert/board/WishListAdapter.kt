package com.cookandroid.lowest_price_alert.board

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity
import com.cookandroid.lowest_price_alert.MainActivity
import com.cookandroid.lowest_price_alert.R
import org.w3c.dom.Text

class WishListAdapter (val context: Context, val wishList: ArrayList<Wish>) : BaseAdapter() {
    override fun getCount(): Int {
        return wishList.size
    }

    override fun getItem(p0: Int): Any {
        return wishList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {/* LayoutInflater는 item을 Adapter에서 사용할 View로 부풀려주는(inflate) 역할을 한다. */
        val view: View = LayoutInflater.from(context).inflate(R.layout.board_lv_wish_item, null)

        /* 위에서 생성된 view를 res-layout-main_lv_item.xml 파일의 각 View와 연결하는 과정이다. */
        val wishId = view.findViewById<TextView>(R.id.wishId)
        val itemId = view.findViewById<TextView>(R.id.itemId)
        val itemPhoto = view.findViewById<ImageView>(R.id.itemPhoto)
        val itemName = view.findViewById<TextView>(R.id.itemName)
        val option = view.findViewById<TextView>(R.id.option)
        val itemPrice = view.findViewById<TextView>(R.id.itemPrice)


        /* ArrayList<Dog>의 변수 dog의 이미지와 데이터를 ImageView와 TextView에 담는다. */
        val wish = wishList[p0]
        val resourceId = context.resources.getIdentifier(wish.itemPhoto, "drawable", context.packageName)
        itemPhoto.setImageResource(resourceId)
        wishId.text = wish.wishId
        itemId.text = wish.itemId
        itemName.text = wish.itemName
        option.text = wish.option
        itemPrice.text = wish.itemPrice

        view.setOnClickListener {
            Toast.makeText(this.context, itemName.text, Toast.LENGTH_SHORT).show()
//            val intent = Intent(this.context, PostContentActivity::class.java)
//            intent.putExtra("boardId", boardId.text)
//            intent.putExtra("postId", postId.text)
//            startActivity(this.context, intent, null)
        }

        return view
    }


}