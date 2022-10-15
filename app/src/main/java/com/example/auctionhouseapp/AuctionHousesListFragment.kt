package com.example.auctionhouseapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ListView


class AuctionHousesListFragment : Fragment() {

    private lateinit var listview : ListView
    private lateinit var auctionHouseArrayList: ArrayList<AuctionHouse>
    //private lateinit var adapterAuctionHousesList: AdapterAuctionHousesList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_auction_houses_list, container, false)
        val ListView = view.findViewById<ListView>(R.id.auction_houses_list)
        //ListView.adapter =
            //AuctionDaysListFragment.CustomListAdapter2(activity as HouseActivity, House)
        return view
    }
}

//    list = new ArrayList<>();
//    list.clear();
//    ref = FirebaseDatabase.getInstance().getReference("education").child(branch).child(sem);
//    ref.addValueEventListener(new ValueEventListener() {
//        @Override
//        public void onDataChange(DataSnapshot dataSnapshot) {
//            for (DataSnapshot data : dataSnapshot.getChildren())
//            {
//                NoticeUpload noticeUpload = data.getValue(NoticeUpload.class);
//                list.add(noticeUpload);
//            }
//            NoticeAdapter adapter = new NoticeAdapter(View_doc.this,list);
//            lst.setAdapter(adapter);
//            dia.dismiss();
//        }
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//            dia.dismiss();
//        }
//    });
//    }