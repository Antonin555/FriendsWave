package com.antonin.friendswave.ui.home


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.antonin.friendswave.R
import com.antonin.friendswave.adapter.MyGridViewAdapter
import com.antonin.friendswave.data.firebase.FirebaseSource
import com.antonin.friendswave.data.repository.UserRepo
import com.antonin.friendswave.databinding.ActivityEditProfilBinding
import com.antonin.friendswave.ui.viewModel.HomeFragmentVMFactory
import com.antonin.friendswave.ui.viewModel.HomeFragmentViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class EditProfilActivity : AppCompatActivity(), KodeinAware {


    override val kodein : Kodein by kodein()
    private val factory : HomeFragmentVMFactory by instance()
    private var viewModel: HomeFragmentViewModel = HomeFragmentViewModel(repository = UserRepo(firebase = FirebaseSource()))
    private lateinit var binding: ActivityEditProfilBinding
    private lateinit var adapter: MyGridViewAdapter
    private lateinit var img_uri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profil)
        viewModel = ViewModelProviders.of(this, factory).get(HomeFragmentViewModel::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        viewModel.fetchUserData()
        viewModel.fetchInteret()
        actualiserSpinner()
        afficheInteret()

        binding.btnLoad.setOnClickListener {

            val img = Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

            getResult.launch(img)

        }
    }


    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

            if (it.resultCode == Activity.RESULT_OK) {

                img_uri = it?.data?.data!!
                binding.imgPreview.setImageURI(img_uri)

            }
        }

    fun actualiserSpinner(){
        var positionToSelect = 0
        val mySpinnerL = binding.spinnerLangues
        val mySpinnerE = binding.spinnerEtudes
        //pour inserer les donnees actuelles de l'user dans les spinners
        binding.viewmodel!!.user_live.observe(this, Observer {
            for (i in 0 until mySpinnerL.count) {
                if (mySpinnerL.getItemAtPosition(i).toString() == it?.langue) {
                    positionToSelect = i
                    break
                }
            }
            mySpinnerL.setSelection(positionToSelect)
            positionToSelect = 0
            for (i in 0 until mySpinnerE.count) {
                if (mySpinnerE.getItemAtPosition(i).toString() == it?.etude) {
                    positionToSelect = i
                    break
                }
            }
            mySpinnerE.setSelection(positionToSelect)
        })
    }

    fun afficheInteret(){
        binding.viewmodel!!.interetList.observe(this, Observer{
            adapter = MyGridViewAdapter(this, it, binding.viewmodel!!)
            binding.gridView.adapter = adapter
        })

    }

}





