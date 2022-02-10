package com.awesomesol.peering.catDiary

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.awesomesol.peering.R
import org.threeten.bp.LocalDate

class MonthListRVAdapter(val items: HashMap<String, Int>, val year:Int) : RecyclerView.Adapter<MonthListRVAdapter.Viewholder>() {
    // 아이템 하나(month_list_rv_item.xml) 갖고와서 하나의 레이아웃 만들어줌

    // 13개
    val ln0:Double=0.0
    var monthRatio:ArrayList<Double> = arrayListOf(ln0,ln0,ln0,ln0,ln0,ln0,ln0,ln0,ln0,ln0,ln0,ln0,ln0)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonthListRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.catdiary_month_list_rv_item, parent, false)
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: MonthListRVAdapter.Viewholder, position: Int) {

        monthRatio = arrayListOf(ln0,ln0,ln0,ln0,ln0,ln0,ln0,ln0,ln0,ln0,ln0,ln0,ln0)

        for (key in items.keys){
            val arr=key.split("-")
            if (arr[0] == year.toString()){
                val count: Double =items[key]?.toDouble() as Double
                val m=arr[1].toInt()
                when(m){
                    1, 3, 5, 7, 8, 10, 12 -> {
                        val ln31:Double=31.0
                        monthRatio[m]= count/ln31
                        Log.d("뷰홀더 monthRatio", "${monthRatio[m]}")}
                    2->  {
                        val ln28:Double=28.0
                        monthRatio[m]= count/ln28
                        Log.d("뷰홀더 monthRatio", "${monthRatio[m]}")}
                    else-> {
                        val ln30:Double=30.0
                        monthRatio[m]= count/ln30
                    }
                }
            }
        }
        monthRatio.removeAt(0)
        Log.d("먼스 뷰홀더", "items, $items")
        Log.d("먼스 뷰홀더", "monthRatio, $monthRatio")

        holder.bindItems(monthRatio[position])
    }

    override fun getItemCount(): Int {
        return 12
    }

    // month_list_rv_item.xml에 하나하나씩 리턴 넣어주는 역할. 아이템의 내용물 넣어주기
    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: Double) {
            val textMonth = itemView.findViewById<TextView>(R.id.textMonth)
            var m=""
            var pos=position+1
            if (pos<10) {m="0"+pos}
            else {m=""+pos}
            textMonth.text = m

            if (item<=0.0){itemView.findViewById<ImageView>(R.id.ghostMonthFill).setImageResource(R.drawable.ghost_o_fill)}
            else if (item<=0.1){itemView.findViewById<ImageView>(R.id.ghostMonthFill).setImageResource(R.drawable.ghost_little_fill)}
            else if (item<=0.5){itemView.findViewById<ImageView>(R.id.ghostMonthFill).setImageResource(R.drawable.ghost_half_fill)}
            else if (item<=0.75){itemView.findViewById<ImageView>(R.id.ghostMonthFill).setImageResource(R.drawable.ghost_more_fill)}
            else if (item>=1.0){itemView.findViewById<ImageView>(R.id.ghostMonthFill).setImageResource(R.drawable.ghost_1_fill)}

            Log.d("먼스 뷰홀더", item.toString())
        }
    }
}