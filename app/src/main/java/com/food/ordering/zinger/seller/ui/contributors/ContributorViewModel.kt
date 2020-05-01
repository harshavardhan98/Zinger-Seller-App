package com.food.ordering.zinger.seller.ui.contributors

import androidx.lifecycle.ViewModel
import com.food.ordering.zinger.seller.data.model.ContributorModel

class ContributorViewModel() : ViewModel() {

    private var contributorList: ArrayList<ContributorModel> = arrayListOf()
    init {
        contributorList.clear()
        contributorList.add(
            ContributorModel(
                "Shrikanth Ravi",
                "Android Developer & UI Designer",
                "https://github.com/shrikanth7698",
                "https://www.linkedin.com/in/shrikanthravi",
                "shrikanthravi.me@gmail.com",
                "https://shrikanthravi.me/",
                "file:///android_asset/shrikanth.jpg"
            )
        )
        contributorList.add(
            ContributorModel(
                "Harshavardhan P",
                "Android & Backend Developer",
                "https://github.com/harshavardhan98",
                "https://www.linkedin.com/in/harshavardhan-p/",
                "harshavardhan.zodiac@gmail.com",
                "https://harshavardhan98.github.io/",
                "file:///android_asset/harsha.jpg"
            )
        )
        contributorList.add(
            ContributorModel(
                "Logesh Dinakaran",
                "Backend Development",
                "https://github.com/ddlogesh",
                "https://www.linkedin.com/in/logesh-dinakaran",
                "ddlogesh@gmail.com",
                "https://logeshdina.tech/",
                "file:///android_asset/logesh.jpg"
            )
        )
    }

    fun getContributor(contributorId: Int):ContributorModel {
        return contributorList[contributorId]
    }

}